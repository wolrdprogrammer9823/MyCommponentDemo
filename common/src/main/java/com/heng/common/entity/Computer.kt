package com.heng.common.entity

/*电脑*/
class Computer{

    var id: Int = 0
    var type: String? = null
    var price: Double = 0.0

    constructor(id: Int, type: String?, price: Double) {
        this.id = id
        this.type = type
        this.price = price
    }

    constructor()

    override fun toString(): String {
        return "Computer(id=$id, type=$type, price=$price)"
    }
}