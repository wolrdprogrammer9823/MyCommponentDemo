package com.heng.common.define

/*自定义二维数组*/
class Array2D<T>(val xSize: Int, val ySize: Int, var array: Array<Array<T>>) {

    companion object {

        inline operator fun <reified T> invoke()
                = Array2D(0,0, Array(0) { arrayOf<T>() })

        inline operator fun <reified T> invoke(xWidth:Int,yWidth:Int)
                = Array2D(xWidth,yWidth, Array(xWidth){ arrayOfNulls<T>(yWidth) })

        inline operator fun <reified T> invoke(xWidth: Int, yWidth: Int, operator : (Int,Int) -> T) : Array2D<T> {
            val array = Array(xWidth) { _ -> Array(yWidth) {operator(it,it)} }
            return Array2D(xWidth,yWidth,array)
        }
    }

    operator fun get(x: Int, y: Int) : T {
        return array[x][y]
    }

    operator fun set(x: Int, y: Int, t : T) {
        array[x][y] = t
    }

    inline fun forEach(operation: (T) -> Unit) {
        array.forEach { it -> it.forEach { operation.invoke(it) } }
    }

    inline fun forEachIndexed(operation: (x: Int, y: Int, T) -> Unit) {
        array.forEachIndexed { x, p -> p.forEachIndexed { y, t -> operation.invoke(x, y, t) } }
    }
}