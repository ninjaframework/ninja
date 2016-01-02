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

package ninja.servlet.async;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import javax.servlet.AsyncContext;
import javax.servlet.http.HttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import ninja.Context;
import ninja.Result;
import ninja.utils.ResultHandler;

public class Servlet3AsyncStrategyTest {

	private ResultHandler resultHandler;

	private HttpServletRequest request;

	private Servlet3AsyncStrategy servlet3AsyncStrategy;

	private Result result;

	private Context context;

	private AsyncContext asyncContext;

	@Before
	public void setUp() throws Exception {
		resultHandler = mock(ResultHandler.class);
		request = mock(HttpServletRequest.class);
		servlet3AsyncStrategy = new Servlet3AsyncStrategy(resultHandler, request);
	}

	@Test
	public void testControllerReturned() {
		assertNull("This method call must always returns null", servlet3AsyncStrategy.controllerReturned());
	}

	@Test
	public void testHandleSync() {
		// when
		servlet3AsyncStrategy.handleAsync();

		// then
		verify(request).startAsync();
	}

	@Test
	public void testReturnResultAsync() {
		// given
		setUpReturnResultAsync();

		// when
		servlet3AsyncStrategy.returnResultAsync(result, context);

		// then
		verify(resultHandler).handleResult(result, context);
		verify(asyncContext).complete();
	}

	private void setUpReturnResultAsync() {
		result = mock(Result.class);
		context = mock(Context.class);
		asyncContext = mock(AsyncContext.class);
		when(request.getAsyncContext()).thenReturn(asyncContext);
	}
}