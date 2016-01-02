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

import static org.powermock.api.mockito.PowerMockito.mock;
import static org.mockito.Mockito.verify;
import javax.servlet.http.HttpServletResponse;
import org.junit.Before;
import org.junit.Test;

public class ResponseStreamsServletTest {
	
	private ResponseStreamsServlet responseStreamServlet;
	
	private HttpServletResponse httpServletResponse;

	@Before
	public void setUp() throws Exception {
		httpServletResponse=mock(HttpServletResponse.class);
		responseStreamServlet=new ResponseStreamsServlet();
	}

	@Test
	public void testGetOutputStream()throws Exception {
		//given
		responseStreamServlet.init(httpServletResponse);
		
		//when
		responseStreamServlet.getOutputStream();
		
		//then
		verify(httpServletResponse).getOutputStream();		
	}
	
	@Test
	public void testGetWriter()throws Exception {
		//given
		responseStreamServlet.init(httpServletResponse);
		
		//when
		responseStreamServlet.getWriter();
		
		//then
		verify(httpServletResponse).getWriter();		
	}
}