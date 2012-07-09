package ninja;

import org.fluentlenium.adapter.FluentTest;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import javax.naming.NameNotFoundException;

public abstract class NinjaIntegrationFluentLeniumTest extends FluentTest {

	public WebDriver webDriver = new HtmlUnitDriver();
	
	public NinjaIntegrationTestHelper ninjaIntegrationTestHelper;
	
    @Before
    public void startupServer() {
        ninjaIntegrationTestHelper = new NinjaIntegrationTestHelper();
    }
	
	
    @Override
    public WebDriver getDefaultDriver() {
        return webDriver;
    }
    
    public String getServerAddress() {
    	return ninjaIntegrationTestHelper.getServerAddress();
    }

    @After
    public void shutdownServer() {
        ninjaIntegrationTestHelper.shutdown();
    }

}
