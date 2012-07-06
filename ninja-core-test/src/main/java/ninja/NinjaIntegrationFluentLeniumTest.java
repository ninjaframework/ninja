package ninja;

import org.fluentlenium.adapter.FluentTest;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public abstract class NinjaIntegrationFluentLeniumTest extends FluentTest {

	public WebDriver webDriver = new HtmlUnitDriver();
	
	public NinjaIntegrationTestHelper ninjaIntegrationTestHelper;
	
	public NinjaIntegrationFluentLeniumTest() {
		ninjaIntegrationTestHelper = new NinjaIntegrationTestHelper();
	}
	
	
    @Override
    public WebDriver getDefaultDriver() {
        return webDriver;
    }
    
    public String getServerAddress() {
    	return ninjaIntegrationTestHelper.getServerAddress();
    }

}
