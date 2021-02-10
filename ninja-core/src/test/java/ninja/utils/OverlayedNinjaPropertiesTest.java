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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import org.junit.Test;
import static org.junit.Assert.*;

public class OverlayedNinjaPropertiesTest {

    @Test
    public void precedence() {
        NinjaProperties ninjaPropertiesForOverlay = new NinjaPropertiesImpl.Builder()
                .withMode(NinjaMode.prod)
                .withExternalConfiguration("conf/overlayed.empty.conf")
                .build();
        OverlayedNinjaProperties ninjaProperties 
                = new OverlayedNinjaProperties(ninjaPropertiesForOverlay);
        // no value anywhere
        assertThat(ninjaProperties.get("key1", null, null), is(nullValue()));
        assertThat(ninjaProperties.getInteger("key2", null, null), is(nullValue()));
        assertThat(ninjaProperties.getBoolean("key3", null, null), is(nullValue()));
        
        
        // defaultValue > nothing
        assertThat(ninjaProperties.get("key1", null, "default"), is("default"));
        assertThat(ninjaProperties.getInteger("key2", null, 0), is(0));
        assertThat(ninjaProperties.getBoolean("key3", null, true), is(Boolean.TRUE));
        
        
        // configProperty > defaultValue
        ninjaPropertiesForOverlay = new NinjaPropertiesImpl.Builder()
                .withMode(NinjaMode.prod)
                .withExternalConfiguration("conf/overlayed.conf")
                .build();
        ninjaProperties 
                = new OverlayedNinjaProperties(ninjaPropertiesForOverlay);
        
        assertThat(ninjaProperties.get("key1", null, "default"), is("test1"));
        assertThat(ninjaProperties.getInteger("key2", null, 0), is(1));
        assertThat(ninjaProperties.getBoolean("key3", null, true), is(Boolean.FALSE));
        
        try {
            // systemProperty > configProperty
            System.setProperty("key1", "system");
            System.setProperty("key2", "2");
            System.setProperty("key3", "true");

            assertThat(ninjaProperties.get("key1", null, "default"), is("system"));
            assertThat(ninjaProperties.getInteger("key2", null, 0), is(2));
            assertThat(ninjaProperties.getBoolean("key3", null, true), is(Boolean.TRUE));


            // currentValue > systemProperty
            assertThat(ninjaProperties.get("key1", "current", "default"), is("current"));
            assertThat(ninjaProperties.getInteger("key2", 3, 0), is(3));
            assertThat(ninjaProperties.getBoolean("key3", false, true), is(Boolean.FALSE));
        } finally {
            System.clearProperty("key1");
            System.clearProperty("key2");
            System.clearProperty("key3");
        }
    }
    
    @Test
    public void badValues() {
                NinjaProperties ninjaPropertiesForOverlay = new NinjaPropertiesImpl.Builder()
                .withMode(NinjaMode.dev)
                .withExternalConfiguration("conf/overlayed.bad.conf")
                .build();
        OverlayedNinjaProperties ninjaProperties 
                = new OverlayedNinjaProperties(ninjaPropertiesForOverlay);
        
        try {
            Integer i = ninjaProperties.getInteger("key2", null, null);
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("Unable to convert property"));
        }
        
        try {
            Long l = ninjaProperties.getLong("key2", null, null);
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("Unable to convert property"));
        }
        
        try {
            Boolean b = ninjaProperties.getBoolean("key3", null, null);
            fail("exception expected");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), containsString("Unable to convert property"));
        }
    }
}
