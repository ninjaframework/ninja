/**
 * Copyright (C) 2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.Test;

public class ResultTest {

    @Test
    public void testConstructor() {

        Result result = new Result(Result.SC_307_TEMPORARY_REDIRECT);
        assertEquals(Result.SC_307_TEMPORARY_REDIRECT, result.getStatusCode());
        assertEquals(0, result.getCookies().size());
        assertEquals(0, result.getHeaders().keySet().size());

    }

    @Test
    public void testGetRenderable() {

        TestObject testObject = new TestObject();
        Result result = new Result(200);
        result.render(testObject);
        assertEquals(testObject, result.getRenderable());

    }

    @Test
    public void testGetContentType() {

        Result result = new Result(200);
        result.contentType("text/my-funky-content-type");
        assertEquals("text/my-funky-content-type", result.getContentType());

    }

    @Test
    public void testAndAddHeaders() {

        Result result = new Result(200);

        result.addHeader("header1", "value1");
        result.addHeader("header2", "value2");

        assertEquals(2, result.getHeaders().size());
        assertEquals("value1", result.getHeaders().get("header1"));
        assertEquals("value2", result.getHeaders().get("header2"));

    }

    @Test
    public void testAndAddCookies() {

        Result result = new Result(200);
        result.addCookie(Cookie.builder("cookie1", "value1").build());
        result.addCookie(Cookie.builder("cookie2", "value2").build());

        assertEquals(2, result.getCookies().size());
        assertEquals("value1", result.getCookies().get(0).getValue());
        assertEquals("value2", result.getCookies().get(1).getValue());

    }

    public void testUnsetCookie() {

        Result result = new Result(200);
        result.unsetCookie("Cookie-to-be-unset");

        assertEquals(1, result.getCookies().size());

        assertEquals("Cookie-to-be-unset", result.getCookies().get(0).getName());
        assertEquals(null, result.getCookies().get(0).getValue());

    }

    @Test
    public void testSetAndGetStatus() {
        Result result = new Result(Result.SC_200_OK);

        // set the status:
        result.status(Result.SC_501_NOT_IMPLEMENTED);

        // and verify that we retrieve the correct one:
        assertEquals(Result.SC_501_NOT_IMPLEMENTED, result.getStatusCode());

    }

    @Test
    public void testSetAndGetTemplate() {

        Result result = new Result(Result.SC_200_OK);

        // set the status:
        result.template("/my/custom/template.ftl.html");

        // and verify that we retrieve the correct one:
        assertEquals("/my/custom/template.ftl.html", result.getTemplate());
        assertEquals(Result.SC_200_OK, result.getStatusCode());

    }

    @Test
    public void testRedirect() {

        Result result = new Result(Result.SC_200_OK);
        result.redirect("http://example.com");

        // assert that headers are set:
        assertEquals(1, result.getHeaders().size());
        assertEquals("http://example.com", result.getHeaders().get(Result.LOCATION));
        assertEquals(Result.SC_303_SEE_OTHER, result.getStatusCode());
    }

    @Test
    public void testRedirectTemporary() {

        Result result = new Result(Result.SC_200_OK);
        result.redirectTemporary("http://example.com");

        // assert that headers are set:
        assertEquals(1, result.getHeaders().size());
        assertEquals("http://example.com", result.getHeaders().get(Result.LOCATION));
        assertEquals(Result.SC_307_TEMPORARY_REDIRECT, result.getStatusCode());

    }

    @Test
    public void testHtml() {

        Result result = new Result(Result.SC_200_OK);
        result.html();

        assertEquals(Result.TEXT_HTML, result.getContentType());

    }

    @Test
    public void testJson() {
        Result result = new Result(Result.SC_200_OK);
        result.json();

        assertEquals(Result.APPLICATON_JSON, result.getContentType());
    }

    @Test
    public void testUtf8IsUsedAsDefaultCharset() {
        Result result = new Result(Result.SC_200_OK);

        assertEquals("utf-8", result.getCharset());
    }

    @Test
    public void testSettingOfCharsetWorks() {
        Result result = new Result(Result.SC_200_OK);
        result.charset("iso-7777");

        assertEquals("iso-7777", result.getCharset());
    }

    /**
     * Simple helper to test if objects get copied to result.
     * 
     */
    public class TestObject {
    }

}
