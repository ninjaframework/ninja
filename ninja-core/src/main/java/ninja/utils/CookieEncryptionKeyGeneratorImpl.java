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

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * Default implementation of {@link CookieEncryptionKeyGenerator}. This class generates a secret key to be used with DES
 * algorithm. The reason is DES algorithm is one of the standard algorithms that every implementation of the Java
 * platform is required to support. Refer to {@link SecretKeyFactory} for more info.
 *
 */
public class CookieEncryptionKeyGeneratorImpl implements CookieEncryptionKeyGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(CookieEncryptionKeyGeneratorImpl.class);

    private String secret;

    @Inject
    public CookieEncryptionKeyGeneratorImpl(NinjaProperties properties) {
        this.secret = properties.get(NinjaConstant.applicationCookieSecret);
    }

    @Override
    public SecretKey generateKey() {
        if (secret == null || secret.isEmpty()) {
            LOGGER.info("No secret specified for session encryption.");
            return null;
        }

        try {

            DESKeySpec dks = new DESKeySpec(secret.getBytes());
            if (DESKeySpec.isWeak(secret.getBytes(), 0)) {
                LOGGER.warn("Specified secret for session encryption is weak!");
            }

            SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
            return skf.generateSecret(dks);

        } catch (InvalidKeyException | NoSuchAlgorithmException | InvalidKeySpecException ex) {
            LOGGER.error("Failed to initialize secret key", ex);
            return null;
        }
    }

    @Override
    public String getTransformation() {
        return "DES";
    }

}
