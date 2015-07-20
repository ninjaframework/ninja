package ninja;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class VersionCacheFilterTest {

    @Mock
    FilterChain filterChain;
    
    @Mock
    Context context;
    
    @Test
    public void testFilter() {
        
        // create filter and fake urls we want to try
        VersionCacheFilter filter = new VersionCacheFilter();
        String version = filter.getVersion();
        
        String[] testUrls = {
                "/$/asset/file.css", 
                "/asset/$/file.css", 
                "/$asset/file.css", 
                "/asset/$file.css", 
                "/asset/file.css$", 
                "/asset/file.css/$"
                };
        
        boolean[] urlsCacheable = {
                true,
                true,
                false,
                false,
                false,
                false
        };
        
        for (int i=0; i<testUrls.length; i++) {
            String testUrl = testUrls[i].replace("$", version);
            boolean urlCacheable = urlsCacheable[i];
            
            // set up request path
            reset(context);
            when(context.getRequestPath()).thenReturn(testUrl);

            filter.filter(filterChain, context);
            
            // verify that version is always enabled
            verify(context).setAttribute(VersionCacheFilter.VERSION_FILTER_ENABLED, "");
            
            // verify that resource is cacheable as per urlCacheable value
            if (urlCacheable) {
                verify(context).setAttribute(VersionCacheFilter.VERSION_RESOURCE_CACHEABLE, "");
            } else {
                verify(context, never()).setAttribute(VersionCacheFilter.VERSION_RESOURCE_CACHEABLE, "");
            }
        }
        
    }
}
