package ninja;

import org.fluentlenium.adapter.FluentTest;
import org.junit.BeforeClass;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

public abstract class NinjaIntegrationFluentLeniumTest extends FluentTest {

	public WebDriver webDriver = new HtmlUnitDriver();
	
	@BeforeClass
	public static void startup() {

		//start server
		NinjaIntegrationTestHelper.startup();
	}
	
	
    @Override
    public WebDriver getDefaultDriver() {
        return webDriver;
    }

}
