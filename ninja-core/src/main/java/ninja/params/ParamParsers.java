/**
 * Copyright (C) 2012-2020 the original author or authors.
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

package ninja.params;

import java.lang.reflect.Array;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.LocalDateTime;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.util.UUID;

import ninja.validation.ConstraintViolation;
import ninja.validation.IsDate;
import ninja.validation.IsEnum;
import ninja.validation.IsFloat;
import ninja.validation.IsInteger;
import ninja.validation.IsUUID;
import ninja.validation.Validation;

/**
 * Built in parsers for parameters
 *
 * @author James Roper
 * @author Jonathan Lannoy
 */
@Singleton
public class ParamParsers {
    private static final Map<Class<?>, ParamParser<?>> PARAM_PARSERS =
            ImmutableMap.<Class<?>, ParamParser<?>>builder()
                    .put(Integer.class, new IntegerParamParser())
                    .put(int.class, new PrimitiveIntegerParamParser())
                    .put(Boolean.class, new BooleanParamParser())
                    .put(boolean.class, new PrimitiveBooleanParamParser())
                    .put(Long.class, new LongParamParser())
                    .put(long.class, new PrimitiveLongParamParser())
                    .put(Float.class, new FloatParamParser())
                    .put(float.class, new PrimitiveFloatParamParser())
                    .put(Double.class, new DoubleParamParser())
                    .put(double.class, new PrimitiveDoubleParamParser())
                    .put(String.class, new StringParamParser())
                    .put(Byte.class, new ByteParamParser())
                    .put(byte.class, new PrimitiveByteParamParser())
                    .put(Short.class, new ShortParamParser())
                    .put(short.class, new PrimitiveShortParamParser())
                    .put(Character.class, new CharacterParamParser())
                    .put(char.class, new PrimitiveCharacterParamParser())
                    .put(Date.class, new DateParamParser())
                    .put(UUID.class, new UUIDParamParser())
                    .build();

    private final Set<ParamParser> customParsers;

    @Inject
    public ParamParsers(Set<ParamParser> customParsers) {
        this.customParsers = customParsers;
    }
    
    public ParamParser<?> getParamParser(Class<?> targetType) {
        for(ParamParser parser : customParsers) {
            if(targetType.isAssignableFrom(parser.getParsedType())) {
                return parser;
            }
        }
        
        if (targetType.isArray()) {
            // check for array of registered types
            Class<?> componentType = targetType.getComponentType();
            ParamParser<?> componentParser = getParamParser(componentType);

            if (componentParser != null) {
                // return CSV parser
                return new CsvParamParser(targetType, componentParser);
            }
        }

        if (targetType.isEnum()) {
            return new GenericEnumParamParser((Class<Enum>) targetType);
        }

        return PARAM_PARSERS.get(targetType);
    }

    /**
     * Registering enums is not anymore needed, the EnumParser will handle all possible enum values, ignoring case.
     */
    @Deprecated
    public static <E extends Enum<E>> void unregisterEnum(final Class<E> enumClass) {
        // Not anymore used
    }

    /**
     * Registering enums is not anymore needed, the EnumParser will handle all possible enum values, ignoring case.
     */
    @Deprecated
    public static <E extends Enum<E>> void registerEnum(final Class<E> enumClass) {
        // Not anymore used 
    }

    /**
     * Registering enums is not anymore needed, the EnumParser will handle all possible enum values, ignoring case.
     */
    @Deprecated
    public static <E extends Enum<E>> void registerEnum(final Class<E> enumClass, final boolean caseSensitive) {
        // Not anymore used
    }

    public ArrayParamParser<?> getArrayParser(Class<?> targetType) {
        if (targetType.isArray()) {
            // check for array of registered types
            Class<?> componentType = targetType.getComponentType();
            ParamParser<?> componentParser = getParamParser(componentType);

            if (componentParser != null) {
                // return multi-valued parameter parser
                return new ArrayParamParser(targetType, componentParser);
            }
        }

        return null;
    }
    
    public ListParamParser<?> getListParser(Class<?> targetInnerType) {
        ParamParser<?> componentParser = getParamParser(targetInnerType);

        if (componentParser != null) {
            // return multi-valued parameter parser
            return new ListParamParser(targetInnerType, componentParser);
        }

        return null;
    }

    public static class PrimitiveIntegerParamParser implements ParamParser<Integer> {
        @Override
        public Integer parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return 0;
            } else {
                try {
                    return Integer.parseInt(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addViolation(new ConstraintViolation(
                            IsInteger.KEY, field, IsInteger.MESSAGE, parameterValue));
                    return 0;
                }
            }
        }

        @Override
        public Class<Integer> getParsedType() {
            return Integer.class;
        }
    }

    public static class IntegerParamParser implements ParamParser<Integer> {
        @Override
        public Integer parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return null;
            } else {
                try {
                    return Integer.parseInt(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addViolation(new ConstraintViolation(
                            IsInteger.KEY, field, IsInteger.MESSAGE, parameterValue));
                    return null;
                }
            }
        }

        @Override
        public Class<Integer> getParsedType() {
            return Integer.class;
        }
    }

    public static class BooleanParamParser implements ParamParser<Boolean> {
        @Override
        public Boolean parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return null;
            } else {
                return parseBoolean(parameterValue);
            }
        }

        @Override
        public Class<Boolean> getParsedType() {
            return Boolean.class;
        }
        
        private Boolean parseBoolean(String value) {
            if (value == null) {
                return null;
            } else if (value.equalsIgnoreCase("true")) {
                return Boolean.TRUE;
            } else if (value.equalsIgnoreCase("false")) {
                return Boolean.FALSE;
            } else {
                return null;
            }
        }    
    }

    public static class PrimitiveBooleanParamParser implements ParamParser<Boolean> {
        @Override
        public Boolean parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return false;
            } else {
                return Boolean.parseBoolean(parameterValue);
            }
        }

        @Override
        public Class<Boolean> getParsedType() {
            return Boolean.class;
        }
    }

    public static class LongParamParser implements ParamParser<Long> {
        @Override
        public Long parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return null;
            } else {
                try {
                    return Long.parseLong(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addViolation(new ConstraintViolation(
                            IsInteger.KEY, field, IsInteger.MESSAGE, parameterValue));
                    return null;
                }
            }
        }

        @Override
        public Class<Long> getParsedType() {
            return Long.class;
        }
    }

    public static class PrimitiveLongParamParser implements ParamParser<Long> {
        @Override
        public Long parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return 0L;
            } else {
                try {
                    return Long.parseLong(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addViolation(new ConstraintViolation(
                            IsInteger.KEY, field, IsInteger.MESSAGE, parameterValue));
                    return 0L;
                }
            }
        }

        @Override
        public Class<Long> getParsedType() {
            return Long.class;
        }
    }

    public static class FloatParamParser implements ParamParser<Float> {
        @Override
        public Float parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return null;
            } else {
                try {
                    return Float.parseFloat(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addViolation(new ConstraintViolation(
                            IsFloat.KEY, field, IsFloat.MESSAGE, parameterValue));
                    return null;
                }
            }
        }

        @Override
        public Class<Float> getParsedType() {
            return Float.class;
        }
    }

    public static class PrimitiveFloatParamParser implements ParamParser<Float> {
        @Override
        public Float parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return 0f;
            } else {
                try {
                    return Float.parseFloat(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addViolation(new ConstraintViolation(
                            IsFloat.KEY, field, IsFloat.MESSAGE, parameterValue));
                    return 0f;
                }
            }
        }

        @Override
        public Class<Float> getParsedType() {
            return Float.class;
        }
    }

    public static class DoubleParamParser implements ParamParser<Double> {
        @Override
        public Double parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return null;
            } else {
                try {
                    return Double.parseDouble(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addViolation(new ConstraintViolation(
                            IsFloat.KEY, field, IsFloat.MESSAGE, parameterValue));
                    return null;
                }
            }
        }

        @Override
        public Class<Double> getParsedType() {
            return Double.class;
        }
    }

    public static class PrimitiveDoubleParamParser implements ParamParser<Double> {
        @Override
        public Double parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return 0d;
            } else {
                try {
                    return Double.parseDouble(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addViolation(new ConstraintViolation(
                            IsFloat.KEY, field, IsFloat.MESSAGE, parameterValue));
                    return 0d;
                }
            }
        }

        @Override
        public Class<Double> getParsedType() {
            return Double.class;
        }
    }

    public static class StringParamParser implements ParamParser<String> {
        @Override
        public String parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return null;
            } else {
                return parameterValue;
            }
        }

        @Override
        public Class<String> getParsedType() {
            return String.class;
        }
    }
    
    public static class EmptyStringParamParser implements ParamParser<String> {
        @Override
        public String parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || validation.hasViolation(field)) {
                return null;
            } else {
                return parameterValue;
            }
        }

        @Override
        public Class<String> getParsedType() {
            return String.class;
        }
    }

    public static class ByteParamParser implements ParamParser<Byte> {
        @Override
        public Byte parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return null;
            } else {
                try {
                    return Byte.parseByte(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addViolation(new ConstraintViolation(
                            IsInteger.KEY, field, IsInteger.MESSAGE, parameterValue));
                    return null;
                }
            }
        }

        @Override
        public Class<Byte> getParsedType() {
            return Byte.class;
        }
    }

    public static class PrimitiveByteParamParser implements ParamParser<Byte> {
        @Override
        public Byte parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return 0;
            } else {
                try {
                    return Byte.parseByte(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addViolation(new ConstraintViolation(
                            IsInteger.KEY, field, IsInteger.MESSAGE, parameterValue));
                    return 0;
                }
            }
        }

        @Override
        public Class<Byte> getParsedType() {
            return Byte.class;
        }
    }

    public static class ShortParamParser implements ParamParser<Short> {
        @Override
        public Short parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return null;
            } else {
                try {
                    return Short.parseShort(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addViolation(new ConstraintViolation(
                            IsInteger.KEY, field, IsInteger.MESSAGE, parameterValue));
                    return null;
                }
            }
        }

        @Override
        public Class<Short> getParsedType() {
            return Short.class;
        }
    }

    public static class PrimitiveShortParamParser implements ParamParser<Short> {
        @Override
        public Short parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return 0;
            } else {
                try {
                    return Short.parseShort(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addViolation(new ConstraintViolation(
                            IsInteger.KEY, field, IsInteger.MESSAGE, parameterValue));
                    return 0;
                }
            }
        }

        @Override
        public Class<Short> getParsedType() {
            return Short.class;
        }
    }

    public static class CharacterParamParser implements ParamParser<Character> {
        @Override
        public Character parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return null;
            } else {
                return parameterValue.charAt(0);
            }
        }

        @Override
        public Class<Character> getParsedType() {
            return Character.class;
        }
    }

    public static class PrimitiveCharacterParamParser implements ParamParser<Character> {
        @Override
        public Character parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return '\0';
            } else {
                return parameterValue.charAt(0);
            }
        }

        @Override
        public Class<Character> getParsedType() {
            return Character.class;
        }
    }

    public static class UUIDParamParser implements ParamParser<UUID> {
        @Override
        public UUID parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return null;
            } else {
                try {
                    return UUID.fromString(parameterValue);
                } catch (IllegalArgumentException e) {
                    validation.addViolation(new ConstraintViolation(
                            IsUUID.KEY, field, IsUUID.MESSAGE, parameterValue));
                    return null;
                }
            }
        }

        @Override
        public Class<UUID> getParsedType() {
            return UUID.class;
        }
    }
    
    public static class GenericEnumParamParser<E extends Enum<E>> implements ParamParser<E> {

        private Class<E> targetType;
        
        public GenericEnumParamParser(Class<E> targetType) {
            this.targetType = targetType;
        }

        @Override
        public E parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return null;
            } else {
                // Equals ignore case will keep backward compatibility                
                for (E value : getParsedType().getEnumConstants()) {
                    if (value.name().equalsIgnoreCase(parameterValue)) {
                        return value;
                    }
                }

                validation.addViolation(new ConstraintViolation(
                        IsEnum.KEY, field, IsEnum.MESSAGE, new Object[] {parameterValue, getParsedType().getName()}));
                return null;
            }
        }

        @Override
        public Class<E> getParsedType() {
            return targetType;
        }
    }
    
    public static class DateParamParser implements ParamParser<Date> {
        @Override
        public Date parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return null;
            } else {
                try {
                    return new LocalDateTime(parameterValue).toDate();
                } catch (IllegalArgumentException e) {
                    validation.addViolation(new ConstraintViolation(
                            IsDate.KEY, field, IsDate.MESSAGE, parameterValue));
                    return null;
                }
            }
        }

        @Override
        public Class<Date> getParsedType() {
            return Date.class;
        }
    }

    /**
     * Parses a single string value as a CSV array of registered types.
     */
    public static class CsvParamParser<T> implements ParamParser<T[]> {

        private final Class<T[]> arrayType;
        private final ParamParser<T> itemParser;

        public CsvParamParser(Class<T[]> arrayType, ParamParser<T> parser) {
            this.arrayType = arrayType;
            this.itemParser = parser;
        }

        @Override
        public T[] parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasViolation(field)) {
                return null;
            } else {
                // split the string value as a csv
                String [] values = parameterValue.split(",(?=([^\"]*\"[^\"]*\")*[^\"]*$)");

                // parse the individual values as the target item type
                Class<T> itemType = (Class<T>) arrayType.getComponentType();
                T[] array = (T[]) Array.newInstance(itemType, values.length);
                for (int i = 0; i < values.length; i++) {
                    T t = itemParser.parseParameter(field, values[i], validation);
                    Array.set(array, i, t);
                }

                if (validation.hasViolation(field)) {
                    return null;
                }

                return array;
            }
        }

        @Override
        public Class<T[]> getParsedType() {
            return arrayType;
        }
    }

    /**
     * Parses a multi-valued parameter as an array of registered types.
     */
    public static class ArrayParamParser<T> {

        private final Class<T[]> arrayType;
        private final ParamParser<T> itemParser;

        public ArrayParamParser(Class<T[]> arrayType, ParamParser<T> parser) {
            this.arrayType = arrayType;
            this.itemParser = parser;
        }

        public T[] parseParameter(String field, String[] parameterValues, Validation validation) {
            if (parameterValues == null || validation.hasViolation(field)) {
                return null;
            } else {
                // parse the individual values as the target item type
                Class<T> itemType = getItemType();
                
                T[] array;
                try {
                    array = (T[]) Array.newInstance(itemType, parameterValues.length);
                } catch(ClassCastException e) {
                    return null;
                }
                
                for (int i = 0; i < parameterValues.length; i++) {
                    T t = itemParser.parseParameter(field, parameterValues[i], validation);
                    Array.set(array, i, t);
                }

                if (validation.hasViolation(field)) {
                    return null;
                }

                return array;
            }
        }

        public Class<T[]> getArrayType() {
            return arrayType;
        }

        public Class<T> getItemType() {
            return (Class<T>) arrayType.getComponentType();
        }
    }
    
    public static class ListParamParser<T> {

        private final Class<T> itemType;
        private final ParamParser<T> itemParser;

        public ListParamParser(Class<T> itemType, ParamParser<T> parser) {
            this.itemType = itemType;
            this.itemParser = parser;
        }

        public List<T> parseParameter(String field, String[] parameterValues, Validation validation) {
            if (parameterValues == null || validation.hasViolation(field)) {
                return null;
            } else {
                List<T> list = Lists.newArrayList();
                for (int i = 0; i < parameterValues.length; i++) {
                    list.add(itemParser.parseParameter(field, parameterValues[i], validation));
                }

                if (validation.hasViolation(field)) {
                    return null;
                }

                return list;
            }
        }

        public Class<T> getItemType() {
            return itemType;
        }
    }

}
