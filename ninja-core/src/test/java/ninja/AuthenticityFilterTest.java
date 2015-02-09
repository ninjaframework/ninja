package ninja;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import ninja.session.Session;
import ninja.utils.NinjaConstant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class AuthenticityFilterTest {
    
    @Mock
    private Context context;
    
    @Mock
    private FilterChain filterChain;
    
    @Mock
    private Session session;
    
    @Mock
    private NinjaDefault ninjaDefault;
    
    Result forbidden = Results
            .forbidden()
            .supportedContentTypes(Result.TEXT_HTML, Result.APPLICATION_JSON, Result.APPLICATION_XML)
            .fallbackContentType(Result.TEXT_HTML)
            .render("")
            .template("");
    
    Result ok = Results
            .ok()
            .supportedContentTypes(Result.TEXT_HTML, Result.APPLICATION_JSON, Result.APPLICATION_XML)
            .fallbackContentType(Result.TEXT_HTML)
            .render("")
            .template("");
    
    private AuthenticityFilter authenticityFilter;

    @Before
    public void init() {
        authenticityFilter = new AuthenticityFilter(ninjaDefault);        
    }
    
    @Test
    public void testAuthenticityFail() {
        when(context.getParameter(NinjaConstant.AUTHENTICITY_TOKEN)).thenReturn("foo");
        when(context.getSession()).thenReturn(session);
        when(context.getSession().getAuthenticityToken()).thenReturn("bar");
        when(ninjaDefault.getForbiddenResult(context)).thenReturn(forbidden);
     
        Result failed = this.authenticityFilter.filter(filterChain, context);
        assertNotNull(failed);
        assertEquals(403, failed.getStatusCode());
    }
    
    @Test
    public void testAuthenticityWorks() {
        when(context.getParameter(NinjaConstant.AUTHENTICITY_TOKEN)).thenReturn("foo");
        when(context.getSession()).thenReturn(session);
        when(context.getSession().getAuthenticityToken()).thenReturn("foo");
        when(filterChain.next(context)).thenReturn(ok);
     
        Result success = this.authenticityFilter.filter(filterChain, context);
        assertNotNull(success);
        assertEquals(200, success.getStatusCode());
    }
}