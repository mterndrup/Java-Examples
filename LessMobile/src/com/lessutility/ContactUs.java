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

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

import com.array.adapters.PopUpContactUsArrayAdapter;
import com.db.DatabaseHandler;

/**
 * Created by Matthew Terndrup - Jan 27, 2014. A user can email LESS using this
 * form. EditTexts are used to gather information inputed.
 * **/
public class ContactUs extends FragmentActivity {

	private static final int NUM_PAGES = 3;
	private static ViewPager mPager;
	private ScreenSlidePagerAdapter mPagerAdapter;
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
	private static boolean isExpanded;
	Typeface font;
	public static TextView general_comment;
	public static int selectedRow = -1;
	public static ListView listView;

	LayoutInflater inflaterPopup;
	static View layoutOverlay;
	static View layoutError;
	static View layoutSingle;
	static View connectError;
	private static LinearLayout topLevel;
	DatabaseHandler db;
	EditText EditText02;
	int width, height;
	private SlidingDrawer slide;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contactus);

		selectedRow = 0;
		db = new DatabaseHandler(this);
		/** Needed for the popup overlay that happens when the menu is expanded */
		inflaterPopup = (LayoutInflater) ContactUs.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutOverlay = inflaterPopup.inflate(R.layout.opaque_overlay,
				(ViewGroup) findViewById(R.id.popup_element));
		layoutError = inflaterPopup.inflate(R.layout.popup_single,
				(ViewGroup) findViewById(R.id.popup_element));
		connectError = inflaterPopup.inflate(R.layout.popup_error,
				(ViewGroup) findViewById(R.id.popup_element));

		topLevel = (LinearLayout) findViewById(R.id.inside);
		width = topLevel.getWidth();
		height = topLevel.getHeight();

		slide = (SlidingDrawer) this.findViewById(R.id.drawer);

		slide.open();

		font = Typeface.createFromAsset(getAssets(), "Lato-Light.ttf");
		// Instantiate a ViewPager and a custom PagerAdapter.
		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});
		mPager.setAdapter(mPagerAdapter);

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
				// hideSoftKeyboard(ContactUs.this);
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
					Intent intent = new Intent(ContactUs.this, Tabs.class);
					startActivity(intent);
				}
			}
		});
		dashboard.setTypeface(font);
		TextView menu = (TextView) findViewById(R.id.menu_settings);
		menu.setTypeface(font);
		TextView about_less = (TextView) findViewById(R.id.about_less);
		about_less.setTypeface(font);
		about_less.setOnClickListener(new OnClickListener() {
			@SuppressLint("NewApi")
			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(ContactUs.this, AboutLESS.class);
					startActivity(intent);
				}
			}
		});
		TextView goToSettings = (TextView) findViewById(R.id.gotosettings);
		goToSettings.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(ContactUs.this,
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
					Intent intent = new Intent(ContactUs.this,
							ChangePassword.class);
					startActivity(intent);
				}
			}
		});
		changepass.setTypeface(font);
		TextView info = (TextView) findViewById(R.id.menu_info);
		info.setTypeface(font);
		TextView contactus = (TextView) findViewById(R.id.contact_us);
		contactus.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(ContactUs.this, ContactUs.class);
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
					Intent intent = new Intent(ContactUs.this, Faq.class);
					startActivity(intent);
				}
			}
		});
		faq.setTypeface(font);
		TextView logout = (TextView) findViewById(R.id.logout);
		logout.setTypeface(font);
		logout.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isExpanded) {
					db.removeAll();
					toggleSlider();
					Intent intent = new Intent(ContactUs.this,
							MainActivity.class);
					startActivity(intent);
				}
			}
		});
		EditText02 = (EditText) findViewById(R.id.EditText02);
		EditText02.setTypeface(Tabs.font);
		TextView sendmessage = (TextView) findViewById(R.id.sendmessage);
		sendmessage.setTypeface(font);
		sendmessage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// hideSoftKeyboard(ContactUs.this);
			}
		});
		general_comment = (TextView) findViewById(R.id.general_comment);
		general_comment.setTypeface(Tabs.font);
		general_comment.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				// hideSoftKeyboard(ContactUs.this);
				initiatePopup();
			}
		});
		sendmessage.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String text = EditText02.getText().toString();
				if ("".equals(text)) {
					initiateError(false, 0);
				} else {
					new Thread(new Runnable() {
						public void run() {
							POST_Contact(general_comment.getText().toString(),
									"");
						}
					}).start();
				}

			}
		});
	}

	public static void MoveNext() {
		// it doesn't matter if you're already in the last item
		mPager.setCurrentItem(mPager.getCurrentItem() + 1);
	}

	public static void gotoPage(int page) {
		// it doesn't matter if you're already in the last item
		mPager.setCurrentItem(page);
	}

	public boolean POST_Contact(String contact, String message) {
		// Create a new HttpClient and Post Header
		HttpClient httpclient = new DefaultHttpClient();
		String url = "https://mobileapi.lessutility.com/v1/contact?token="
				+ MainActivity.access_token;
		HttpPost httpPost = new HttpPost(url);
		InputStream inputStream = null;
		String resultString = "";
		JSONObject obj = null;
		boolean failed = false;

		try {
			// Adds the post data
			String json = "";

			// build jsonObject
			JSONObject jsonObject = new JSONObject();
			jsonObject.accumulate("category", general_comment.getText()
					.toString());
			jsonObject.accumulate("message", EditText02.getText().toString());

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
		} catch (Exception e) {
			initiateConnectionError(true, -1);
			Log.d("InputStream", e.getLocalizedMessage());
			failed = true;
		}
		if (!failed) {
			sentMessageConfirmation(true);
		}
		return failed;
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

	// This is where the PopupWindow is initiated for selecting the message
	// subject
	public static PopupWindow pwindo;

	private void initiatePopup() {
		LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		View popupView = layoutInflater.inflate(R.layout.popup_contactus, null);
		pwindo = new PopupWindow(popupView, slidingPanel.getWidth(),
				slidingPanel.getHeight(), true);
		pwindo.setTouchable(true);
		pwindo.setOutsideTouchable(true);
		pwindo.setBackgroundDrawable(new BitmapDrawable());
		pwindo.setFocusable(true);
		String[] values = new String[] { "General Comment",
				"Account Cancellation", "Feature Suggestion",
				"Technical Support", "Utility Support", "Other" };
		Button continueButton = (Button) popupView
				.findViewById(R.id.continue_button);
		Button backButton = (Button) popupView.findViewById(R.id.back_button);
		backButton.setTypeface(font);
		continueButton.setOnClickListener(cancel_button_click_listener);
		continueButton.setTypeface(font);
		backButton.setOnClickListener(cancel_button_click_listener);
		listView = (ListView) popupView.findViewById(R.id.contactus_listview);
		listView.setDivider(null);
		listView.setAdapter(new PopUpContactUsArrayAdapter(this, values, font,
				1));
		pwindo.showAtLocation(popupView, Gravity.CENTER, 0, 0);
	}

	// This dismisses the popup Window
	private OnClickListener cancel_button_click_listener = new OnClickListener() {
		public void onClick(View v) {
			pwindo.dismiss();
		}
	};

	public static void toggleSlider() {
		if (!isExpanded) {
			isExpanded = true;
			// Expand
			general_comment.setClickable(false);
			new ExpandAnimation(slidingPanel, panelWidth,
					Animation.RELATIVE_TO_SELF, 0.0f,
					Animation.RELATIVE_TO_SELF, 0.75f, 0, 0.0f, 0, 0.0f);
			initiateOverlay();
		} else {
			isExpanded = false;
			general_comment.setClickable(true);
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

	// This method is used with the ViewPager
	private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
		public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			return SocialSlidePageFragment.create(position);
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}

	private static PopupWindow singleoverlay;

	private void sentMessageConfirmation(final boolean pass) {
		try {
			ContactUs.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					int width = topLevel.getWidth();
					int height = topLevel.getHeight();
					layoutSingle = inflaterPopup.inflate(R.layout.popup_single,
							(ViewGroup) findViewById(R.id.popup_element));
					int popupHeight = (int) (height * 1.10f);
					singleoverlay = new PopupWindow(layoutSingle, width,
							popupHeight, true);
					singleoverlay.setTouchable(true);
					singleoverlay.setOutsideTouchable(true);
					singleoverlay.setFocusable(true);
					singleoverlay.setBackgroundDrawable(new BitmapDrawable());
					singleoverlay.showAtLocation(layoutSingle, Gravity.CENTER,
							0, 0);
					TextView error = (TextView) layoutSingle
							.findViewById(R.id.error);
					error.setTypeface(MainActivity.font);
					error.setText("Your message has been sent!");
					LinearLayout element = (LinearLayout) layoutSingle
							.findViewById(R.id.popup_element);
					element.setOnClickListener(cancel_single_click_listener);
					Button cancel = (Button) layoutSingle
							.findViewById(R.id.cancel_button);
					cancel.setText("okay");
					cancel.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							singleoverlay.dismiss();
							Intent intent = new Intent(ContactUs.this,
									Tabs.class);
							startActivity(intent);
						}
					});

				}

			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static OnClickListener cancel_single_click_listener = new OnClickListener() {
		public void onClick(View v) {
			singleoverlay.dismiss();
		}
	};

	private static PopupWindow erroroverlay;

	private void initiateError(final boolean connection, final int value) {
		try {
			ContactUs.this.runOnUiThread(new Runnable() {

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
					TextView text = (TextView) layoutError
							.findViewById(R.id.error);
					text.setTypeface(Tabs.font);
					if (!connection) {
						if (value == 0) {
							text.setText("Please enter a message before sending");
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
		}
	}

	private static PopupWindow connectoverlay;

	private void initiateConnectionError(final boolean connection,
			final int value) {
		try {
			ContactUs.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					int width = topLevel.getWidth();
					int height = topLevel.getHeight();
					int popupHeight = (int) (height);
					connectoverlay = new PopupWindow(connectError, width,
							popupHeight, true);
					connectoverlay.setTouchable(true);
					connectoverlay.setFocusable(false);
					connectoverlay.setOutsideTouchable(false);
					connectoverlay.setBackgroundDrawable(new BitmapDrawable());
					connectoverlay.showAtLocation(connectError, Gravity.CENTER,
							0, 0);
					LinearLayout element = (LinearLayout) connectError
							.findViewById(R.id.popup_element);
					TextView text = (TextView) connectError
							.findViewById(R.id.cityselection);
					text.setTypeface(Tabs.font);
					text.setText("Unable to connect to network");
					element.setOnClickListener(cancel_Connection_click_listener);
					Button cancel = (Button) connectError
							.findViewById(R.id.cancel_button);
					cancel.setOnClickListener(cancel_Connection_click_listener);
					Button retry = (Button) connectError
							.findViewById(R.id.retry_button);
					retry.setOnClickListener(new OnClickListener() {
						public void onClick(View v) {
							connectoverlay.dismiss();
							new Thread(new Runnable() {
								public void run() {
									POST_Contact(general_comment.getText()
											.toString(), "");
								}
							}).start();
						}
					});
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static OnClickListener cancel_Error_click_listener = new OnClickListener() {
		public void onClick(View v) {
			erroroverlay.dismiss();

		}
	};

	private static OnClickListener cancel_Connection_click_listener = new OnClickListener() {
		public void onClick(View v) {
			connectoverlay.dismiss();

		}
	};

	/*
	 * public static void hideSoftKeyboard(Activity activity) {
	 * 
	 * InputMethodManager inputMethodManager = (InputMethodManager)
	 * activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
	 * inputMethodManager
	 * .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
	 * }
	 */
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
		// super.onBackPressed();

		Intent i = new Intent(ContactUs.this, Tabs.class);
		startActivity(i);
		finish();
	}
}
