package ninja.metrics;

import org.aopalliance.intercept.MethodInvocation;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import com.codahale.metrics.Timer.Context;
import com.google.inject.Provider;

public class TimedInterceptorTest {

    private static class TestObject {

        @Timed("timed_test")
        public void timedMethod() {

        }
    }

    @Mock
    private Provider<MetricsService> metricsServiceProvider;

    @Mock
    private MethodInvocation invocation;

    @Mock
    private MetricsService metricsService;

    @Mock
    private MetricRegistry metricsRegistry;

    @Mock
    private Timer timer;

    @Mock
    private Context timerContext;

    private TimedInterceptor timedInterceptor;

    @Before
    public void setup() throws NoSuchMethodException, SecurityException {

        // configure mocks
        MockitoAnnotations.initMocks(this);
        Mockito.when(invocation.getMethod()).thenReturn(TestObject.class.getDeclaredMethod("timedMethod"));
        Mockito.when(metricsServiceProvider.get()).thenReturn(metricsService);
        Mockito.when(metricsService.getMetricRegistry()).thenReturn(metricsRegistry);
        Mockito.when(metricsRegistry.timer(Mockito.anyString())).thenReturn(timer);
        Mockito.when(timer.time()).thenReturn(timerContext);

        // create object under test
        timedInterceptor = new TimedInterceptor(metricsServiceProvider);
    }

    @Test
    public void testStopCalledNotClosed() throws Throwable {
        timedInterceptor.invoke(invocation);
        Mockito.verify(timerContext, Mockito.times(1)).stop();
        Mockito.verify(timerContext, Mockito.times(0)).close();
    }
}
