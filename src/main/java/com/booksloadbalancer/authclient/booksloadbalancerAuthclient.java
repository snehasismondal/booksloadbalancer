package com.booksloadbalancer.authclient;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONObject;
import org.springframework.context.annotation.Configuration;
@Configuration
public class booksloadbalancerAuthclient {

	
	public static String getOAuthToken(String oauthurl) {
		String clientId="bookservice";
		String clientSecret="123456";
		String oAuthToken = "";
		try (CloseableHttpClient httpclient = HttpClientBuilder.create().build()) {
			//String oauthurl = "http://localhost:1000/oauth/token";
			HttpPost httppost = new HttpPost(oauthurl);
			String serviceresponse = "";

			// client_id:client_secret value is encoded and passed as request header
			byte[] encodedBytes = Base64.encodeBase64((clientId + ":" + clientSecret).getBytes());
			String basicToken =   new String(encodedBytes) ;
			System.out.println(":::"+basicToken);
			System.out.println("basicToken " + basicToken);
			httppost.addHeader("Content-Type", "application/x-www-form-urlencoded");
			httppost.addHeader("Authorization", "Basic " + basicToken);
			/* set the post body request */
			StringEntity entity = new StringEntity("grant_type=client_credentials");//body of the request
			httppost.setEntity(entity);

			ResponseHandler<String> responseHandler = new BasicResponseHandler();
			serviceresponse = httpclient.execute(httppost, responseHandler);
			JSONObject response = new JSONObject(serviceresponse);
			System.out.println(response);
			oAuthToken = response.getString("access_token");

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			System.out.println("Exception in getOAuthToken OAuthTokenUtil " + ex);
		}
		return oAuthToken;

	}
}
