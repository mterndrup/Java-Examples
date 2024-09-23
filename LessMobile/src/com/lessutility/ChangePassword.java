package com.lessutility;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.db.DatabaseHandler;

/**
 * Created by Matthew Terndrup - Jan 27, 2014. This page is where the user can
 * change their password by typing info into EditTexts
 * **/
public class ChangePassword extends Activity {

	private static LinearLayout slidingPanel;
	private static boolean isExpanded;
	private DisplayMetrics metrics;
	private RelativeLayout headerPanel;
	private RelativeLayout menuPanel;
	private static int panelWidth;
	private ImageView menuViewButton;
	FrameLayout.LayoutParams menuPanelParameters;
	FrameLayout.LayoutParams slidingPanelParameters;
	LinearLayout.LayoutParams headerPanelParameters;
	LinearLayout.LayoutParams listViewParameters;

	LayoutInflater inflaterPopup;
	static View layoutOverlay;
	static View layoutError;
	private static LinearLayout topLevel;
	DatabaseHandler db;
	EditText pass, newpass, confirmpassword;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.changepassword);

		db = new DatabaseHandler(this);
		/** Needed for the popup overlay that happends when the menu is expanded */
		inflaterPopup = (LayoutInflater) ChangePassword.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutOverlay = inflaterPopup.inflate(R.layout.opaque_overlay,
				(ViewGroup) findViewById(R.id.popup_element));
		layoutError = inflaterPopup.inflate(R.layout.popup_single,
				(ViewGroup) findViewById(R.id.popup_element));
		topLevel = (LinearLayout) findViewById(R.id.inside);

		// Initialize the sliding hamburger menu
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

		Typeface font = Typeface.createFromAsset(getAssets(), "Lato-Light.ttf");

		menuViewButton = (ImageView) findViewById(R.id.menuViewButton);
		// Slide the Panel on button click
		menuViewButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toggleSlider();
			}
		});

		TextView pageTitle = (TextView) findViewById(R.id.pageTitle);
		pageTitle.setTypeface(font);
		TextView dashboard = (TextView) findViewById(R.id.dashboard);
		dashboard.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(ChangePassword.this, Tabs.class);
					startActivity(intent);
				}
			}

		});
		dashboard.setTypeface(font);
		TextView menu = (TextView) findViewById(R.id.menu_settings);
		menu.setTypeface(font);
		TextView info = (TextView) findViewById(R.id.menu_info);
		info.setTypeface(font);
		TextView logout = (TextView) findViewById(R.id.logout);
		logout.setTypeface(font);
		logout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					db.removeAll();
					toggleSlider();
					Intent intent = new Intent(ChangePassword.this,
							MainActivity.class);
					startActivity(intent);
				}
			}

		});

		TextView about = (TextView) findViewById(R.id.about_less);
		about.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(ChangePassword.this,
							AboutLESS.class);
					startActivity(intent);
				}
			}

		});
		about.setTypeface(font);
		TextView goToSettings = (TextView) findViewById(R.id.gotosettings);
		goToSettings.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(ChangePassword.this,
							NotificationOptions.class);
					startActivity(intent);
				}
			}

		});
		goToSettings.setTypeface(font);

		TextView changepass = (TextView) findViewById(R.id.changepass);
		changepass.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(ChangePassword.this,
							ChangePassword.class);
					startActivity(intent);
				}
			}

		});
		changepass.setTypeface(font);
		TextView contactus = (TextView) findViewById(R.id.contact_us);
		contactus.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(ChangePassword.this,
							ContactUs.class);
					startActivity(intent);
				}
			}

		});
		contactus.setTypeface(font);
		TextView faq = (TextView) findViewById(R.id.faq);
		faq.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(ChangePassword.this, Faq.class);
					startActivity(intent);
				}
			}

		});
		faq.setTypeface(font);

		TextView currentpassword = (TextView) findViewById(R.id.currentpassword);
		TextView newpassword = (TextView) findViewById(R.id.newpasswordT);
		TextView confirm = (TextView) findViewById(R.id.confirm);
		currentpassword.setTypeface(font);
		newpassword.setTypeface(font);
		confirm.setTypeface(font);

		Button cancel = (Button) findViewById(R.id.cancel_button);
		cancel.setTypeface(font);
		cancel.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(ChangePassword.this, Tabs.class);
				startActivity(intent);
			}

		});

		pass = (EditText) findViewById(R.id.edittext);
		newpass = (EditText) findViewById(R.id.newpassword);
		confirmpassword = (EditText) findViewById(R.id.confirmpassword);
		Button set = (Button) findViewById(R.id.set_button);
		set.setTypeface(font);
		set.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String passText = pass.getText().toString();
				String newpassText = newpass.getText().toString();
				String cpassText = confirmpassword.getText().toString();
				if (passText.equals("") || newpassText.equals("")
						|| cpassText.equals("")) {
					initiateError(false, 0);
				} else if (passText.length() < 6 || newpassText.length() < 6
						|| cpassText.length() < 6) {
					initiateError(false, 1);
				} else {
					if (newpassText.equals(cpassText)) {
						boolean reset = setPass();
						if (!reset) {
							boolean errorOut = initiateError(false, 3);
							if (!errorOut) {
								Intent intent = new Intent(ChangePassword.this,
										Tabs.class);
								startActivity(intent);
							}
						} else {
							initiateError(true, -1);
						}
					} else {
						initiateError(false, 2);
					}

				}

			}

		});

	}

	private static PopupWindow erroroverlay;

	private boolean initiateError(final boolean connection, final int type) {
		boolean errorOut = false;
		try {
			ChangePassword.this.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					int width = topLevel.getWidth();
					int height = topLevel.getHeight();
					int popupHeight = (int) (height);
					erroroverlay = new PopupWindow(layoutError, width,
							popupHeight, true);
					erroroverlay.setTouchable(true);
					erroroverlay.setFocusable(false);
					erroroverlay.setOutsideTouchable(false);
					erroroverlay.setBackgroundDrawable(new BitmapDrawable());
					erroroverlay.showAtLocation(layoutError, Gravity.CENTER, 0,
							0);
					LinearLayout element = (LinearLayout) layoutError
							.findViewById(R.id.popup_element);
					TextView error = (TextView) layoutError
							.findViewById(R.id.error);
					error.setTypeface(Tabs.font);
					if (!connection) {
						if (type == 0) {
							error.setText("Please enter a password");
						} else if (type == 1) {
							error.setText("Password must be at least 6 characters");
						} else if (type == 2) {
							error.setText("\"New Password\" and \"Confirm Password\" fields must match.");
						} else if (type == 3) {
							error.setText("Your password has been reset");
						}
					}
					element.setOnClickListener(cancel_Error_click_listener);
					Button cancel = (Button) layoutError
							.findViewById(R.id.cancel_button);
					cancel.setText("okay");
					cancel.setOnClickListener(cancel_Error_click_listener);
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
			errorOut = true;
		}
		return errorOut;
	}

	public static void toggleSlider() {
		if (!isExpanded) {
			isExpanded = true;

			initiateOverlay();
			// Expand
			new ExpandAnimation(slidingPanel, panelWidth,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.75f, 0, 0.0f, 0, 0.0f);
		} else {
			isExpanded = false;

			opaqueoverlay.dismiss();
			// Collapse
			new CollapseAnimation(slidingPanel, panelWidth,
					TranslateAnimation.RELATIVE_TO_SELF, 0.75f,
					TranslateAnimation.RELATIVE_TO_SELF, 0.0f, 0, 0.0f, 0, 0.0f);

		}
	}

	private boolean setPass() {
		boolean failed = false;
		try {
			URL url = new URL(
					"https://mobileapi.lessutility.com/v1/user/password?token="
							+ MainActivity.access_token);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("PUT");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			OutputStreamWriter osw = new OutputStreamWriter(
					connection.getOutputStream());
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("current_pass", pass.getText().toString());
			jsonObject.accumulate("new_pass", newpass.getText().toString());
			jsonObject.accumulate("new_pass_confirm", confirmpassword.getText()
					.toString());

			// convert JSONObject to JSON to String
			String json = jsonObject.toString();
			osw.write(json);
			osw.flush();
			osw.close();
			System.err.println(connection.getResponseCode());
		} catch (Exception e) {
			failed = true;
		}
		return failed;
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

	private static OnClickListener cancel_Error_click_listener = new OnClickListener() {
		public void onClick(View v) {
			erroroverlay.dismiss();

		}
	};

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
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(ChangePassword.this,
				Tabs.class);
		startActivity(intent);
	}
}
