package com.osmosoft.cryptoshield.crypto.storage

import android.content.Context
import android.content.SharedPreferences
import android.security.KeyPairGeneratorSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.math.BigInteger
import java.security.Key
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.SecureRandom
import java.util.*
import javax.crypto.Cipher
import javax.crypto.CipherInputStream
import javax.crypto.CipherOutputStream
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal

private const val APP_SHARED_PREF = "app_shared_pref"
private const val PROVIDER_ANDROID_KEY_STORE = "AndroidKeyStore"
private const val KEY_ALIAS = "crypto_shield_key_store"
private const val RSA_MODE = "RSA/ECB/PKCS1Padding"
private const val PROVIDER_ANDROID_OPEN_SSL = "AndroidOpenSSL"
private const val ENCRYPTED_KEY = "encrypted_key"
private const val PUBLIC_IV = "public_init_vector"
private const val AES_MODE = "AES/GCM/NoPadding"

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

        generateAESKey(context)
        generateRandomVI(context)

    }

    
    //return RSA encrypted message 
    private fun RSAEncrypt(msg: ByteArray): ByteArray{
        Log.d(TAG, "RSAEncrypt: start")
        //get key entry from keyStore
        val privateKeyEntry: KeyStore.PrivateKeyEntry =
                keyStore.getEntry(KEY_ALIAS,null) as KeyStore.PrivateKeyEntry

        Log.d(TAG, "RSAEncrypt: received public key from keystore")
        Log.d(TAG, "RSAEncrypt: publicKey: ${privateKeyEntry.certificate.publicKey}")
        //encrypt the message(msg)
        val inputCipher: Cipher = Cipher.getInstance(RSA_MODE, PROVIDER_ANDROID_OPEN_SSL)
        inputCipher.init(Cipher.ENCRYPT_MODE,privateKeyEntry.certificate.publicKey)
        
        val outputStream: ByteArrayOutputStream = ByteArrayOutputStream()
        val cipherOutputStream: CipherOutputStream = CipherOutputStream(outputStream,inputCipher)
        cipherOutputStream.write(msg)
        cipherOutputStream.close()

        Log.d(TAG, "RSAEncrypt: message encrypted")
        return outputStream.toByteArray()
    }

    //return decrypted RSA-message
    private fun RSADecrypt(msg: ByteArray): ByteArray{
        Log.d(TAG, "RSADecrypt: start")
        //get key entry from keyStore
        val privateKeyEntry: KeyStore.PrivateKeyEntry =
                keyStore.getEntry(KEY_ALIAS,null) as KeyStore.PrivateKeyEntry

        Log.d(TAG, "RSADecrypt: received private key from keystore")
        Log.d(TAG, "RSADecrypt: privateKey: ${privateKeyEntry.privateKey}")
        val outputCipher: Cipher = Cipher.getInstance(RSA_MODE, PROVIDER_ANDROID_OPEN_SSL)
        outputCipher.init(Cipher.DECRYPT_MODE,privateKeyEntry.privateKey)
        val cipherInputStream: CipherInputStream = CipherInputStream(
                ByteArrayInputStream(msg),
                outputCipher
        )

        Log.d(TAG, "RSADecrypt: message decrypted")
        return cipherInputStream.readBytes()

    }


    //generate and save key to the shared pref
    private fun generateAESKey(context: Context){
        Log.d(TAG, "generateAESKey")
        val pref: SharedPreferences = getAppSharedPrefs(context)
        var encryptedKeyBase64 = pref.getString(ENCRYPTED_KEY,null)
        //if AESKey generated first time
        if(encryptedKeyBase64 == null){
            val key: ByteArray = ByteArray(16)
            val secureRandom: SecureRandom = SecureRandom()
            secureRandom.nextBytes(key)
            val encryptedKey = RSAEncrypt(key)
            encryptedKeyBase64 = Base64.encodeToString(encryptedKey,Base64.DEFAULT)

            //save key to shared_pref
            pref.edit()
                .putString(ENCRYPTED_KEY,encryptedKeyBase64)
                .apply()
        }
    }


    //return SecretKey
    private fun getSecretKey(context: Context) : Key{
        val pref: SharedPreferences = getAppSharedPrefs(context)
        val encryptedKeyBase64 = pref.getString(ENCRYPTED_KEY,null)
        val encryptedKey: ByteArray = Base64.decode(encryptedKeyBase64,Base64.DEFAULT)
        val key: ByteArray = RSADecrypt(encryptedKey)

        return SecretKeySpec(key,"AES")

    }

    //return AES encrypted message
    fun encrypt(context: Context, msg: String): String{
        val pref: SharedPreferences = getAppSharedPrefs(context)
        val publicIV: String = pref.getString(PUBLIC_IV,null)!!
        val cipher: Cipher = Cipher.getInstance(AES_MODE)
        //init cipher
        cipher.init(
                Cipher.ENCRYPT_MODE,
                getSecretKey(context),
                IvParameterSpec(Base64.decode(publicIV,Base64.DEFAULT))
        )


        val encodedBytes: ByteArray = cipher.doFinal(msg.toByteArray())

        return Base64.encodeToString(encodedBytes,Base64.DEFAULT)

    }


    //return decrypted AES-message
    fun decrypt(context: Context, msg: String): String{
        val pref: SharedPreferences = getAppSharedPrefs(context)
        val publicIV: String = pref.getString(PUBLIC_IV,null)!!
        val cipher: Cipher = Cipher.getInstance(AES_MODE)
        //init cipher
        cipher.init(
                Cipher.DECRYPT_MODE,
                getSecretKey(context),
                IvParameterSpec(Base64.decode(publicIV,Base64.DEFAULT))
        )

        //decode message using Base64
        val decodedValue: ByteArray = Base64.decode(
                msg.toByteArray(),
                Base64.DEFAULT

        )

        //finally decrypt decoded message
        val decryptedValue: ByteArray = cipher.doFinal(decodedValue)

        return String(decryptedValue)

    }

    //generate and save random init vector for AES-cipher
    fun generateRandomVI(context: Context){
        Log.d(TAG, "generateRandomVI")
        val pref: SharedPreferences = getAppSharedPrefs(context)
        val publicIV: String? = pref.getString(PUBLIC_IV,null)
        //if publicIV generate first time
        if (publicIV == null){
            //generate and save
            val random: SecureRandom = SecureRandom()
            val generated: ByteArray = random.generateSeed(12)
            val generatedIVStr: String = Base64.encodeToString(generated,Base64.DEFAULT)
            pref.edit()
                    .putString(PUBLIC_IV,generatedIVStr)
                    .apply()
        }
    }

    //return local(app) shared preferences
    fun getAppSharedPrefs(context: Context): SharedPreferences{
        return context.getSharedPreferences(
                APP_SHARED_PREF,
                Context.MODE_PRIVATE
        )
    }

    
    

}