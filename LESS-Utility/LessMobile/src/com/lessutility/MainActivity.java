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
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beans.User;
import com.crashlytics.android.Crashlytics;
import com.db.DatabaseHandler;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.parsers.JSON.JSON_Post;

/**
 * This is the 1st page a user sees, also called the login page
 * **/
public class MainActivity extends Activity {

	public static Typeface font;
	protected AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
	public static boolean loggedin = false, errorOut;
	int interval = 0;
	Animation moveup;
	DisplayMetrics metrics;
	LayoutInflater inflaterPopup;
	static View layoutOverlay;

	JSONArray user = null;
	public static String access_token;
	public static JSONObject result;
	final User userInfo = new User();
	TextView error;
	DatabaseHandler db;
	static ProgressDialog dialog;
	String url = "https://mobileapi.lessutility.com/v1/user/session";
	private static LinearLayout topLevel;
	TextView back;
	TextView retry;
	private String android_id;
	
	TextView tvRegStatusResult;
	EditText email, password;
	Context context;

	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
		setContentView(R.layout.activity_main);
		metrics = getResources().getDisplayMetrics();
		context = this.getBaseContext();
		db = new DatabaseHandler(this);
		final int dbSize = db.getContactsCount();
		font = Typeface.createFromAsset(getAssets(), "Lato-Light.ttf");

		android_id = Secure.getString(this.getContentResolver(),
				Secure.ANDROID_ID);

		inflaterPopup = (LayoutInflater) MainActivity.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		

		topLevel = (LinearLayout) findViewById(R.id.inside);
		
		/**
		 * The following segment of code initiates the fade-in animation It
		 * also plays the sound that occurs when loading the page
		 **/

		final RelativeLayout fadein_box = (RelativeLayout) findViewById(R.id.fadein_box);
		fadein_box.startAnimation(fadeIn);
		MediaPlayer mp = MediaPlayer.create(MainActivity.this,
				R.raw.login_sound);
		
		final RelativeLayout bottom_area = (RelativeLayout) findViewById(R.id.bottom_area);
		// moveup = AnimationUtils.loadAnimation(getApplicationContext(),
		// R.anim.moveup);
		// bottom_area.startAnimation(moveup);

		email = (EditText) findViewById(R.id.edittext_email_login);
		email.setTypeface(font);
		password = (EditText) findViewById(R.id.edittext_pass_login);
		password.setTypeface(font);
		error = (TextView) findViewById(R.id.error);
		error.setTypeface(font);
		error.setVisibility(View.GONE);
		Button login = (Button) findViewById(R.id.login_button);
		final JSON_Post jP = new JSON_Post();
		login.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

				if (isConnected(context)) {
					userInfo.setUsername("" + email.getText());
					userInfo.setPassword("" + password.getText());
					dialog = ProgressDialog.show(MainActivity.this, "",
							"Loading. Please wait...", true);

					dialog.setCanceledOnTouchOutside(true);
					new MyTask(dialog).execute();
				} else {
					initiateError(false);
				}
			}
		});
		login.setTypeface(font);

		TextView createAccount = (TextView) findViewById(R.id.login_createAccount_textview);
		createAccount.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						FindHome.class);
				startActivity(intent);
			}

		});
		createAccount.setTypeface(font);

		TextView forgotpass = (TextView) findViewById(R.id.login_forgotpassword);
		forgotpass.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(MainActivity.this,
						ForgotPassword.class);
				startActivity(intent);
			}

		});
		forgotpass.setTypeface(font);


		if (dbSize == 0) {
			
			// fadeIn.setDuration(500);
			// fadeIn.setFillAfter(true);
			if (!loggedin) {
				// mp.start();
			}
			
		} else {
			access_token = db.getToken();
			Intent intent = new Intent(MainActivity.this, Tabs.class);
			startActivity(intent);
		}

	}
	
	

	public class MyTask extends AsyncTask<Void, Void, Void> {
		public MyTask(ProgressDialog progress) {
			dialog = progress;
		}

		public void onPreExecute() {
			dialog.show();
		}

		public Void doInBackground(Void... unused) {
			new Thread(new Runnable() {
				public void run() {
					errorOut = POST_Login(url, userInfo);
				}
			}).start();
			return null;
		}

	}

	
	

	public boolean POST_Login(String url, User user) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);
		InputStream inputStream = null;
		String resultString = "";
		JSONObject obj = null;
		boolean errorOut = false;

		try {
			// Adds the post data
			String json = "";

			// build jsonObject
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("username", email.getText().toString());
			jsonObject.accumulate("password", password.getText().toString());

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
				access_token = result.getString("access_token");
				db.addContact(access_token);
			}
		} catch (Exception e) {
			// Log.d("InputStream", e.getLocalizedMessage());
			// handler.post(new Runnable() {
			// public void run() {
			// dialog.dismiss();
			// error.setVisibility(View.VISIBLE);
			initiateError(true);
			// }
			// });
		}
		if (result == null) {
			initiateError(true);
		} else {

			if (errorOut) {
				initiateError(false);
			} else {
				dialog.dismiss();
				Intent intent = new Intent(MainActivity.this, Tabs.class);
				startActivity(intent);
			}

		}
		return errorOut;
	}

	private static PopupWindow opaqueoverlay;

	private void initiateError(final boolean connection) {
		try {
			MainActivity.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					dialog.dismiss();
					if (connection) {
						error.setText("Unable to Connect to Internet");
						error.setVisibility(View.VISIBLE);
						/*
						int width =  topLevel.getWidth();
						int height =  topLevel.getHeight();
						int popupHeight = (int) (height);
						layoutOverlay = inflaterPopup.inflate(R.layout.popup_error,
								(ViewGroup) findViewById(R.id.popup_element));
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
						element.setOnClickListener(cancel_overlay_click_listener);
						Button cancel = (Button) layoutOverlay.findViewById(R.id.cancel_button);
						cancel.setOnClickListener(cancel_overlay_click_listener);
						Button retry = (Button) layoutOverlay.findViewById(R.id.retry_button);
						retry.setOnClickListener(new OnClickListener() {

							public void onClick(View v) {
									
							}
						});
						*/
					} else {
						error.setText("Incorrect email or password");
						error.setVisibility(View.VISIBLE);
					}
				}
			});

			// opaqueoverlay.showAtLocation(layoutOverlay, Gravity.CENTER, 0,
			// 0);
			// LinearLayout element = (LinearLayout) layoutOverlay
			// .findViewById(R.id.popup_element);

			// retry.setOnClickListener(cancel_overlay_click_listener);
			// back.setOnClickListener(cancel_overlay_click_listener);
			// element.setOnClickListener(cancel_overlay_click_listener);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static OnClickListener cancel_overlay_click_listener = new OnClickListener() {
		public void onClick(View v) {
			opaqueoverlay.dismiss();

		}
	};

	

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

	public static boolean isConnected(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
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
	
	public void onBackPressed() {
		
	}

}
