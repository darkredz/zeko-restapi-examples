package io.zeko.restapi.examples.models.entities

import io.zeko.model.Entity

class Address : Entity {
    constructor(map: Map<String, Any?>) : super(map)
    constructor(vararg props: Pair<String, Any?>) : super(*props)
    var id: Int?     by map
    var userId: Int? by map
    var street1: String? by map
    var street2: String? by map
}
