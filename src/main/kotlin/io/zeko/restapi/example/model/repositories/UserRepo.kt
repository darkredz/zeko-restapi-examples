package io.zeko.restapi.example.model.repositories

import io.vertx.core.Vertx
import io.vertx.core.logging.Logger
import io.zeko.db.sql.Insert
import io.zeko.db.sql.Query
import io.zeko.db.sql.Update
import io.zeko.db.sql.dsl.and
import io.zeko.db.sql.dsl.eq
import io.zeko.db.sql.dsl.greaterEq
import io.zeko.restapi.example.DB
import io.zeko.restapi.example.model.entities.User
import io.zeko.model.DataMapper
import io.zeko.model.MapperConfig
import io.zeko.model.TableInfo
import io.zeko.restapi.example.model.entities.Address
import io.zeko.restapi.example.model.entities.Role
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import java.time.LocalDate
import java.time.ZonedDateTime
import kotlin.collections.LinkedHashMap


class UserRepo(val vertx: Vertx) : KoinComponent {
    val logger: Logger by inject()
    val db: DB by inject()

    suspend fun getByEmail(email: String): User? {
        var user: User? = null
        db.session().once { sess ->
            val sql = Query().fields("id", "first_name", "last_name", "password", "email", "status", "dob", "last_access_at", "created_at")
                    .from("user")
                    .where("email" eq email)
                    .limit(1).toSql()
            val rows = sess.queryPrepared(sql, listOf(email), { User(it) }) as List<User>
            if (rows.isNotEmpty()) user = rows[0]
        }
        return user
    }

    suspend fun updateLastAccess(user: User) {
        db.session().once { sess ->
            val userUpdate = User().apply { lastAccessAt = ZonedDateTime.now() }
            val sql = Update(userUpdate, true).where("id" eq user.id!!).toSql()
            val newUid = sess.update(sql, listOf(userUpdate.lastAccessAt))
            logger.info("New User $newUid")
        }
    }

    suspend fun create(firstName: String, lastName: String, email: String, dob: LocalDate, country: String, hashPwd: String): Int {
        val user = User().apply {
            this.lastName = lastName
            this.firstName = firstName
            this.email = email
            this.dob = dob
            status = 1
            password = hashPwd
            createdAt = ZonedDateTime.now()
            lastAccessAt = ZonedDateTime.now()
        }

        val insertUser = Insert(user, true).toSql()
        var userId = 0

        db.session().transaction {
            val res = it.insert(insertUser, user.dataMap().values.toList()) as List<Int>
            //do more queries bla to test transaction rollbacks
            userId = res[0]
        }
        return userId
    }

    suspend fun getActiveUser(id: Int): User? {
        var user: User? = null
        db.session().once { sess ->
            val sql = Query().fields("id", "first_name", "last_name", "email",
                    "notified", "status", "dob", "last_access_at", "created_at")
                            .from("user")
                            .where(("id" eq id) and ("status" eq 1))
                            .limit(1).toSql()
            val rows = sess.query(sql, { User(it) }) as List<User>
            if (rows.isNotEmpty()) user = rows[0]
        }
        return user
    }

    private fun relateRoleAddressWithMap(): LinkedHashMap<String, TableInfo> {
        val tables = linkedMapOf<String, TableInfo>()
        tables["user"] = TableInfo(key = "id", dataClassHandler = { User(it) })
        tables["role"] = TableInfo(key = "id", dataClassHandler = { Role(it) }, move_under = "user", foreign_key = "user_id", many_to_many = true, remove = listOf("user_id"))
        tables["address"] = TableInfo(key = "id", dataClassHandler = { Address(it) }, move_under = "user", foreign_key = "user_id", many_to_one = true, remove = listOf("user_id"))
        return tables
    }

    private fun relateRoleAddress(): MapperConfig {
        val tables = MapperConfig("id", true)
                .table("user").mapTo { User(it) }
                .table("role").mapTo { Role(it) }
                    .manyToMany(true).moveUnder("user").foreignKey("user_id")
                .table("address").mapTo { Address(it) }
                    .manyToOne(true).moveUnder("user").foreignKey("user_id")
        return tables
    }

    suspend fun listActiveWithRoleAddress(page: Int, pageSize: Int = 2): List<User> {
        var query = Query()
                .table("user").fields("id", "first_name", "last_name", "dob")
                .table("role").fields("id", "role_name", "user.id = user_id")
                .table("address").fields("id", "street1", "street2", "user.id = user_id")
                .from(
                        Query().fields("*").from("user")
                                .where("user.status" greaterEq 0)
                                .orderDesc("id")
                                .limit(pageSize, pageSize * (page - 1))
                                .asTable("user")
                )
                .leftJoin("address").on("user_id = user.id")
                .leftJoin("user_has_role").on("user_id = user.id")
                .leftJoin("role").on("id = user_has_role.role_id")
                .orderDesc("user.id")

        val (sql, columns) = query.compile()
        lateinit var rows: List<User>

        db.session().once {
            val result = it.query(sql, columns)
            rows = DataMapper().mapStruct(relateRoleAddress(), result) as List<User>
        }
        return rows
    }

}
