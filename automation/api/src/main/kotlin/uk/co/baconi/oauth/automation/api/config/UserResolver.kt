package uk.co.baconi.oauth.automation.api.config

import com.typesafe.config.ConfigFactory
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.support.TypeBasedParameterResolver
import uk.co.baconi.oauth.automation.api.getConfig
import java.util.concurrent.atomic.AtomicInteger
import kotlin.annotation.AnnotationTarget.VALUE_PARAMETER
import kotlin.jvm.optionals.getOrElse

@Target(AnnotationTarget.ANNOTATION_CLASS, VALUE_PARAMETER)
annotation class UserType(val state: UserState = UserState.Active)

@UserType(UserState.Active)
annotation class ActiveUser

@UserType(UserState.Suspended)
annotation class SuspendedUser

@UserType(UserState.Locked)
annotation class LockedUser

@UserType(UserState.Closed)
annotation class ClosedUser

class UserResolver : TypeBasedParameterResolver<User>() {

    companion object {

        private val base = ConfigFactory.load().getConfig("uk.co.baconi.oauth.automation.api").getObject("users")

        private val users = base.keys.map(::toUser)

        private val indices = mutableMapOf<UserState, AtomicInteger>()

        private fun toUser(username: String): User {
            val value = base.getConfig(username)
            val password = value.getString("password")
            val state = value.getEnum(UserState::class.java, "state")

            return User(username, password, state)
        }
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): User {

        val userType = parameterContext.findAnnotation(UserType::class.java).getOrElse { UserType() }

        val pool = users
            .filter { user -> user.state == userType.state }

        val index = indices
            .getOrPut(userType.state) { AtomicInteger(0) }
            .getAndUpdate { current -> (current + 1) % pool.size }

        return pool[index]
    }
}