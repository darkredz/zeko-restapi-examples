package io.zeko.restapi.examples

import io.vertx.core.logging.Logger
import io.zeko.db.sql.connections.DBLogLevel
import io.zeko.db.sql.connections.DBLogger


class AppDBLog(val logger: Logger): DBLogger {
    var paramsLevel: DBLogLevel = DBLogLevel.DEBUG
    var sqlLevel: DBLogLevel = DBLogLevel.DEBUG

    override fun logQuery(sql: String, params: List<Any?>?) {
        if (sqlLevel.level >= DBLogLevel.DEBUG.level) {
            logger.debug("[SQL] $sql")
        }
        if (paramsLevel.level >= DBLogLevel.DEBUG.level && params != null) {
            logger.debug("[SQL_PARAM] $params")
        }
    }

    override fun logRetry(numRetriesLeft: Int, err: Exception) {
        logger.warn("[SQL_RETRY:$numRetriesLeft] $err")
    }

    override fun logUnsupportedSql(err: Exception) {
        logger.warn("[SQL_UNSUPPORTED] $err")
    }

    override fun logError(err: Exception) {
        logger.error("[SQL_ERROR] $err")
    }

    override fun getParamsLogLevel(): DBLogLevel {
        return paramsLevel
    }

    override fun getSqlLogLevel(): DBLogLevel {
        return sqlLevel
    }

    override fun setParamsLogLevel(level: DBLogLevel): DBLogger {
        this.paramsLevel = level
        return this
    }

    override fun setSqlLogLevel(level: DBLogLevel): DBLogger {
        this.sqlLevel = level
        return this
    }

    override fun setLogLevels(sqlLevel: DBLogLevel, paramsLevel: DBLogLevel): DBLogger {
        this.sqlLevel = sqlLevel
        this.paramsLevel = paramsLevel
        return this
    }
}
