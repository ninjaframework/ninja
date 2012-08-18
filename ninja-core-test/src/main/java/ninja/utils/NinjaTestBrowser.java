/**
 * Copyright (C) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ninja.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ninja.NinjaTest;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.collect.Maps;

public class NinjaTestBrowser {

    private DefaultHttpClient httpClient;

    public NinjaTestBrowser() {
        httpClient = new DefaultHttpClient();
    }

    /**
     * The raw HttpClient. You can use it to fire your own requests.
     * 
     * Note 1: This HttpClient will save the state by reusing cookies. You can
     * do login / logout cycles using this class.
     * 
     * Note 2: Will be shut down when calling the shutdown method. This is
     * generally done by another test helper (like {@link NinjaTest}) that
     * encapsulates this class.
     * 
     * @return The HttpClient. Ready and there to be used.
     */
    public HttpClient getHttpClient() {

        return this.httpClient;

    }

    /**
     * @return all cookies saved by this TestBrowser
     */
    public List<Cookie> getCookies() {
        return httpClient.getCookieStore().getCookies();
    }

    public Cookie getCookieWithName(String name) {

        List<Cookie> cookies = getCookies();

        // skip through cookies and return cookie you want
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }

        return null;
    }

    public HttpResponse makeRequestAndGetResponse(String url,
                                                  Map<String, String> headers) {

        HttpResponse response = null;

        try {

            HttpGet getRequest = new HttpGet(url);

            // add all headers
            for (Entry<String, String> header : headers.entrySet()) {
                getRequest.addHeader(header.getKey(), header.getValue());
            }

            response = httpClient.execute(getRequest);
            getRequest.reset();

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return response;

    }

    public String makeRequest(String url) {
        return makeRequest(url, null);
    }

    public String makeRequest(String url, Map<String, String> headers) {

        StringBuffer sb = new StringBuffer();
        try {

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

            getRequest.releaseConnection();

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sb.toString();

    }

    public String makePostRequestWithFormParameters(String url,
                                                    Map<String, String> headers,
                                                    Map<String, String> formParameters) {

        StringBuffer sb = new StringBuffer();

        try {

            HttpPost postRequest = new HttpPost(url);

            if (headers != null) {
                // add all headers
                for (Entry<String, String> header : headers.entrySet()) {
                    postRequest.addHeader(header.getKey(), header.getValue());
                }
            }

            // add form parameters:
            List<BasicNameValuePair> formparams = new ArrayList<BasicNameValuePair>();
            if (formParameters != null) {

                for (Entry<String, String> parameter : formParameters
                        .entrySet()) {

                    formparams.add(new BasicNameValuePair(parameter.getKey(),
                            parameter.getValue()));
                }

            }

            // encode form parameters and add
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formparams);
            postRequest.setEntity(entity);

            HttpResponse response;

            response = httpClient.execute(postRequest);

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (response.getEntity().getContent())));

            String output;
            while ((output = br.readLine()) != null) {
                sb.append(output);
            }

            postRequest.releaseConnection();

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return sb.toString();

    }

    public String uploadFile(String url, String paramName, File fileToUpload) {

        String response = null;

        try {

            httpClient.getParams().setParameter(
                    CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost post = new HttpPost(url);

            MultipartEntity entity = new MultipartEntity(
                    HttpMultipartMode.BROWSER_COMPATIBLE);

            // For File parameters
            entity.addPart(paramName, new FileBody((File) fileToUpload));

            post.setEntity(entity);

            // Here we go!
            response = EntityUtils.toString(httpClient.execute(post)
                    .getEntity(), "UTF-8");
            post.releaseConnection();

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

    public String makeJsonRequest(String url) {

        Map<String, String> headers = Maps.newHashMap();
        headers.put("accept", "application/json; charset=utf-8");

        return makeRequest(url, headers);

    }

    public String postJson(String url, Object object) {

        Map<String, String> headers = Maps.newHashMap();
        headers.put("Content-Type", "application/json");

        try {
            httpClient.getParams().setParameter(
                    CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);

            HttpPost post = new HttpPost(url);
            StringEntity entity = new StringEntity(
                    new ObjectMapper().writeValueAsString(object));
            entity.setContentType("application/json");
            post.setEntity(entity);
            post.releaseConnection();

            // Here we go!
            return EntityUtils.toString(httpClient.execute(post).getEntity(),
                    "UTF-8");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void shutdown() {
        httpClient.getConnectionManager().shutdown();
    }
}
