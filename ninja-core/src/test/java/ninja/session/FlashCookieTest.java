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

package ninja.session;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ninja.Context;
import ninja.Cookie;
import ninja.Result;
import ninja.Results;
import ninja.utils.NinjaConstant;
import ninja.utils.NinjaProperties;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Collections;

@RunWith(MockitoJUnitRunner.class)
public class FlashCookieTest {

    @Mock
    private Context context;

    @Mock
    private Result result;

    @Captor
    private ArgumentCaptor<Cookie> cookieCaptor;

    @Mock
    private NinjaProperties ninjaProperties;

    @Before
    public void setUp() {

        when(ninjaProperties.getOrDie(NinjaConstant.applicationCookiePrefix))
                .thenReturn("NINJA");

    }

    @Test
    public void testFlashScopeDoesNothingWhenFlashCookieEmpty() {

        FlashScope flashCookie = new FlashScopeImpl(ninjaProperties);

        flashCookie.init(context);

        // put nothing => intentionally to check if no flash cookie will be
        // saved
        flashCookie.save(context, result);

        // no cookie should be set as the flash scope is empty...:
        verify(result, never()).addCookie(Matchers.any(Cookie.class));
    }

    @Test
    public void testFlashCookieSettingWorks() {

        FlashScope flashCookie = new FlashScopeImpl(ninjaProperties);

        flashCookie.init(context);

        flashCookie.put("hello", "flashScope");

        // put nothing => intentionally to check if no flash cookie will be
        // saved
        flashCookie.save(context, result);

        // a cookie will be set => hello:flashScope
        verify(result).addCookie(cookieCaptor.capture());

        // verify some stuff on the set cookie
        assertEquals("NINJA_FLASH", cookieCaptor.getValue().getName());
        assertEquals("hello=flashScope", cookieCaptor.getValue()
                .getValue());
        assertEquals(-1, cookieCaptor.getValue().getMaxAge());

        assertEquals(1, ((FlashScopeImpl) flashCookie)
                .getCurrentFlashCookieData().size());
        assertEquals(1, ((FlashScopeImpl) flashCookie)
                .getOutgoingFlashCookieData().size());

    }

    @Test
    public void testThatFlashCookieWorksAndIsActiveOnlyOneTime() {
        // setup this testmethod
        Cookie cookie = Cookie.builder("NINJA_FLASH",
                "hello=flashScope").build();
        when(context.getCookie("NINJA_FLASH")).thenReturn(cookie);

        FlashScope flashCookie = new FlashScopeImpl(ninjaProperties);

        flashCookie.init(context);

        // make sure the old cookue gets parsed:
        assertEquals("flashScope", flashCookie.get("hello"));

        flashCookie.put("another message", "is there...");
        flashCookie.put("yet another message", "is there...");

        flashCookie.save(context, result);

        // a cookie will be set => hello:flashScope
        verify(result).addCookie(cookieCaptor.capture());

        // verify some stuff on the set cookie
        assertEquals("NINJA_FLASH", cookieCaptor.getValue().getName());
        // the new flash messages must be there..
        // but the old has disappeared (flashScope):
        assertEquals(
                "another+message=is+there...&yet+another+message=is+there...",
                cookieCaptor.getValue().getValue());
        assertEquals(3, ((FlashScopeImpl) flashCookie)
                .getCurrentFlashCookieData().size());
        assertEquals(2, ((FlashScopeImpl) flashCookie)
                .getOutgoingFlashCookieData().size());
    }

    @Test
    public void testThatFlashCookieClearWorks() {
        // setup this testmethod
        Cookie cookie = Cookie.builder("NINJA_FLASH",
                "hello=flashScope").build();
        when(context.getCookie("NINJA_FLASH")).thenReturn(cookie);

        FlashScope flashCookie = new FlashScopeImpl(ninjaProperties);

        flashCookie.init(context);

        // make sure the old cookue gets parsed:
        assertEquals("flashScope", flashCookie.get("hello"));

        flashCookie.put("funny new flash message", "is there...");

        // now test clearCurrentFlashCookieData
        flashCookie.clearCurrentFlashCookieData();

        assertEquals(0, ((FlashScopeImpl) flashCookie)
                .getCurrentFlashCookieData().size());
        assertEquals(1, ((FlashScopeImpl) flashCookie)
                .getOutgoingFlashCookieData().size());

    }

    @Test
    public void testThatFlashCookieClearOfOutgoingWorks() {
        // setup this testmethod
        Cookie cookie = Cookie.builder("NINJA_FLASH",
                "hello=flashScope").build();
        when(context.getCookie("NINJA_FLASH")).thenReturn(cookie);

        FlashScope flashCookie = new FlashScopeImpl(ninjaProperties);

        flashCookie.init(context);

        // make sure the old cookue gets parsed:
        assertEquals("flashScope", flashCookie.get("hello"));

        flashCookie.put("funny new flash message", "is there...");

        // now test clearCurrentFlashCookieData
        flashCookie.discard();

        assertEquals(2, ((FlashScopeImpl) flashCookie)
                .getCurrentFlashCookieData().size());
        assertEquals(0, ((FlashScopeImpl) flashCookie)
                .getOutgoingFlashCookieData().size());

    }

    @Test
    public void testThatFlashCookieKeepWorks() {
        // setup this testmethod
        Cookie cookie = Cookie.builder("NINJA_FLASH",
                "hello=flashScope").build();
        when(context.getCookie("NINJA_FLASH")).thenReturn(cookie);

        FlashScope flashCookie = new FlashScopeImpl(ninjaProperties);

        flashCookie.init(context);

        // make sure the old cookue gets parsed:
        assertEquals("flashScope", flashCookie.get("hello"));

        // make sure outgoing is 0
        assertEquals(1, ((FlashScopeImpl) flashCookie)
                .getCurrentFlashCookieData().size());
        assertEquals(0, ((FlashScopeImpl) flashCookie)
                .getOutgoingFlashCookieData().size());

        // now call keep.
        flashCookie.keep();
        // => now both queues must be 1
        assertEquals(1, ((FlashScopeImpl) flashCookie)
                .getCurrentFlashCookieData().size());
        assertEquals(1, ((FlashScopeImpl) flashCookie)
                .getOutgoingFlashCookieData().size());

    }

    @Test
    public void testThatCorrectMethodOfNinjaPropertiesIsUsedSoThatStuffBreaksWhenPropertyIsAbsent() {

        FlashScope flashCookie = new FlashScopeImpl(ninjaProperties);

        // Make sure that getOrDie has been called. This makes sure we have set
        // a cookie prefix:
        verify(ninjaProperties).getOrDie(NinjaConstant.applicationCookiePrefix);
    }

}
