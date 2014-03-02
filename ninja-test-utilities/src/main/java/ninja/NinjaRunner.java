package ninja;

import com.google.inject.Inject;
import com.google.inject.Injector;
import ninja.utils.NinjaTestServer;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

public class NinjaRunner extends BlockJUnit4ClassRunner {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private Injector injector = null;

  public NinjaRunner(Class<?> klass) throws InitializationError {
    super(klass);
    NinjaTestServer ninjaTestServer = new NinjaTestServer();
    injector = ninjaTestServer.getInjector();
  }

  @Override
  protected Object createTest() throws Exception {
    Object testObj = super.createTest();

    for (Field field : testObj.getClass().getDeclaredFields()) {
      if (field.isAnnotationPresent(Inject.class)) {
        Object value = injector.getInstance(field.getType());
        logger.debug("try to inject {} to {}", value, testObj);
        try {
          if (field.isAccessible()) {
            field.set(testObj, value);
          }
          else {
            field.setAccessible(true);
            field.set(testObj, value);
            field.setAccessible(false);
          }
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      }
    }

    return testObj;
  }

}

