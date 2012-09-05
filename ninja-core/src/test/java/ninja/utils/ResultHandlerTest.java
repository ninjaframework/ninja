package ninja.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import ninja.Context;
import ninja.Result;
import ninja.Results;
import ninja.template.TemplateEngine;
import ninja.template.TemplateEngineManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ResultHandlerTest {
    
    @Mock
    private TemplateEngineManager templateEngineManager;
    
    @Mock
    private TemplateEngine templateEngine;
    
    private ResultHandler resultHandler;
    
    @Mock 
    private Context context;
    
    
    @Before
    public void init() {
        
        resultHandler = new ResultHandler(templateEngineManager);
        when(templateEngineManager.getTemplateEngineForContentType(
                Result.APPLICATON_JSON)).thenReturn(templateEngine);
        
    }
    
    /**
     * If Cache-Control is not set the no-cache strategy has to be applied.
     * 
     * We expect
     * Cache-Control: ...
     * Date: ...
     * Expires: ...
     */
    @Test
    public void testAddingOfDefaultHeadersWorks() {
        
        Result result = Results.json();
        // just a new object as dummy...
        result.render(new Object());
        
        // make sure the stuff is not set by default json method (just in case...)       
        assertNull(result.getHeaders().get(Result.CACHE_CONTROL));
        assertNull(result.getHeaders().get(Result.DATE));
        assertNull(result.getHeaders().get(Result.EXPIRES));
        
        // handle result
        resultHandler.handleResult(result, context);
        
        // make sure stuff is there:
        assertEquals(Result.CACHE_CONTROL_DEFAULT_NOCACHE_VALUE, result.getHeaders().get(Result.CACHE_CONTROL));
        assertNotNull(result.getHeaders().get(Result.DATE));
        assertEquals(DateUtil.formatForHttpHeader(0L), result.getHeaders().get(Result.EXPIRES));
        
    }
    
    @Test
    public void testCacheControlDoesNotGetTouchedWhenSet() {
        
        Result result = Results.json();
        //just a simple cache control header:
        result.addHeader(Result.CACHE_CONTROL, "must-revalidate");
        // just a new object as dummy...
        result.render(new Object());
        
        // handle result
        resultHandler.handleResult(result, context);
        
        // make sure stuff is there:
        assertEquals("must-revalidate", result.getHeaders().get(Result.CACHE_CONTROL));
        assertNull(result.getHeaders().get(Result.DATE));
        assertNull(result.getHeaders().get(Result.EXPIRES));
        
    }

}
