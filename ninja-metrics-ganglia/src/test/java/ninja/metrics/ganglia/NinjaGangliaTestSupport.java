package ninja.metrics.ganglia;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.ganglia.GangliaReporter;
import info.ganglia.gmetric4j.gmetric.GMetric;
import ninja.metrics.MetricsService;
import ninja.utils.NinjaProperties;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.powermock.api.mockito.PowerMockito.mock;

public class NinjaGangliaTestSupport {

    protected NinjaGanglia ninjaGanglia;

    protected NinjaProperties ninjaProperties;

    protected MetricsService metricsService;

    protected MetricRegistry metricRegistry;

    protected GMetric gMetric;

    protected GangliaReporter reporter;

    protected GangliaReporter.Builder builder;

    protected void mockReporter() {
        reporter = mock(GangliaReporter.class);
        Mockito.doNothing().when(reporter).start(70, TimeUnit.SECONDS);
    }

    protected void mockMetricRegistry() {
        metricRegistry = mock(MetricRegistry.class);
        Mockito.when(metricsService.getMetricRegistry()).thenReturn(metricRegistry);
    }

    protected void mockMetricService() {
        metricsService = mock(MetricsService.class);
        Mockito.when(metricsService.getHostname()).thenReturn("myHostName");
    }

    protected void mockGMetric() throws Exception {
        gMetric = mock(GMetric.class);
        PowerMockito.spy(GMetric.class);
        PowerMockito.whenNew(GMetric.class).withArguments("www.google.com", 8080, GMetric.UDPAddressingMode.MULTICAST, 1).thenReturn(gMetric);
    }

    protected void mockGangliaReporterBuilder() throws Exception {
        builder = PowerMockito.mock(GangliaReporter.Builder.class);
        assertNotNull("Builder must not be null", builder);
        PowerMockito.when(GangliaReporter.class, "forRegistry", metricRegistry).thenReturn(builder);
        assertEquals(metricRegistry, metricsService.getMetricRegistry());

        assertEquals("Builder must not be null", builder, GangliaReporter.forRegistry(metricRegistry));
        Mockito.when(builder.convertRatesTo(TimeUnit.SECONDS)).thenReturn(builder);
        PowerMockito.when(builder, "convertDurationsTo", TimeUnit.MILLISECONDS).thenReturn(builder);
        PowerMockito.when(builder, "build", gMetric).thenReturn(reporter);

        assertEquals(reporter, builder.build(gMetric));
        assertEquals(builder, builder.convertDurationsTo(TimeUnit.MILLISECONDS));
        assertEquals(builder, builder.convertRatesTo(TimeUnit.SECONDS));
        assertEquals(builder, GangliaReporter.forRegistry(metricRegistry));
    }

    protected void mockNinjaProperties() {
        ninjaProperties = mock(NinjaProperties.class);
        Mockito.when(ninjaProperties.getOrDie("metrics.ganglia.address")).thenReturn("www.google.com");
        Mockito.when(ninjaProperties.getIntegerWithDefault("metrics.ganglia.port", 8649)).thenReturn(8080);
        Mockito.when(ninjaProperties.getWithDefault("metrics.ganglia.period", "60s")).thenReturn("70s");
        Mockito.when(ninjaProperties.getBooleanWithDefault("metrics.ganglia.enabled", false)).thenReturn(true);
    }
}
