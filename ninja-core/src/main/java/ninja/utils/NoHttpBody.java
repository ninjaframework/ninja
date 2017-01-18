/**
 * Copyright (C) 2012-2017 the original author or authors.
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

package ninja.utils;

/**
 * This is a marker class used to handle Results in {@link ResultHandler}.
 * 
 * It causes the ResultHandler to render no body, just the header. Useful
 * when issuing a redirect and no corresponding content should be shown.
 * 
 * You might want to use Result.NO_HTTP_BODY as static shortcut.
 * 
 * @author ra
 *
 */
public class NoHttpBody {
    // intentionally left empty. Just a marker class.
}
