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

package ninja.bodyparser;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Set;

import ninja.ContentTypes;
import ninja.Context;
import ninja.utils.SwissKnife;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.google.inject.Singleton;

@Singleton
public class BodyParserEnginePost implements BodyParserEngine {

    private final Logger logger = LoggerFactory.getLogger(BodyParserEnginePost.class);

    @Override
    public <T> T invoke(Context context, Class<T> classOfT) {
        
        T t = null;

        try {
            t = classOfT.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Can't create new instance of class {}", classOfT.getName(), e);
            return null;
        }

        Set<String> declaredFields = getAllDeclaredFieldsAsStringSet(classOfT);

        for (Entry<String, String[]> ent : context.getParameters().entrySet()) {

            if (declaredFields.contains(ent.getKey())) {

                try {

                    Field field = classOfT.getDeclaredField(ent.getKey());
                    Class<?> fieldType = field.getType();
                    field.setAccessible(true);

                    String[] values = ent.getValue();
                    Object convertedValue;

                    // convert the values based on the field type,
                    // supported types are collections, arrays and boxed primities

                    if (Collection.class.isAssignableFrom(fieldType)) {
                        convertedValue = SwissKnife.convertCollection(values, getGenericType(field));
                    } else if (fieldType.isArray()) {
                        convertedValue = SwissKnife.convertArray(values, fieldType.getComponentType());
                    } else {
                        convertedValue = SwissKnife.convert(values[0], fieldType);
                    }

                    if (convertedValue != null) {
                        field.set(t, convertedValue);
                    }

                } catch (NoSuchFieldException 
                        | SecurityException 
                        | IllegalArgumentException 
                        | IllegalAccessException e) {

                    logger.warn(
                            "Error parsing incoming Post request into class {}. Key {} and value {}.", 
                            classOfT.getName(), ent.getKey(), ent.getValue(), e);
                }

            }

        }
        return t;
    }

    public String getContentType() {
        return ContentTypes.APPLICATION_POST_FORM;
    }

    private <T> Set<String> getAllDeclaredFieldsAsStringSet(Class<T> clazz) {

        Set<String> declaredFields = Sets.newHashSet();

        for (Field field : clazz.getDeclaredFields()) {
            declaredFields.add(field.getName());
        }

        return declaredFields;

    }

    private Class<?> getGenericType(Field field) {
        ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        return (Class<?>) genericType.getActualTypeArguments()[0];
    }
}
