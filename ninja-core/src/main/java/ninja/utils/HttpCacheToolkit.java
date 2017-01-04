/**
 * Copyright (C) 2012-2016 the original author or authors.
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

import java.util.Optional;

import ninja.Context;
import ninja.Result;

import com.google.inject.ImplementedBy;

@ImplementedBy(HttpCacheToolkitImpl.class)
public interface HttpCacheToolkit {

    /**
     * Checks if resource has been modified.
     * Checks via etag or lastModified when etag not present.
     * 
     * @param etag - may be absent
     * @param lastModified - may be absent
     * @param context the Context of this request
     * @return true if modified / false if not.
     */
    boolean isModified(Optional<String> etag, Optional<Long> lastModified, Context context);

    /**
     * Adds etag to result.
     * 
     * @param context The context
     * @param result The result to populate with etag
     * @param lastModified Last modified => In that case used to generate etag.
     */
    void addEtag(Context context, Result result, Long lastModified);
    
}
