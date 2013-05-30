/**
 * Copyright (C) 2013 the original author or authors.
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class Crypto {

    private final String applicationSecret;

    /**
     * Secret is a secret key. Usually something like:
     * "Fxu6U5BTGIJZ06c8bD1xkhHc3Ct5JZXlst8tJ1K5uJJPaLdceDo6CUz0iWpjjQUY".
     * 
     * @param applicationSecret
     *            the secret to use for signing.
     * @param random
     *            the random generator to be used for generating the secret
     *            => usually new Random(), but can also be set to e.g. new Random(1) to
     *            test the function.
     */
    @Inject
    public Crypto(NinjaProperties ninjaProperties) {
        this.applicationSecret = ninjaProperties
                .getOrDie(NinjaConstant.applicationSecret);

    }

    public String signHmacSha1(String message) {

        return signHmacSha1(message, applicationSecret);

    }

    private String signHmacSha1(String value, String key) {
        try {

            // Get an hmac_sha1 key from the raw key bytes
            byte[] keyBytes = key.getBytes();
            SecretKeySpec signingKey = new SecretKeySpec(keyBytes, "HmacSHA1");

            // Get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance("HmacSHA1");
            mac.init(signingKey);

            // Compute the hmac on input data bytes
            byte[] rawHmac = mac.doFinal(value.getBytes());

            // Convert raw bytes to Hex
            byte[] hexBytes = new Hex().encode(rawHmac);

            // Covert array of Hex bytes to a String
            return new String(hexBytes, "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
