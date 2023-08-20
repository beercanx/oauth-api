package uk.co.baconi.oauth.api.common.client

import io.kotest.assertions.assertSoftly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import uk.co.baconi.oauth.api.common.client.ClientType.Confidential
import uk.co.baconi.oauth.api.common.scope.ScopeRepository

class ClientSecretServiceIntegrationTest {

    private val scopeRepository = ScopeRepository()
    private val clientSecretRepository = ClientSecretRepository()
    private val clientConfigurationRepository = ClientConfigurationRepository(scopeRepository)

    private val underTest = ClientSecretService(clientSecretRepository, clientConfigurationRepository)

    @Test
    fun `should be able to authenticate a valid client`() {
        assertSoftly(underTest.authenticate("client-secret-service", "s#gL1£ToSITrUC1giGUm")) {
            shouldNotBeNull()
            id shouldBe ClientId("client-secret-service")
            configuration.type shouldBe Confidential
        }
    }
}