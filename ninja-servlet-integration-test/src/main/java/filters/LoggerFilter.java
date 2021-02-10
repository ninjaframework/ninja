/**
 * Copyright (C) the original author or authors.
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

package filters;

import ninja.Context;
import ninja.Filter;
import ninja.FilterChain;
import ninja.Result;

import org.slf4j.Logger;

import com.google.inject.Inject;

/**
 * Just a simple demo filter that changes exemplifies two things 1. Change the
 * output of the response 2. Change the status code. 3. Stops execution of all
 * other filters and the route method itself.
 * 
 * We are simply using 418 I'm a teapot (RFC 2324) .
 * 
 * @author ra
 * 
 */
public class LoggerFilter implements Filter {
	
	private final Logger logger;

	@Inject
	public LoggerFilter(Logger logger) {
		this.logger = logger;
		
	}

	@Override
	public Result filter(FilterChain chain, Context context) {

		logger.info("Got request from : " + context.getRequestPath());
        return chain.next(context);
	}

}
