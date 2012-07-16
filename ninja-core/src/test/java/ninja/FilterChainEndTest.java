package ninja;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;

import ninja.classesfortest.TestDummyController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import com.google.inject.Provider;

@RunWith(MockitoJUnitRunner.class)
public class FilterChainEndTest {

	@Mock
	private Provider<TestDummyController> controllerProvider;

	@Mock
	private Context context;

	private TestDummyController testDummyController = new TestDummyController();

	@Before
	public void setup() {
		testDummyController = new TestDummyController();
		when(controllerProvider.get()).thenReturn(testDummyController);
	}

	@Test
	public void testNoParameterMethodCallingWorks() throws Exception {

		Method method = TestDummyController.class.getMethod("noParameter");

		FilterChainEnd filterChainEnd = new FilterChainEnd(controllerProvider,
		        method);

		filterChainEnd.next(context);

		assertTrue(testDummyController.noParameterMethodExecuted);

	}

	@Test
	public void testSettingContextOnlyWorks() throws Exception {

		Method method = TestDummyController.class.getMethod("context",
		        Context.class);

		// we assume one si
		FilterChainEnd filterChainEnd = new FilterChainEnd(controllerProvider,
		        method);

		filterChainEnd.next(context);

		assertFalse(testDummyController.noParameterMethodExecuted);
		assertTrue(testDummyController.contextMethodExecuted);
		assertNotNull(testDummyController.context);

	}

	@Test
	public void testSettingOfParamAnnotationWorks() throws Exception {

		Method method = TestDummyController.class.getMethod("param",
		        Context.class, String.class);
		when(context.getParameter("param1")).thenReturn("param1");

		// we assume one si
		FilterChainEnd filterChainEnd = new FilterChainEnd(controllerProvider,
		        method);

		filterChainEnd.next(context);

		assertFalse(testDummyController.noParameterMethodExecuted);
		assertFalse(testDummyController.contextMethodExecuted);
		assertNotNull(testDummyController.context);
		assertEquals("param1", testDummyController.param1);

	}

	@Test
	public void testSettingOfPathAnnotationWorks() throws Exception {

		Method method = TestDummyController.class.getMethod("pathParam",
		        Context.class, String.class);
		when(context.getPathParameter("pathParam1")).thenReturn("pathParam1");

		// we assume one si
		FilterChainEnd filterChainEnd = new FilterChainEnd(controllerProvider,
		        method);

		filterChainEnd.next(context);

		assertFalse(testDummyController.noParameterMethodExecuted);
		assertFalse(testDummyController.contextMethodExecuted);
		assertTrue(testDummyController.pathParamMethodExecuted);
		assertNotNull(testDummyController.context);
		assertEquals("pathParam1", testDummyController.pathParam1);

	}

	@Test
	public void testSettingOfCompletelyArbitraryMethodSignatureWorks()
	        throws Exception {

		Method method = TestDummyController.class.getMethod(
		        "completelyMixedMethodSignature", String.class, String.class,
		        Context.class, String.class, String.class);

		when(context.getPathParameter("pathParam1")).thenReturn("pathParam1");
		when(context.getPathParameter("pathParam2")).thenReturn("pathParam2");

		when(context.getParameter("param1")).thenReturn("param1");
		when(context.getParameter("param2")).thenReturn("param2");
		// we assume one si
		FilterChainEnd filterChainEnd = new FilterChainEnd(controllerProvider,
		        method);

		filterChainEnd.next(context);

		assertFalse(testDummyController.noParameterMethodExecuted);
		assertFalse(testDummyController.contextMethodExecuted);
		assertFalse(testDummyController.pathParamMethodExecuted);
		assertTrue(testDummyController.completelyMixedMethodSignatureExecuted);
		assertNotNull(testDummyController.context);
		assertEquals("param1", testDummyController.param1);
		assertEquals("pathParam1", testDummyController.pathParam1);
		assertEquals("param2", testDummyController.param2);
		assertEquals("pathParam2", testDummyController.pathParam2);

	}

	@Test
	public void testThatNullIsReturnedWhenParamsNotThere() throws Exception {

		Method method = TestDummyController.class.getMethod(
		        "completelyMixedMethodSignature", String.class, String.class,
		        Context.class, String.class, String.class);

		when(context.getPathParameter("pathParam1")).thenReturn(null);
		when(context.getPathParameter("pathParam2")).thenReturn("pathParam2");

		when(context.getParameter("param1")).thenReturn(null);
		when(context.getParameter("param2")).thenReturn("param2");
		// we assume one si
		FilterChainEnd filterChainEnd = new FilterChainEnd(controllerProvider,
		        method);

		Result result = filterChainEnd.next(context);

		assertNotNull(result);

		assertFalse(testDummyController.noParameterMethodExecuted);
		assertFalse(testDummyController.contextMethodExecuted);
		assertFalse(testDummyController.pathParamMethodExecuted);
		assertTrue(testDummyController.completelyMixedMethodSignatureExecuted);
		assertNotNull(testDummyController.context);
		assertEquals(null, testDummyController.param1);
		assertEquals(null, testDummyController.pathParam1);
		assertEquals("param2", testDummyController.param2);
		assertEquals("pathParam2", testDummyController.pathParam2);

	}

}
