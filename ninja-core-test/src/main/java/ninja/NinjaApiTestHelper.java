package ninja;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.google.common.collect.Maps;

public class NinjaApiTestHelper {

	public static String makeRequest(String url, Map<String, String> headers) {

		StringBuffer sb = new StringBuffer();
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();
			
			HttpGet getRequest = new HttpGet(url);
			
			//add all headers
			for (Entry<String, String> header : headers.entrySet()) {
				getRequest.addHeader(header.getKey(), header.getValue());
			}
			
			HttpResponse response;

			response = httpClient.execute(getRequest);

			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
				        + response.getStatusLine().getStatusCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(
			        (response.getEntity().getContent())));

			String output;
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}

			httpClient.getConnectionManager().shutdown();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return sb.toString();

	}
	
	public static String makeJsonRequest(String url) {
		
		Map<String, String> headers = Maps.newHashMap();
		headers.put("accept", "application/json");
		
		return makeRequest(url, headers);

	}

}
