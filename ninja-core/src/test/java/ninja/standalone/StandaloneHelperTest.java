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

package ninja.standalone;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import ninja.utils.ForwardingServiceLoader;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StandaloneHelperTest {

    @Test
    public void checkContextPath() {
        StandaloneHelper.checkContextPath("/mycontext");
        StandaloneHelper.checkContextPath("/mycontext/hello");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void contextPathMustStartWithForwardSlash() {
        StandaloneHelper.checkContextPath("mycontext");
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void contextPathMayNotEndWithForwardSlash() {
        StandaloneHelper.checkContextPath("/mycontext/");
    }
    
    @Test
    public void createDevelopmentSSLContext() throws Exception {
        URI keystoreUri = new URI(Standalone.DEFAULT_DEV_NINJA_SSL_KEYSTORE_URI);
        char[] keystorePassword = Standalone.DEFAULT_DEV_NINJA_SSL_KEYSTORE_PASSWORD.toCharArray();
        URI truststoreUri = new URI(Standalone.DEFAULT_DEV_NINJA_SSL_TRUSTSTORE_URI);
        char[] truststorePassword = Standalone.DEFAULT_DEV_NINJA_SSL_TRUSTSTORE_PASSWORD.toCharArray();
        
        SSLContext sslContext
            = StandaloneHelper.createSSLContext(keystoreUri, keystorePassword, truststoreUri, truststorePassword);
        
        assertThat(sslContext, is(not(nullValue())));
    }
    
    @Test
    public void resolveStandaloneClass() throws Exception {
        Class<? extends Standalone> resolvedStandaloneClass;
        
        try {
            // try to resolve system property
            StandaloneHelper.resolveStandaloneClass("sys_prop_does_not_exist", null, null);
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("sys_prop_does_not_exist"));
        }
        
        // try to find using META-INF/services
        // mock the service loader
        ForwardingServiceLoader<Standalone> serviceLoader
            = mock(ForwardingServiceLoader.class);

        List<Standalone> standalones = new ArrayList<>();
        standalones.add(new MockStandalone());
        when(serviceLoader.iterator()).thenReturn(standalones.iterator());

        resolvedStandaloneClass = StandaloneHelper.resolveStandaloneClass(null, serviceLoader, "default_does_not_exist");
       
        assertThat(resolvedStandaloneClass.newInstance(), instanceOf(MockStandalone.class));
        
        try {
            // try to resolve using default
            when(serviceLoader.iterator()).thenReturn(new ArrayList<Standalone>().iterator());
            StandaloneHelper.resolveStandaloneClass(null, serviceLoader, "default_does_not_exist");
            fail();
        } catch (Exception e) {
            assertThat(e.getMessage(), containsString("default_does_not_exist"));
        }
        
    }
    
}
