package ninja;

import static org.mockito.Mockito.when;
import ninja.session.Session;
import ninja.utils.NinjaConstant;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
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
        this.authenticityFilter.filter(filterChain, context);
        Mockito.verify(ninjaDefault).getForbiddenResult(context);
    }
    
    @Test
    public void testAuthenticityWorks() {
        when(context.getParameter(NinjaConstant.AUTHENTICITY_TOKEN)).thenReturn("foo");
        when(context.getSession()).thenReturn(session);
        when(context.getSession().getAuthenticityToken()).thenReturn("foo");
        this.authenticityFilter.filter(filterChain, context);
        Mockito.verify(filterChain).next(context);
    }
}