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

package ninja.params;

import com.google.common.collect.ImmutableMap;
import ninja.validation.ConstraintViolation;
import ninja.validation.IsFloat;
import ninja.validation.IsInteger;
import ninja.validation.Validation;

import java.util.Map;

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
                    .build();

    public static ParamParser<?> getParamParser(Class<?> targetType) {
        return PARAM_PARSERS.get(targetType);
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
}
