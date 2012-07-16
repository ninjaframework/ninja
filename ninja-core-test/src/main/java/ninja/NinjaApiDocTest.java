package ninja;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Map.Entry;

import ninja.utils.NinjaConstant;

import org.apache.http.client.utils.URIBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.devbliss.doctest.DocTest;

/**
 * Superclass for doctests that require a running server. Uses {@link NinjaApiTest} for the server
 * stuff.
 * 
 * @author hschuetz
 * 
 */
public abstract class NinjaApiDocTest extends DocTest {
    private static NinjaIntegrationTestHelper ninjaIntegrationTestHelper;

    public NinjaApiDocTest() {
    }

    @BeforeClass
    public static void startServerInTestMode() {
        System.setProperty(NinjaConstant.MODE_KEY_NAME, NinjaConstant.MODE_TEST);
        ninjaIntegrationTestHelper = new NinjaIntegrationTestHelper();
    }

    @AfterClass
    public static void shutdownServer() {
        ninjaIntegrationTestHelper.shutdown();
    }

    public URI buildUri(String relativePath, Map<String, String> parameters) throws URISyntaxException {
        return build(relativePath, parameters).build();
    }

    public URI buildUri(String relativePath) throws URISyntaxException {
        return build(relativePath, null).build();
    }

    private URIBuilder build(String relativePath, Map<String, String> parameters) {
        URIBuilder uriBuilder =
                new URIBuilder(ninjaIntegrationTestHelper.getServerAddressAsUri()).setPath(relativePath);
        addParametersToURI(parameters, uriBuilder);
        return uriBuilder;
    }

    private void addParametersToURI(Map<String, String> parameters, URIBuilder uriBuilder) {
        if (parameters != null) {
            for (Entry<String, String> param : parameters.entrySet()) {
                uriBuilder.setParameter(param.getKey(), param.getValue());
            }
        }
    }


}
