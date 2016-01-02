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

import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.whenNew;
import static org.mockito.Matchers.anyObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Injector;

import ninja.Result;
import ninja.Route;
import ninja.bodyparser.BodyParserEngine;
import ninja.bodyparser.BodyParserEngineManager;
import ninja.session.FlashScope;
import ninja.session.Session;
import ninja.utils.NinjaProperties;
import ninja.utils.ResultHandler;
import ninja.validation.Validation;

/**
 * This class holds the properties of NinjaServlet
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class NinaServletContextBase {

	@Mock
	protected HttpServletRequest httpServletRequest;

	@Mock
	protected FlashScope flashCookie;

	@Mock
	protected BodyParserEngineManager bodyParserEngineManager;

	protected NinjaServletContext context;

	@Mock
	protected NinjaProperties ninjaProperties;

	@Mock
	protected ResultHandler resultHandler;

	@Mock
	protected Session sessionCookie;

	@Mock
	protected Validation validation;

	@Mock
	protected Injector injector;

	@Mock
	protected ServletContext servletContext;

	@Mock
	protected HttpServletResponse httpServletResponse;

	@Mock
	protected BodyParserEngine bodyParserEngine;

	@Mock
	protected Route route;
	
	protected Result result;

	@Before
	public void setUp() {
		// default setup for httpServlet request.
		// According to servlet spec the following will be returned:
		when(httpServletRequest.getContextPath()).thenReturn("");
		when(httpServletRequest.getRequestURI()).thenReturn("/");
		context = new NinjaServletContext(bodyParserEngineManager, flashCookie, ninjaProperties, resultHandler,
				sessionCookie, validation, injector);
	}
	    
    protected Vector<String> getRequestHeaders(){
    	Vector<String> headers=new Vector<>();
    	headers.add("accepted-language");
    	return headers; 	
    }
    
    protected Vector<String> getAcceptedLanguages(){
    	Vector<String> languages=new Vector<>();
    	languages.add("en-US");
    	languages.add("fr-FR");
    	return languages; 	
    }
    
    protected void setUpGetAttributes(){
    	Vector<String> vec=new Vector<>();
    	vec.add("context path");
    	when(httpServletRequest.getAttributeNames()).thenReturn(vec.elements()); 
    	when(httpServletRequest.getAttribute("accepted-language")).thenReturn("en-US");
    }
    
    protected void setUpEnUsHeader(){
    	Vector  v=new Vector();
    	v.add("en-us");
    	when(httpServletRequest.getHeaders("accepted-language")).thenReturn(v.elements());
    }
    
    protected void setFinalizeHeaders(){
    	result=mock(Result.class);
    	Map<String,String> map=new HashMap<>();
    	map.put("accepted-language", "en-US");
    	when(result.getHeaders()).thenReturn(map);
    }
    
    protected void setGetFileItemIterator()throws Exception{
    	ServletFileUpload  upload=mock(ServletFileUpload.class);
    	whenNew(ServletFileUpload .class).withNoArguments().thenReturn(upload);
    	when(upload.getItemIterator((HttpServletRequest)anyObject())).thenThrow(new IOException ()); 
    }
}