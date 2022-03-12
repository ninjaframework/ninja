package ninja.conversion;

import java.util.Objects;

/**
 * Composite key consisting of the source and destination of the conversion.
 */
final class ConverterCompositeKey {

    private final Class<?> sourceTypeClass;
    private final Class<?> targetTypeClass;

    /**
     * Build a new instance.
     *
     * @param sourceTypeClass The source type class
     * @param targetTypeClass The target type class
     */
    ConverterCompositeKey(final Class<?> sourceTypeClass,
                          final Class<?> targetTypeClass) {
        this.sourceTypeClass = sourceTypeClass;
        this.targetTypeClass = targetTypeClass;
    }

    @Override
    public boolean equals(final Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        final ConverterCompositeKey that = (ConverterCompositeKey) object;
        return sourceTypeClass.equals(that.sourceTypeClass) && targetTypeClass.equals(that.targetTypeClass);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceTypeClass, targetTypeClass);
    }
}
