/**
 * Copyright (C) 2012 the original author or authors.
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
 * The main entry class for the framework.
 * 
 * The context contains the context to handle.
 * 
 * 
 * @author ra
 *
 */
public interface Ninja {

	/**
	 * Please ninja framwork - handle this context...
	 * @param context
	 */
	void invoke(ContextImpl context);

    /**
     * Start the Ninja Framework
     */
    void start();

    /**
     * Stop the Ninja Framework
     */
    void shutdown();
}