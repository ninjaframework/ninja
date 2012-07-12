package ninja;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import ninja.session.SessionCookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SecureFilterTest {

    @Mock
    private Context context;
    
    @Mock
    private SessionCookie sessionCookie;

    @Mock 
    private FilterChain filterChain;
    
    @Mock 
    private Result result;

    SecureFilter secureFilter;

    @Before
    public void setup() {
        secureFilter = new SecureFilter();

    }

    @Test
    public void testSecureFilter() {

        when(context.getSessionCookie()).thenReturn(null);

        // filter that
        secureFilter.filter(filterChain, context);

        verifyZeroInteractions(filterChain);
    }
    
    @Test
    public void testSessionIsNotReturingWhenUserNameMissing() {

        when(context.getSessionCookie()).thenReturn(sessionCookie);
        when(sessionCookie.get("username")).thenReturn(null);
        
        // filter that
        secureFilter.filter(filterChain, context);

        verifyZeroInteractions(filterChain);
    }

    @Test
    public void testWorkingSessionWhenUsernameIsThere() {

        when(context.getSessionCookie()).thenReturn(sessionCookie);
        when(sessionCookie.get("username")).thenReturn("myname");
        
        // filter that
        secureFilter.filter(filterChain, context);

        verify(filterChain).next(context);
    }

}
