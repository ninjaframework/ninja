package ninja.params;

import com.google.common.collect.ImmutableMap;
import ninja.Validation;

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
            if (parameterValue == null) {
                return 0;
            } else {
                try {
                    return Integer.parseInt(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldError(field, "error.integer.format", parameterValue);
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
            if (parameterValue == null) {
                return null;
            } else {
                try {
                    return Integer.parseInt(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldError(field, "error.integer.format", parameterValue);
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
            if (parameterValue == null) {
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
            if (parameterValue == null) {
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
            if (parameterValue == null) {
                return null;
            } else {
                try {
                    return Long.parseLong(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldError(field, "error.integer.format", parameterValue);
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
            if (parameterValue == null) {
                return 0L;
            } else {
                try {
                    return Long.parseLong(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldError(field, "error.integer.format", parameterValue);
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
            if (parameterValue == null) {
                return null;
            } else {
                try {
                    return Float.parseFloat(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldError(field, "error.float.format", parameterValue);
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
            if (parameterValue == null) {
                return 0f;
            } else {
                try {
                    return Float.parseFloat(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldError(field, "error.float.format", parameterValue);
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
            if (parameterValue == null) {
                return null;
            } else {
                try {
                    return Double.parseDouble(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldError(field, "error.float.format", parameterValue);
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
            if (parameterValue == null) {
                return 0d;
            } else {
                try {
                    return Double.parseDouble(parameterValue);
                } catch (NumberFormatException e) {
                    validation.addFieldError(field, "error.float.format", parameterValue);
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
