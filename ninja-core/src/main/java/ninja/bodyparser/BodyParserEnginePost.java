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

package ninja.bodyparser;

import java.lang.reflect.Field;
import java.util.Map.Entry;

import ninja.ContentTypes;
import ninja.Context;

import org.slf4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BodyParserEnginePost implements BodyParserEngine {

    private final Logger logger;

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
                Field field = classOfT.getDeclaredField(ent.getKey());
                field.setAccessible(true);
                
                Class<?> fieldType = field.getType();
                String value = ent.getValue()[0];
                
                if (fieldType == int.class) {
                    field.setInt(t, Integer.parseInt(value));
                    
                } else if (fieldType == Integer.class) {
                    field.set(t, Integer.valueOf(value));
                    
                } else if (fieldType == long.class) {
                    field.setLong(t, Long.parseLong(value));
                    
                } else if (fieldType == Long.class) {
                    field.set(t, Long.valueOf(value));
                    
                } else if (fieldType == float.class) {
                    field.setFloat(t, Float.parseFloat(value));
                    
                } else if (fieldType == Float.class) {
                    field.set(t, Float.valueOf(value));
                    
                } else if (fieldType == double.class) {
                    field.setDouble(t, Double.parseDouble(value));
                    
                } else if (fieldType == Double.class) {
                    field.set(t, Double.valueOf(value));
                    
                } else if (fieldType == boolean.class) {
                    field.setBoolean(t, Boolean.parseBoolean(value));
                    
                } else if (fieldType == Boolean.class) {
                    field.set(t, Boolean.valueOf(value));
                    
                } else if (fieldType == byte.class) {
                    field.setByte(t, Byte.parseByte(value));
                    
                } else if (fieldType == Byte.class) {
                    field.set(t, Byte.valueOf(value));
                    
                } else if (fieldType == short.class) {
                    field.setShort(t, Short.parseShort(value));
                    
                } else if (fieldType == Short.class) {
                    field.set(t, Short.valueOf(value));
                    
                } else if (fieldType == char.class) {
                    field.setChar(t, value.charAt(0));
                    
                } else if (fieldType == Character.class) {
                    field.set(t, Character.valueOf(value.charAt(0)));
                    
                } else {
                    field.set(t, value);
                }
                
            } catch (Exception e) {
                logger.warn(
                        "Error parsing incoming Post for key " + ent.getKey()
                                + " and value " + ent.getValue(), e);
            }
        }
        return t;
    }
    
    public String getContentType() {
        return ContentTypes.APPLICATION_POST_FORM; 
    }
}
