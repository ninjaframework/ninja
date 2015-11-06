/**
 * Copyright (C) 2012-2015 the original author or authors.
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

package ninja.cache;

import java.util.Map;

/**
 * Interface hiding cache implementation.
 * 
 * Inject that interface into the methods where you want to use it and you are
 * ready to go.
 * 
 * <code>@Inject Cache cache</code>.
 * 
 * Heavily inspired by excellent Play! 1.2.5 implementation.
 * 
 */
public interface Cache {

    public void add(String key, Object value, int expiration);

    public boolean safeAdd(String key, Object value, int expiration);

    public void set(String key, Object value, int expiration);

    public boolean safeSet(String key, Object value, int expiration);

    public void replace(String key, Object value, int expiration);

    public boolean safeReplace(String key, Object value, int expiration);

    public Object get(String key);

    public Map<String, Object> get(String[] keys);

    public long incr(String key, int by);

    public long decr(String key, int by);

    public void clear();

    public void delete(String key);

    public boolean safeDelete(String key);

}
