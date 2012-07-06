package ninja;


public class NinjaApiTest {

	NinjaIntegrationTestHelper ninjaIntegrationTestHelper;
	
	public NinjaApiTest() {
	    System.out.println("in constructor :)");
	    ninjaIntegrationTestHelper = new NinjaIntegrationTestHelper();
    }
	
	
	public String getServerAddress() {
		return ninjaIntegrationTestHelper.getServerAddress();
	}
	

}
