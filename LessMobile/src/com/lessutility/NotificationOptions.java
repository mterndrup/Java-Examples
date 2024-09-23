package com.lessutility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beans.Notifications;
import com.beans.Switch;
import com.db.DatabaseHandler;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.parsers.JSON.JSON_Parser;

/**
 * This is the Settings page where a user can change their notification options
 * 
 * **/
public class NotificationOptions extends Activity {

	FrameLayout.LayoutParams menuPanelParameters;
	FrameLayout.LayoutParams slidingPanelParameters;
	LinearLayout.LayoutParams headerPanelParameters;
	LinearLayout.LayoutParams listViewParameters;
	private DisplayMetrics metrics;
	private static int panelWidth;
	private RelativeLayout headerPanel;
	private RelativeLayout menuPanel;
	private static LinearLayout slidingPanel;
	private ImageView menuViewButton;
	private static boolean isExpanded, isAccordianExpanded, isOn = true;
	private CheckBox CheckboxAppDaily, CheckboxAppWeekly;
	CheckBox[] cArray;
	Switch[] sArray;
	Boolean[] checks;
	Vector<Notifications> notifications;

	// Needed for the white overlay that comes up when expanding the menu
	LayoutInflater inflaterPopup, errorPopup;
	static View layoutOverlay, errorLayoutOverlay;
	DatabaseHandler db;
	private static String regId;
	JSONObject jObj;
	int i = 0, seperator = 0, size;
	GoogleCloudMessaging gcm;
	private String regid;
	/**
	 * Tag used on log messages.
	 */
	static final String TAG = "GCMDemo";
	public static final String PROPERTY_REG_ID = "registration_id";
	private static final String PROPERTY_APP_VERSION = "appVersion";
	private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
	private String registrationStatus = "Not yet registered";
	Context context;
	String SENDER_ID = "965797032188";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_multiple);
		context = this.getBaseContext();
		notifications = new Vector<Notifications>();
		db = new DatabaseHandler(this);
		regId = GCMRegistrar.getRegistrationId(this);
		getNotifications();
		/** Needed for the popup overlay that happends when the menu is expanded */
		inflaterPopup = (LayoutInflater) NotificationOptions.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutOverlay = inflaterPopup.inflate(R.layout.opaque_overlay,
				(ViewGroup) findViewById(R.id.popup_element));

		if (checkPlayServices()) {
			gcm = GoogleCloudMessaging.getInstance(this);
			regid = getRegistrationId(context);
			// registerClient();

			// if (regid.isEmpty()) {
			registerInBackground();
			// }
		} else {
			Log.i(TAG, "No valid Google Play Services APK found.");
		}

		/** Error message overlay */
		errorPopup = (LayoutInflater) NotificationOptions.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		errorLayoutOverlay = inflaterPopup.inflate(R.layout.opaque_overlay,
				(ViewGroup) findViewById(R.id.popup_element));

		Typeface font = Typeface.createFromAsset(getAssets(), "Lato-Light.ttf");
		// Initialize Menu Slider
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		panelWidth = (int) ((metrics.widthPixels) * 0.75);

		headerPanel = (RelativeLayout) findViewById(R.id.header);
		headerPanelParameters = (LinearLayout.LayoutParams) headerPanel
				.getLayoutParams();
		headerPanelParameters.width = metrics.widthPixels;
		headerPanel.setLayoutParams(headerPanelParameters);

		menuPanel = (RelativeLayout) findViewById(R.id.menuPanel);
		menuPanelParameters = (FrameLayout.LayoutParams) menuPanel
				.getLayoutParams();
		menuPanelParameters.width = panelWidth;
		menuPanel.setLayoutParams(menuPanelParameters);

		slidingPanel = (LinearLayout) findViewById(R.id.slidingPanel);
		slidingPanelParameters = (FrameLayout.LayoutParams) slidingPanel
				.getLayoutParams();
		slidingPanelParameters.width = metrics.widthPixels;
		slidingPanel.setLayoutParams(slidingPanelParameters);

		// Slide the Panel
		menuViewButton = (ImageView) findViewById(R.id.menuViewButton);
		menuViewButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toggleSlider();
			}
		});

		TextView dashboard = (TextView) findViewById(R.id.dashboard);
		dashboard.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(NotificationOptions.this,
							Tabs.class);
					startActivity(intent);
				}

			}

		});
		dashboard.setTypeface(font);

		TextView header = (TextView) findViewById(R.id.pageTitle);
		TextView menu_settings = (TextView) findViewById(R.id.menu_settings);
		TextView gotosettings = (TextView) findViewById(R.id.gotosettings);
		TextView changepass = (TextView) findViewById(R.id.changepass);
		TextView menu_info = (TextView) findViewById(R.id.menu_info);
		TextView about_less = (TextView) findViewById(R.id.about_less);
		TextView contact_us = (TextView) findViewById(R.id.contact_us);
		TextView faq = (TextView) findViewById(R.id.faq);
		TextView logout = (TextView) findViewById(R.id.logout);
		header.setTypeface(font);
		menu_settings.setTypeface(font);
		gotosettings.setTypeface(font);
		changepass.setTypeface(font);
		menu_info.setTypeface(font);
		about_less.setTypeface(font);
		contact_us.setTypeface(font);
		faq.setTypeface(font);
		logout.setTypeface(font);
		changepass.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(NotificationOptions.this,
							ChangePassword.class);
					startActivity(intent);
				}

			}

		});
		contact_us.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(NotificationOptions.this,
							ContactUs.class);
					startActivity(intent);
				}

			}

		});
		about_less.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(NotificationOptions.this,
							AboutLESS.class);
					startActivity(intent);
				}

			}

		});
		logout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					db.removeAll();
					toggleSlider();
					Intent intent = new Intent(NotificationOptions.this,
							MainActivity.class);
					startActivity(intent);
				}

			}

		});

		View linearLayout = findViewById(R.id.blankLinear);
		cArray = new CheckBox[Tabs.addresses.length() * 2];
		sArray = new Switch[Tabs.address.length()];
		final Integer[] prems = new Integer[Tabs.addresses.length()];

		for (i = 0; i < Tabs.addresses.length(); i++) {
			RelativeLayout rel = new RelativeLayout(this);
			rel.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			final TextView address = new TextView(this);
			String premise_id = "";
			JSONObject firstAddress;
			try {
				firstAddress = new JSONObject(Tabs.addresses.getString(i));
				address.setText(firstAddress.getString("address"));
				premise_id = firstAddress.getString("premise_id");
			} catch (JSONException e) {
				// e.printStackTrace();
			}
			address.setId(i);
			address.setTextColor(Color.WHITE);
			address.setTypeface(Tabs.font);
			address.setPadding(0, 40, 0, 40);
			address.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
					.getDimension(R.dimen.title_textsize));
			address.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT));
			address.setGravity(Gravity.CENTER);
			rel.addView(address);
			((LinearLayout) linearLayout).addView(rel);
			View child1 = LayoutInflater.from(this).inflate(
					R.layout.notify_child1, null);
			TextView appNotify = (TextView) child1.findViewById(R.id.appNotify);
			appNotify.setTypeface(Tabs.font);
			final CheckBox dailyCheck = (CheckBox) child1
					.findViewById(R.id.CheckboxAppDaily);
			dailyCheck.setTypeface(Tabs.font);
			final int count = i;
			int checked = 0;
			int notSize = notifications.size();
			if (notSize > 0) {
				for (int v = 0; v < notifications.size(); v++) {
					Notifications notify = notifications.elementAt(v);
					if (premise_id.equals(notify.getPremise_id())) {
						checked = Integer.parseInt(notify
								.getNotification_frequency());
					}
					if (checked == 1 || checked == 3) {
						dailyCheck.setChecked(true);
					}
				}
			}

			if (checked == 0) {
				prems[i] = 0;
			} else {
				prems[i] = checked;
			}
			dailyCheck.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (dailyCheck.isChecked()) {
						// System.out.println("Checked");
						if (size == 0) {
							POST_Notifications(count, true, false, false);
						} else {
							if (prems[count] == 0) {
								updateNotifications(count, true, false, false);
							} else {
								updateNotifications(count, true, false, false);
							}
						}
						dailyCheck.setButtonDrawable(R.drawable.checkbox_on);
					} else {
						if (size == 0) {
							// delete
							POST_Notifications(count, true, true, false);
						} else {
							if (prems[count] == 0) {
								// delete
								updateNotifications(count, true, true, false);
							} else {
								// updatedelete
								updateNotifications(count, true, true, false);
							}
						}
						dailyCheck.setButtonDrawable(R.drawable.checkbox_off);

					}
				}
			});

			final CheckBox weeklyCheck = (CheckBox) child1
					.findViewById(R.id.CheckboxAppWeekly);
			weeklyCheck.setTypeface(Tabs.font);
			if (notSize > 0) {
				for (int x = 0; x < notifications.size(); x++) {
					Notifications notify = notifications.elementAt(x);
					if (premise_id.equals(notify.getPremise_id())) {
						checked = Integer.parseInt(notify
								.getNotification_frequency());
					}
					if (checked == 2 || checked == 3) {
						weeklyCheck.setChecked(true);
					}
				}
			}
			weeklyCheck.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (weeklyCheck.isChecked()) {
						// System.out.println("Checked");
						if (size == 0) {
							POST_Notifications(count, false, false, false);
						} else {
							if (prems[count] == 0) {
								updateNotifications(count, false, false, false);
							} else {
								// update
								updateNotifications(count, false, false, false);
							}
						}

						weeklyCheck.setButtonDrawable(R.drawable.checkbox_on);
					} else {
						if (size == 0) {
							// delete
							POST_Notifications(count, false, true, false);
						} else {
							if (prems[count] == 0) {
								// delete
								updateNotifications(count, false, true, false);
							} else {
								// update
								updateNotifications(count, false, true, false);
							}
						}
						weeklyCheck.setButtonDrawable(R.drawable.checkbox_off);
					}
				}
			});
			cArray[i + seperator] = dailyCheck;
			cArray[(i + seperator) + 1] = weeklyCheck;
			seperator++;
			View checkboxView = child1.findViewById(R.id.toggle);
			Switch s = new Switch();
			if (checkboxView != null && checkboxView instanceof Checkable) {

				com.draw.SwitchButton switchButton = (com.draw.SwitchButton) checkboxView;
				switchButton
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								// TODO Auto-generated method stub
								if (sArray[count].isOn()
										|| sArray[count].isFirst()) {
									dailyCheck.setEnabled(false);
									dailyCheck
											.setButtonDrawable(R.drawable.checkbox_disabled);
									dailyCheck.setTextColor(Color.GRAY);
									weeklyCheck.setEnabled(false);
									weeklyCheck
											.setButtonDrawable(R.drawable.checkbox_disabled);
									weeklyCheck.setTextColor(Color.GRAY);
									sArray[count].setOn(false);
									if (!sArray[count].isFirst()) {
										POST_Notifications(count, false, true,
												false);
									} else {
										sArray[count].setFirst(false);
									}
								} else {
									dailyCheck.setEnabled(true);
									dailyCheck
											.setButtonDrawable(R.drawable.checkbox_off);
									dailyCheck.setTextColor(Color.WHITE);
									weeklyCheck.setEnabled(true);
									weeklyCheck
											.setButtonDrawable(R.drawable.checkbox_off);
									weeklyCheck.setTextColor(Color.WHITE);
									sArray[count].setOn(true);
								}
							}
						});
			}
			if (notSize == 0 || checked == 0) {
				s.setOn(false);
				s.setFirst(true);
				sArray[i] = s;
				checkboxView.performClick();
			} else {
				s.setOn(true);
				sArray[i] = s;
			}

			((LinearLayout) linearLayout).addView(child1);
		}

	}

	private OnClickListener cancel_button_click_listener = new OnClickListener() {
		public void onClick(View v) {
			pwindo.dismiss();

		}
	};

	private static PopupWindow erroroverlay;

	private void initiateError() {
		try {
			NotificationOptions.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					erroroverlay = new PopupWindow(errorLayoutOverlay,
							panelWidth, 400, true);
					erroroverlay.setTouchable(true);
					erroroverlay.setFocusable(false);
					erroroverlay.setOutsideTouchable(false);
					erroroverlay.setBackgroundDrawable(new BitmapDrawable());
					erroroverlay.showAtLocation(errorLayoutOverlay,
							Gravity.CENTER, 0, 0);
					LinearLayout element = (LinearLayout) errorLayoutOverlay
							.findViewById(R.id.popup_element);
					element.setOnClickListener(cancel_overlay_click_listener);
					Button cancel = (Button) errorLayoutOverlay
							.findViewById(R.id.cancel_button);
					cancel.setOnClickListener(cancel_overlay_click_listener);
					Button retry = (Button) errorLayoutOverlay
							.findViewById(R.id.retry_button);
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

	public void updateNotifications(int position, boolean daily,
			boolean uncheck, boolean off) {
		boolean failed = false;
		String gcm = GCMRegistrar.getRegistrationId(this);
		try {
			if("".equals(gcm)){
				
			}else{
				URL url = new URL(
						"https://mobileapi.lessutility.com/v1/user/notifications/push/"
								+ gcm + "?token="
								+ MainActivity.access_token + "&device_id=yes");
				HttpURLConnection connection = (HttpURLConnection) url
						.openConnection();
				connection.setRequestMethod("PUT");
				connection.setDoOutput(true);
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("Accept", "application/json");
				OutputStreamWriter osw = new OutputStreamWriter(
						connection.getOutputStream());
				String json = "";

				// build jsonObject
				String id = GCMRegistrar.getRegistrationId(this);
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("device_type", "1");
				jsonObject.accumulate("device_id", id);
				JSONArray pushArray = new JSONArray();
				JSONObject pushObject = new JSONObject();
				int pushType = 0;

				if (!off) {
					jsonObject.accumulate("push_enabled", "1");
					if (cArray[position * 2].isChecked() || daily) {
						if (!uncheck) {
							pushType = 1;
						}
					}
					if (cArray[(position * 2) + 1].isChecked() || !daily) {
						if (!uncheck) {
							if (pushType == 1) {
								pushType = 3;
							} else {
								pushType = 2;
							}
						}
					}
				}

				String premise_id = Tabs.addresses.getJSONObject(position)
						.getString("premise_id");
				pushObject.accumulate("premise_id", premise_id);
				pushObject.accumulate("notification_frequency", pushType);
				pushObject.accumulate("notification_time", "15");
				pushArray.put(pushObject);
				jsonObject.accumulate("notifications", pushArray);
				// convert JSONObject to JSON to String
				json = jsonObject.toString();
				osw.write(json);
				osw.flush();
				osw.close();
				System.err.println(connection.getResponseCode());
			}
			
		} catch (Exception e) {
			failed = true;
		}
	}

	public boolean POST_Notifications(int position, boolean daily,
			boolean uncheck, boolean off) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		String url = "https://mobileapi.lessutility.com/v1/user/notifications/push?token="
				+ MainActivity.access_token;
		/*
		 * String url =
		 * "https://mobileapi.lessutility.com/v1/user/notifications/push/" +
		 * GCMRegistrar.getRegistrationId(this) + "?token=" +
		 * MainActivity.access_token + "&device_id=yes";
		 */
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
			String id = GCMRegistrar.getRegistrationId(this);
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("device_type", "1");
			jsonObject.accumulate("device_id", id);
			JSONArray pushArray = new JSONArray();
			JSONObject pushObject = new JSONObject();
			int pushType = 0;

			if (!off) {
				jsonObject.accumulate("push_enabled", "1");
				if (cArray[position * 2].isChecked() || daily) {
					if (!uncheck) {
						pushType = 1;
					}
				}
				if (cArray[(position * 2) + 1].isChecked() || !daily) {
					if (!uncheck) {
						if (pushType == 1) {
							pushType = 3;
						} else {
							pushType = 2;
						}
					}
				}
			}

			String premise_id = Tabs.addresses.getJSONObject(position)
					.getString("premise_id");
			pushObject.accumulate("premise_id", premise_id);
			pushObject.accumulate("notification_frequency", pushType);
			pushObject.accumulate("notification_time", "15");
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
		size++;
		return errorOut;
	}

	private JSONObject getNotifications() {
		JSON_Parser jGet = new JSON_Parser();
		jObj = new JSONObject();
		try {
			String url = "https://mobileapi.lessutility.com/v1/user/notifications?token="
					+ Tabs.access_token
					+ "&device_id="
					+ GCMRegistrar.getRegistrationId(this);
			jObj = jGet.getJSONFromUrl(url);
			JSONArray jsonMainArr = jObj.getJSONArray("notification_push");
			JSONObject j = jsonMainArr.getJSONObject(0);
			JSONArray jA = j.getJSONArray("notifications");
			size = jA.length();
			if (size > 0) {
				for (int i = 0; i < size; i++) {
					JSONObject current = jA.getJSONObject(i);
					String premise_id = current.getString("premise_id");
					String notification_frequency = current
							.getString("notification_frequency");
					Notifications notify = new Notifications();
					notify.setPremise_id(premise_id);
					notify.setNotification_frequency(notification_frequency);
					notifications.add(notify);
				}
			}
		} catch (Exception e) {
			// Log.d("InputStream", e.getLocalizedMessage());

		}
		return jObj;
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

	private PopupWindow pwindo;

	private void initiatePopup() {
		// TODO Auto-generated method stub
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.popup_new_phone, null);
		// pwindo = new PopupWindow(popupView, MainActivity.screenWidth,
		// MainActivity.screenHeight, true);
		Button cancelButton = (Button) popupView
				.findViewById(R.id.cancel_button);
		Button saveButton = (Button) popupView.findViewById(R.id.save_button);
		cancelButton.setOnClickListener(cancel_button_click_listener);

		pwindo.showAtLocation(popupView, Gravity.CENTER, 0, 0);
	}

	public static void toggleSlider() {
		if (!isExpanded) {
			isExpanded = true;

			// Expand
			new ExpandAnimation(slidingPanel, panelWidth,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.75f, 0, 0.0f, 0, 0.0f);
			initiateOverlay();
		} else {
			isExpanded = false;

			opaqueoverlay.dismiss();
			// Collapse
			new CollapseAnimation(slidingPanel, panelWidth,
					TranslateAnimation.RELATIVE_TO_SELF, 0.75f,
					TranslateAnimation.RELATIVE_TO_SELF, 0.0f, 0, 0.0f, 0, 0.0f);

		}
	}

	/**
	 * When the sliding menu is expanded, an overlay pops up A PopupWIndow is
	 * initiated with an opaque white background that stretches 1/4 of the
	 * screen
	 **/
	private static PopupWindow opaqueoverlay;

	private static void initiateOverlay() {
		try {
			int popupWidth = (int) ((int) (slidingPanel.getWidth()) * 0.25f);
			int popupHeight = (int) (slidingPanel.getHeight() * 1.10f);
			opaqueoverlay = new PopupWindow(layoutOverlay, popupWidth,
					popupHeight, true);
			opaqueoverlay.setTouchable(true);
			opaqueoverlay.setFocusable(false);
			opaqueoverlay.setOutsideTouchable(false);
			opaqueoverlay.setBackgroundDrawable(new BitmapDrawable());
			opaqueoverlay.showAtLocation(layoutOverlay, Gravity.RIGHT, 0, 0);
			LinearLayout element = (LinearLayout) layoutOverlay
					.findViewById(R.id.inside_element);
			element.setOnClickListener(cancel_overlay_click_listener);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static OnClickListener cancel_overlay_click_listener = new OnClickListener() {
		public void onClick(View v) {
			toggleSlider();
		}
	};

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(NotificationOptions.this, Tabs.class);
		startActivity(intent);
	}

	/**
	 * Check the device to make sure it has the Google Play Services APK. If it
	 * doesn't, display a dialog that allows users to download the APK from the
	 * Google Play Store or enable it in the device's system settings.
	 */
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	public void registerClient() {

		try {
			// Check that the device supports GCM (should be in a try / catch)
			GCMRegistrar.checkDevice(this);

			// Check the manifest to be sure this app has all the required
			// permissions.
			GCMRegistrar.checkManifest(this);

			// Get the existing registration id, if it exists.
			regid = GCMRegistrar.getRegistrationId(this);

			if (regid.equals("")) {

				registrationStatus = "Registering...";

				// tvRegStatusResult.setText(registrationStatus);

				// register this device for this project
				// GCMRegistrar.register(this, SENDER_ID);
				regid = gcm.register(SENDER_ID);
				// regid = GCMRegistrar.getRegistrationId(this);

				registrationStatus = "Registration Acquired";

				// This is actually a dummy function. At this point, one
				// would send the registration id, and other identifying
				// information to your server, which should save the id
				// for use when broadcasting messages.
				// sendRegistrationToServer();

			} else {

				registrationStatus = "Already registered";

			}

		} catch (Exception e) {

			e.printStackTrace();
			registrationStatus = e.getMessage();

		}

		Log.d(TAG, registrationStatus);
		// tvRegStatusResult.setText(registrationStatus);

		// This is part of our CHEAT. For this demo, you'll need to
		// capture this registration id so it can be used in our demo web
		// service.
		Log.d(TAG, regid);

	}

	// You need to do the Play Services APK check here too.

	@Override
	protected void onResume() {
		super.onResume();
		checkPlayServices();
	}

	/**
	 * Gets the current registration ID for application on GCM service.
	 * <p>
	 * If result is empty, the app needs to register.
	 * 
	 * @return registration ID, or empty string if there is no existing
	 *         registration ID.
	 */
	@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	/**
	 * @return Application's {@code SharedPreferences}.
	 */
	private SharedPreferences getGCMPreferences(Context context) {
		// This sample app persists the registration ID in shared preferences,
		// but
		// how you store the regID in your app is up to you.
		return getSharedPreferences(MainActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}
	
	/**
	 * Registers the application with GCM servers asynchronously.
	 * <p>
	 * Stores the registration ID and app versionCode in the application's
	 * shared preferences.
	 */
	@SuppressWarnings("unchecked")
	private void registerInBackground() {
		new AsyncTask() {
			@Override
			protected String doInBackground(Object... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					// You should send the registration ID to your server over
					// //
					// HTTP, so it can use GCM/HTTP or CCS to send messages to
					// your
					// app. The request to your server should be authenticated
					// if your app // is using accounts.
					sendRegistrationIdToBackend();

					// For this demo: we don't need to send it because the //
					// device // will send upstream messages to a server that
					// echo
					// back // the // message using the 'from' address in the
					// message.

					// Persist the regID - no need to register again.
					storeRegistrationId(context, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage(); // If there is an error,
					// don't just keep trying to register. // Require the user
					// to
					// click a button again, or perform // exponential back-off.
				}
				return msg;
			}

			protected void onPostExecute(String msg) {
				//mDisplay.append(msg + "\n");
			}

		}.execute(null, null, null);
	}

	/**
	 * Stores the registration ID and app versionCode in the application's
	 * {@code SharedPreferences}.
	 * 
	 * @param context
	 *            application's context.
	 * @param regId
	 *            registration ID
	 */
	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	/**
	 * Sends the registration ID to your server over HTTP, so it can use
	 * GCM/HTTP or CCS to send messages to your app. Not needed for this demo
	 * since the device sends upstream messages to a server that echoes back the
	 * message using the 'from' address in the message.
	 */
	private void sendRegistrationIdToBackend() {
		// Your implementation here.
	}

}
