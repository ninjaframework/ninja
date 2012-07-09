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

    public String getServerAddress() {
		return ninjaIntegrationTestHelper.getServerAddress();
	}

    @After
    public void shutdownServer() {
        ninjaIntegrationTestHelper.shutdown();
    }

}
