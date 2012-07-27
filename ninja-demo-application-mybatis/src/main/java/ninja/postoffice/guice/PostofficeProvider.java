package ninja.postoffice.guice;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import ninja.postoffice.Postoffice;
import ninja.postoffice.commonsmail.PostofficeCommonsmailImpl;
import ninja.postoffice.mock.PostofficeMockImpl;
import ninja.utils.NinjaProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class PostofficeProvider implements Provider<Postoffice> {
    private static final Logger log = LoggerFactory.getLogger(PostofficeProvider.class);

    private final NinjaProperties ninjaProperties;
    private final Injector injector;

    private Postoffice mailer;

    @Inject
    PostofficeProvider(NinjaProperties ninjaProperties, Injector injector) {
        this.ninjaProperties = ninjaProperties;
        this.injector = injector;
    }

    @Override
    public Postoffice get() {

        if (mailer == null) {
            Class<? extends Postoffice> postofficeClass = null;

            String postofficeClassName = ninjaProperties.get(PostofficeConstant.postofficeImplementation);
            if (postofficeClassName != null) {
                try {

                    Class<?> clazz = Class.forName(postofficeClassName);
                    postofficeClass = clazz.asSubclass(Postoffice.class);

                    log.info("postoffice.implementation is: " + postofficeClass);

                } catch (ClassNotFoundException e) {
                    throw new RuntimeException("Class defined in configuration postoffice.implementation " +
                            "not found (" + postofficeClass + ")", e);
                } catch (ClassCastException e) {
                    throw new RuntimeException("Class defined in configuration postoffice.implementation " +
                            "is not an instance of Postoffice (" + postofficeClass + ")", e);
                }
            }

            if (postofficeClass == null) {
                if (!ninjaProperties.isProd()) {
                    postofficeClass = PostofficeMockImpl.class;
                    log.info("In dev mode - using mock Postoffice implementation "
                            + postofficeClass);
                } else {
                    postofficeClass = PostofficeCommonsmailImpl.class;
                    log.info("In produdction mode - using default Postoffice implementation "
                            + postofficeClass);
                }
            }

            mailer = injector.getInstance(postofficeClass);
        }
        return mailer;
    }
}