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
import java.security.InvalidKeyException;
import java.util.Objects;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

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

    public static final String ALGORITHM = "AES";

    private static final Logger LOGGER = LoggerFactory.getLogger(CookieEncryption.class);

    private SecretKey secretKey;

    @Inject
    public CookieEncryption(NinjaProperties properties) {

        if (properties.getBooleanWithDefault(NinjaConstant.applicationCookieEncrypted, false)) {

            String secret = properties.getOrDie(NinjaConstant.applicationSecret);
            try {
                int maxKeyLenghtBits = Cipher.getMaxAllowedKeyLength(ALGORITHM);
                if (maxKeyLenghtBits == Integer.MAX_VALUE) {
                    maxKeyLenghtBits = 256;
                }

                this.secretKey = new SecretKeySpec(secret.getBytes(), 0, maxKeyLenghtBits / Byte.SIZE, ALGORITHM);

            } catch (Exception ex) {
                LOGGER.warn("Encryption secret not created. Sessions cookies will not be encrypted.", ex);
            }

        }

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
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encrypted = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));

            // convert encrypted bytes to string in base64
            return Base64.encodeBase64URLSafeString(encrypted);

        } catch (InvalidKeyException ex) {
            LOGGER.error(getHelperLogMessage());
            LOGGER.debug("", ex);
        } catch (GeneralSecurityException ex) {
            LOGGER.error("Failed to encrypt data. {}", ex.getMessage());
            LOGGER.debug("", ex);
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
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decrypted = cipher.doFinal(decoded);

            // convert bytes to string
            return new String(decrypted, StandardCharsets.UTF_8);

        } catch (InvalidKeyException ex) {
            LOGGER.error(getHelperLogMessage());
            LOGGER.debug("", ex);
        } catch (GeneralSecurityException ex) {
            LOGGER.error("Failed to decrypt data. {}", ex.getMessage());
            LOGGER.debug("", ex);
        }
        return "";
    }

    private String getHelperLogMessage() {
        StringBuilder sb = new StringBuilder();
        sb.append("Invalid key provided. Check if application secret is properly set.").append(System.lineSeparator());
        sb.append("You can remove '").append(NinjaConstant.applicationSecret).append("' key in configuration file ");
        sb.append("and restart application. Ninja will generate new key for you.");
        return sb.toString();
    }
}
