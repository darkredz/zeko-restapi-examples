package io.zeko.restapi.example.controller

import io.vertx.core.Vertx
import io.vertx.core.buffer.Buffer
import io.vertx.core.http.HttpHeaders
import io.vertx.core.streams.Pump
import io.vertx.ext.web.RoutingContext
import io.vertx.kotlin.coroutines.await
import io.zeko.restapi.annotation.Params
import io.zeko.restapi.annotation.http.Routing
import io.zeko.restapi.core.controllers.ProjectInitController
import org.slf4j.Logger
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


@Routing("/project")
class ProjectCreatorController(vertx: Vertx, logger: Logger, context: RoutingContext) : ProjectInitController(vertx, logger, context) {

    /**
     * Visit this url to generate and download a new project setup automatically by the framework. JDBC url should be uri encoded
     * http://localhost:9999/project/create?artifact_id=trade-game&group_id=com.mycorp.superapp&version=1.0.0
     * &package_name=com.mycorp.superapp.trade&http_port=8888
     * &jwt_key=YourKey&jwt_refresh_key=YourKey&jwt_expiry=604800&jwt_refresh_expiry=1209600&jwt_refresh_when_expire=false
     * &controllers=user,stock,game_admin,game_manager
     * &db_driver=hikari&jdbc_url=jdbc%3Amysql%3A%2F%2Flocalhost%3A3306%2Fzeko_test%3Fuser%3Droot%26password%3D123456
     */
    @Routing("/create", "route", true, "Create New Zeko Project")
    @Params([
        "artifact_id => required, minLength;3, maxLength;60, alphaNumDash",
        "group_id => required, minLength;3, maxLength;60, [a-zA-Z0-9\\\\_\\\\.]",
        "version => required, minLength;3, maxLength;10, regex;[0-9\\\\.]+",
        "package_name => required, minLength;3, maxLength;60, regex;[a-zA-Z0-9\\\\_\\\\.]+",
        "jwt_key => required",
        "jwt_refresh_key => required",
        "jwt_expiry => required, min;60;",
        "jwt_refresh_expiry => required, min;60;",
        "jwt_refresh_when_expire => required, isBoolean",
        "db_driver => required, inArray;jasync;hikari;vertx",
        "jdbc_url => required, minLength;16, maxLength;160",
        "controllers => required, separateBy",
        "http_port => required, isInteger, min;80, max:20000"
    ])
    suspend fun createNew(ctx: RoutingContext) {
        val files = super.createNew(ctx, true)

        if (files != null) {
            val res = validateInput()
            val fileName = res.values["artifact_id"].toString() + ".zip"
            vertx.executeBlocking<String> {
                val fos = FileOutputStream(fileName)
                val zipOut = ZipOutputStream(fos)
                for (file in files!!) {
                    val zipEntry = ZipEntry(file.name)
                    zipOut.putNextEntry(zipEntry)
                    zipOut.write(Buffer.buffer(file.content).bytes)
                }
                zipOut.close()
                fos.close()
                it.complete(fileName)
            }.await()

            ctx.response()
                .putHeader(HttpHeaders.CONTENT_TYPE, "application/zip, application/octet-stream")
                .putHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"$fileName\"")

            ctx.response().sendFile(fileName) {
                vertx.fileSystem().delete(fileName)
            }
        }
    }
}
