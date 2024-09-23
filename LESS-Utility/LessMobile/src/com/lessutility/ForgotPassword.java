package com.lessutility;

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
import retrofit.RequestInterceptor.RequestFacade;

import com.beans.User;
import com.crashlytics.android.Crashlytics;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * This is the page where a user can reset their password when having login
 * issues
 * 
 * **/
public class ForgotPassword extends Activity {

	private PopupWindow resetPopup;
	RelativeLayout panel;
	Typeface font;
	EditText forgot;
	LayoutInflater inflaterPopup;
	static View layoutOverlay;
	private static LinearLayout topLevel;
	public static JSONObject result;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forgotpassword);
		Crashlytics.start(this);
		inflaterPopup = (LayoutInflater) ForgotPassword.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutOverlay = inflaterPopup.inflate(R.layout.popup_error,
				(ViewGroup) findViewById(R.id.popup_element));
		topLevel = (LinearLayout) findViewById(R.id.inside);
		font = Typeface.createFromAsset(getAssets(), "Lato-Light.ttf");
		panel = (RelativeLayout) findViewById(R.id.panel);

		forgot = (EditText) findViewById(R.id.forgotpassword_edittext);
		forgot.setTypeface(font);
		Button reset = (Button) findViewById(R.id.reset_button);
		reset.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				resetPopup();
			}

		});
		reset.setTypeface(font);

		Button cancel = (Button) findViewById(R.id.cancel_button);
		cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(ForgotPassword.this,
						MainActivity.class);
				startActivity(intent);
			}

		});
		cancel.setTypeface(font);
	}

	private void resetPopup() {
		try {
			// We need to get the instance of the LayoutInflater
			LayoutInflater inflater = (LayoutInflater) ForgotPassword.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.popup_reset_password,
					(ViewGroup) findViewById(R.id.popup_element));

			TextView areyousure = (TextView) layout
					.findViewById(R.id.areyousure);
			areyousure.setTypeface(font);
			int width = panel.getWidth();
			int height = panel.getHeight();
			resetPopup = new PopupWindow(layout, width, (int) (height*1.1f), true);
			resetPopup.setOutsideTouchable(true);
			resetPopup.setBackgroundDrawable(new BitmapDrawable());
			resetPopup.setFocusable(true);
			resetPopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

			Button cancel = (Button) layout
					.findViewById(R.id.cancel_button_popup);
			cancel.setOnClickListener(cancel_button_click_listener);
			Button reset = (Button) layout
					.findViewById(R.id.reset_button_popup);
			RelativeLayout inside = (RelativeLayout) layout
					.findViewById(R.id.inside);
			inside.setOnClickListener(cancel_button_click_listener);
			reset.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					String text = forgot.getText().toString();
					if("".equals(text)){
						initiateError(false);
					}else{
						resetPassword();
						resetPopup.dismiss();
						Intent intent = new Intent(ForgotPassword.this, MainActivity.class);
						startActivity(intent);
					}
				}

			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private OnClickListener cancel_button_click_listener = new OnClickListener() {
		public void onClick(View v) {
			resetPopup.dismiss();

		}
	};
	
	private static PopupWindow opaqueoverlay;

	private void initiateError(final boolean connection) {
		try {
			ForgotPassword.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					int width =  topLevel.getWidth();
					int height =  topLevel.getHeight();
					int popupHeight = (int) (height);
					opaqueoverlay = new PopupWindow(layoutOverlay, width,
							popupHeight, true);
					opaqueoverlay.setTouchable(true);
					opaqueoverlay.setFocusable(false);
					opaqueoverlay.setOutsideTouchable(false);
					opaqueoverlay.setBackgroundDrawable(new BitmapDrawable());
					opaqueoverlay.showAtLocation(layoutOverlay, Gravity.CENTER,
							0, 0);
					LinearLayout element = (LinearLayout) layoutOverlay
							.findViewById(R.id.popup_element);
					TextView text = (TextView) layoutOverlay
							.findViewById(R.id.popup_element);
					text.setTypeface(MainActivity.font);
					if(!connection){
						text.setText("Enter an email address");
					}
					element.setOnClickListener(cancel_overlay_click_listener);
					Button cancel = (Button) layoutOverlay.findViewById(R.id.cancel_button);
					cancel.setOnClickListener(cancel_overlay_click_listener);
					Button retry = (Button) layoutOverlay.findViewById(R.id.retry_button);
					retry.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
								
						}
					});
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


	
	public boolean resetPassword() {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		String url = "https://mobileapi.lessutility.com/v1/user/password";
		HttpPost httpPost = new HttpPost(url);
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
			jsonObject.accumulate("email", forgot.getText().toString());

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
		} catch (Exception e) {
			errorOut = true;
			initiateError(true);
			// }
			// });
		}
		if (result == null) {
			initiateError(true);
			errorOut = true;
		} else {

			if (errorOut) {
				initiateError(false);
			} else {
				//dialog.dismiss();
				//Intent intent = new Intent(MainActivity.this, Tabs.class);
				//startActivity(intent);
			}

		}
		return errorOut;
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
