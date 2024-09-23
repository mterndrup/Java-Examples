package com.lessutility;


import com.db.DatabaseHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Matthew Terndrup - Jan 27, 2014. AboutLESS shows a simple mission
 * statement.
 * **/
public class AboutLESS extends Activity {

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
	static View layoutOverlay;
	DatabaseHandler db;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.aboutless);

		db = new DatabaseHandler(this);
		/** Needed for the popup overlay that happens when the menu is expanded */
		inflaterPopup = (LayoutInflater) AboutLESS.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutOverlay = inflaterPopup.inflate(R.layout.opaque_overlay,
				(ViewGroup) findViewById(R.id.popup_element));

		Typeface font = Typeface.createFromAsset(getAssets(), "Lato-Light.ttf");
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
					Intent intent = new Intent(AboutLESS.this, Tabs.class);
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
					Intent intent = new Intent(AboutLESS.this,
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
					Intent intent = new Intent(AboutLESS.this,
							ChangePassword.class);
					startActivity(intent);
				}
			}

		});
		changepass.setTypeface(font);
		TextView info = (TextView) findViewById(R.id.menu_info);
		info.setTypeface(font);
		TextView about_less = (TextView) findViewById(R.id.about_less);
		about_less.setTypeface(font);
		TextView about_less_text = (TextView) findViewById(R.id.about_less_text);
		about_less_text.setTypeface(font);
		TextView contactus = (TextView) findViewById(R.id.contact_us);
		contactus.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(AboutLESS.this, ContactUs.class);
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
					Intent intent = new Intent(AboutLESS.this, Faq.class);
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
					Intent intent = new Intent(AboutLESS.this, MainActivity.class);
					startActivity(intent);
				}
			}

		});
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

	/**
	 * When the sliding menu is expanded, an overlay pops up A PopupWIndow is
	 * initiated with an opaque white background that stretches 1/4 of the
	 * screen
	 **/
	private static PopupWindow opaqueoverlay;

	private static void initiateOverlay() {
		try {
			int popupWidth = (int) ((int) (metrics.widthPixels) * 0.25f);
			int popupHeight = (int) (slidingPanel.getHeight()*1.10f);
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
		Intent intent = new Intent(AboutLESS.this,
				Tabs.class);
		startActivity(intent);
	}

}
