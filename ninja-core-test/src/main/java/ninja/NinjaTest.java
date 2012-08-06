package ninja;


import java.net.URI;

import ninja.utils.NinjaTestBrowser;
import ninja.utils.NinjaTestServer;

import org.junit.After;
import org.junit.Before;

/**
 * Baseclass for tests that require a running server.
 * 
 * @author rbauer
 * 
 */
public class NinjaTest {

    /** Backend of the test => Starts Ninja */
	public NinjaTestServer ninjaTestServer;
	
	/** A persistent HttpClient that stores cookies to make requests */
	public NinjaTestBrowser ninjaTestBrowser;
	
	public NinjaTest() {
	    //intentionally left emtpy.
	    //startup stuff is done in @Before method.
    }

    @Before
    public void startupServerAndBrowser() {
        ninjaTestServer = new NinjaTestServer();
        ninjaTestBrowser = new NinjaTestBrowser();
    }

    /**
     * Something like http://localhost:8080/ 
     * 
     * Will contain trailing slash!
     * @return
     */
    public String getServerAddress() {
		return ninjaTestServer.getServerAddress();
	}

    public URI getServerAddressAsUri() {
        return ninjaTestServer.getServerAddressAsUri();
    }

    @After
    public void shutdownServerAndBrowser() {
        ninjaTestServer.shutdown();
        ninjaTestBrowser.shutdown();
    }

}
