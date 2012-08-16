package ninja;

import ninja.utils.NinjaTestServer;

import org.fluentlenium.adapter.FluentTest;
import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.htmlunit.HtmlUnitDriver;

import javax.naming.NameNotFoundException;

public abstract class NinjaFluentLeniumTest extends FluentTest {

	public WebDriver webDriver = new HtmlUnitDriver();
	
	public NinjaTestServer ninjaTestServer;
	
    @Before
    public void startupServer() {
        ninjaTestServer = new NinjaTestServer();
    }
	
	
    @Override
    public WebDriver getDefaultDriver() {
        return webDriver;
    }
    
    public String getServerAddress() {
    	return ninjaTestServer.getServerAddress();
    }

    @After
    public void shutdownServer() {
        ninjaTestServer.shutdown();
    }

}
