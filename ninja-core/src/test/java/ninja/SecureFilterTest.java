package ninja;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class SecureFilterTest {

	@Mock
	private Context context;

	@Mock
	HttpServletRequest httpServletRequest;

	SecureFilter secureFilter;

	@Before
	public void setup() {

		MockitoAnnotations.initMocks(this);

		secureFilter = new SecureFilter();

	}

	@Test
	public void testSecureFilter() {

		// make sure continue is set to false when we got no cookies:
		when(context.getHttpServletRequest()).thenReturn(httpServletRequest);
		when(httpServletRequest.getCookies()).thenReturn(null);

		// filter that
		secureFilter.filter(context);

		// and we expect a false from the secure filter...
		assertFalse(secureFilter.continueExecution());
	}

}
