/**
 * Copyright (C) 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.utils;

import java.security.NoSuchAlgorithmException;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.commons.codec.binary.Base64;


public class SecretGenerator {

    public static final String ALGORITHM = "AES";

    /**
     * Generates secret key encoded in base64. This string is suitable as secret for your application (key
     * "application.secret" in conf/application.conf).
     *
     * @return A string that can be used as "application.secret".
     */
    public static String generateSecret() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(128);
            SecretKey key = keyGenerator.generateKey();

            return Base64.encodeBase64String(key.getEncoded());

        } catch (NoSuchAlgorithmException ex) {
            throw new RuntimeException("Failed to generate application secret", ex);
        }
    }    

}
