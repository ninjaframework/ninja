/**
 * Copyright (C) 2012-2017 the original author or authors.
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

package ninja;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import ninja.utils.NoHttpBody;

import org.junit.Test;

public class ResultsTest {

    @Test
    public void testResultsStatus() {

        Result result = Results.status(200);
        assertEquals(200, result.getStatusCode());

    }

    @Test
    public void testResultsOk() {

        Result result = Results.ok();
        assertEquals(200, result.getStatusCode());

    }

    @Test
    public void testResultsNotFound() {

        Result result = Results.notFound();
        assertEquals(Result.SC_404_NOT_FOUND, result.getStatusCode());

    }

    @Test
    public void testResultsForbidden() {

        Result result = Results.forbidden();
        assertEquals(Result.SC_403_FORBIDDEN, result.getStatusCode());

    }

    @Test
    public void testResultsBadRequest() {

        Result result = Results.badRequest();
        assertEquals(Result.SC_400_BAD_REQUEST, result.getStatusCode());

    }

    @Test
    public void testResultsNoContent() {

        Result result = Results.noContent();
        assertEquals(Result.SC_204_NO_CONTENT, result.getStatusCode());
        assertTrue(result.getRenderable() instanceof NoHttpBody);

    }

    @Test
    public void testResultsInternalServerError() {

        Result result = Results.internalServerError();
        assertEquals(Result.SC_500_INTERNAL_SERVER_ERROR,
                result.getStatusCode());

    }

    @Test
    public void testResultsRedirect() {

        Result result = Results.redirect("http://example.com");
        assertEquals(Result.SC_303_SEE_OTHER, result.getStatusCode());
        assertEquals("http://example.com",
                result.getHeaders().get(Result.LOCATION));
        assertTrue(result.getRenderable() instanceof NoHttpBody);

    }

    @Test
    public void testResultsRedirectTemporary() {

        Result result = Results.redirectTemporary("http://example.com");
        assertEquals(Result.SC_307_TEMPORARY_REDIRECT, result.getStatusCode());
        assertEquals("http://example.com",
                result.getHeaders().get(Result.LOCATION));
        assertTrue(result.getRenderable() instanceof NoHttpBody);
    }

    @Test
    public void testResultsContentType() {

        Result result = Results.contentType("text/my-cool-content-type");
        assertEquals(Result.SC_200_OK, result.getStatusCode());
        assertEquals("text/my-cool-content-type", result.getContentType());

    }

    @Test
    public void testResultsHtml() {

        Result result = Results.html();
        assertEquals(Result.SC_200_OK, result.getStatusCode());
        assertEquals(Result.TEXT_HTML, result.getContentType());
    }

    @Test
    public void testResultsHtmlWithStatusCode() {

        Result result = Results.html().status(Result.SC_300_MULTIPLE_CHOICES);
        assertEquals(Result.SC_300_MULTIPLE_CHOICES, result.getStatusCode());
        assertEquals(Result.TEXT_HTML, result.getContentType());
    }

    @Test
    public void testResultsJson() {

        Result result = Results.json();
        assertEquals(Result.SC_200_OK, result.getStatusCode());
        assertEquals(Result.APPLICATION_JSON, result.getContentType());

    }

    @Test
    public void testResultsJsonWithObjectToRender() {

        TestObject testObject = new TestObject();

        Result result = Results.json().render(testObject);
        assertEquals(Result.SC_200_OK, result.getStatusCode());
        assertEquals(Result.APPLICATION_JSON, result.getContentType());
        assertEquals(testObject, result.getRenderable());

    }

    @Test
    public void testResultsXml() {

        Result result = Results.xml();
        assertEquals(Result.SC_200_OK, result.getStatusCode());
        assertEquals(Result.APPLICATION_XML, result.getContentType());

    }

    @Test
    public void testResultsTODO() {

        Result result = Results.TODO();
        assertEquals(Result.SC_501_NOT_IMPLEMENTED, result.getStatusCode());
        assertEquals(Result.APPLICATION_JSON, result.getContentType());

    }

    @Test
    public void testResultsAsync() {

        Result result = Results.async();

        assertTrue(result instanceof AsyncResult);

    }

    /**
     * Simple helper to test if objects get copied to result.
     * 
     */
    public class TestObject {
    }

}
