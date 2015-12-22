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

package ninja.standalone;

import java.net.URI;
import javax.net.ssl.SSLContext;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;

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
    
}
