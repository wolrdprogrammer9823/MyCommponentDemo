package com.component.datastore.dao
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.component.datastore.entity.User

@Dao
interface UserDao {

    @Insert
    fun insertUser(user : User)

    @Query("select * from user where username = :username and password = :password")
    fun queryUser(username : String, password : String) : Boolean

    @Query("select * from user where username = :username")
    fun queryUsername(username: String) : Boolean
}