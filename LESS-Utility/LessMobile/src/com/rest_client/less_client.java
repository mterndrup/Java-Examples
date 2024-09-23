package com.rest_client;

import org.json.JSONObject;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.Server;
import retrofit.client.Response;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

public class less_client {

	private static final String API_URL = "https://mobileapi.lessutility.com";

	interface LESS {
		@FormUrlEncoded
		@POST("/v1/user/login/")
		Response postLogin(@Field("username") String username,
				@Field("password") String password, Callback<JSONObject> jObj);
	}

	public static void main(String... args) {
		RequestInterceptor requestInterceptor = new RequestInterceptor() {

			@Override
			public void intercept(RequestFacade request) {
				request.addHeader("Accept", "application/json");
				request.addHeader("Content-type", "application/json");
			}
		}; //
			// Create a very simple REST adapter which points the LESS API
			// //
			// endpoint.
		RestAdapter restAdapter = new RestAdapter.Builder()
				.setEndpoint(new Server(API_URL))
				.setRequestInterceptor(requestInterceptor).build();

		// Create an instance of our LESS API interface.
		LESS less = restAdapter.create(LESS.class);
		//Callback c = new Callback<User>();
		Response user = less.postLogin("terndrupm@lessmob.com", "blacki04", null);

		
		int test = 0;test++;
	}

}
