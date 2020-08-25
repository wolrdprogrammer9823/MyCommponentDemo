package com.heng.common.util
import java.security.MessageDigest
import kotlin.experimental.and

object MD5Util {

    fun getMD5Code(originalCode : String) : String {
        try {
            val md5Digest = MessageDigest.getInstance("MD5")
            md5Digest.update(originalCode.toByteArray(Charsets.UTF_8))
            val encryption : ByteArray = md5Digest.digest()
            val stringBuffer = StringBuffer()
            for (i in encryption.indices) {
                if (Integer.toHexString((encryption[i].and(0xff.toByte())).toInt()).length == 1) {

                    stringBuffer.append("0")
                        .append(Integer.toHexString((encryption[i].and(0xff.toByte())).toInt()))
                } else {

                    stringBuffer.append(Integer.toHexString(encryption[i].and(0xff.toByte()).toInt()))
                }
            }
            return stringBuffer.toString()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }

}