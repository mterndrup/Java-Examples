package com.parsers.JSON;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Vector;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.beans.Addresses;
import com.beans.DailyData;
import com.beans.Intervals;

public class JSON_Parser {
	static InputStream is = null;
	static JSONObject jObj = null;
	static String json = "";

	// constructor
	public JSON_Parser() {
	}

	public JSONObject getJSONFromUrl(String url) {
		// Making HTTP request
		try {
			// defaultHttpClient
			DefaultHttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(url);
			HttpResponse httpResponse = httpClient.execute(httpGet);
			HttpEntity httpEntity = httpResponse.getEntity();
			is = httpEntity.getContent();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					is, "iso-8859-1"), 8);
			StringBuilder sb = new StringBuilder();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			is.close();
			json = sb.toString();
		} catch (Exception e) {
			Log.e("Buffer Error", "Error converting result " + e.toString());
		}
		// try parse the string to a JSON object
		try {
			jObj = new JSONObject(json);
		} catch (JSONException e) {
			Log.e("JSON Parser", "Error parsing data " + e.toString());
		}
		// return JSON String
		return jObj;
	}

	public static DailyData parseJSONObject(JSONObject jObj) {

		DailyData dataBean = new DailyData();
		try {
			JSONObject jsonData = jObj.getJSONObject("data");
			JSONArray temps = jsonData.getJSONArray("tempi");
			Vector<Float> tempis = new Vector<Float>();
			for (int z = 0; z < temps.length(); z++) {
				Float temp = Float.parseFloat(temps.get(z).toString());
				tempis.add(temp);
			}
			dataBean.setTemps(tempis);

			dataBean.setCityAverage(Float.parseFloat(jsonData
					.getString("cityaverage")));
			dataBean.setTotalCost(Float.parseFloat(jsonData
					.getString("totalCost")));
			dataBean.setTotalkWh(Float.parseFloat(jsonData
					.getString("totalkwh")));
			JSONArray intervals = jsonData.getJSONArray("intervals");
			DecimalFormat format = new DecimalFormat("0.00");
			Vector<Intervals> ints = new Vector<Intervals>();
			for (int i = 0; i < intervals.length(); i++) {
				Intervals intCurrent = new Intervals();
				JSONObject j = intervals.getJSONObject(i);
				Double current = Double.parseDouble(j.getString("cost"));
				intCurrent.setCost(Float.parseFloat(format.format(current)));
				intCurrent.setKw(Float.parseFloat(j.getString("kw")));
				intCurrent.setDate(j.getString("date"));
				ints.add(intCurrent);
			}
			dataBean.setIntervals(ints);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataBean;
	}
	
	public static ArrayList<Addresses> parseJSONAddressSearch(JSONObject jObj) {

		ArrayList<Addresses> dataBean = new ArrayList<Addresses>();
		try {
			JSONArray objects = jObj.getJSONArray("objects");
			for (int z = 0; z < objects.length(); z++) {
				com.beans.Addresses currentAddress = new com.beans.Addresses();
				JSONObject j = (JSONObject) objects.getJSONObject(z);
				currentAddress.setAddress(j.getString("address"));
				currentAddress.setPremiseid(j.getString("premiseid"));
				dataBean.add(currentAddress);
			}

		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataBean;
	}
}
