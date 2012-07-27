package ninja.params;

/**
 * This is used to hold the argument class, so that it can be injected into
 * extractors/validators so they can know what type they are extracting.
 */
public class ArgumentClassHolder {
    private final Class<?> argumentClass;

    public ArgumentClassHolder(Class<?> argumentClass) {
        this.argumentClass = argumentClass;
    }

    public Class<?> getArgumentClass() {
        return argumentClass;
    }
}
