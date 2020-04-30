package io.zeko.restapi.example.controller

import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.zeko.restapi.annotation.http.*
import io.zeko.restapi.annotation.Params
import io.zeko.restapi.core.controllers.ApiController
import io.zeko.restapi.core.validations.ValidateResult
import io.vertx.core.logging.Logger
import io.vertx.ext.web.RoutingContext
import io.zeko.db.sql.exceptions.DuplicateKeyException
import io.zeko.restapi.example.model.services.UserService
import io.zeko.restapi.core.utilities.endJson
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.time.LocalDate

@Routing("/user")
class UserController : KoinComponent, ApiController {
    val userService: UserService by inject()

    constructor(vertx: Vertx, logger: Logger, context: RoutingContext) : super(vertx, logger, context)

    override fun validateInput(statusCode: Int): ValidateResult {
        return super.validateInput(422)
    }

    @GetSuspend("/check-user-status/:user_id","Check user notification status")
    @Params(["user_id => required, isInteger, min;1, max;99999999"])
    suspend fun checkUserStatus(ctx: RoutingContext) {
        val res = validateInput()
        if (!res.success) {
            logger.debug("FAILED! " + Json.encodePrettily(res.errors))
            return
        }

        val uid = res.values["user_id"].toString().toInt()
        val user = userService.getProfileStatus(uid)
        endJson(user)
    }

    @PostSuspend("/email-test")
    @Params([
        "email => required, length;5;45, email"
    ])
    suspend fun emailTest(ctx: RoutingContext) {
        val res = validateInput()
        if (!res.success) return
        val result = userService.sendRegistrationEmail(res.values["email"].toString(), "Test User")
        endJson(result)
    }

    @Get("/check-token", "Check user access token data")
    fun checkToken(ctx: RoutingContext) {
        val user = ctx.user().principal()
        endJson(user)
    }

    @PostSuspend("/login", "Check user subscription status")
    @Params([
        "email => required, length;5;45, email",
        "password => required, length;1;32"
    ])
    suspend fun login(ctx: RoutingContext) {
        val res = validateInput()
        if (!res.success) return

        val email = res.values["email"].toString()
        val password = res.values["password"].toString()
        val loginResult = userService.login(email, password)

        ctx.endJson(loginResult)
    }

    @PostSuspend("/register","Register user")
    @Params([
        "first_name => required, length;1;45, alphaQuoteDashSpace",
        "last_name => required, length;1;45, alphaQuoteDashSpace",
        "dob => required, dateFormat, dateBetween;1911-01-01;2005-01-01",
        "email => required, length;5;45, email",
        "password => required, length;6;32, passwordSimple",
        "country_iso => required, length;2;2, inArray;MY;SG;CN;US;JP;UK",
        "campaign => isInteger, min;1, max;3"  //optional
    ])
    suspend fun register(ctx: RoutingContext) {
        val res = validateInput()
        if (!res.success) return

        val input = res.values
        val firstName = input["first_name"].toString()
        val lastName = input["last_name"].toString()
        val email = input["email"].toString()
        val dob = LocalDate.parse(input["dob"].toString())
        val country = input["country_iso"].toString()
        val password = input["password"].toString()

        try {
            val user = userService.register(firstName, lastName, email, dob, country, password)
            ctx.endJson(user)
        } catch (err: DuplicateKeyException) {
            if (err == "user_email_UN" as Any) {
                errorJson("email" to "Email address is already registered")
            }
        }
    }
}
