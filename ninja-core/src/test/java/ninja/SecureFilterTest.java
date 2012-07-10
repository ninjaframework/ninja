package ninja;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import ninja.Context.HTTP_STATUS;
import ninja.session.SessionCookie;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SecureFilterTest {

    @Mock
    private Context context;
    
    @Mock
    private SessionCookie sessionCookie;

    SecureFilter secureFilter;

    @Before
    public void setup() {
        secureFilter = new SecureFilter();

        when(context.status(Mockito.any(HTTP_STATUS.class))).thenReturn(context);
        when(context.template(Mockito.anyString())).thenReturn(context);
        
    }

    @Test
    public void testSecureFilter() {

        when(context.getSessionCookie()).thenReturn(null);

        // filter that
        secureFilter.filter(context);

        // and we expect a false from the secure filter...
        assertFalse(secureFilter.continueExecution());
    }
    
    @Test
    public void testSessionIsNotReturingWhenUserNameMissing() {

        when(context.getSessionCookie()).thenReturn(sessionCookie);
        when(sessionCookie.get("username")).thenReturn(null);
        
        // filter that
        secureFilter.filter(context);

        // and we expect a false from the secure filter...
        assertFalse(secureFilter.continueExecution());
    }

    @Test
    public void testWorkingSessionWhenUsernameIsThere() {

        when(context.getSessionCookie()).thenReturn(sessionCookie);
        when(sessionCookie.get("username")).thenReturn("myname");
        
        // filter that
        secureFilter.filter(context);

        // and we expect a false from the secure filter...
        assertTrue(secureFilter.continueExecution());
    }

}
