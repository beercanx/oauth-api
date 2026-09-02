package uk.co.baconi.oauth.automation.api.config

import com.typesafe.config.ConfigFactory
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.jupiter.api.extension.ParameterContext
import org.junit.jupiter.api.extension.ParameterResolver
import org.junit.jupiter.params.provider.AnnotationBasedArgumentsProvider
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.ArgumentsSource
import org.junit.jupiter.params.support.ParameterDeclarations
import uk.co.baconi.oauth.automation.api.getConfig
import java.util.concurrent.atomic.AtomicInteger
import java.util.stream.Stream
import kotlin.annotation.AnnotationTarget.*
import kotlin.jvm.optionals.getOrElse

@Target(ANNOTATION_CLASS, FUNCTION, VALUE_PARAMETER)
@ArgumentsSource(UserProvider::class)
annotation class UserType(val state: UserState = UserState.Active)

@UserType(UserState.Active)
annotation class ActiveUser

@UserType(UserState.Suspended)
annotation class SuspendedUser

@UserType(UserState.Locked)
annotation class LockedUser

@UserType(UserState.Closed)
annotation class ClosedUser

private object UserPool {

    private val base = ConfigFactory.load().getConfig("uk.co.baconi.oauth.automation.api").getObject("users")

    val pool = base.keys.map(::toUser)

    private fun toUser(username: String): User {
        val value = base.getConfig(username)
        val password = value.getString("password")
        val state = value.getEnum(UserState::class.java, "state")

        return User(username, password, state)
    }
}

class UserProvider : AnnotationBasedArgumentsProvider<UserType>() {

    override fun provideArguments(
        parameters: ParameterDeclarations,
        context: ExtensionContext,
        userType: UserType
    ): Stream<out Arguments> {

        return UserPool.pool
            .stream()
            .filter { user -> user.state == userType.state }
            .map(Arguments::of)
    }
}

class UserResolver : ParameterResolver {

    companion object {
        private val indices = mutableMapOf<UserState, AtomicInteger>()
    }

    override fun supportsParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): Boolean {

        // Don't support when [ParameterizedTest] is using [UserType].
        if (parameterContext.declaringExecutable.isAnnotationPresent(UserType::class.java)) {
            return false
        }

        return User::class.java.isAssignableFrom(parameterContext.parameter.type)
    }

    override fun resolveParameter(parameterContext: ParameterContext, extensionContext: ExtensionContext): User {

        val userType = parameterContext.findAnnotation(UserType::class.java).getOrElse { UserType() }

        val pool = UserPool.pool
            .filter { user -> user.state == userType.state }

        val index = indices
            .getOrPut(userType.state) { AtomicInteger(0) }
            .getAndUpdate { current -> (current + 1) % pool.size }

        return pool[index]
    }
}