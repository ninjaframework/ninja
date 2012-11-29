package ninja;

import ninja.application.ApplicationRoutes;

/**
 * Utility class which provides absolute paths to Ninja's relative conventional paths.
 * 
 * @author <a href="mailto:jb@barop.de">Johannes Barop</a>
 * 
 */
public final class NinjaPaths {

    /**
     * Is set once during initialization.
     */
    static String ninjaPackage;

    /**
     * Hidden constructor as this class should not be instantiated.
     */
    private NinjaPaths() {
        // Intentionally left empty
    }

    /**
     * @return Path to the default configuration.
     * 
     *         Make sure that file exists. Otherwise the application won't start
     *         up.
     */
    public static String getConfiguration() {
        return getPath("conf/application.conf");
    }

    /**
     * @return Path to the default message file.
     * 
     *         Make sure that file exists. Otherwise the application won't start
     *         up.
     */
    public static String getI18n() {
        return getPath("conf/messages.properties");
    }

    /**
     * @return Path to a language-specific message file.
     */
    public static String getI18n(String lang) {
        return getPath(String.format("conf/messages.%s.properties", lang));
    }

    /**
     * @return Guice module class for custom injections.
     */
    public static String getModuleClass() {
        return getPackageOrClass("conf.Module");
    }

    /**
     * @return Class which implements {@link ApplicationRoutes} and configures
     *         the applications routes.
     */
    public static String getRouteClass() {
        return getPackageOrClass("conf.Routes");
    }

    /**
     * @return Directory where to look for stuff which is requested with
     *         "/assets/stuff".
     */
    public static String getAssets() {
        return getPath("assets/");
    }
    
    /**
     * @return The package where Ninja expects your controllers.
     */
    public static String getControllerPackage() {
        return getPackageOrClass("controllers");
    }

    /**
     * @return Directory where to look for view templates.
     */
    public static String getViews() {
        return getPath("views/");
    }

    /**
     * Build an absolute classpath path based on {@value #ninjaPackage} and the
     * given relative path.
     */
    private static String getPath(String relativePath) {
        String path = relativePath;

        if (ninjaPackage != null) {
            return ninjaPackage.replace('.', '/') + "/" + path;
        }

        return path;
    }

    /**
     * Build an package/class name based on {@value #ninjaPackage} and the given
     * relative package/class.
     */
    private static String getPackageOrClass(String relativePackageOrClass) {
        String packageOrClass = relativePackageOrClass;

        if (ninjaPackage != null) {
            return ninjaPackage + "." + packageOrClass;
        }

        return packageOrClass;
    }

}
