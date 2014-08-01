/**
 * Copyright (C) 2012-2014 the original author or authors.
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
import java.util.Map;

import ninja.validation.ConstraintViolation;
import ninja.validation.IsEnum;
import ninja.validation.IsFloat;
import ninja.validation.IsInteger;
import ninja.validation.Validation;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Built in parsers for parameters
 *
 * @author James Roper
 */
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
                    .build();

    private static final Map<Class<? extends Enum<?>>, ParamParser<?>> ENUM_PARSERS = Maps.newHashMap();

    public static ParamParser<?> getParamParser(Class<?> targetType) {

        if (targetType.isArray()) {
            // check for array of registered types
            Class<?> componentType = targetType.getComponentType();
            ParamParser<?> componentParser = getParamParser(componentType);

            if (componentParser != null) {
                // return CSV parser
                return new CsvParamParser(targetType, componentParser);
            }
        }

        if (ENUM_PARSERS.containsKey(targetType)) {
            return ENUM_PARSERS.get(targetType);
        }

        return PARAM_PARSERS.get(targetType);
    }

    public static <E extends Enum<E>> void unregisterEnum(final Class<E> enumClass) {
        ENUM_PARSERS.remove(enumClass);
    }

    public static <E extends Enum<E>> void registerEnum(final Class<E> enumClass) {
        registerEnum(enumClass, true);
    }

    public static <E extends Enum<E>> void registerEnum(final Class<E> enumClass, final boolean caseSensitive) {
        EnumParamParser<E> parser = new EnumParamParser<E>() {

            @Override
            public Class<E> getParsedType() {
                return enumClass;
            }

            @Override
            protected boolean isCaseSensitive() {
                return caseSensitive;
            }

        };

        ENUM_PARSERS.put(enumClass, parser);
    }

    public static ArrayParamParser<?> getArrayParser(Class<?> targetType) {
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

    public static class PrimitiveIntegerParamParser implements ParamParser<Integer> {
        @Override
        public Integer parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || validation.hasFieldViolation(field)) {
                return 0;
            } else {
                try {
                    return Integer.parseInt(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
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
            if (parameterValue == null || validation.hasFieldViolation(field)) {
                return null;
            } else {
                try {
                    return Integer.parseInt(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
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
            if (parameterValue == null || validation.hasFieldViolation(field)) {
                return null;
            } else {
                return Boolean.parseBoolean(parameterValue);
            }
        }

        @Override
        public Class<Boolean> getParsedType() {
            return Boolean.class;
        }
    }

    public static class PrimitiveBooleanParamParser implements ParamParser<Boolean> {
        @Override
        public Boolean parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || validation.hasFieldViolation(field)) {
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
            if (parameterValue == null || validation.hasFieldViolation(field)) {
                return null;
            } else {
                try {
                    return Long.parseLong(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
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
            if (parameterValue == null || validation.hasFieldViolation(field)) {
                return 0L;
            } else {
                try {
                    return Long.parseLong(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
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
            if (parameterValue == null || validation.hasFieldViolation(field)) {
                return null;
            } else {
                try {
                    return Float.parseFloat(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
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
            if (parameterValue == null || validation.hasFieldViolation(field)) {
                return 0f;
            } else {
                try {
                    return Float.parseFloat(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
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
            if (parameterValue == null || validation.hasFieldViolation(field)) {
                return null;
            } else {
                try {
                    return Double.parseDouble(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
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
            if (parameterValue == null || validation.hasFieldViolation(field)) {
                return 0d;
            } else {
                try {
                    return Double.parseDouble(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
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
            if (parameterValue == null || validation.hasFieldViolation(field)) {
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
            if (parameterValue == null || validation.hasFieldViolation(field)) {
                return null;
            } else {
                try {
                    return Byte.parseByte(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
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
            if (parameterValue == null || validation.hasFieldViolation(field)) {
                return 0;
            } else {
                try {
                    return Byte.parseByte(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
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
            if (parameterValue == null || validation.hasFieldViolation(field)) {
                return null;
            } else {
                try {
                    return Short.parseShort(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
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
            if (parameterValue == null || validation.hasFieldViolation(field)) {
                return 0;
            } else {
                try {
                    return Short.parseShort(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
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
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasFieldViolation(field)) {
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
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasFieldViolation(field)) {
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

    /**
     * Converts a parameter to an Enum value by (case-insensitive) value matching.
     *
     * @param <E>
     */
    public static abstract class EnumParamParser<E extends Enum<E>> implements ParamParser<E> {
        @Override
        public E parseParameter(String field, String parameterValue, Validation validation) {
            if (parameterValue == null || validation.hasFieldViolation(field)) {
                return null;
            } else {
                final boolean caseSensitive = isCaseSensitive();

                E[] values = getParsedType().getEnumConstants();
                for (E value : values) {
                    if (caseSensitive) {
                        if (value.name().equals(parameterValue)) {
                            return value;
                        }
                    } else {
                        if (value.name().equalsIgnoreCase(parameterValue)) {
                            return value;
                        }
                    }
                }

                validation.addFieldViolation(field, ConstraintViolation.createForFieldWithDefault(
                        IsEnum.KEY, field, IsEnum.MESSAGE, new Object[] {parameterValue, getParsedType().getName()}));

                return null;
            }
        }

        @Override
        public abstract Class<E> getParsedType();

        /**
         * Determines if the enum parser is case-sensitive.
         */
        protected abstract boolean isCaseSensitive();
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
            if (parameterValue == null || parameterValue.isEmpty() || validation.hasFieldViolation(field)) {
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

                if (validation.hasFieldViolation(field)) {
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
            if (parameterValues == null || validation.hasFieldViolation(field)) {
                return null;
            } else {
                // parse the individual values as the target item type
                Class<T> itemType = getItemType();
                T[] array = (T[]) Array.newInstance(itemType, parameterValues.length);
                for (int i = 0; i < parameterValues.length; i++) {
                    T t = itemParser.parseParameter(field, parameterValues[i], validation);
                    Array.set(array, i, t);
                }

                if (validation.hasFieldViolation(field)) {
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

}
