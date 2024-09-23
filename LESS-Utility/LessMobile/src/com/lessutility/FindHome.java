package com.lessutility;

import java.net.URLEncoder;
import java.util.ArrayList;


import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.TextView;

import com.beans.Addresses;
import com.crashlytics.android.Crashlytics;
import com.parsers.JSON.JSON_Parser;

/**
 * This is the page where the user inputs their address during the signup
 * process
 * 
 * **/
public class FindHome extends Activity {

	public static ArrayList<Addresses> addresses;
	JSON_Parser jGet;
	ProgressDialog dialog;

	LayoutInflater inflaterPopup;
	View layoutOverlay;
	View singleOverlay;
	private static LinearLayout topLevel;
	EditText edit_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.findhome);

		Crashlytics.start(this);
		inflaterPopup = (LayoutInflater) FindHome.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutOverlay = inflaterPopup.inflate(R.layout.popup_error,
				(ViewGroup) findViewById(R.id.popup_element));
		singleOverlay = inflaterPopup.inflate(R.layout.popup_single,
				(ViewGroup) findViewById(R.id.popup_element));
		topLevel = (LinearLayout) findViewById(R.id.inside);

		/** This is where the font typeface for the text is set **/
		Typeface font = Typeface.createFromAsset(getAssets(),
				"Raleway-Thin.ttf");
		TextView pageTitle = (TextView) findViewById(R.id.pageTitle);
		pageTitle.setTypeface(font);
		edit_text = (EditText) findViewById(R.id.findhome_edittext);
		edit_text.setTypeface(font);
		TextView city = (TextView) findViewById(R.id.city);
		city.setTypeface(font);
		TextView cityselection = (TextView) findViewById(R.id.cityselection);
		cityselection.setTypeface(font);

		Button continue_button = (Button) findViewById(R.id.continue_button);
		continue_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				String addressText = edit_text.getText().toString();
				if ("".equals(addressText)) {

				} else {
					dialog = ProgressDialog.show(FindHome.this, "",
							"Loading. Please wait...", true);
					new MyTask(dialog).execute();
				}

			}

		});
		continue_button.setTypeface(font);

		Button back_button = (Button) findViewById(R.id.back_button);
		back_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent intent = null;
				if (!Tabs.editButton) {
					intent = new Intent(FindHome.this, MainActivity.class);
				} else {
					intent = new Intent(FindHome.this, MainActivity.class);
				}
				startActivity(intent);
			}

		});
		back_button.setTypeface(font);
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
					getAddress();
				}
			}).start();
			return null;
		}

	}

	public void getAddress() {
		Handler handler = new Handler(Looper.getMainLooper());
		boolean none = false;
		try {
			String address = edit_text.getText().toString();
			jGet = new JSON_Parser();
			JSONObject jObj = jGet
					.getJSONFromUrl("https://mobileapi.lessutility.com/v1/premise?search="
							+ URLEncoder.encode(address, "UTF-8"));
			try{
				addresses = jGet.parseJSONAddressSearch(jObj);
			} catch (Exception e) {
				none = true;
			}
			dialog.dismiss();
			if (addresses.size() != 0) {
				Intent intent = new Intent(FindHome.this, SelectAddress.class);
				startActivity(intent);
			}

		} catch (Exception e) {
			// Log.d("InputStream", e.getLocalizedMessage());
			dialog.dismiss();
			if (addresses==null) {
				initiatesingle();
			} else {
				initiateError();
			}
		}

	}

	private static PopupWindow opaqueoverlay;

	private void initiateError() {
		try {
			FindHome.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					int width = topLevel.getWidth();
					int height = topLevel.getHeight();
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
					element.setOnClickListener(cancel_overlay_click_listener);
					Button cancel = (Button) layoutOverlay
							.findViewById(R.id.cancel_button);
					cancel.setOnClickListener(cancel_overlay_click_listener);
					Button retry = (Button) layoutOverlay
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

	private static PopupWindow singleoverlay;

	private void initiatesingle() {
		try {
			FindHome.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					int width = topLevel.getWidth();
					int height = topLevel.getHeight();
					int popupHeight = (int) ((int) (height)*1.1f);
					singleoverlay = new PopupWindow(singleOverlay, width,
							popupHeight, true);
					singleoverlay.setTouchable(true);
					singleoverlay.setFocusable(false);
					singleoverlay.setOutsideTouchable(false);
					singleoverlay.setBackgroundDrawable(new BitmapDrawable());
					singleoverlay.showAtLocation(singleOverlay, Gravity.CENTER,
							0, 0);
					LinearLayout element = (LinearLayout) singleOverlay
							.findViewById(R.id.popup_element);
					TextView error = (TextView) singleOverlay
							.findViewById(R.id.error);
					error.setText("No addresses found");
					element.setOnClickListener(cancel_single_click_listener);
					Button cancel = (Button) singleOverlay
							.findViewById(R.id.cancel_button);
					cancel.setOnClickListener(cancel_single_click_listener);
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

	private static OnClickListener cancel_single_click_listener = new OnClickListener() {
		public void onClick(View v) {
			singleoverlay.dismiss();

		}
	};

	public boolean isNetworkOnline() {
		boolean status = false;
		try {
			ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getNetworkInfo(0);
			if (netInfo != null
					&& netInfo.getState() == NetworkInfo.State.CONNECTED) {
				status = true;
			} else {
				netInfo = cm.getNetworkInfo(1);
				if (netInfo != null
						&& netInfo.getState() == NetworkInfo.State.CONNECTED)
					status = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return status;

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
