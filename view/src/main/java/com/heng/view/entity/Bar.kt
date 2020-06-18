package com.heng.view.entity

class Bar {

    //绘制柱状条的四个位置
    var left = 0
    var top = 0
    var right = 0
    var bottom = 0
    //柱状条原始数据的大小
    var value = 0f
    //柱状条原始数据大小转换成对应的像素大小
    var transformedValue = 0f
    //柱状图动画中用到，表示柱状条动画过程中顶部位置的变量，取值范围为[0,top]
    var currentTop = 0

    fun isInside(x: Float, y: Float): Boolean {
        return x > left && x < right && y > top && y < bottom
    }
}