package io.zeko.restapi.example.model.entities

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import io.zeko.model.Entity

@JsonPropertyOrder("email", "user_id", "subscribed")
class AccountEmail : Entity {
    constructor(map: Map<String, Any?>) : super(map)
    constructor(vararg props: Pair<String, Any?>) : super(*props)
    var email: String? by map
    @get:JsonProperty("user_id")
    var userId: String?     by map
    var subscribed: Boolean? by map
}
