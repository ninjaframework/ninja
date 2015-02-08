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

import javax.crypto.Cipher;
import javax.crypto.SecretKey;

import com.google.inject.AbstractModule;
import com.google.inject.ImplementedBy;

/**
 * Secret key generator for cookie encryption. {@link CookieEncryptionKeyGeneratorImpl Default implementation} of this
 * interface generates a DES key. To use other key algorithms, implement this interface accordingly and
 * {@link AbstractModule#bind(java.lang.Class) bind} this interface class to your implementation.
 *
 */
@ImplementedBy(CookieEncryptionKeyGeneratorImpl.class)
public interface CookieEncryptionKeyGenerator {

    /**
     * Generates a secret key to encrypt/decrypt session cookies.
     *
     * @return the secret key for cookie encryption/decryption; {@code null} if generation of key fails
     */
    SecretKey generateKey();

    /**
     * Gets the name of the transformation to be used while creating {@link Cipher} instance. Transformation shall be
     * compatible with the key generated in {@link CookieEncryptionKeyGenerator#generateKey() } method.
     *
     * @return the name of the transformation name
     */
    String getTransformation();
}
