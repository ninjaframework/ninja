/**
 * Copyright (C) 2013 the original author or authors.
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
 * Yet another Http Header Constants file.
 * http://en.wikipedia.org/wiki/List_of_HTTP_header_fields
 */
public interface HttpHeaderConstants {

    String IF_NONE_MATCH = "If-None-Match";

    String IF_MODIFIED_SINCE = "If-Modified-Since";

    String CACHE_CONTROL = "Cache-Control";

    String ETAG = "ETag";

    String LAST_MODIFIED = "Last-Modified";

}
