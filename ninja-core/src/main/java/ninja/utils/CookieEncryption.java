/*
 * Copyright 2015 ninjaframework.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ninja.utils;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * This class encrypts/decrypts session cookie data. Resultant encrypted strings are encoded in base64, and decryption
 * expects base64 encoded string.
 */
@Singleton
public class CookieEncryption {

    private static final Logger LOGGER = LoggerFactory.getLogger(CookieEncryption.class);

    private String transformation;
    private SecretKey secretKey;

    @Inject
    public CookieEncryption(CookieEncryptionKeyGenerator keyGenerator) {
        this.transformation = keyGenerator.getTransformation();
        this.secretKey = keyGenerator.generateKey();
    }

    /**
     * Encrypts data with secret key.
     *
     * @param data text to encrypt
     * @return encrypted text in base64 format
     */
    public String encrypt(String data) {

        Objects.requireNonNull(data, "Data to be encrypted");

        if (secretKey == null) {
            // silently return back given data
            return data;
        }

        try {
            // encrypt data
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // convert encrypted bytes to string in base64
            return Base64.encodeBase64URLSafeString(encrypted);

        } catch (GeneralSecurityException ex) {
            LOGGER.error("Failed to encrypt data", ex);
        }
        return "";
    }

    /**
     * Decrypts data with secret key.
     *
     * @param data text to decrypt in base64 format
     * @return decrypted text
     */
    public String decrypt(String data) {

        Objects.requireNonNull(data, "Data to be decrypted");

        if (secretKey == null) {
            // silently return back given data
            return data;
        }

        // convert base64 encoded string to bytes
        byte[] decoded = Base64.decodeBase64(data);
        try {
            // decrypt bytes
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(decoded);

            // convert bytes to string
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (GeneralSecurityException ex) {
            LOGGER.error("Failed to decrypt data", ex);
        }
        return "";
    }

}
