package com.osmosoft.cryptoshield.crypto.storage;

import android.os.Build;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;

import androidx.annotation.RequiresApi;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.KeyGenerator;

public class JKeyStore {

    private final String alias = "alias";

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void init() throws NoSuchProviderException, NoSuchAlgorithmException {
        final KeyGenerator keyGenerator = KeyGenerator
                .getInstance(KeyProperties.KEY_ALGORITHM_AES,
                "AndroidKeyStore");

        final KeyGenParameterSpec keyGenParameterSpec =
                new KeyGenParameterSpec.Builder(alias,
                        KeyProperties.PURPOSE_ENCRYPT |
                        KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .build();
    }
}
