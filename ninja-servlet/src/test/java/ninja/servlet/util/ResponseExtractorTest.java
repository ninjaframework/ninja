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

package ninja.servlet.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;
import ninja.Context;
import ninja.params.ArgumentExtractor;
import ninja.servlet.NinjaServletContext;

public class ResponseExtractorTest {
	
	private ArgumentExtractor<HttpServletResponse> responseExtractor;
	
	private Context context;
	
	private NinjaServletContext ninjaServletContext;
	
	@Before
	public void setUp() throws Exception {
		responseExtractor=new ResponseExtractor();
	}

	@Test
	public void testGetFieldName() {
		assertNull("This method call must always returns null", responseExtractor.getFieldName());
	}
	
	@Test(expected=RuntimeException.class)
	public void testExtractException() {
		//given
		context=mock(Context.class);
		
		//when
		responseExtractor.extract(context);
		
		//then
		fail("A RuntimeException must have occured by now as context is not of type NinjaServletContext");
	}
	
	@Test
	public void testExtract() {
		//given
		ninjaServletContext=mock(NinjaServletContext.class);
		
		//when
		responseExtractor.extract(ninjaServletContext);
		
		//then
		verify(ninjaServletContext).getHttpServletResponse();
	}
	
	@Test
	public void testGetExtractedType(){
		assertEquals("Extracted Type must be equal to servlet response type", HttpServletResponse.class, 
				responseExtractor.getExtractedType());
	}	
}