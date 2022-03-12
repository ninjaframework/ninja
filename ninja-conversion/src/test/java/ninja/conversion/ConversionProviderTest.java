package ninja.conversion;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import ninja.utils.NinjaMode;
import ninja.utils.NinjaProperties;
import ninja.utils.NinjaPropertiesImpl;
import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ConversionProviderTest {

    @Test
    public void testProvider() {
        final Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                bind(NinjaProperties.class).toInstance(NinjaPropertiesImpl.builder()
                        .withMode(NinjaMode.test)
                        .build());
            }
        }, new ConversionModule());

        final Conversion conversion = injector.getInstance(Conversion.class);
        Assert.assertNotNull(conversion);

        Assert.assertTrue(conversion.canConvert(String.class, Integer.class));
    }
}
