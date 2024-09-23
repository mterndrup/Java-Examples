package com.parsers.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import android.util.Log;

import com.beans.User;

public class JSON_Post {


	public JSONObject retrofit_POST_Login(String url, User user) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		InputStream inputStream = null;
		String result = "";
		JSONObject obj = null;

		try {
			// Adds the post data
			String json = "";

			// build jsonObject
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("username", "terndrupm@lessmob.com");
			jsonObject.accumulate("password", "blacki04");

			// convert JSONObject to JSON to String
			json = jsonObject.toString();

			// set json to StringEntity
			StringEntity se = new StringEntity(json);

			// set httpPost Entity
			httpPost.setEntity(se);

			// Set some headers to inform server about the type of the
			// content
			RequestInterceptor requestInterceptor = new RequestInterceptor() {
				@Override
				public void intercept(RequestFacade request) {
					request.addHeader("Accept", "application/json");
					request.addHeader("Content-type", "application/json");
				}
			};
			
			RestAdapter restAdapter = new RestAdapter.Builder()
			  .setEndpoint(url)
			  .setRequestInterceptor(requestInterceptor)
			  .build();

			// Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				result = convertInputStreamToString(inputStream);
			else
				result = "Did not work!";

			obj = new JSONObject(result);
		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		return obj;
	}

	private static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}
}
