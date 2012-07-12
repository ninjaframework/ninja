package ninja;


import org.junit.After;
import org.junit.Before;

public class NinjaApiTest {

	NinjaIntegrationTestHelper ninjaIntegrationTestHelper;
	
	public NinjaApiTest() {
	    System.out.println("in constructor :)");
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

    @After
    public void shutdownServer() {
        ninjaIntegrationTestHelper.shutdown();
    }

}
