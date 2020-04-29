package io.zeko.restapi.examples

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import io.vertx.core.AbstractVerticle
import io.vertx.core.DeploymentOptions
import io.vertx.core.Vertx
import io.vertx.core.VertxOptions
import io.vertx.core.http.HttpServerOptions
import io.vertx.core.json.Json
import io.vertx.core.logging.LoggerFactory
import io.vertx.ext.auth.PubSecKeyOptions
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.auth.jwt.JWTAuthOptions
import io.zeko.restapi.examples.models.repositories.UserRepo
import io.zeko.restapi.examples.models.services.UserService
import io.zeko.restapi.core.mail.MailConfig
import io.zeko.restapi.core.mail.MailService
import io.zeko.restapi.core.mail.MandrillMail
import io.zeko.restapi.core.mail.SendGridMail
import org.koin.core.Koin
import org.koin.dsl.module.module
import org.koin.log.EmptyLogger
import org.koin.standalone.StandAloneContext


class BootstrapVerticle : AbstractVerticle() {

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val vertx = Vertx.vertx(VertxOptions().setHAEnabled(false))
            vertx.deployVerticle(BootstrapVerticle())
        }
    }

    override fun start() {
        val logger = LoggerFactory.getLogger("app")
        logger.info("STARTING APP...")

        Json.mapper.registerModule(JavaTimeModule())
        Json.mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
        Json.mapper.propertyNamingStrategy = PropertyNamingStrategy.SNAKE_CASE

        //set JWT keys for auth
        val jwtAuthKeys = listOf(
                PubSecKeyOptions().setAlgorithm("HS256").setPublicKey("MTI5MDFmtKaWXdpZW5TJadcTIzSkjaRZaQy").setSymmetric(true)
        )
        val jwtOpt = JWTAuthOptions().setPubSecKeys(jwtAuthKeys)
        var jwtAuth = JWTAuth.create(vertx, jwtOpt)

        val jwtRefreshOpt = JWTAuthOptions().setPubSecKeys(listOf(
                PubSecKeyOptions().setAlgorithm("HS256").setPublicKey("aWI5MFjSkMaWRtKddXZJaTcpZayTIzDQ5Tm").setSymmetric(true)
        ))
        var jwtAuthRefresh = JWTAuth.create(vertx, jwtRefreshOpt)

        //Setup mail service with sendgrid
        val webClientSendGrid = SendGridMail.createSharedClient(vertx)
        val sendGridConfig = MailConfig(
                "Your Api Key",
                "noreply@zeko.io", "Zeko",
                true, "dev.mail@gmail.com"
        )

        //send email with circuit breaker
        val mailCircuitBreaker = SendGridMail.createCircuitBreaker(vertx)

        val appModules = listOf(module {
            single { vertx }
            single { logger }
            single { DB(vertx, get()) }
            single("mail") { mailCircuitBreaker }
//            single<MailService> { MandrillMail(webClientMandrill, mandrillConfig, get()) }
            single<MailService> { SendGridMail(webClientSendGrid, sendGridConfig, get()) }
            single("jwtAuth") { jwtAuth }
            single("jwtAuthRefresh") { jwtAuthRefresh }
            factory { UserService(get(), get()) }
            factory { UserRepo(vertx) }
            factory { RestApiVerticle() }
        })

        StandAloneContext.stopKoin()
        StandAloneContext.startKoin(appModules)
        Koin.logger = EmptyLogger()

        vertx.registerVerticleFactory(KoinVerticleFactory)
        vertx.deployVerticle(
                "${KoinVerticleFactory.prefix()}:${RestApiVerticle::class.java.canonicalName}",
                DeploymentOptions().setInstances(1)
        )
    }
}



