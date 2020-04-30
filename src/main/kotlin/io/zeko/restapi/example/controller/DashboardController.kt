package io.zeko.restapi.example.controller

import io.vertx.core.Vertx
import io.zeko.restapi.annotation.http.*
import io.zeko.restapi.annotation.Params
import io.zeko.restapi.core.controllers.ApiController
import io.zeko.restapi.core.validations.ValidateResult
import io.vertx.core.logging.Logger
import io.vertx.ext.web.RoutingContext
import io.zeko.restapi.example.model.services.UserService
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

@Routing("/dashboard")
class DashboardController : KoinComponent, ApiController {
    val userService: UserService by inject()

    constructor(vertx: Vertx, logger: Logger, context: RoutingContext) : super(vertx, logger, context)

    override fun validateInput(statusCode: Int): ValidateResult {
        return super.validateInput(422)
    }

    //specify schema reference, defined in swagger.cmpSchemaDir folder, api-schemas/ in this example project
    @GetSuspend("/list-active-users","List all users with active status", "users")
    @Params(["page => isInteger, min;1, max;99"])
    suspend fun listActiveUsers(ctx: RoutingContext) {
        val res = validateInput()
        //only one optional value, ignore if no errors
        if (!res.success) return

        val page = if (res.values.containsKey("page")) res.values["page"].toString().toInt() else 1
        val users = userService.listActive(page)
        endJson(users)
    }

}
