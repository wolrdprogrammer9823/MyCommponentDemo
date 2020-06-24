package com.heng.common.network.retrofit.bean

data class LoginResponse
    (
    val errorCode: Int,
    val errorMessage: String?,
    val data: Data
) {
    data class Data(
        val id : Int,
        val admin : Boolean,
        val username : String,
        val password : String,
        val icon : String?,
        val nickname : String,
        val publicName : String,
        val email : String?,
        val token : String?,
        val type : Int,
        val chapterTops : Array<Any>?,
        val collectIds : Array<Any>?
    )
}