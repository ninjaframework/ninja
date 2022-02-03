package ninja.conversion;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import io.github.classgraph.*;
import ninja.conversion.exception.DuplicateConverterException;
import ninja.conversion.exception.InvalidConverterException;
import ninja.utils.NinjaProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Provider for the interface {@link Conversion} implementation.
 */
@Singleton
public class ConversionProvider implements Provider<Conversion> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConversionProvider.class);

    private static final String CONFIG_CONVERTERS_PACKAGE_KEY = "conversions.converter_package_location";
    private static final String[] DEFAULT_CONVERTERS_PACKAGE = new String[]{"converters"};

    private final Injector injector;
    private final NinjaProperties ninjaProperties;

    /**
     * Build a new instance.
     *
     * @param injector        Instance of "Injector"
     * @param ninjaProperties Instance of "NinjaProperties"
     */
    @Inject
    public ConversionProvider(final Injector injector,
                              final NinjaProperties ninjaProperties) {
        this.injector = injector;
        this.ninjaProperties = ninjaProperties;
    }

    @Override
    public Conversion get() {
        final Map<ConverterCompositeKey, TypeConverter<?, ?>> converterMap = new HashMap<>();

        final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        final String[] converterPackage = ninjaProperties.getStringArray(CONFIG_CONVERTERS_PACKAGE_KEY);

        try (final ScanResult scanResult = new ClassGraph()
                .acceptPackages(converterPackage != null ? converterPackage : DEFAULT_CONVERTERS_PACKAGE)
                .addClassLoader(classLoader)
                .enableAllInfo()
                .scan()) {

            for (final ClassInfo classInfo : scanResult.getAllClasses()) {
                if (classInfo.implementsInterface(TypeConverter.class)) {
                    // Determine source class and target class
                    final List<TypeArgument> typeArgumentList = classInfo.getTypeSignature().getSuperinterfaceSignatures()
                            .get(0)
                            .getTypeArguments();
                    if (typeArgumentList.size() != 2) {
                        // Technically not possible, but you might as well be 100% sure
                        throw new InvalidConverterException(classInfo.getName(), "Bad number of type parameter");
                    }

                    final String sourceClassName = typeArgumentList.get(0).getTypeSignature().toString();
                    final Class<?> sourceClassType = revolveClassFromString(sourceClassName, classLoader)
                            .orElseThrow(() -> new InvalidConverterException(
                                    classInfo.getName(), "Can't retrieve Class<?> from '" + sourceClassName + "'"));

                    final String targetClassName = typeArgumentList.get(1).getTypeSignature().toString();
                    final Class<?> targetClassType = revolveClassFromString(targetClassName, classLoader)
                            .orElseThrow(() -> new InvalidConverterException(
                                    classInfo.getName(), "Can't retrieve Class<?> from '" + targetClassName + "'"));

                    // Retrieves constructor
                    final MethodInfoList constructorInfoList = classInfo.getConstructorInfo();
                    if (constructorInfoList.isEmpty()) {
                        throw new InvalidConverterException(classInfo.getName(), "No constructor found");
                    }

                    final MethodInfo constructorInfo = constructorInfoList.get(0);

                    // Try to instantiate converter
                    final TypeConverter<?, ?> converter = (TypeConverter<?, ?>) injector.getInstance(
                            constructorInfo.loadClassAndGetConstructor().getDeclaringClass());

                    // Adds it to the Map of instantiated converters
                    final ConverterCompositeKey compositeKey = new ConverterCompositeKey(sourceClassType, targetClassType);
                    if (converterMap.putIfAbsent(compositeKey, converter) != null) {
                        throw new DuplicateConverterException(classInfo.getName(), sourceClassName, targetClassName);
                    }

                    LOGGER.info("Register new Converter<source={}, target={}>", sourceClassName, targetClassName);
                }
            }
        }

        return new ConversionImpl(converterMap);
    }

    /**
     * Resolve {@code Class<?>} from a class name.
     *
     * @param className   Name of the class
     * @param classLoader Loader to use
     * @return Resolved {@code Class<?>}
     */
    private Optional<Class<?>> revolveClassFromString(final String className, final ClassLoader classLoader) {
        if (StringUtils.isBlank(className)) {
            return Optional.empty();
        }

        try {
            return Optional.of(Class.forName(className, false, classLoader));
        } catch (final ClassNotFoundException ignore) {
            return Optional.empty();
        }
    }
}
