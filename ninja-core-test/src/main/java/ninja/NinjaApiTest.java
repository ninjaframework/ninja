package ninja;


import java.net.URI;

import org.junit.After;
import org.junit.Before;

/**
 * Baseclass for tests that require a running server.
 * 
 * @author rbauer
 * 
 */
public class NinjaApiTest {

	NinjaIntegrationTestHelper ninjaIntegrationTestHelper;
	
	public NinjaApiTest() {
		//nothing to do...
    }

    @Before
    public void startupServer() {
        ninjaIntegrationTestHelper = new NinjaIntegrationTestHelper();
    }

    /**
     * Something like http://localhost:8080/ 
     * 
     * Will contain trailing slash!
     * @return
     */
    public String getServerAddress() {
		return ninjaIntegrationTestHelper.getServerAddress();
	}

    public URI getServerAddressAsUri() {
        return ninjaIntegrationTestHelper.getServerAddressAsUri();
    }

    @After
    public void shutdownServer() {
        ninjaIntegrationTestHelper.shutdown();
    }

}
