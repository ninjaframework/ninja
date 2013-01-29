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

package ninja.bodyparser;

import java.util.Map.Entry;

import ninja.Context;
import ognl.Ognl;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BodyParserEnginePost implements BodyParserEngine {

    private Logger logger;

    @Inject
    public BodyParserEnginePost(Logger logger) {
        this.logger = logger;

    }

    @Override
    public <T> T invoke(Context context, Class<T> classOfT) {
        T t = null;

        try {
            t = classOfT.newInstance();
        } catch (Exception e) {
            logger.error("can't newInstance class " + classOfT.getName(), e);
            return null;
        }
        for (Entry<String, String[]> ent : context.getParameters().entrySet()) {
            try {               
                Ognl.setValue(ent.getKey(), t, ent.getValue());
            } catch (Exception e) {
                logger.warn(
                        "Error parsing incoming Post for key " + ent.getKey()
                                + " and value " + ent.getValue(), e);
            }
        }
        return t;
    }

}
