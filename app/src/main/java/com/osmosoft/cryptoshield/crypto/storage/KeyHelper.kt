package com.osmosoft.cryptoshield.crypto.storage

import android.content.Context
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyProperties
import android.util.Log
import java.math.BigInteger
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.util.*
import javax.security.auth.x500.X500Principal

private const val PROVIDER_ANDROID_KEY_STORE = "AndroidKeyStore"
private const val KEY_ALIAS = "crypto_shield_key_store"

private const val TAG = "KEY_HELPER_TAG"

//class for storage/get key in AndroidKeyStore
//object for use singleton
object KeyHelper {

    private lateinit var keyStore: KeyStore

    //init instance
    fun init(context: Context){
        keyStore = KeyStore.getInstance(PROVIDER_ANDROID_KEY_STORE)
        keyStore.load(null)

        Log.d(TAG, "init")

        //if keyPair not generated earlier
        if(!keyStore.containsAlias(KEY_ALIAS)){
            Log.d(TAG, "create alias")

            val start: Calendar = Calendar.getInstance()
            val end: Calendar = Calendar.getInstance()
            //key is will be valid during 30 years
            end.add(Calendar.YEAR,30)

            //I know is deprecated, but I need support 18 api level
            val keyPairGeneratorSpec: KeyPairGeneratorSpec =
                    KeyPairGeneratorSpec.Builder(context)
                            .setAlias(KEY_ALIAS)
                            .setSubject(X500Principal("CN=$KEY_ALIAS"))
                            .setSerialNumber(BigInteger.TEN)
                            .setStartDate(start.time)
                            .setEndDate(end.time)
                            .build()

            val keyPairGenerator: KeyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA,
                    PROVIDER_ANDROID_KEY_STORE
            )

            keyPairGenerator.initialize(keyPairGeneratorSpec)
            keyPairGenerator.generateKeyPair()

            Log.d(TAG, "keypair generated")
        }
    }

//    private fun RSAEncrypt(msg: ByteArray): ByteArray{
//
//    }

}