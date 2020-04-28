package io.zeko.restapi.examples.models.entities

import io.zeko.model.Entity

class Role : Entity {
    constructor(map: Map<String, Any?>) : super(map)
    constructor(vararg props: Pair<String, Any?>) : super(*props)
    val id: Int?     by map
    val roleName: String? by map
    val userId: Int? by map
}
