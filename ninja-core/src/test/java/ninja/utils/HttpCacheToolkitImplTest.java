package ninja.utils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import ninja.Context;
import ninja.Result;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class HttpCacheToolkitImplTest {

    @Mock
    NinjaProperties ninjaProperties;

    @Mock
    Result result;

    @Mock
    Context context;

    @Test
    public void testIsModified() {

        HttpCacheToolkit httpCacheToolkit = new HttpCacheToolkitImpl(
                ninjaProperties);

        // test etag support:
        when(context.getHeader(HttpHeaderConstants.IF_NONE_MATCH)).thenReturn(
                "etag_xyz");

        // same etag => not modified
        assertFalse(httpCacheToolkit.isModified("etag_xyz", 0L, context));
        // new etag => modified
        assertTrue(httpCacheToolkit
                .isModified("etag_xyz_modified", 0L, context));

        // remove etag to test modified timestamp caching:
        when(context.getHeader(HttpHeaderConstants.IF_NONE_MATCH)).thenReturn(
                null);

        // => no if modified since request => null
        when(context.getHeader(HttpHeaderConstants.IF_MODIFIED_SINCE))
                .thenReturn(null);
        assertTrue(httpCacheToolkit
                .isModified("etag_xyz_modified", 0L, context));

        // => older timestamp => modified
        when(context.getHeader(HttpHeaderConstants.IF_MODIFIED_SINCE))
                .thenReturn("Thu, 01 Jan 1970 00:00:00 GMT");
        assertTrue(httpCacheToolkit.isModified("etag_xyz_modified", 1000L,
                context));

        // => same timestamp => not modified
        when(context.getHeader(HttpHeaderConstants.IF_MODIFIED_SINCE))
                .thenReturn("Thu, 01 Jan 1970 00:00:00 GMT");
        assertFalse(httpCacheToolkit.isModified("etag_xyz_modified", 0L,
                context));

        // => newer timestamp => not modified
        when(context.getHeader(HttpHeaderConstants.IF_MODIFIED_SINCE))
                .thenReturn("Thu, 01 Jan 1971 00:00:00 GMT");
        assertFalse(httpCacheToolkit.isModified("etag_xyz_modified", 0L,
                context));

        // => strange timestamp => modified
        when(context.getHeader(HttpHeaderConstants.IF_MODIFIED_SINCE))
                .thenReturn("STRANGE_TIMESTAMP");
        assertTrue(httpCacheToolkit
                .isModified("etag_xyz_modified", 0L, context));

    }

    @Test
    public void testAddETag() {
        HttpCacheToolkit httpCacheToolkit = new HttpCacheToolkitImpl(
                ninjaProperties);
        ////////////////////////////////////////////////
        // test Cache-Control header
        ////////////////////////////////////////////////
        // check Cache header:
        // if not in production => no cache:
        when(ninjaProperties.isProd()).thenReturn(false);

        httpCacheToolkit.addEtag(context, result, 0L);
        verify(result).addHeader(HttpHeaderConstants.CACHE_CONTROL, "no-cache");

        // in production => make sure cache header is set accordingly:
        when(ninjaProperties.isProd()).thenReturn(true);

        // set regular header with request to http cache control constant:
        reset(result);

        when(
                ninjaProperties.getWithDefault(
                        NinjaConstant.HTTP_CACHE_CONTROL,
                        NinjaConstant.HTTP_CACHE_CONTROL_DEFAULT)).thenReturn(
                "1234");

        httpCacheToolkit.addEtag(context, result, 0L);
        verify(result).addHeader(HttpHeaderConstants.CACHE_CONTROL,
                "max-age=1234");

        // if cache time = 0 => set to no-cache:
        reset(result);

        when(
                ninjaProperties.getWithDefault(
                        NinjaConstant.HTTP_CACHE_CONTROL,
                        NinjaConstant.HTTP_CACHE_CONTROL_DEFAULT)).thenReturn(
                "0");

        httpCacheToolkit.addEtag(context, result, 0L);
        verify(result).addHeader(HttpHeaderConstants.CACHE_CONTROL, "no-cache");

        
        ////////////////////////////////////////////////
        // Test Add etag header
        ////////////////////////////////////////////////
        
        // do not add etag when not configured:
        reset(result);

        when(ninjaProperties.getBooleanWithDefault(
                        NinjaConstant.HTTP_USE_ETAG,
                        NinjaConstant.HTTP_USE_ETAG_DEFAULT)).thenReturn(
                false);

        httpCacheToolkit.addEtag(context, result, 0L);
        // not in prod => no-cache
        verify(result).addHeader(HttpHeaderConstants.CACHE_CONTROL, "no-cache");
        // IMPORTANT: etag not added
        verify(result, never()).addHeader(HttpHeaderConstants.ETAG, eq(anyString()));

        
        // add etag when configured:
        reset(result);

        when(ninjaProperties.getBooleanWithDefault(
                        NinjaConstant.HTTP_USE_ETAG,
                        NinjaConstant.HTTP_USE_ETAG_DEFAULT)).thenReturn(
                true);

        httpCacheToolkit.addEtag(context, result, 1234L);
        // not in prod => no-cache
        verify(result).addHeader(HttpHeaderConstants.CACHE_CONTROL, "no-cache");
        // IMPORTANT: etag added
        verify(result).addHeader(HttpHeaderConstants.ETAG, "\"1234\"");
      
        
        ////////////////////////////////////////////////
        // Test isModified 304 setting in result
        ////////////////////////////////////////////////
        // test lastmodified is added when etags match:
        when(context.getMethod()).thenReturn("GET");
        when(context.getHeader(HttpHeaderConstants.IF_NONE_MATCH)).thenReturn("\"1234\"");
        
        reset(result);
        httpCacheToolkit.addEtag(context, result, 1234L);
        
        verify(result).status(Result.SC_304_NOT_MODIFIED);
    
    
        // test lastmodified not added when stuff does not match
        // => but Last-Modified header is added
        when(context.getMethod()).thenReturn("GET");
        when(context.getHeader(HttpHeaderConstants.IF_NONE_MATCH)).thenReturn("\"12___34\"");
        
        reset(result);
        httpCacheToolkit.addEtag(context, result, 1234L);
        
        verify(result, never()).status(Result.SC_304_NOT_MODIFIED);
        verify(result).addHeader(HttpHeaderConstants.LAST_MODIFIED,
                    DateUtil.formatForHttpHeader(1234L));
    
    }

}
