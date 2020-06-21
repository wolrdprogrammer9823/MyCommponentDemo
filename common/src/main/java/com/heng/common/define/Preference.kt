package com.heng.common.define
import android.content.Context
import android.content.SharedPreferences
import com.heng.common.CommonConstant
import java.lang.IllegalArgumentException
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class Preference<T>(private val key: String, private val value: T) : ReadWriteProperty<Any?,T>{

    companion object {

        private lateinit var preference: SharedPreferences

        fun setContext(context: Context) {
            preference =
                context.getSharedPreferences(
                    context.packageName + CommonConstant.SHARED_NAME,
                    Context.MODE_PRIVATE
                )
        }

        fun clear() {
            preference.edit().clear().apply()
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = findPreferences(key, value)

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = putPreferences(key,value)

    @Suppress("UNCHECKED_CAST")
    private fun <U> findPreferences(name: String, default: U): U = with(preference) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default)!!
            is Float -> getFloat(name, default)
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            else -> {
                throw IllegalArgumentException("This type can not be save into SharedPreferences")
            }
        }
        res as U
    }

    private fun <U> putPreferences(name: String, value: U) = with(preference.edit()) {
        when (value) {
            is Int -> putInt(name, value)
            is Long -> putLong(name, value)
            is Float -> putFloat(name, value)
            is Boolean -> putBoolean(name, value)
            is String -> putString(name, value)
            else -> {
                throw IllegalArgumentException("This type can not be put into SharedPreferences")
            }
        }.apply()
    }
}