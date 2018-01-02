/**
 * Copyright (C) 2012- the original author or authors.
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class TimeUtilTest {

    @Test
    public void test() {
        assertEquals(86400, TimeUtil.parseDuration("1d"));
        assertEquals(10, TimeUtil.parseDuration("10s"));
        assertEquals(2592000, TimeUtil.parseDuration("30d"));
        assertEquals(2592000, TimeUtil.parseDuration(null));
        
        boolean catchedException = false;
        try {
            TimeUtil.parseDuration("NOT_A_VALID_INPUT");
            
        } catch (IllegalArgumentException e) {
                catchedException = true;
        }

        assertTrue(catchedException); 
        
    }

}
