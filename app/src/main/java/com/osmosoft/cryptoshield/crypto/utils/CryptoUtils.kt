package com.osmosoft.cryptoshield.crypto.utils

import java.security.MessageDigest
import kotlin.text.Charsets.UTF_8

//object - is a singleton pattern implemented on language level
 object CryptoUtils {

     //return a digest(checksum) byte array
    private fun md5(str: String): ByteArray = MessageDigest.getInstance("MD5").digest(str.toByteArray(UTF_8))

     //return a hex presentation of byte array
    private fun ByteArray.toHex() = joinToString("") { "%02x".format(it) }

     fun toMD5Hash(msg: String): String{
        return md5(msg).toHex()
    }
}