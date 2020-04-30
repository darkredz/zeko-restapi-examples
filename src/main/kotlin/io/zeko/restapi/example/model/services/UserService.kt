package io.zeko.restapi.example.model.services

import io.vertx.circuitbreaker.CircuitBreaker
import io.vertx.core.json.JsonObject
import io.vertx.core.logging.Logger
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.zeko.restapi.example.model.entities.User
import io.zeko.restapi.example.model.repositories.UserRepo
import io.zeko.restapi.core.mail.MailResponse
import io.zeko.restapi.core.mail.MailService
import io.zeko.restapi.core.security.JWTAuthHelper
import io.zeko.restapi.core.security.PasswordSecurity
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.time.LocalDate


class UserService(val userRepo: UserRepo, val logger: Logger) : KoinComponent {

    val jwtAuth: JWTAuth by inject("jwtAuth")
    val jwtAuthRefresh: JWTAuth by inject("jwtAuthRefresh")
    val mailService by inject<MailService>()
    val circuitBreaker: CircuitBreaker by inject("mail")

    suspend fun sendRegistrationEmail(email: String, fullName: String): MailResponse? {
        return mailService.sendInCircuit(circuitBreaker, email, fullName,
                    "User Registration Success",
                    "<h2>Success!</h2><p>You are now a new user!</p>","Success! You are now a new user!",
                            listOf("register"))
    }

    suspend fun login(email: String, password: String): JsonObject {
        val user = userRepo.getByEmail(email)
        var ok = false
        if (user != null) {
            ok = PasswordSecurity().validatePassword(password, user.password!!)
        }
        var token: String? = null
        var refreshToken: String? = null

        if (ok) {
            val tokenData = json {
                obj(
                        "iss" to "http://localhost",
                        "id" to user!!.id,
                        "email" to user!!.email,
                        "first_name" to user!!.firstName,
                        "last_name" to user!!.lastName
                )
            }
            val authTokens = JWTAuthHelper(jwtAuth, jwtAuthRefresh).generateAuthTokens(tokenData)
//            val authTokens = JWTAuthHelper(jwtAuth, jwtAuthRefresh).generateAuthTokens(tokenData, 60, 300)
            token = authTokens["access_token"]
            refreshToken = authTokens["refresh_token"]
            userRepo.updateLastAccess(user!!)
        }

        return json {
            obj(
                "auth" to ok,
                "access_token" to token,
                "refresh_token" to refreshToken
            )
        }
    }

    suspend fun getProfileStatus(uid: Int): User? {
        val user = userRepo.getActiveUser(uid)
        return User().apply {
            id = user?.id
            firstName = user?.firstName
            lastName = user?.lastName
            notified = user?.notified
            status = user?.status
            lastAccessAt = user?.lastAccessAt
        }
    }

    suspend fun register(firstName: String, lastName: String, email: String, dob: LocalDate, country: String, password: String): User {
        val hashPwd = PasswordSecurity().generatePasswordHash(password, 58)
        val userId = userRepo.create(firstName, lastName, email, dob, country, hashPwd)
        if (userId > 0) {
            sendRegistrationEmail(email, "$firstName $lastName")
        }
        return User().apply { id = userId }
    }

    suspend fun listActive(page: Int): List<User> {
        return userRepo.listActiveWithRoleAddress(page)
    }
}

