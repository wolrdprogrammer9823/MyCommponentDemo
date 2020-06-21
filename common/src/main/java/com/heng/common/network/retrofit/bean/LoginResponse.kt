package com.heng.common.network.retrofit.bean

data class LoginResponse
    (
    val errorCode: Int,
    val errorMessage: String?,
    val data: Data
) {
    data class Data(
        val id :Int,
        val userName :String,
        val password :String,
        val icon : String?,
        val type :String,
        val collectionsId :List<Int>?
    )
}