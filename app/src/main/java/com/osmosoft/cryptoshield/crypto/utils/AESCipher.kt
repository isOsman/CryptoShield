package com.osmosoft.cryptoshield.crypto.utils

import android.util.Base64
import com.osmosoft.cryptoshield.crypto.utils.CryptoUtils.toHex
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

//AES encryption
const val CIPHER_NAME_AES = "AES"
object AESCipher {

    //return encrypted message with specific key
    fun encrypt(msg: String, key: String): String{
        val cipher = Cipher.getInstance(CIPHER_NAME_AES)
        val keySpec = SecretKeySpec(key.toByteArray(), CIPHER_NAME_AES)
        cipher.init(Cipher.ENCRYPT_MODE,keySpec)
        val encrypt = cipher.doFinal(msg.toByteArray())

        return Base64.encodeToString(encrypt,Base64.DEFAULT)
    }

    //return decrypted message with specific key
    fun decrypt(msg: String, key: String): String{
        val cipher = Cipher.getInstance(CIPHER_NAME_AES)
        val keySpec = SecretKeySpec(key.toByteArray(), CIPHER_NAME_AES)
        cipher.init(Cipher.DECRYPT_MODE,keySpec)
        val decrypt = cipher.doFinal(Base64.decode(msg,Base64.DEFAULT))

        return String(decrypt)
    }
}