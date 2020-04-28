package io.zeko.restapi.examples

import io.vertx.core.Vertx
import io.vertx.core.logging.Logger
import io.vertx.kotlin.core.json.json
import io.vertx.kotlin.core.json.obj
import io.zeko.db.sql.connections.*


class DB {
    private var vertx: Vertx
    private var connPool: JasyncDBPool
//    private var connPool2: HikariDBPool
//    private var connPool3: VertxDBPool
    private var dbLogger: DBLogger

    constructor(vertx: Vertx, logger: Logger) {
        this.vertx = vertx

        val mysqlConfig = json {
            obj(
                    "url" to "jdbc:mysql://localhost:3306/zeko_test?user=root&password=123456",
                    "max_pool_size" to 30
            )
        }

        dbLogger = AppDBLog(logger).setParamsLogLevel(DBLogLevel.ALL)
        connPool = JasyncDBPool(mysqlConfig)
//        connPool2 = HikariDBPool(mysqlConfig)
//        connPool3 = VertxDBPool(vertx, mysqlConfig)
    }

    suspend fun session(): DBSession = JasyncDBSession(connPool, connPool.createConnection()).setQueryLogger(dbLogger)
//    suspend fun session2(): DBSession = HikariDBSession(connPool2, connPool2.createConnection()).setQueryLogger(dbLogger)
//    suspend fun session3(): DBSession = VertxDBSession(connPool3, connPool3.createConnection()).setQueryLogger(dbLogger)
}
