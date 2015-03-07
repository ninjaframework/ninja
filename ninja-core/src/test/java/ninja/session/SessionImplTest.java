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

package ninja.session;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collection;

import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.utils.CookieEncryption;
import ninja.utils.Crypto;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;
import ninja.utils.SecretGenerator;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

@RunWith(Parameterized.class)
public class SessionImplTest {

    @Mock
    private Context context;

    @Mock
    private Result result;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    private Crypto crypto;
    private CookieEncryption encryption;

    @Mock
    NinjaProperties ninjaProperties;

    @Parameter
    public boolean encrypted;

    /**
     * This method provides parameters for {@code encrypted} field. The first set contains {@code false} so that
     * {@link CookieEncryption} is not initialized and test class is run without session cookie encryption. Second set
     * contains {@code true} so that sessions cookies are encrypted.
     *
     * @return
     */
    @Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] { { false }, { true } });
    }

    @Before
    public void setUp() {

        MockitoAnnotations.initMocks(this);

        when(
                ninjaProperties
                .getInteger(NinjaConstant.sessionExpireTimeInSeconds))
                .thenReturn(10000);
        when(
                ninjaProperties.getBooleanWithDefault(
                        NinjaConstant.sessionSendOnlyIfChanged, true))
                .thenReturn(true);
        when(
                ninjaProperties.getBooleanWithDefault(
                        NinjaConstant.sessionTransferredOverHttpsOnly, true))
                .thenReturn(true);
        when(
                ninjaProperties.getBooleanWithDefault(
                        NinjaConstant.sessionHttpOnly, true)).thenReturn(true);

        when(ninjaProperties.getOrDie(NinjaConstant.applicationSecret))
                .thenReturn(SecretGenerator.generateSecret());

        when(ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix))
                .thenReturn("NINJA");

        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.applicationCookieEncrypted, false)).thenReturn(encrypted);

        encryption = new CookieEncryption(ninjaProperties);
        crypto = new Crypto(ninjaProperties);

    }

    @Test
    public void testSessionDoesNotGetWrittenToResponseWhenEmptyAndOnlySentWhenChanged() {

        Session sessionCookie = new SessionImpl(crypto, encryption, ninjaProperties);

        sessionCookie.init(context);

        // put nothing => empty session will not be sent as we send only changed
        // stuff...
        sessionCookie.save(context, result);

        // no cookie should be set as the flash scope is empty...:
        verify(result, never()).addCookie(Matchers.any(Cookie.class));
    }

    @Test
    public void testSessionCookieSettingWorks() throws Exception {

        Session sessionCookie = new SessionImpl(crypto, encryption, ninjaProperties);

        sessionCookie.init(context);

        sessionCookie.put("hello", "session!");

        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context, result);

        // a cookie will be set
        verify(result).addCookie(cookieCaptor.capture());

        // verify some stuff on the set cookie
        assertEquals("NINJA_SESSION", cookieCaptor.getValue().getName());

        // assert some stuff...
        // Make sure that sign is valid:
        String cookieString = cookieCaptor.getValue().getValue();

        String cookieFromSign = cookieString.substring(cookieString.indexOf("-") + 1);

        String computedSign = crypto.signHmacSha1(cookieFromSign);

        assertEquals(computedSign, cookieString.substring(0, cookieString.indexOf("-")));

        if (encrypted) {
            cookieFromSign = encryption.decrypt(cookieFromSign);
        }
        // Make sure that cookie contains timestamp
        assertTrue(cookieFromSign.contains(Session.TIMESTAMP_KEY));

    }

    @Test
    public void testHttpsOnlyWorks() throws Exception {

        Session sessionCookie = new SessionImpl(crypto, encryption, ninjaProperties);

        sessionCookie.init(context);

        sessionCookie.put("hello", "session!");

        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context, result);

        // a cookie will be set
        verify(result).addCookie(cookieCaptor.capture());

        // verify some stuff on the set cookie
        assertEquals(true, cookieCaptor.getValue().isSecure());

    }

    @Test
    public void testNoHttpsOnlyWorks() throws Exception {
        // setup this testmethod
        when(
                ninjaProperties.getBooleanWithDefault(
                        NinjaConstant.sessionTransferredOverHttpsOnly, true))
                .thenReturn(false);

        Session sessionCookie = new SessionImpl(crypto, encryption, ninjaProperties);

        sessionCookie.init(context);

        sessionCookie.put("hello", "session!");

        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context, result);

        // a cookie will be set
        verify(result).addCookie(cookieCaptor.capture());

        // verify some stuff on the set cookie
        assertEquals(false, cookieCaptor.getValue().isSecure());

    }

    @Test
    public void testHttpOnlyWorks() throws Exception {

        Session sessionCookie = new SessionImpl(crypto, encryption, ninjaProperties);

        sessionCookie.init(context);

        sessionCookie.put("hello", "session!");

        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context, result);

        // a cookie will be set
        verify(result).addCookie(cookieCaptor.capture());

        // verify some stuff on the set cookie
        assertEquals(true, cookieCaptor.getValue().isHttpOnly());

    }

    @Test
    public void testNoHttpOnlyWorks() throws Exception {
        // setup this testmethod
        when(
                ninjaProperties.getBooleanWithDefault(
                        NinjaConstant.sessionHttpOnly, true)).thenReturn(false);

        Session sessionCookie = new SessionImpl(crypto, encryption, ninjaProperties);

        sessionCookie.init(context);

        sessionCookie.put("hello", "session!");

        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context, result);

        // a cookie will be set
        verify(result).addCookie(cookieCaptor.capture());

        // verify some stuff on the set cookie
        assertEquals(false, cookieCaptor.getValue().isHttpOnly());

    }

    @Test
    public void testThatCookieSavingAndInitingWorks() {

        Session sessionCookie = new SessionImpl(crypto, encryption, ninjaProperties);

        sessionCookie.init(context);

        sessionCookie.put("key1", "value1");
        sessionCookie.put("key2", "value2");
        sessionCookie.put("key3", "value3");

        // put nothing => intentionally to check if no session cookie will be
        // saved
        sessionCookie.save(context, result);

        // a cookie will be set
        verify(result).addCookie(cookieCaptor.capture());

        // now we simulate a new request => the session storage will generate a
        // new cookie:
        Cookie newSessionCookie = Cookie.builder(
                cookieCaptor.getValue().getName(),
                cookieCaptor.getValue().getValue()).build();

        // that will be returned by the httprequest...
        when(context.getCookie(cookieCaptor.getValue().getName())).thenReturn(
                newSessionCookie);

        // init new session from that cookie:
        Session sessionCookie2 = new SessionImpl(crypto, encryption, ninjaProperties);

        sessionCookie2.init(context);

        assertEquals("value1", sessionCookie2.get("key1"));
        assertEquals("value2", sessionCookie2.get("key2"));
        assertEquals("value3", sessionCookie2.get("key3"));

    }

    @Test
    public void testThatCorrectMethodOfNinjaPropertiesIsUsedSoThatStuffBreaksWhenPropertyIsAbsent() {

        // we did not set the cookie prefix
        when(ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix))
                .thenReturn(null);

        // stuff must break => ...
        Session sessionCookie = new SessionImpl(crypto, encryption, ninjaProperties);

        verify(ninjaProperties).getOrDie(NinjaConstant.applicationCookiePrefix);
    }

    @Test
    public void testSessionCookieDelete() {
        Session sessionCookie = new SessionImpl(crypto, encryption, ninjaProperties);
        sessionCookie.init(context);
        final String key = "mykey";
        final String value = "myvalue";
        sessionCookie.put(key, value);

        // value should have been set:
        assertEquals(value, sessionCookie.get(key));

        // value should be returned when removing:
        assertEquals(value, sessionCookie.remove(key));

        // after removing, value should not be there anymore:
        assertNull(sessionCookie.get(key));
    }

    @Test
    public void testGetAuthenticityTokenWorks() {

        Session sessionCookie = new SessionImpl(crypto, encryption, ninjaProperties);

        sessionCookie.init(context);

        String authenticityToken = sessionCookie.getAuthenticityToken();

        sessionCookie.save(context, result);

        // a cookie will be set
        verify(result).addCookie(cookieCaptor.capture());

        String cookieValue = cookieCaptor.getValue().getValue();
        String cookieValueWithoutSign = cookieValue.substring(cookieValue.indexOf("-") + 1);

        if (encrypted) {
            cookieValueWithoutSign = encryption.decrypt(cookieValueWithoutSign);
        }

        //verify that the authenticity token is set
        assertTrue(cookieValueWithoutSign.contains(Session.AUTHENTICITY_KEY + "=" + authenticityToken));
        // also make sure the timestamp is there:
        assertTrue(cookieValueWithoutSign.contains(Session.TIMESTAMP_KEY));

    }

    @Test
    public void testGetIdTokenWorks() {

        Session sessionCookie = new SessionImpl(crypto, encryption, ninjaProperties);

        sessionCookie.init(context);

        String idToken = sessionCookie.getId();

        sessionCookie.save(context, result);

        // a cookie will be set
        verify(result).addCookie(cookieCaptor.capture());

        String cookieValue = cookieCaptor.getValue().getValue();
        String valueWithoutSign = cookieValue.substring(cookieValue.indexOf("-") + 1);

        if (encrypted) {
            valueWithoutSign = encryption.decrypt(valueWithoutSign);
        }
        //verify that the id token is set:
        assertTrue(valueWithoutSign.contains(Session.ID_KEY + "=" + idToken));
        // also make sure the timestamp is there:
        assertTrue(valueWithoutSign.contains(Session.TIMESTAMP_KEY));

    }

}
