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

import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;
import ninja.Context;
import ninja.Result;

public class BlockingAsyncStrategyTest {
	
	private BlockingAsyncStrategy blockingAsyncStrategy;
	
	private Result result;
	
	private Context context;

	@Before
	public void setUp() throws Exception {
		blockingAsyncStrategy=new BlockingAsyncStrategy();
	}

	@Test
	public void testBlockingAsyncStrategy() {
		//given
		blockingAsyncStrategy.returnResultAsync(result, context);
		
		//when
		Result controllerResult =blockingAsyncStrategy.controllerReturned();
		
		//then
		assertEquals("Results must be equal", controllerResult, result);
	}
	
	@Test
	public void testHandleAsync(){
		//it is a no op method in actual code so cant verify anything
		//So just for code coverage we are calling this method.
		//when
		blockingAsyncStrategy.handleAsync();
	}
}