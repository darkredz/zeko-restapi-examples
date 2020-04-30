package io.zeko.restapi.example.job

import io.vertx.core.Vertx
import io.vertx.core.json.Json
import io.vertx.core.logging.Logger
import io.zeko.restapi.example.model.entities.User
import io.zeko.restapi.example.model.services.UserService
import io.zeko.restapi.annotation.cron.Cron
import io.zeko.restapi.annotation.cron.CronSuspend
import io.zeko.restapi.core.cron.CronJob
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class UserCronJob(vertx: Vertx, logger: Logger) : CronJob(vertx, logger), KoinComponent {

    val userService: UserService by inject()

    @CronSuspend("*/2 * * * *")
    suspend fun showUser() {
        val user = userService.getProfileStatus(1)
        logger.info("Cron showUser " + Json.encode(user))
    }

    @Cron("*/1 * * * *")
    fun showUserNormal() {
        val uid = 1
        val user = User().apply {
            id = uid
            firstName = "Leng"
            lastName = "Mango"
        }
        logger.info("Cron showUserNormal " + Json.encode(user))
    }

}
