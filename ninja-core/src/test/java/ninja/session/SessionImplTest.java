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
import static org.mockito.Mockito.*;

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
import org.hamcrest.CoreMatchers;
import org.junit.Assert;

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
import org.mockito.Mockito;
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

    @Mock
    SessionTime sessionTime;

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

        when(sessionTime.currentTimeMillis())
            .thenReturn(System.currentTimeMillis());

        when(ninjaProperties.getBooleanWithDefault(NinjaConstant.applicationCookieEncrypted, false)).thenReturn(encrypted);

        encryption = new CookieEncryption(ninjaProperties);
        crypto = new Crypto(ninjaProperties);

    }

    @Test
    public void testSessionDoesNotGetWrittenToResponseWhenEmptyAndOnlySentWhenChanged() {

        Session sessionCookie =
            getNewSession();

        sessionCookie.init(context);

        // put nothing => empty session will not be sent as we send only changed
        // stuff...
        sessionCookie.save(context, result);

        // no cookie should be set as the flash scope is empty...:
        verify(result, never()).addCookie(Matchers.any(Cookie.class));
    }

    @Test
    public void testSessionCookieSettingWorks() throws Exception {

        Session sessionCookie = getNewSession();

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

        Session sessionCookie = getNewSession();

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

        Session sessionCookie = getNewSession();

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

        Session sessionCookie = getNewSession();

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

        Session sessionCookie = getNewSession();

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

        Session sessionCookie = getNewSession();

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
        Session sessionCookie2 = getNewSession();

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
        Session sessionCookie = getNewSession();

        verify(ninjaProperties).getOrDie(NinjaConstant.applicationCookiePrefix);
    }

    @Test
    public void testSessionCookieDelete() {
        Session sessionCookie = getNewSession();
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

        Session sessionCookie = getNewSession();

        sessionCookie.init(context);

        String authenticityToken = sessionCookie.getAuthenticityToken();

        String cookieValueWithoutSign = captureFinalCookie(sessionCookie);

        //verify that the authenticity token is set
        assertTrue(cookieValueWithoutSign.contains(Session.AUTHENTICITY_KEY + "=" + authenticityToken));
        // also make sure the timestamp is there:
        assertTrue(cookieValueWithoutSign.contains(Session.TIMESTAMP_KEY));

    }

    @Test
    public void testGetIdTokenWorks() {

        Session sessionCookie = getNewSession();

        sessionCookie.init(context);

        String idToken = sessionCookie.getId();

        String valueWithoutSign = captureFinalCookie(sessionCookie);
        //verify that the id token is set:
        assertTrue(valueWithoutSign.contains(Session.ID_KEY + "=" + idToken));
        // also make sure the timestamp is there:
        assertTrue(valueWithoutSign.contains(Session.TIMESTAMP_KEY));

    }

    @Test
    public void testThatCookieUsesContextPath() {
        Mockito.when(context.getContextPath()).thenReturn("/my_context");
        Session sessionCookie = getNewSession();
        sessionCookie.init(context);
        sessionCookie.put("anykey", "anyvalue");

        sessionCookie.save(context, result);

        verify(result).addCookie(cookieCaptor.capture());
        Cookie cookie = cookieCaptor.getValue();
        Assert.assertThat(cookie.getPath(), CoreMatchers.equalTo("/my_context/"));
    }

    @Test
    public void testExpiryTime() {

        // 1. Check that session is still saved when expiry time is set

        Session sessionCookie1 = getNewSession();
        sessionCookie1.init(context);

        sessionCookie1.put("1", "2");

        sessionCookie1.setExpiryTime(10 * 1000L);

        Assert.assertThat(sessionCookie1.get("1"), CoreMatchers.equalTo("2"));

        sessionCookie1.save(context, result);

        Session sessionCookie2 = roundTrip(sessionCookie1);

        Assert.assertThat(sessionCookie2.get("1"), CoreMatchers.equalTo("2"));

        // 2. Check that session is invalidated when past the expiry time

        // Set the current time past when it is called.
        when(sessionTime.currentTimeMillis()).thenReturn(System.currentTimeMillis() + 11 * 1000L);

        Session sessionCookie3 = roundTrip(sessionCookie2);

        Assert.assertNull(sessionCookie3.get("1"));
    }

    @Test
    public void testExpiryTimeRoundTrip() {
        // Round trip the session cookie with an expiry time in the future
        // Then remove the expiration time to make sure it is still valid
        when(
            ninjaProperties.getInteger(NinjaConstant.sessionExpireTimeInSeconds))
            .thenReturn(null);

        Session sessionCookie1 = getNewSession();
        sessionCookie1.init(context);

        sessionCookie1.put("1", "2");

        sessionCookie1.setExpiryTime(10 * 1000L);

        Assert.assertThat(sessionCookie1.get("1"), CoreMatchers.equalTo("2"));

        Session sessionCookie2 = roundTrip(sessionCookie1);

        Assert.assertThat(sessionCookie2.get("1"), CoreMatchers.equalTo("2"));

        Assert.assertThat(sessionCookie2.get(Session.EXPIRY_TIME_KEY), CoreMatchers.equalTo("10000"));

        sessionCookie2.setExpiryTime(null);

        Session sessionCookie3 = roundTrip(sessionCookie2);

        Assert.assertNull(sessionCookie3.get(Session.EXPIRY_TIME_KEY));
    }

    private Session roundTrip(Session sessionCookie1) {
        sessionCookie1.save(context, result);

        // Get the cookie ...
        verify(result, atLeastOnce()).addCookie(cookieCaptor.capture());

        when(context.getCookie("NINJA_SESSION")).thenReturn(cookieCaptor.getValue());

        // ... and roundtrip it into an new session
        Session sessionCookie2 = getNewSession();
        sessionCookie2.init(context);
        return sessionCookie2;
    }

    private Session getNewSession() {
        return new SessionImpl(crypto, encryption, ninjaProperties, sessionTime);
    }

    private String captureFinalCookie(Session sessionCookie) {
        sessionCookie.save(context, result);

        // SessionImpl should set the cookie
        verify(result).addCookie(cookieCaptor.capture());

        String cookieValue = cookieCaptor.getValue().getValue();
        String cookieValueWithoutSign = cookieValue.substring(cookieValue.indexOf("-") + 1);

        if (encrypted) {
            cookieValueWithoutSign = encryption.decrypt(cookieValueWithoutSign);
        }

        return cookieValueWithoutSign;
    }

}
