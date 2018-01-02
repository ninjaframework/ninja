/**
 * Copyright (C) 2012- the original author or authors.
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
 * Renderables can be returned inside a result.
 * 
 * Renderables are responsible for finalizing the headers before anything is
 * written to the output streams.
 * 
 * context.finalizeHeaders(result) is your friend.
 * 
 * It is not done automatically as you may want to change the status of the
 * response, the return type and so on...
 * 
 * 
 * @author rbauer
 * 
 */
public interface Renderable {

    void render(Context context, Result result);

}
