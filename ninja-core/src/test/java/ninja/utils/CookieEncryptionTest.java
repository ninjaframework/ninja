/**
 * Copyright (C) the original author or authors.
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

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CookieEncryptionTest {

    @Mock
    NinjaProperties ninjaProperties;
    
    @Test
    public void testThatEncryptionAndDecryptionWorksWhenEnabled() {
        String applicationSecret = SecretGenerator.generateSecret();
        when(ninjaProperties.getOrDie(NinjaConstant.applicationSecret))
               .thenReturn(applicationSecret);
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.applicationCookieEncrypted, false))
               .thenReturn(true);
        CookieEncryption cookieEncryption = new CookieEncryption(ninjaProperties);
        
        String stringToEncrypt = "a_very_big_secret";
        String encrypted = cookieEncryption.encrypt(stringToEncrypt);
        assertThat(encrypted, not(equalTo(stringToEncrypt)));
        
        String decrypted = cookieEncryption.decrypt(encrypted);
        assertThat(decrypted, equalTo(stringToEncrypt));
    }
    
    @Test
    public void testThatEncryptionDoesNotDoAnythingWhenDisabled() {
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.applicationCookieEncrypted, false))
               .thenReturn(false);

        CookieEncryption cookieEncryption = new CookieEncryption(ninjaProperties);
        
        String stringToEncrypt = "a_very_big_secret";
        String encrypted = cookieEncryption.encrypt(stringToEncrypt);
        assertThat(encrypted, equalTo(stringToEncrypt));
        
        String decrypted = cookieEncryption.decrypt(encrypted);
        assertThat(decrypted, equalTo(stringToEncrypt));
    }

    @Test(expected = RuntimeException.class)
    public void testThatEncryptionFailsWhenSecretEmpty() {
        String applicationSecret = "";
        when(ninjaProperties.getOrDie(NinjaConstant.applicationSecret))
               .thenReturn(applicationSecret);
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.applicationCookieEncrypted, false))
               .thenReturn(true);
        new CookieEncryption(ninjaProperties);
    }
    
    @Test(expected = RuntimeException.class)
    public void testThatEncryptionFailsWhenSecretTooSmall() {
        String applicationSecret = "1234";
        when(ninjaProperties.getOrDie(NinjaConstant.applicationSecret))
               .thenReturn(applicationSecret);
        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.applicationCookieEncrypted, false))
               .thenReturn(true);
        new CookieEncryption(ninjaProperties);
    }
    
    
    
}
