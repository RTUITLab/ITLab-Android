package ru.rtuitlab.itlab.presentation.utils

import ru.rtuitlab.itlab.BuildConfig
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.*

object GravatarGetter {
    fun toMd5(text:String):String{
        val email = text.trim().lowercase(Locale.getDefault())
        val md = MessageDigest.getInstance("MD5")
        val hashInBytes = md.digest(email.toByteArray(StandardCharsets.UTF_8))
        val sb = StringBuilder()
        for (b in hashInBytes) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }
    fun requestLinkToGetGravatar(email:String,sizeOfImage:Int):String{
        return BuildConfig.GRAVATAR_URI+toMd5(email)+"?s=$sizeOfImage"
    }
}