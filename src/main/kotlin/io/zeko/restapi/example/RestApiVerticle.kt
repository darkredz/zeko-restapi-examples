package io.zeko.restapi.example

import org.slf4j.Logger
import io.vertx.ext.auth.jwt.JWTAuth
import io.vertx.ext.web.Router
import io.vertx.ext.web.handler.BodyHandler
import io.zeko.restapi.core.TimeoutHandler
import io.zeko.restapi.core.security.JWTAuthHandler
import io.zeko.restapi.core.security.JWTAuthRefreshHandler
import io.zeko.restapi.core.verticles.ZekoVerticle
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject

class RestApiVerticle : ZekoVerticle(), KoinComponent {
    val jwtAuth: JWTAuth by inject("jwtAuth")
    val jwtAuthRefresh: JWTAuth by inject("jwtAuthRefresh")
    val logger: Logger by inject()

    val skipAuth = listOf("/user/login", "/user/register", "/user/refresh-token", "/ping-health", "/project/create")

    override suspend fun start() {
        val router = Router.router(vertx)
        router.route().handler(BodyHandler.create());
        router.route().handler(TimeoutHandler(10000L, 503))

        router.route("/*").handler(JWTAuthHandler(jwtAuth, skipAuth))

        router.post("/user/refresh-token").handler(JWTAuthRefreshHandler(jwtAuth, jwtAuthRefresh))
        //auth access token 60s, refresh token 300s, only allow refresh after token expired
//        router.post("/user/refresh-token").handler(JWTAuthRefreshHandler(jwtAuth, jwtAuthRefresh, 60, 300, true))

        //Make some error
        router.get("/make-error").koto { ctx -> val s = 12 / 0; ctx.response().end("ok") }

        bindRoutes("io.zeko.restapi.example.controller.GeneratedRoutes", router, logger, true)
//        bindRoutes(io.zeko.restapi.example.controller.GeneratedRoutes(vertx), router, logger)
        withAccessLog(router, logger)

        //handles error, output default status code, default message, log error
        handleRuntimeError(router, logger)

        //start running cron jobs
//        startCronJobs("io.zeko.restapi.example.job.GeneratedCrons", logger)
//        startCronJobs(io.zeko.restapi.example.job.GeneratedCrons(vertx, logger), logger)

        vertx.createHttpServer()
                .requestHandler(router)
                .listen(config.getInteger("http_port", 9999))
    }
}
