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

package ninja.bodyparser;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ninja.ContentTypes;
import ninja.Context;
import ninja.params.ParamParser;
import ninja.params.ParamParsers;
import ninja.params.ParamParsers.ArrayParamParser;
import ninja.params.ParamParsers.ListParamParser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class BodyParserEnginePost implements BodyParserEngine {

    private final Logger logger = LoggerFactory.getLogger(BodyParserEnginePost.class);

    private final ParamParsers paramParsers;

    @Inject
    public BodyParserEnginePost(ParamParsers paramParsers) {
        this.paramParsers = paramParsers;
    }
    
    @Override
    public <T> T invoke(Context context, Class<T> classOfT) {
        // Grab parameters from context only once for efficiency
        Map<String, String[]> parameters = context.getParameters();
        
        return invoke(context, parameters, classOfT, "");
    }
    
    // Allows to instantiate inner objects with a prefix for each parameter key
    private <T> T invoke(Context context, Map<String, String[]> parameters, Class<T> classOfT, String paramPrefix) {
        
        T t = null;

        try {
            t = classOfT.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            logger.error("Can't create new instance of class {}", classOfT.getName(), e);
            return null;
        }

        for (String declaredField : getAllDeclaredFieldsAsStringSet(classOfT)) {

            try {

                Field field = classOfT.getDeclaredField(declaredField);
                Class<?> fieldType = field.getType();
                field.setAccessible(true);

                if (parameters.containsKey(paramPrefix + declaredField)) {
                    
                    String[] values = parameters.get(paramPrefix + declaredField);

                    if (Collection.class.isAssignableFrom(fieldType) || List.class.isAssignableFrom(fieldType)) {

                        ListParamParser<?> parser = (ListParamParser<?>) paramParsers.getListParser(getGenericType(field));
                        if (parser == null) {
                            logger.warn("No parser defined for a collection of type {}", getGenericType(field).getCanonicalName());
                        } else {
                            field.set(t, parser.parseParameter(field.getName(), values, context.getValidation()));
                        }

                    } else if (fieldType.isArray()) {

                        ArrayParamParser<?> parser = paramParsers.getArrayParser(fieldType);
                        if (parser == null) {
                            logger.warn("No parser defined for an array of type {}", fieldType.getComponentType().getCanonicalName());
                        } else {
                            field.set(t, parser.parseParameter(field.getName(), values, context.getValidation()));
                        }

                    } else {

                        ParamParser<?> parser = (ParamParser<?>) paramParsers.getParamParser(fieldType);
                        if (parser == null) {
                            logger.warn("No parser defined for type {}", fieldType.getCanonicalName());
                        } else {
                            field.set(t, parser.parseParameter(field.getName(), values[0], context.getValidation()));
                        }

                    }

                } else {
                    
                    // Check if we have one parameter key corresponding to one valued inner attribute of this object field
                    for (String parameter : parameters.keySet()) {
                        if(parameter.startsWith(paramPrefix + declaredField + ".")) {
                            if(isEmptyParameter(parameters.get(parameter))) {
                                field.set(t, invoke(context, parameters, fieldType, paramPrefix + declaredField + "."));
                                break;
                            }
                        }
                    }
                    
                }

            } catch (NoSuchFieldException 
                    | SecurityException 
                    | IllegalArgumentException 
                    | IllegalAccessException e) {

                logger.warn(
                        "Error parsing incoming Post request into class {}. Key {} and value {}.", 
                        classOfT.getName(), paramPrefix + declaredField, parameters.get(paramPrefix + declaredField), e);
            }

        }
        return t;
    }
    
    private boolean isEmptyParameter(String[] parameterValues) {
        if(parameterValues != null && parameterValues.length > 0) {
            for(String parameterValue : parameterValues) {
                if(parameterValue != null && !parameterValue.isEmpty()) {
                    return true;
                }
            }
        }
        return false;
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
