/*
 * Copyright (C) 2012-2015 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.servlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.List;
import java.util.Map;
import org.apache.commons.fileupload.FileItemIterator;
import org.junit.Before;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import com.google.common.collect.Multimap;

@PrepareForTest(NinjaServletContext.class)
public class NinjaServletContextUtilTest extends NinaServletContextBase {

	private String reqAttrVal;

	private Map<String, List<String>> headers;

	private List<String> languages;

	private List<String> paramList;

	private Map<String, List<String>> formFieldsMap;

	private Multimap multimap;

	private FileItemIterator fileItemIterator;	

	@Before
	public void setUp() {
		super.setUp();
		context.init(servletContext, httpServletRequest, httpServletResponse);
	}

	@Test
	public void testGetMethod() {
		// given
		String postMethod="POST";
		when(httpServletRequest.getMethod()).thenReturn(postMethod);

		// when
		reqAttrVal = context.getMethod();

		// then
		assertEquals("Request method must be POST", postMethod, reqAttrVal);
	}

	@Test
	public void testGetRequestUri() {
		// given
		String requestUri="/ninja";
		when(httpServletRequest.getRequestURI()).thenReturn(requestUri);

		// when
		reqAttrVal = context.getRequestUri();

		// then
		assertEquals("Request uri must be /ninja", requestUri, reqAttrVal);
	}

	@Test
	public void testGetScheme() {
		// given
		String scheme="http";
		when(httpServletRequest.getScheme()).thenReturn(scheme);

		// when
		reqAttrVal = context.getScheme();

		// then
		assertEquals("Scheme must be equal to http", scheme, reqAttrVal);
	}

	@Test
	public void testGetAttribute() {
		// given
		String pageParam="page";
		String homePage="homePage";
		when(httpServletRequest.getAttribute(pageParam)).thenReturn(homePage);

		// when
		reqAttrVal = (String) context.getAttribute(pageParam);

		// then
		assertEquals("page param value must be equal to homePage", homePage, reqAttrVal);
	}

	@Test
	public void testSetAttribute() {
		// given
		String pageParam="page";
		String homePage="homePage";
		when(httpServletRequest.getAttribute(pageParam)).thenReturn(homePage);

		// when
		context.setAttribute(pageParam, homePage);
		reqAttrVal = (String) context.getAttribute(pageParam);

		// then
		assertEquals("page param value must be equal to homePage", homePage, reqAttrVal);
	}

	@Test
	public void testGetHeaders() {
		// given
		String acceptedLanguageHeader="accepted-language";
		String enUs="en-US";
		when(httpServletRequest.getHeaderNames()).thenReturn(getRequestHeaders().elements());
		when(httpServletRequest.getHeaders(acceptedLanguageHeader))
				.thenReturn(getAcceptedLanguages().elements());

		// when
		headers = context.getHeaders();
		languages = headers.get(acceptedLanguageHeader);

		// then
		assertTrue("Request headers must contain accept-language header",
				headers.containsKey(acceptedLanguageHeader));
		assertTrue("Accept-language header must contain a value en-us", languages.contains(enUs));
	}

	@Test
	public void tempGetParameterValuesNullParams() throws Exception {
		int zeroSize=0;
		assertEquals("Empty list should return as there is no matching param", (Integer)zeroSize,
				(Integer) context.getParameterValues("non matching parameter").size());
	}

	@Test
	public void tempGetParameterValuesDataParams() throws Exception {
		// given
		String[] pageParams = { "titlePage", "homePage" };
		Integer two=2;
		when(httpServletRequest.getParameterValues("page")).thenReturn(pageParams);

		// when
		paramList = context.getParameterValues("page");

		// then
		assertEquals("List must contain two param values", two, (Integer) paramList.size());
	}

	@Test
	public void testGetAttributes() {
		// given
		setUpGetAttributes();

		// then
		assertTrue("Context must contain key paramName", context.getAttributes().containsKey("context path"));
	}

	@Test
	public void testGetParameterAsFileItemNullCheck() {
		assertNull(context.getParameterAsFileItem("non existance param"));
	}

	@Test
	public void testGettHeadersEnUs() {
		// given
		setUpEnUsHeader();

		// then
		assertTrue("Accepted Language header value must be en-us",
				context.getHeaders("accepted-language").contains("en-us"));
	}

	@Test
	public void testControllerReturnedNullCheck() {
		assertNull("Control must return null value as we didn't set any value", context.controllerReturned());
	}
	
	@Test
	public void testGetParameterAsFileItems2() {
		Integer zeroSize=0;
		assertEquals("Empty list must be returned as we didn't set anything", zeroSize,
				(Integer) context.getParameterAsFileItems("non existance param").size());
	}

	@Test
	public void testGetParameters() {
		// when
		context.getParameters();

		// then
		verify(httpServletRequest).getParameterMap();
	}

	@Test
	public void testGetCookiesNullCheck() {
		assertNull("Cookie must return null as we didn't set anything", context.getCookie("non existance cookie"));
	}

	@Test
	public void testGetCookiesEmptyCheck() {
		Integer zeroSize=0;
		assertEquals("Cookies initial size must be zero", zeroSize,
				(Integer) context.getCookies().size());
	}

	@Test
	public void testIsSyncDefaultCheck() {
		assertFalse("Async value must be null by default", context.isAsync());
	}

	@Test
	public void testGetFileItemIterator() throws Exception {
		// given
		setGetFileItemIterator();

		// when
		fileItemIterator = context.getFileItemIterator();

		// then
		assertNull("Method call must return null as Error occured while processing operation", fileItemIterator);

	}

	@Test
	public void testFinalizeHeaders() throws Exception {
		// given
		setFinalizeHeaders();
		when(result.getContentType()).thenReturn("type");

		// when
		context.finalizeHeaders(result, true);

		// then
		verify(httpServletResponse).setContentType((String) anyObject());
	}

	@Test
	public void testFinalizeHeadersWithoutContentType() throws Exception {
		// given
		setFinalizeHeaders();

		// when
		context.finalizeHeaders(result, true);

		// then
		verify(httpServletResponse).addHeader((String) anyObject(), (String) anyObject());
	}
	
	@Test
	public void testGetParameter() {
		// given
		when(httpServletRequest.getParameter("requestMethod")).thenReturn("POST");

		// when
		context.init(servletContext, httpServletRequest, httpServletResponse);

		// then
		assertEquals(null, context.getParameter("key_not_there"));
		assertEquals("defaultValue", context.getParameter("key_not_there", "defaultValue"));
		assertEquals("POST", context.getParameter("requestMethod"));
	}
}