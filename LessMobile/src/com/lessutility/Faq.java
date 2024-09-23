package com.lessutility;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;


import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParserException;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.array.adapters.ListViewArrayAdapterFaq;
import com.beans.Faqs;
import com.db.DatabaseHandler;
import com.parsers.Item;
import com.parsers.XMLHandler;

public class Faq extends ListActivity {

	// All static variables
	static final String URL = "https://lessutility.com/faq/feed/";
	// XML node keys
	static final String KEY_ITEM = "item"; // parent node
	static final String KEY_TITLE = "title";
	static final String KEY_DESC = "description";

	private static LinearLayout slidingPanel;
	private static DisplayMetrics metrics;
	private RelativeLayout headerPanel;
	private RelativeLayout menuPanel;
	private static int panelWidth;
	FrameLayout.LayoutParams menuPanelParameters;
	FrameLayout.LayoutParams slidingPanelParameters;
	LinearLayout.LayoutParams headerPanelParameters;
	LinearLayout.LayoutParams listViewParameters;
	private ImageView menuViewButton;
	private static boolean isExpanded;

	// Needed for the white overlay that comes up when expanding the menu
	static int screenWidth;
	static int screenHeight;
	LayoutInflater inflaterPopup;
	static View layoutOverlay, layoutError;
	DatabaseHandler db;
	LinearLayout topLevel;
	ArrayList<Item> items;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.faq);

		db = new DatabaseHandler(this);
		final Typeface font = Typeface.createFromAsset(getAssets(),
				"Raleway-Thin.ttf");
		ArrayList<Faqs> menuItems = null;
		/** Needed for the popup overlay that happens when the menu is expanded */
		inflaterPopup = (LayoutInflater) Faq.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutOverlay = inflaterPopup.inflate(R.layout.opaque_overlay,
				(ViewGroup) findViewById(R.id.popup_element));
		layoutError = inflaterPopup.inflate(R.layout.popup_error,
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
					Intent intent = new Intent(Faq.this, Tabs.class);
					startActivity(intent);
				}
			}

		});
		dashboard.setTypeface(font);
		TextView menu = (TextView) findViewById(R.id.menu_settings);
		menu.setTypeface(font);
		TextView pageTitle = (TextView) findViewById(R.id.pageTitle);
		pageTitle.setTypeface(font);
		TextView goToSettings = (TextView) findViewById(R.id.gotosettings);
		goToSettings.setOnClickListener(new OnClickListener() {

			@SuppressLint("NewApi")
			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(Faq.this,
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
					Intent intent = new Intent(Faq.this, ChangePassword.class);
					startActivity(intent);
				}
			}

		});
		changepass.setTypeface(font);
		TextView info = (TextView) findViewById(R.id.menu_info);
		info.setTypeface(font);
		TextView about_less = (TextView) findViewById(R.id.about_less);
		about_less.setTypeface(font);
		about_less.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(Faq.this, AboutLESS.class);
					startActivity(intent);
				}
			}

		});
		TextView contactus = (TextView) findViewById(R.id.contact_us);
		contactus.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(Faq.this, ContactUs.class);
					startActivity(intent);
				}
			}

		});
		contactus.setTypeface(font);
		TextView faq = (TextView) findViewById(R.id.faq);
		faq.setTypeface(font);
		TextView logout = (TextView) findViewById(R.id.logout);
		logout.setTypeface(font);
		logout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					db.removeAll();
					toggleSlider();
					Intent intent = new Intent(Faq.this, MainActivity.class);
					startActivity(intent);
				}
			}

		});
				try {
					/* Create a URL we want to load some xml-data from. */
					URL url = new URL("https://lessutility.com/faq/feed/");

					/* Get a SAXParser from the SAXPArserFactory. */
					SAXParserFactory spf = SAXParserFactory.newInstance();
					SAXParser sp = spf.newSAXParser();

					/* Get the XMLReader of the SAXParser we created. */
					XMLReader xr = sp.getXMLReader();
					/*
					 * Create a new ContentHandler and apply it to the
					 * XML-Reader
					 */
					XMLHandler myExampleHandler = new XMLHandler();
					xr.setContentHandler(myExampleHandler);

					/* Parse the xml-data from our URL. */
					xr.parse(new InputSource(url.openStream()));
					/* Parsing has finished. */

					/* Our ExampleHandler now provides the parsed data to us. */
					// ParsedExampleDataSet parsedExampleDataSet =
					// myExampleHandler.getParsedData();

					items = myExampleHandler.getItems();

					final ListView listView1 = getListView();
					// Adding menuItems to ListView
					ListViewArrayAdapterFaq adapter = new ListViewArrayAdapterFaq(
							getApplicationContext(), items, font);
					listView1.setAdapter(adapter);

					setListAdapter(adapter);

					// selecting single ListView item
					ListView lv = getListView();
				} catch (IOException e) {
					initiateError();
				} catch (ParserConfigurationException e) {
					initiateError();
				} catch (SAXException e) {
					initiateError();
				}
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

	private static PopupWindow erroroverlay;

	private void initiateError() {
		try {
			Faq.this.runOnUiThread(new Runnable() {

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
					element.setOnClickListener(cancel_error_click_listener);
					Button cancel = (Button) layoutError
							.findViewById(R.id.cancel_button);
					cancel.setOnClickListener(cancel_error_click_listener);
					Button retry = (Button) layoutError
							.findViewById(R.id.retry_button);
					retry.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {

						}
					});
				}
			});

		} catch (Exception e) {
			//e.printStackTrace();
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
			int popupWidth = (int) ((int) (metrics.widthPixels) * 0.25f);
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
	private static OnClickListener cancel_error_click_listener = new OnClickListener() {
		public void onClick(View v) {
			erroroverlay.dismiss();
		}
	};
	
	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Faq.this,
				Tabs.class);
		startActivity(intent);
	}

}