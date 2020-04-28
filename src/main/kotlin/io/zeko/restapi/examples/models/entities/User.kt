package io.zeko.restapi.examples.models.entities

import com.fasterxml.jackson.annotation.*
import io.zeko.model.Entity
import io.zeko.model.Type
import java.time.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder("id", "first_name", "last_name", "email", "status", "subscribed", "last_access_at", "created_at")
class User : Entity {
    constructor(map: Map<String, Any?>) : super(map)
    constructor(vararg props: Pair<String, Any?>) : super(*props)

    override fun propTypeMapping() = mapOf(
        "status" to Type.INT,
        "notified" to Type.BOOL,
        "lastAccessAt" to Type.ZONEDATETIME_UTC,
        "createdAt" to Type.ZONEDATETIME_UTC,
        "dob" to Type.DATE
    )

    var id: Int? by map
    var firstName: String? by map
    var lastName: String? by map
    var email: String? by map
    var notified: Boolean? by map
    var status: Int? by map

    var dob: LocalDate? by map
    var lastAccessAt: ZonedDateTime? by map
    var createdAt: ZonedDateTime? by map

    @get:JsonProperty("roles")
    var role: List<Role>? by map
    @get:JsonProperty("addresses")
    var address: List<Address>? by map

    @get:JsonIgnore
    var password: String? by map
}
