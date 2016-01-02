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

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;
import javax.servlet.http.HttpServletRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.modules.junit4.PowerMockRunner;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertNotNull;
import ninja.utils.ResultHandler;

@RunWith(PowerMockRunner.class)
public class AsyncStrategyFactoryHolderTest {

	private HttpServletRequest request;

	private AsyncStrategyFactory asyncStrategyFactory;

	private ResultHandler resultHandler;

	private AsyncStrategy asyncStrategy;

	@Test
	public void testServlet3AsyncStrategy() {
		// given
		setUpAsyncStrategyFactoryForServlet3();

		// when
		asyncStrategy = asyncStrategyFactory.createStrategy(request, resultHandler);

		// then
		assertNotNull("AsyncStrategy shouldn't be null after object creation",asyncStrategy);
		assertThat("AsyncStrategy must be of type Servlet3AsyncStrategy as  isSupported value is true",
				asyncStrategy.getClass(), is(instanceOf(Servlet3AsyncStrategy.class.getClass())));
	}

	@Test
	public void testSingletonServlet3AsyncStrategy() {
		// given
		request = mock(HttpServletRequest.class);
		when(request.isAsyncSupported()).thenReturn(true);

		// when
		asyncStrategyFactory = AsyncStrategyFactoryHolder.getInstance(request);

		// then
		assertEquals("Factory must return same singleton instance", asyncStrategyFactory,
				AsyncStrategyFactoryHolder.getInstance(request));
	}

	@Test
	public void testBlockingAsyncStrategy() {
		// given
		setUpAsyncStrategyFactoryForBlockAsyncStrategy();

		// when
		asyncStrategy = asyncStrategyFactory.createStrategy(request, resultHandler);

		// then
		assertThat("AsyncStrategy must be of type BlockingAsyncStrategy as  isSupported value is false",
				asyncStrategy.getClass(), is(instanceOf(BlockingAsyncStrategy.class.getClass())));
	}

	private void setUpAsyncStrategyFactoryForBlockAsyncStrategy() {
		request = mock(HttpServletRequest.class);
		when(request.isAsyncSupported()).thenThrow(new LinkageError());
		asyncStrategyFactory = AsyncStrategyFactoryHolder.getInstance(request);
	}
	
	private void setUpAsyncStrategyFactoryForServlet3() {
		request = mock(HttpServletRequest.class);
		when(request.isAsyncSupported()).thenReturn(true);
		asyncStrategyFactory = AsyncStrategyFactoryHolder.getInstance(request);
	}
}