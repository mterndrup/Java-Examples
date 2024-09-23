package com.lessutility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.beans.Addresses;
import com.crashlytics.android.Crashlytics;
import com.db.DatabaseHandler;
import com.google.android.gcm.GCMRegistrar;

/**
 * This page the last one during the signup process The user selects the
 * notification options they want associated with their new account
 * **/
public class SignUp extends Activity {
	Addresses selectedAddress;
	private boolean isExpanded, isOn = true, isDaily, isWeekly;
	TextView signup_email, password;
	EditText edittext_email, edittext_password;
	CheckBox dailyCheck, weeklyCheck;
	DatabaseHandler db;
	private static ScrollView topLevel;
	LayoutInflater inflaterPopup;
	static View layoutOverlay;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		Crashlytics.start(this);
		db = new DatabaseHandler(this);
		inflaterPopup = (LayoutInflater) SignUp.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutOverlay = inflaterPopup.inflate(R.layout.popup_single,
				(ViewGroup) findViewById(R.id.popup_element));
		topLevel = (ScrollView) findViewById(R.id.full_screen);
		selectedAddress = SelectAddress.selectedFromList;

		Typeface font = Typeface.createFromAsset(getAssets(),
				"Raleway-Thin.ttf");

		final LinearLayout hiddenSettings = (LinearLayout) findViewById(R.id.hidden_notification_settings);
		RelativeLayout clickable = (RelativeLayout) findViewById(R.id.notification_settings);

		TextView address_header = (TextView) findViewById(R.id.signup_address);
		TextView address = (TextView) findViewById(R.id.signup_address_set);
		address.setText(selectedAddress.getAddress());
		signup_email = (TextView) findViewById(R.id.signup_email);
		edittext_email = (EditText) findViewById(R.id.edittext_email);
		edittext_email.setTypeface(font);
		edittext_password = (EditText) findViewById(R.id.edittext_password);
		edittext_password.setTypeface(font);
		password = (TextView) findViewById(R.id.signup_password);
		TextView pageTitle = (TextView) findViewById(R.id.pageTitle);
		TextView appNotify = (TextView) findViewById(R.id.appNotify);
		address_header.setTypeface(font);
		address.setTypeface(font);
		signup_email.setTypeface(font);
		password.setTypeface(font);
		pageTitle.setTypeface(font);
		appNotify.setTypeface(font);
		TextView notifications = (TextView) findViewById(R.id.textview_notify_settings);
		notifications.setTypeface(font);
		if (true == Tabs.editButton) {
			signup_email.setVisibility(View.GONE);
			edittext_email.setVisibility(View.GONE);
			password.setVisibility(View.GONE);
			edittext_password.setVisibility(View.GONE);
		}

		final ImageView arrow = (ImageView) findViewById(R.id.signup_arrow);
		final Matrix matrix = new Matrix();
		arrow.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				int flip = 1;
				if (!isExpanded) {
					flip = -1;
					hiddenSettings.setVisibility(View.VISIBLE);

				} else {
					hiddenSettings.setVisibility(View.GONE);

				}
				arrow.setScaleType(ScaleType.MATRIX); // required
				matrix.postRotate(270f * flip, arrow.getDrawable().getBounds()
						.width() / 2,
						arrow.getDrawable().getBounds().height() / 2);
				arrow.setImageMatrix(matrix);
				isExpanded = !isExpanded;
			}

		});

		clickable.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				int flip = 1;
				if (!isExpanded) {
					flip = -1;
					hiddenSettings.setVisibility(View.VISIBLE);

				} else {
					hiddenSettings.setVisibility(View.GONE);

				}
				arrow.setScaleType(ScaleType.MATRIX); // required
				matrix.postRotate(270f * flip, arrow.getDrawable().getBounds()
						.width() / 2,
						arrow.getDrawable().getBounds().height() / 2);
				arrow.setImageMatrix(matrix);
				isExpanded = !isExpanded;
			}

		});

		dailyCheck = (CheckBox) findViewById(R.id.CheckboxAppDaily);
		dailyCheck.setTypeface(Tabs.font);
		dailyCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (dailyCheck.isChecked()) {
					dailyCheck.setButtonDrawable(R.drawable.checkbox_on);
				} else {
					dailyCheck.setButtonDrawable(R.drawable.checkbox_off);

				}
			}
		});
		weeklyCheck = (CheckBox) findViewById(R.id.CheckboxAppWeekly);
		weeklyCheck.setTypeface(Tabs.font);
		weeklyCheck.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (weeklyCheck.isChecked()) {
					weeklyCheck.setButtonDrawable(R.drawable.checkbox_on);
				} else {
					weeklyCheck.setButtonDrawable(R.drawable.checkbox_off);

				}
			}
		});
		View checkboxView = findViewById(R.id.toggle);
		if (checkboxView != null && checkboxView instanceof Checkable) {

			com.draw.SwitchButton switchButton = (com.draw.SwitchButton) checkboxView;
			switchButton
					.setOnCheckedChangeListener(new OnCheckedChangeListener() {

						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							// TODO Auto-generated method stub
							if (isOn) {
								dailyCheck.setEnabled(false);
								dailyCheck
										.setButtonDrawable(R.drawable.checkbox_disabled);
								dailyCheck.setTextColor(Color.GRAY);
								weeklyCheck.setEnabled(false);
								weeklyCheck
										.setButtonDrawable(R.drawable.checkbox_disabled);
								weeklyCheck.setTextColor(Color.GRAY);
							} else {
								dailyCheck.setEnabled(true);
								dailyCheck
										.setButtonDrawable(R.drawable.checkbox_off);
								dailyCheck.setTextColor(Color.WHITE);
								weeklyCheck.setEnabled(true);
								weeklyCheck
										.setButtonDrawable(R.drawable.checkbox_off);
								weeklyCheck.setTextColor(Color.WHITE);
							}
							isOn = !isOn;
						}
					});
		}

		Button register = (Button) findViewById(R.id.register_button);
		register.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (true == Tabs.editButton) {
					addPremise();
				} else {
					String email = edittext_email.getText().toString();
					String pass = edittext_password.getText().toString();
					int passSize = pass.length();
					if ("".equals(email) || null == email) {
						initiateError(false, false);
					} else {
						boolean valid = isEmailValid(email);
						if (valid) {
							if (passSize > 5) {
								registerUser();
							} else {
								initiateError(true, false);
							}

						} else {
							initiateError(false, false);
						}

					}

				}

			}

		});
		register.setTypeface(font);

		Button back = (Button) findViewById(R.id.back_button);
		back.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(SignUp.this, SelectAddress.class);
				startActivity(intent);
			}

		});
		back.setTypeface(font);
	}

	public boolean POST_Login() {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"https://mobileapi.lessutility.com/v1/user/session");
		InputStream inputStream = null;
		String resultString = "";
		JSONObject obj = null;
		boolean errorOut = false;
		Handler handler = new Handler(Looper.getMainLooper());

		try {
			// Adds the post data
			String json = "";

			// build jsonObject
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("username", edittext_email.getText()
					.toString());
			jsonObject.accumulate("password", edittext_password.getText()
					.toString());

			// convert JSONObject to JSON to String
			json = jsonObject.toString();

			// set json to StringEntity
			StringEntity se = new StringEntity(json);

			// set httpPost Entity
			httpPost.setEntity(se);

			// Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			// Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				resultString = convertInputStreamToString(inputStream);
			else
				resultString = "Did not work!";

			obj = new JSONObject(resultString);
			MainActivity.result = obj;
			try {
				String errorString = MainActivity.result.getString("errors");
				if (0 < errorString.length()) {
					errorOut = true;
				}
			} catch (Exception e) {

			}
			if (!errorOut) {
				MainActivity.access_token = MainActivity.result
						.getString("access_token");
				db.addContact(MainActivity.access_token);
			}
		} catch (Exception e) {

		}

		return errorOut;
	}

	private static PopupWindow opaqueoverlay;

	private void initiateError(final boolean pass, final boolean multiple) {
		try {
			SignUp.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					int width = topLevel.getWidth();
					int height = topLevel.getHeight();
					int popupHeight = (int) ((int) (height) * 1.1f);
					opaqueoverlay = new PopupWindow(layoutOverlay, width,
							popupHeight, true);
					opaqueoverlay.setTouchable(true);
					opaqueoverlay.setFocusable(false);
					opaqueoverlay.setOutsideTouchable(false);
					opaqueoverlay.setBackgroundDrawable(new BitmapDrawable());
					opaqueoverlay.showAtLocation(layoutOverlay, Gravity.CENTER,
							0, 0);
					TextView error = (TextView) layoutOverlay
							.findViewById(R.id.error);
					error.setTypeface(MainActivity.font);
					if (pass) {
						error.setText("Password must contain at least six characters");
					} else {
						if (!multiple) {
							error.setText("Email Address is already in use");
						} else {
							error.setText("This email address is already in use");
						}
					}
					LinearLayout element = (LinearLayout) layoutOverlay
							.findViewById(R.id.popup_element);
					element.setOnClickListener(cancel_overlay_click_listener);
					Button cancel = (Button) layoutOverlay
							.findViewById(R.id.cancel_button);
					cancel.setOnClickListener(cancel_overlay_click_listener);

				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static OnClickListener cancel_overlay_click_listener = new OnClickListener() {
		public void onClick(View v) {
			opaqueoverlay.dismiss();

		}
	};

	public void addPremise() {
		boolean failed = false;
		int v = 0;
		for (int i = 0; i < Tabs.addresses.length(); i++) {
			String added = selectedAddress.getPremiseid();
			try {
				JSONObject json = Tabs.addresses.getJSONObject(i);
				String address = json.getString("premise_id");
				int z = added.compareTo(address);
				if (z > 0) {
					v = i + 1;
				}else{
					v = i;
				}
			} catch (JSONException e) {
				// e.printStackTrace();
			}
		}
		try {
			URL url = new URL(
					"https://mobileapi.lessutility.com/v1/user/premise/"
							+ selectedAddress.getPremiseid() + "?token="
							+ MainActivity.access_token);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("PUT");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			OutputStreamWriter osw = new OutputStreamWriter(
					connection.getOutputStream());

			osw.flush();
			osw.close();
			System.err.println(connection.getResponseCode());
		} catch (Exception e) {
			failed = true;
		}
		Calendar now = Calendar.getInstance();
		int year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH); // Note: zero based!
		int day = now.get(Calendar.DAY_OF_MONTH);
		if (month < 10) {
			Tabs.dateString = "0" + (month + 1) + "/";
		} else {
			Tabs.dateString = (month + 1) + "/";
		}
		if (day < 10) {
			Tabs.dateString += "0" + (day - 1) + "/" + year;
		} else {
			Tabs.dateString += (day - 1) + "/" + year;
		}
		POST_Notifications();
		Tabs.getUser();
		int value = Tabs.getJSON(v);
		if (value == 1) {
			Tabs.mTabHost.invalidate();
		} else {
			Tabs.reloadData();
		}
		Tabs.currentAddress = v;
		Tabs.editButton = false;
		Intent intent = new Intent(SignUp.this, Tabs.class);
		startActivity(intent);
	}

	public boolean POST_Notifications() {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		String url = "https://mobileapi.lessutility.com/v1/user/notifications/push/"
				+ GCMRegistrar.getRegistrationId(this)
				+ "?token="
				+ MainActivity.access_token + "&device_id=yes";
		HttpPost httpPost = new HttpPost(url);
		InputStream inputStream = null;
		String resultString = "";
		JSONObject obj = null;
		boolean errorOut = false;
		Handler handler = new Handler(Looper.getMainLooper());
		JSONObject result;
		try {
			// Adds the post data
			String json = "";

			// build jsonObject
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("device_type", "1");
			jsonObject.accumulate("device_id",
					GCMRegistrar.getRegistrationId(this));
			JSONArray pushArray = new JSONArray();
			JSONObject pushObject = new JSONObject();
			int pushType = 0;
			if (isOn) {
				jsonObject.accumulate("push_enabled", "1");
				if (dailyCheck.isChecked()) {
					pushType = 1;
				}
				if (weeklyCheck.isChecked()) {
					if (pushType == 1) {
						pushType = 3;
					} else {
						pushType = 2;
					}
				}
				// weeklypush.accumulate("notification_time", "15");
				pushObject.accumulate("premise_id",
						selectedAddress.getPremiseid());
				pushObject.accumulate("notification_frequency", pushType);
				pushObject.accumulate("notification_time", "15");
				pushArray.put(pushObject);
			}
			pushArray.put(pushObject);
			jsonObject.accumulate("notifications", pushArray);
			// convert JSONObject to JSON to String
			json = jsonObject.toString();

			// set json to StringEntity
			StringEntity se = new StringEntity(json);

			// set httpPost Entity
			httpPost.setEntity(se);

			// Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			// Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				resultString = convertInputStreamToString(inputStream);
			else
				resultString = "Did not work!";

			obj = new JSONObject(resultString);
			result = obj;
			try {
				String errorString = result.getString("errors");
				if (0 < errorString.length()) {
					errorOut = true;
				}
			} catch (Exception e) {

			}
			if (!errorOut) {

			}
		} catch (Exception e) {
			errorOut = true;
		}
		return errorOut;
	}

	public void registerUser() {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(
				"https://mobileapi.lessutility.com/v1/user");
		InputStream inputStream = null;
		String resultString = "";
		JSONObject obj = null;
		String json = "";
		boolean errorOut = false;

		try {
			// Adds the post data

			// build jsonObject
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("premise_id", selectedAddress.getPremiseid());
			jsonObject
					.accumulate("email1", edittext_email.getText().toString());
			jsonObject
					.accumulate("email2", edittext_email.getText().toString());
			jsonObject.accumulate("pass1", edittext_password.getText()
					.toString());
			jsonObject.accumulate("pass2", edittext_password.getText()
					.toString());

			JSONArray jArray = new JSONArray();
			JSONObject pushJSON = new JSONObject();
			pushJSON.accumulate("device_type", "0");
			pushJSON.accumulate("device_id",
					GCMRegistrar.getRegistrationId(this));
			if (isOn) {
				pushJSON.accumulate("push_enabled", "1");
				JSONArray pushArray = new JSONArray();
				JSONObject pushObject = new JSONObject();
				int pushType = 0;
				if (dailyCheck.isChecked()) {
					pushType = 1;
				}
				if (weeklyCheck.isChecked()) {
					if (pushType == 1) {
						pushType = 3;
					} else {
						pushType = 2;
					}
				}
				// weeklypush.accumulate("notification_time", "15");
				pushObject.accumulate("premise_id",
						selectedAddress.getPremiseid());
				pushObject.accumulate("notification_frequency", pushType);
				pushObject.accumulate("notification_time", "15");
				pushArray.put(pushObject);
				pushJSON.accumulate("notifications", pushArray);
			}
			jArray.put(pushJSON);
			jsonObject.accumulate("notification_push", jArray);

			// convert JSONObject to JSON to String
			json = jsonObject.toString();

			// set json to StringEntity
			StringEntity se = new StringEntity(json);

			// set httpPost Entity
			httpPost.setEntity(se);

			// Set some headers to inform server about the type of the
			// content
			httpPost.setHeader("Accept", "application/json");
			httpPost.setHeader("Content-type", "application/json");

			// Execute POST request to the given URL
			HttpResponse httpResponse = httpclient.execute(httpPost);

			// receive response as inputStream
			inputStream = httpResponse.getEntity().getContent();

			// convert inputstream to string
			if (inputStream != null)
				resultString = convertInputStreamToString(inputStream);
			else
				resultString = "Did not work!";

			obj = new JSONObject(resultString);
			try {
				String errorString = obj.getString("errors");
				if (0 < errorString.length()) {
					errorOut = true;
				}
			} catch (Exception e) {

			}
		} catch (Exception e) {
			Log.d("InputStream", e.getLocalizedMessage());
		}
		if (!errorOut) {
			POST_Login();
			Intent intent = new Intent(SignUp.this, Tabs.class);
			startActivity(intent);
		} else {
			initiateError(false, true);
		}
	}

	public boolean isEmailValid(String email) {
		String regExpn = "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
				+ "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
				+ "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
				+ "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
				+ "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

		CharSequence inputStr = email;

		Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(inputStr);

		if (matcher.matches())
			return true;
		else
			return false;
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

	public boolean dispatchTouchEvent(MotionEvent event) {
		View view = getCurrentFocus();
		boolean ret = super.dispatchTouchEvent(event);

		if (view instanceof EditText) {
			View w = getCurrentFocus();
			int scrcoords[] = new int[2];
			w.getLocationOnScreen(scrcoords);
			float x = event.getRawX() + w.getLeft() - scrcoords[0];
			float y = event.getRawY() + w.getTop() - scrcoords[1];

			if (event.getAction() == MotionEvent.ACTION_UP
					&& (x < w.getLeft() || x >= w.getRight() || y < w.getTop() || y > w
							.getBottom())) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(getWindow().getCurrentFocus()
						.getWindowToken(), 0);
			}
		}
		return ret;
	}
}
