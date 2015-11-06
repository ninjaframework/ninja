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

package ninja.utils;

import com.google.common.base.CaseFormat;
import com.google.common.primitives.Primitives;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.joda.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * A helper class that contains a lot of random stuff that helps to get things
 * done.
 *
 * @author ra
 *
 */
public class SwissKnife {

    private static final Logger logger = LoggerFactory.getLogger(SwissKnife.class);

    private static final Map<String, Method> CONVERTERS = new HashMap<>();

    static {
        Method[] methods = Converter.class.getDeclaredMethods();
        for (Method method : methods) {
            if (method.getParameterTypes().length == 1
                    && method.getParameterTypes()[0] == String.class) {
                
                CONVERTERS.put(method.getReturnType().getName(), method);
            }
        }
    }

    /**
     * This is important: We load stuff as UTF-8.
     *
     * We are using in the default Apache Commons loading mechanism.
     *
     * With two little tweaks: 1. We don't accept any delimimter by default 2.
     * We are reading in UTF-8
     *
     * More about that:
     * http://commons.apache.org/configuration/userguide/howto_filebased
     * .html#Loading
     *
     * From the docs: - If the combination from base path and file name is a
     * full URL that points to an existing file, this URL will be used to load
     * the file. - If the combination from base path and file name is an
     * absolute file name and this file exists, it will be loaded. - If the
     * combination from base path and file name is a relative file path that
     * points to an existing file, this file will be loaded. - If a file with
     * the specified name exists in the user's home directory, this file will be
     * loaded. - Otherwise the file name is interpreted as a resource name, and
     * it is checked whether the data file can be loaded from the classpath.
     *
     * @param fileOrUrlOrClasspathUrl Location of the file. Can be on file
     * system, or on the classpath. Will both work.
     * @return A PropertiesConfiguration or null if there were problems getting
     * it.
     */
    public static PropertiesConfiguration loadConfigurationInUtf8(String fileOrUrlOrClasspathUrl) {

        PropertiesConfiguration propertiesConfiguration = new PropertiesConfiguration();
        propertiesConfiguration.setEncoding(NinjaConstant.UTF_8);
        propertiesConfiguration.setDelimiterParsingDisabled(true);
        propertiesConfiguration.setFileName(fileOrUrlOrClasspathUrl);
        propertiesConfiguration.getLayout().setSingleLine(NinjaConstant.applicationSecret, true);

        try {

            propertiesConfiguration.load(fileOrUrlOrClasspathUrl);

        } catch (ConfigurationException e) {

            logger.info("Could not load file " + fileOrUrlOrClasspathUrl
                    + " (not a bad thing necessarily, but I am returing null)", e);

            return null;
        }

        return propertiesConfiguration;
    }

    /**
     * Returns the lower class name. Eg. A class named MyObject will become
     * "myObject".
     *
     * @param object Object for which to return the lowerCamelCaseName
     * @return the lowerCamelCaseName of the Object
     */
    public static String getRealClassNameLowerCamelCase(Object object) {

        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, object.getClass().getSimpleName());

    }

    /**
     * Convert value to a collection type
     *
     * @param from string values
     * @param to collection type of the class
     * @return collection class type value or null if something goes wrong
     */
    public static <T> Collection<T> convertCollection(String[] from, Class<T> to) {
        Collection<T> result = new ArrayList<>();
        for (String value : from) {
            result.add(convert(value, to));
        }
        return result;
    }

    /**
     * Convert value to an array of type
     *
     * @param from string values
     * @param to type of array
     * @return array of values or null if something goes wrong
     */
    @SuppressWarnings("unchecked")
    public static <T> T[] convertArray(String[] from, Class<T> to) {
        T[] result;
        try {
            result = (T[]) Array.newInstance(to, from.length);
        } catch (ClassCastException e) {
            logger.trace(e.getMessage(), e);
            return null;
        }
        for (int i = 0; i < from.length; i++) {
            result[i] = convert(from[i], to);
        }
        return result;
    }

    /**
     * Convert value to class type value.
     * 
     * If something goes wrong it returns null.
     *
     * @param from string value
     * @param to type of the class
     * @return class type value or null if something goes wrong.
     */
    public static <T> T convert(String from, Class<T> to) {
       
        Class<T> toAsNonPrimitiveType;
        
        if (from == null) {
            return null;
        }
                      
        T t = null;
         
        toAsNonPrimitiveType = Primitives.wrap(to);

        if (toAsNonPrimitiveType.isAssignableFrom(from.getClass())) {
            return toAsNonPrimitiveType.cast(from);
        }

        Method converter = CONVERTERS.get(toAsNonPrimitiveType.getName());
        
        if (converter == null) {
            
            logger.error(
                    "No converter found to convert {}. "
                    + "Returning null. "
                    + "You may want to extend the class.", toAsNonPrimitiveType);
            
        } else {
            
            try {

                t = toAsNonPrimitiveType.cast(converter.invoke(toAsNonPrimitiveType, from));
                
            } catch (IllegalAccessException 
                    | IllegalArgumentException 
                    | InvocationTargetException ex) {

                logger.error(
                        "Cannot convert from "
                        + from.getClass().getName() + " to " + toAsNonPrimitiveType.getName()
                        + ". Conversion failed with " + ex.getMessage(), ex);
            }

        }
        
         return t;

    }

    private static class Converter {

        public static Integer toInteger(String value) {
            return Integer.valueOf(value);
        }

        public static Long toLong(String value) {
            return Long.valueOf(value);
        }

        public static Float toFloat(String value) {
            return Float.valueOf(value);
        }

        public static Double toDouble(String value) {
            return Double.valueOf(value);
        }

        public static Boolean toBoolean(String value) {
            return Boolean.valueOf(value);
        }

        public static Byte toByte(String value) {
            return Byte.valueOf(value);
        }

        public static Short toShort(String value) {
            return Short.valueOf(value);
        }

        public static Date toDate(String value) {
            if (value != null && value.length() > 0) {
              return new LocalDateTime(value).toDate();
            } else {
                return null;
            }
        }

        public static Character toCharacter(String value) {

            if (value.length() > 0) {
                return value.charAt(0);
            } else {
                return null;
            }

        }
    }

}
