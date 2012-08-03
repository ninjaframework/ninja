package ninja;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import com.google.common.collect.Maps;
import org.codehaus.jackson.map.ObjectMapper;

public class NinjaApiTestHelper {

	public static HttpResponse makeRequestAndGetResponse(String url,
	        Map<String, String> headers) {

		HttpResponse response = null;

		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpGet getRequest = new HttpGet(url);

			// add all headers
			for (Entry<String, String> header : headers.entrySet()) {
				getRequest.addHeader(header.getKey(), header.getValue());
			}

			response = httpClient.execute(getRequest);
			httpClient.getConnectionManager().shutdown();

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;

	}

    public static String makeRequest(String url) {
        return makeRequest(url, null);
    }

    public static String makeRequest(String url, Map<String, String> headers) {

		StringBuffer sb = new StringBuffer();
		try {

			DefaultHttpClient httpClient = new DefaultHttpClient();

			HttpGet getRequest = new HttpGet(url);

            if (headers != null) {
                // add all headers
                for (Entry<String, String> header : headers.entrySet()) {
                    getRequest.addHeader(header.getKey(), header.getValue());
                }
            }

			HttpResponse response;

			response = httpClient.execute(getRequest);

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

	public static String uploadFile(String url, String paramName,
	        File fileToUpload) {

		String response = null;

		try {

			HttpClient client = new DefaultHttpClient();
			client.getParams().setParameter(
			        CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

			HttpPost post = new HttpPost(url);
			MultipartEntity entity = new MultipartEntity(
			        HttpMultipartMode.BROWSER_COMPATIBLE);

			// For File parameters
			entity.addPart(paramName, new FileBody(((File) fileToUpload),
			        "application/zip"));

			post.setEntity(entity);

			// Here we go!
			response = EntityUtils.toString(client.execute(post).getEntity(),
			        "UTF-8");
			
			client.getConnectionManager().shutdown();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return response;

	}

	public static String makeJsonRequest(String url) {

		Map<String, String> headers = Maps.newHashMap();
		headers.put("accept", "application/json; charset=utf-8");

		return makeRequest(url, headers);

	}

    public static String postJson(String url, Object object) {

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");

        HttpClient client = new DefaultHttpClient();
        try {
            client.getParams().setParameter(
                    CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(new ObjectMapper().writeValueAsString(object));
            entity.setContentType("application/json");
            post.setEntity(entity);

            // Here we go!
            return EntityUtils.toString(client.execute(post).getEntity(),
                    "UTF-8");

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            client.getConnectionManager().shutdown();
        }
    }
}
