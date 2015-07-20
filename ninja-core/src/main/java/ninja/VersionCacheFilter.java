package ninja;

import java.nio.ByteBuffer;

import ninja.utils.HttpCacheToolkitImpl;

import org.apache.commons.codec.binary.Base32;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Interval;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Singleton;

/**
 * This filter disable default resource caching strategy from {@link HttpCacheToolkitImpl} by telling:
 * <br>- that versionning is enabled
 * <br>- that resource can only be cached if url contained the version in the path
 * <br><br>
 * When enabled, You need to add the version on all urls, any where, but not before the contextPath.
 * <br><br>
 * Examples:
 * <br>- [contextPath]/[version]/assets/folder/file.css
 * <br>- [contextPath]/assets/[version]/folder/file.css
 * <br>- [contextPath]/assets/folder/[version]/file.css
 * <br><br>
 * The version value is computed at startup and can be accessed using {@link #getVersion()}.
 * 
 * 
 * @author Christian Bourgeois
 *
 */
@Singleton
public class VersionCacheFilter implements Filter {
    
    private static final Logger Logger = LoggerFactory.getLogger(VersionCacheFilter.class);
    
    public static final String VERSION_FILTER_ENABLED = "ninja_version_cache_enabled";
    public static final String VERSION_RESOURCE_CACHEABLE = "ninja_version_cache_detected";
    
    private String version;

    public VersionCacheFilter() {
        this.version = computeVersion();
        Logger.info("Assets versionning is enabled with key {}, site is protected against browsers cache", this.version);
    }
    
    @Override
    public Result filter(FilterChain filterChain, Context context) {
        context.setAttribute(VERSION_FILTER_ENABLED, "");
        String requestPath = context.getRequestPath();
        if (requestPath.contains("/"+version+"/")) {
            requestPath.replace("/"+version+"/", "");
            context.changeRequestPath(requestPath);
            context.setAttribute(VERSION_RESOURCE_CACHEABLE, "");
        }
        
        return filterChain.next(context);
    }
    
    public String getVersion() {
        return version;
    }
    
    private String computeVersion() {
        DateTime now = new DateTime().withZone(DateTimeZone.UTC);
        DateTime start = new DateTime(2015, 1, 1, 0, 0, DateTimeZone.UTC);
        int diff = (int) new Interval(start, now).toDuration().getStandardSeconds();
        byte[] bytes = ByteBuffer.allocate(4).putInt(diff).array();
        String value = new Base32().encodeToString(bytes).replace("=", "");
        return value;
    }
}