package com.heng.common.test
import com.heng.common.define.Array2D

fun main() {

    //testArray2D()

    val data = intArrayOf(1, 2, 3, 4, 5)
    for (i in 0.until(data.size - 1)) {
        println("i->$i")
    }
}

fun testArray2D() {

    val data = Array2D<Int>(2,2)
    data[0,0] = 1
    data[0,1] = 2
    data[1,0] = 3
    data[1,1] = 4
    data.forEach {
        println(it)
    }

    println("xSize:${data.xSize},ySize:${data.ySize}")
    println("value:${data[0, 1]}")
}