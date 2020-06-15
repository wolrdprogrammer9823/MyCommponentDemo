package com.heng.common.tool
import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.facade.service.SerializationService
import com.google.gson.Gson
import java.lang.reflect.Type

@Route(path = "/service/json")
class JsonServiceImpl : SerializationService {

    private var mGoon : Gson? = null

    override fun init(context: Context?) {
        mGoon = Gson()
    }

    override fun <T : Any?> json2Object(input: String?, clazz: Class<T>?): T {
        return mGoon?.fromJson(input, clazz)!!
    }

    override fun object2Json(instance: Any?): String {
        return mGoon?.toJson(instance)!!
    }

    override fun <T : Any?> parseObject(input: String?, clazz: Type?): T {
        return mGoon?.fromJson(input, clazz)!!
    }
}