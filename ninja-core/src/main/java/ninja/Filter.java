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

package ninja;

/**
 * A simple filter that can be applied to controller methods or while classes.
 * 
 * usually you use <code>@FilterWith(MyFilter.class)</code> where MyFilter.class
 * is implementing this interface.
 * 
 * <code>@FilterWith</code> works also with multiple filter
 * <code>@FilterWith({MyFirstFilter.class, MySecondFilter.class})</code>
 * 
 * @author ra
 * 
 */
public interface Filter {
    /**
     * Filter the request. Filters should invoke the filterChain.nextFilter()
     * method if they wish the request to proceed.
     * 
     * @param filterChain
     *            The filter chain
     * @param context
     *            The context
     */
    Result filter(FilterChain filterChain, Context context);
}
