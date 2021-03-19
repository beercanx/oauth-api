package com.sbgcore.oauth.api.customer.openbet

import com.sbgcore.oauth.api.customer.FailureReason.*
import com.sbgcore.oauth.api.customer.MatchFailure
import com.sbgcore.oauth.api.customer.MatchResponse
import com.sbgcore.oauth.api.customer.MatchService
import com.sbgcore.oauth.api.customer.MatchSuccess
import com.skybettingandgaming.oxi.client.LoginSequenceMismatchException
import com.skybettingandgaming.oxi.dto.*
import com.skybettingandgaming.oxi.dto.adapters.OffsetDateTimeOxiXmlAdapter
import io.ktor.client.*
import io.ktor.client.request.*

class OpenBetMatchService(private val httpClient: HttpClient) : MatchService {

    private val offsetDateTimeOxiXmlAdapter = OffsetDateTimeOxiXmlAdapter()

    companion object {
        const val TEMPORARY_PIN_FLAG = "PIN"
        const val ACCOUNT_LOCKED_FLAG = "LOCK"
    }

    override suspend fun match(username: String, password: String): MatchResponse {

        return try {

            val result: RespAccountValidate1 = httpClient.post {
                body = ReqAccountValidate1
                    .builder()
                    .accountNo(username)
                    .pin(password)
                    .updateLastLogin(OxiBoolean.TRUE)
                    .returnLastLogin(OxiBoolean.TRUE)
                    .returnBalance(OxiBoolean.FALSE)
                    .returnPreferences(OxiBoolean.FALSE)
                    .build()
            }

            when(val accountFailure = result.accountFailure) {
                is AccountFailure -> when(accountFailure.accountFailureCode) {
                    "209" -> MatchFailure(Mismatch) // Mismatch
                    "3008" -> MatchFailure(Mismatch) // Mismatch & Locked
                    else -> MatchFailure(Other)
                }
                else -> when {
                    result.hasStatusFlag(ACCOUNT_LOCKED_FLAG) -> {
                        MatchFailure(Locked)
                    }
                    result.status != OxiStatus.ACTIVE -> {
                        MatchFailure(Other)
                    }
                    else -> {
                        val temporary = result.hasStatusFlag(TEMPORARY_PIN_FLAG)
                        val lastLogin = offsetDateTimeOxiXmlAdapter.unmarshal(result.lastLogin) // TODO - Update OXI DTO to do this for us.

                        MatchSuccess(temporary, lastLogin)
                    }
                }
            }
        } catch (exception: LoginSequenceMismatchException) {
            // Basically we've sent two requests, for the same user, that succeeded to OpenBet within the same second
            // and one of them has now failed.
            MatchFailure(Conflict)
        }
    }

    private fun RespAccountValidate1.hasStatusFlag(flag: String): Boolean = statusFlags?.statusFlagName?.any {
        it.value == flag
    } ?: false
}