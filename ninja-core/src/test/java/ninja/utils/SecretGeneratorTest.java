/**
 * Copyright (C) 2012-2014 the original author or authors.
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

import java.security.SecureRandom;
import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

public class SecretGeneratorTest {

    @Test
    public void testGenerateSecret() {
        
        assertEquals("5EJYQbXUb81LhuSoNO5l4eh2ZrNPoUBzZaGNixcPOFUsKzRkpTOeu9sm8CGUKaXZ",
                SecretGenerator.generateSecret(new Random(323232L)));

        assertEquals("oC8rHI6rDAiYSgMKHP6b4NlWG8UDdo5ALy66t3h2A5mhwWIBGjdyeFDBCoUn8Cov",
                SecretGenerator.generateSecret(new Random(2L)));
        
        assertEquals("0C27oI94jXZkXyB0ID8ZPq1zinxNmrenSwItFwRXphCKOC6ZwGTFX3nYZsYKafxw",
                SecretGenerator.generateSecret(new Random(3L)));
        
        assertEquals("Ac6TZUupXMSMt96dxCu0x56MvaXdckxIP6FoeBtujabkMVrnIBnDJumIoX5uS9nh", 
                SecretGenerator.generateSecret(new SecureRandom(new byte[] {3, 5, 7, 11, 13})));
        
        assertEquals("gkbYww3X72rEhsffof2nJo00tFrKDk4jFYFzkOydgU0Umw4kdHgtLIuprE0rTjdq", 
                SecretGenerator.generateSecret(new SecureRandom(new byte[] {83, 71, 113, 17, 47})));

    }

}
