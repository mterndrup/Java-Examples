package com.lessutility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TextView;

import com.array.adapters.PopUpAddressArrayAdapter;
import com.beans.DailyData;
import com.beans.Example;
import com.crashlytics.android.Crashlytics;
import com.custom.viewpager.CustomViewPager;
import com.date.slider.DateMonthPicker;
import com.date.slider.DatePickerFragment;
import com.date.slider.DateWeekPicker;
import com.db.DatabaseHandler;
import com.google.android.gcm.GCMRegistrar;
import com.parsers.JSON.JSON_Parser;

@SuppressLint("CutPasteId")
/**This class holds the Fragments that draw the bar graph
 * It also has the bottom ViewPager that slides pages over with user data
 * **/
public class Tabs extends FragmentActivity {

	// These variables are references throughout the app
	public static boolean multipleAddresses;
	public static String mainAddress;
	// Other variables
	static final int DEFAULTDATESELECTOR_ID = 0;
	public static FragmentTabHost mTabHost;
	FrameLayout.LayoutParams menuPanelParameters;
	FrameLayout.LayoutParams slidingPanelParameters;
	LinearLayout.LayoutParams headerPanelParameters;
	LinearLayout.LayoutParams listViewParameters;
	private DisplayMetrics metrics;
	private static int panelWidth;
	private static RelativeLayout topLevel;
	private RelativeLayout headerPanel;
	private RelativeLayout menuPanel;
	static LinearLayout slidingPanel;
	private static LinearLayout belowPanel;
	private ImageView menuViewButton;
	private static boolean isExpanded;
	public static boolean changedDate;
	protected AlphaAnimation fadeOut = new AlphaAnimation(1.0f, 0.0f);
	public static boolean editButton, showMinuses, debug = false, getData;

	private static final int NUM_PAGES = 4;
	public static int density = -1;
	public static CustomViewPager mPager;
	private static PagerAdapter mPagerAdapter;

	private static TextView amount;
	public static TextView dayDate;
	private static TextView yearTextView;
	private static TextView killowattsHTextView;
	public static double totalDayKillowattH;

	public static double totalWeekAmount;
	public static double totalWeekKillowattH;
	public static int week_timeframe;
	public static String week_start;
	public static int viewPagerHeight;
	JSONObject result;

	CheckBox repeatChkBx;
	static SlidingDrawer slidingDrawer = null;
	Button slideButton = null;
	boolean isOpen;
	static TextView yearButton;
	public static Typeface font;
	static View layoutPopup;
	static LayoutInflater inflaterPopup;
	static View layoutOverlay;
	static View budgetOverlay;
	static View layoutDelete;

	public static void playSound() {

		// mp.start();
	}

	public static MediaPlayer mp;
	public static JSONArray addresses;
	public static String access_token;
	public static DailyData dailyData;
	public static JSONObject dailyJSON;
	public static DailyData weeklyData;
	public static JSONObject weeklyJSON;
	public static DailyData cycleData;
	public static JSONObject cycleJSON;
	static JSON_Parser jGet = null;
	public static String meter = null;
	public static String dateString = "";
	public static int currentTab, year, month, day;
	static String endpoint = "https://mobileapi.lessutility.com";
	static ProgressDialog dialog;
	static DatabaseHandler db;
	public static JSONArray meters;
	static TextView address;
	public static boolean noBudgetStop = false;
	private String broadcastMessage = "No broadcast message";
	public static int currentAddress = 0;
	public static InputMethodManager imm;
	public static Example example;
	static String stringValue;
	static View layoutSingle;
	private BroadcastReceiver gcmReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			broadcastMessage = intent.getExtras().getString("gcm");

			if (broadcastMessage != null) {
				showNotification();
			}
		}
	};
	// This intent filter will be set to filter on the string
	// "GCM_RECEIVED_ACTION"
	IntentFilter gcmFilter;
	static DecimalFormat df;

	@SuppressWarnings("deprecation")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tabs);
		Crashlytics.start(this);
		db = new DatabaseHandler(this);

		gcmFilter = new IntentFilter();
		gcmFilter.addAction("GCM_RECEIVED_ACTION");

		example = new Example(this);
		Calendar now = Calendar.getInstance();
		year = now.get(Calendar.YEAR);
		int month = now.get(Calendar.MONTH); // Note: zero based!
		int day = now.get(Calendar.DAY_OF_MONTH);
		df = new DecimalFormat("#.##");
		if (month < 10) {
			dateString = "0" + (month + 1) + "/";
		} else {
			dateString = (month + 1) + "/";
		}
		if (day < 10) {
			if (day == 1) {
				Calendar cal = Calendar.getInstance();
				cal.add(Calendar.MONTH, month);
				cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
				String m = month+"";
				int d = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
				String dd = d+"";
				if(month< 10){
					m = "0"+month;
				}if(d<10){
					dd = "0"+d;
				}
				dateString = (m)+"/"+dd+"/"+year;
			} else {
				dateString += "0" + (day - 1) + "/" + year;
			}
		} else {
			dateString += (day - 1) + "/" + year;
		}

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

		// Initiate the popupWindow that allows a user to change data set per
		// address
		inflaterPopup = (LayoutInflater) Tabs.this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		layoutPopup = inflaterPopup.inflate(R.layout.popup_datepicker,
				(ViewGroup) findViewById(R.id.popup_element));
		layoutOverlay = inflaterPopup.inflate(R.layout.opaque_overlay,
				(ViewGroup) findViewById(R.id.popup_element));
		layoutDelete = inflaterPopup.inflate(R.layout.popup_delete,
				(ViewGroup) findViewById(R.id.popup_element));
		budgetOverlay = inflaterPopup.inflate(R.layout.popup_budget,
				(ViewGroup) findViewById(R.id.popup_element));
		layoutSingle = inflaterPopup.inflate(R.layout.popup_single,
				(ViewGroup) findViewById(R.id.popup_element));

		// mp = MediaPlayer.create(Tabs.this, R.raw.dashboard_sound);
		font = Typeface.createFromAsset(getAssets(), "Lato-Light.ttf");

		// Instantiate a ViewPager and a PagerAdapter.
		topLevel = (RelativeLayout) findViewById(R.id.topLevel);
		mPager = (CustomViewPager) findViewById(R.id.pager);
		slidingPanel = (LinearLayout) findViewById(R.id.slidingPanel);
		belowPanel = (LinearLayout) findViewById(R.id.belowPanel);

		metrics = new DisplayMetrics();
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		viewPagerHeight = metrics.heightPixels / 3;
		changeViewPager(viewPagerHeight, false, false, true);

		mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {

				// When changing pages, reset the action bar actions since they
				// are dependent
				// on which page is currently active. An alternative approach is
				// to have each
				// fragment expose actions itself (rather than the activity
				// exposing actions),
				// but for simplicity, the activity provides the actions in this
				// sample.
				// supportInvalidateOptionsMenu();
			}

			@Override
			public void onPageScrolled(int position, float positionOffset,
					int positionOffsetPixels) {
			}

			@Override
			public void onPageScrollStateChanged(int state) {
			}
		});

		totalDayKillowattH = 16;
		killowattsHTextView = (TextView) findViewById(R.id.killowatts);
		killowattsHTextView.setTypeface(font);
		killowattsHTextView.getPaint().setAntiAlias(true);
		dayDate = (TextView) findViewById(R.id.day_date);
		dayDate.setTypeface(font);
		dayDate.getPaint().setAntiAlias(true);
		amount = (TextView) findViewById(R.id.day_amount);
		amount.setTypeface(font);
		amount.getPaint().setAntiAlias(true);
		if (amount == null) {
			Intent intent = new Intent(Tabs.this, MainActivity.class);
			startActivity(intent);
		}
		yearTextView = (TextView) findViewById(R.id.year_timeframe);
		yearTextView.setTypeface(font);
		yearTextView.getPaint().setAntiAlias(true);

		if (dailyData != null) {
			killowattsHTextView.setText(dailyData.getTotalkWh() + "kWh");
			amount.setText("$" + df.format(dailyData.getTotalCost()));
			dayDate.setText(formatOriginalDate(dateString));
			yearTextView.setText("" + year);
		} else {

		}

		TextView date = (TextView) findViewById(R.id.day_date);
		date.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				int tab = mTabHost.getCurrentTab();
				if (tab == 0) {
					showDatePickerDialog();
				} else if (tab == 1) {
					showDateWeekPickerDialog();
				} else if (tab == 2) {
					showMonthPickerDialog();
				}
				// showDialog(DEFAULTDATESELECTOR_ID);
			}
		});
		ImageView arrowBlue = (ImageView) findViewById(R.id.arrowBlue);
		arrowBlue.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// showDialog(DEFAULTDATESELECTOR_ID);
			}

		});

		yearButton = (TextView) findViewById(R.id.year_timeframe);
		yearButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {

			}
		});

		access_token = MainActivity.access_token;
		jGet = new JSON_Parser();
		if (access_token != null) {
			if (!debug) {
				if (MainActivity.result == null) {
					getJSON(0);
				} else {
					if (dailyJSON == null) {
						getJSON(0);
					}
					if (dailyData == null) {
						getJSON(0);
					}

				}

			} else {
				access_token = null;
			}

		}

		if (access_token == null) {
			Intent intent = new Intent(Tabs.this, MainActivity.class);
			startActivity(intent);
		}
		/**
		 * 1 Tabhost is used to hold together 3 tabs that contain fragment views
		 * Each tab holds a chart (DAY, WEEK, CYCLE) The colors are set using
		 * the setSelectedTabColor method
		 **/
		mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
		setupTabs();
		mTabHost.setOnTabChangedListener(new OnTabChangeListener() {

			public void onTabChanged(String str) {
				dialog = ProgressDialog.show(Tabs.this, "",
						"Loading. Please wait...", true);

				dialog.setCanceledOnTouchOutside(true);
				setSelectedTabColor();
				// mPager.setCurrentItem(0);
				currentTab = mTabHost.getCurrentTab();
				if (currentTab == 0) {
					if (access_token != null) {
						try {
							ScreenSlidePageFragment.personalTextView
									.setText("$"
											+ df.format(dailyData
													.getTotalCost()));
							ScreenSlidePageFragment.cityTextView.setText("$"
									+ df.format(dailyData.getCityAverage()));
							yearTextView.setText(year + "");
							dayDate.setText(formatOriginalDate(dateString));
							killowattsHTextView.setText(df.format(dailyData
									.getTotalkWh()) + "kWh");
							amount.setText("$" + dailyData.getTotalCost());
						} catch (Exception e) {
							// Log.d("InputStream", e.getLocalizedMessage());

						}
					}
				} else if (currentTab == 1) {
					if (access_token != null) {
						try {
							if (weeklyData == null || changedDate || getData) {
								weeklyData = new DailyData();
								String weeklyUrl = "https://mobileapi.lessutility.com/v1/meter/"
										+ meter
										+ "/usage?token="
										+ access_token
										+ "&date="
										+ dateString
										+ "&cycle_type=1";
								weeklyJSON = jGet.getJSONFromUrl(weeklyUrl);
								weeklyData = JSON_Parser
										.parseJSONObject(weeklyJSON);
							}
							ScreenSlidePageFragment.personalTextView
									.setText("$"
											+ df.format(weeklyData
													.getTotalCost()));
							ScreenSlidePageFragment.cityTextView.setText("$"
									+ df.format(weeklyData.getCityAverage()));
							amount.setText("$" + weeklyData.getTotalCost());
							yearTextView.setText(formatJSONDate(weeklyData
									.getIntervals().get(0).getDate()));
							dayDate.setText(formatJSONDate(weeklyData
									.getIntervals()
									.get(weeklyData.getIntervals().size() - 1)
									.getDate()));
							killowattsHTextView.setText(df.format(weeklyData
									.getTotalkWh()) + "kWh");
						} catch (Exception e) {
							// Log.d("InputStream", e.getLocalizedMessage());

						}
					}
				} else if (currentTab == 2) {
					if (access_token != null) {
						try {
							if (cycleData == null || changedDate || getData) {
								cycleData = new DailyData();
								String weeklyUrl = "https://mobileapi.lessutility.com/v1/meter/"
										+ meter
										+ "/usage?token="
										+ access_token
										+ "&date="
										+ dateString
										+ "&cycle_type=2";
								try {
									cycleJSON = jGet.getJSONFromUrl(weeklyUrl);
									cycleData = JSON_Parser
											.parseJSONObject(cycleJSON);
								} catch (Exception e) {
									// e.printStackTrace();
								}
							}
							if (cycleJSON != null) {
								ScreenSlidePageFragment.personalTextView
										.setText("$"
												+ df.format(cycleData
														.getTotalCost()));
								ScreenSlidePageFragment.cityTextView
										.setText("$"
												+ df.format(cycleData
														.getCityAverage()));
								amount.setText("$"
										+ df.format(cycleData.getTotalCost()));
								String date = cycleData.getIntervals().get(0)
										.getDate();
								yearTextView.setText(formatOriginalDate(date));
								dayDate.setText(formatOriginalDate(cycleData
										.getIntervals()
										.get(cycleData.getIntervals().size() - 1)
										.getDate()));
								killowattsHTextView.setText(df.format(cycleData
										.getTotalkWh()) + "kWh");
							}
						} catch (Exception e) {
							// Log.d("InputStream", e.getLocalizedMessage());

						}
					}
				}

				dialog.dismiss();
			}
		});

		/** The custom slider panel is initiated here **/
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

		slidingPanelParameters = (FrameLayout.LayoutParams) slidingPanel
				.getLayoutParams();
		slidingPanelParameters.width = metrics.widthPixels;
		slidingPanel.setLayoutParams(slidingPanelParameters);

		// This is where the actual sliding of the panel occurs
		menuViewButton = (ImageView) findViewById(R.id.menuViewButton);
		menuViewButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				toggleSlider();
			}
		});

		TextView goToSettings = (TextView) findViewById(R.id.gotosettings);
		goToSettings.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(Tabs.this,
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
					Intent intent = new Intent(Tabs.this, ChangePassword.class);
					startActivity(intent);
				}
			}

		});
		changepass.setTypeface(font);

		TextView dashboard = (TextView) findViewById(R.id.dashboard);
		dashboard.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isExpanded)
					toggleSlider();
			}

		});
		dashboard.setTypeface(font);

		TextView contactus = (TextView) findViewById(R.id.contact_us);
		contactus.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(Tabs.this, ContactUs.class);
					startActivity(intent);
				}
			}

		});
		contactus.setTypeface(font);

		address = (TextView) findViewById(R.id.address);
		address.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				initiatePopupWindow();
			}
		});
		address.setText(mainAddress);
		address.setTypeface(font);

		TextView faq = (TextView) findViewById(R.id.faq);
		faq.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(Tabs.this, Faq.class);
					startActivity(intent);
				}
			}

		});
		faq.setTypeface(font);

		TextView menu = (TextView) findViewById(R.id.menu_settings);
		TextView killowatts = (TextView) findViewById(R.id.killowatts);
		TextView info = (TextView) findViewById(R.id.menu_info);
		TextView about = (TextView) findViewById(R.id.about_less);
		TextView logout = (TextView) findViewById(R.id.logout);
		menu.setTypeface(font);
		info.setTypeface(font);
		about.setTypeface(font);
		logout.setTypeface(font);
		killowatts.setTypeface(font);
		logout.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();

					db.removeAll();
					editButton = false;
					MainActivity.loggedin = true;
					Intent intent = new Intent(Tabs.this, MainActivity.class);
					startActivity(intent);
				}

			}

		});

		about.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (isExpanded) {
					toggleSlider();
					Intent intent = new Intent(Tabs.this, AboutLESS.class);
					startActivity(intent);
				}

			}

		});

	}

	public void showDatePickerDialog() {
		DialogFragment newFragment = new DatePickerFragment();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	public void showDateWeekPickerDialog() {
		DateWeekPicker newFragment = new DateWeekPicker();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	public void showMonthPickerDialog() {
		DialogFragment newFragment = new DateMonthPicker();
		newFragment.show(getSupportFragmentManager(), "datePicker");
	}

	private void setupTabs() {
		// TODO Auto-generated method stub
		mTabHost.setup(this, getSupportFragmentManager(), R.id.realtabcontent);
		mTabHost.setBackgroundColor(Color.BLACK);
		mTabHost.getTabWidget().setStripEnabled(false);
		mTabHost.getTabWidget().setDividerDrawable(null);
		mTabHost.addTab(mTabHost.newTabSpec("tab1").setIndicator("DAY"),
				Tab1.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("tab2").setIndicator("WEEK"),
				Tab2.class, null);
		mTabHost.addTab(mTabHost.newTabSpec("tab3").setIndicator("CYCLE"),
				Tab3.class, null);
		setSelectedTabColor();
	}

	public static int getJSON(int i) {
		// TODO Auto-generated method stub
		JSONObject jObj = jGet.getJSONFromUrl(endpoint + "/v1/user?token="
				+ access_token);
		int value = 0;
		try {
			addresses = jObj.getJSONArray("premiseList");
			JSONObject firstAddress = new JSONObject(addresses.getString(i));
			mainAddress = firstAddress.getString("address");
			meters = firstAddress.getJSONArray("meters");
			if (meters.length() == 0) {
				value = 1;
				dailyData = new DailyData();
				killowattsHTextView.setText("");
				dayDate.setText("");
				yearTextView.setText("");
				amount.setText("");
			} else {
				meter = meters.get(0).toString();
				dailyData = new DailyData();
				dailyJSON = jGet.getJSONFromUrl(endpoint + "/v1/meter/" + meter
						+ "/usage?token=" + access_token + "&date="
						+ dateString + "&cycle_type=0");

				dailyData = JSON_Parser.parseJSONObject(dailyJSON);
				db.addDailyIntervals(dailyData.getIntervals());
				killowattsHTextView.setText(df.format(dailyData.getTotalkWh())
						+ "kWh");
				dayDate.setText(formatOriginalDate(dateString));
				yearTextView.setText("" + year);
				amount.setText("$" + df.format(dailyData.getTotalCost()));
				addresses = MainActivity.result.getJSONArray("premiseList");
				showMinuses = false;
				editButton = false;
			}

		} catch (Exception e) {
			// Log.d("InputStream", e.getLocalizedMessage());

		}
		return value;
	}

	private String formatJSONDate(String dateString) {
		String newDate = "";
		String[] splitDate = dateString.split(" ");
		String month = splitDate[1].substring(1, 3);
		String day = splitDate[1].substring(4, 6);
		if ("01".equals(month)) {
			newDate = "JAN " + day;
		} else if ("02".equals(month)) {
			newDate = "FEB " + day;
		} else if ("03".equals(month)) {
			newDate = "MAR " + day;
		} else if ("04".equals(month)) {
			newDate = "APR " + day;
		} else if ("05".equals(month)) {
			newDate = "MAY " + day;
		} else if ("06".equals(month)) {
			newDate = "JUN " + day;
		} else if ("07".equals(month)) {
			newDate = "JUL " + day;
		} else if ("08".equals(month)) {
			newDate = "AUG " + day;
		} else if ("09".equals(month)) {
			newDate = "SEP " + day;
		} else if ("10".equals(month)) {
			newDate = "OCT " + day;
		} else if ("11".equals(month)) {
			newDate = "NOV " + day;
		} else if ("12".equals(month)) {
			newDate = "DEC " + day;
		}
		return newDate;
	}

	private static String formatOriginalDate(String dateString) {
		String newDate = "";
		String[] splitDate = dateString.split("/");
		if ("01".equals(splitDate[0])) {
			newDate = "JAN " + splitDate[1];
		} else if ("02".equals(splitDate[0])) {
			newDate = "FEB " + splitDate[1];
		} else if ("03".equals(splitDate[0])) {
			newDate = "MAR " + splitDate[1];
		} else if ("04".equals(splitDate[0])) {
			newDate = "APR " + splitDate[1];
		} else if ("05".equals(splitDate[0])) {
			newDate = "MAY " + splitDate[1];
		} else if ("06".equals(splitDate[0])) {
			newDate = "JUN " + splitDate[1];
		} else if ("07".equals(splitDate[0])) {
			newDate = "JUL " + splitDate[1];
		} else if ("08".equals(splitDate[0])) {
			newDate = "AUG " + splitDate[1];
		} else if ("09".equals(splitDate[0])) {
			newDate = "SEP " + splitDate[1];
		} else if ("10".equals(splitDate[0])) {
			newDate = "OCT " + splitDate[1];
		} else if ("11".equals(splitDate[0])) {
			newDate = "NOV " + splitDate[1];
		} else if ("12".equals(splitDate[0])) {
			newDate = "DEC " + splitDate[1];
		}
		return newDate;
	}

	public static void invalidateTabHost(boolean reloadPage) {
		if (reloadPage) {
			yearButton.performClick();
		} else {
			mTabHost.invalidate();
		}
	}

	/**
	 * A toggleSlider method is created to actually slide the panel The
	 * ExpandAnimation and CollapseAnimation classes are reference here
	 **/
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

	private int getDensity() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		switch (metrics.densityDpi) {
		case DisplayMetrics.DENSITY_LOW:
			density = 0;
			break;
		case DisplayMetrics.DENSITY_MEDIUM:
			density = 1;
			break;
		case DisplayMetrics.DENSITY_HIGH:
			density = 2;
			break;
		case DisplayMetrics.DENSITY_XHIGH:
			density = 3;
			break;
		case DisplayMetrics.DENSITY_XXHIGH:
			density = 4;
			break;
		}
		return density;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	private void setSelectedTabColor() {
		RelativeLayout.LayoutParams rllp = new RelativeLayout.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT);
		rllp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		rllp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		final WindowManager.LayoutParams params = getWindow().getAttributes();

		View currentView;
		int density = getDensity();
		int textSize = 22, height = 100;
		if (density == 1) {
			textSize = 36;
		} else if (density == 3) {
			height = 140;
		}
		int current = mTabHost.getCurrentTab();
		for (int i = 0; i < mTabHost.getTabWidget().getChildCount(); i++) {
			TextView txt = (TextView) mTabHost.getTabWidget().getChildAt(i)
					.findViewById(android.R.id.title);
			txt.setTextSize(textSize);
			txt.setTypeface(font);
			txt.setLayoutParams(rllp);
			txt.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
			currentView = mTabHost.getTabWidget().getChildAt(i);
			LinearLayout.LayoutParams currentLayout = (LinearLayout.LayoutParams) currentView
					.getLayoutParams();
			currentLayout.setMargins(0, 0, 0, 0);
			currentView.setPadding(0, 0, 0, 0);
			currentView.setLayoutParams(currentLayout);
			currentView.getLayoutParams().height = height;
			if (i == current) {
				mTabHost.getTabWidget().getChildAt(i)
						.setBackgroundResource(R.drawable.button_type1_active);
				txt.setBackgroundResource(R.drawable.button_type1_active);
			} else {
				mTabHost.getTabWidget().getChildAt(i)
						.setBackgroundResource(R.drawable.button_type1);
				txt.setBackgroundResource(R.drawable.button_type1);
				txt.setTextColor(Color.WHITE);
			}
			if (i == 0 || i == 1) {
				txt.setPadding(0, 0, 5, 0);
			} else {
				txt.setPadding(0, 0, 7, 0);
			}
		}
	}

	/**
	 * If a button or image needs to move the ViewPager to the next one, this
	 * method is used.
	 **/
	public static void MoveNext() {
		// it doesn't matter if you're already in the last item
		mPager.setCurrentItem(mPager.getCurrentItem() + 1);
	}

	public static void gotoPage(int page) {
		// it doesn't matter if you're already in the last item
		mPager.setCurrentItem(page);
	}

	public static void MovePrevious() {
		// it doesn't matter if you're already in the first item
		mPager.setCurrentItem(mPager.getCurrentItem() - 1);
	}

	public static void reloadViewPager(String budget_goal2) {
		ScreenSlidePageFragment.budget_goal = budget_goal2;
		mPagerAdapter.notifyDataSetChanged();
		mPager.invalidate();
		gotoPage(2);
	}

	/**
	 * This is the method that is used to change the height of the ViewPager The
	 * last page [Period Details] holds an image that changes the height
	 **/
	public static void changeViewPager(final int height, final boolean next,
			final boolean rotate, final boolean first) {
		final Matrix matrix = new Matrix();
		ViewTreeObserver viewTreeObserver = mPager.getViewTreeObserver();
		viewTreeObserver
				.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

					@Override
					public void onGlobalLayout() {

						LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
								LinearLayout.LayoutParams.WRAP_CONTENT,
								LinearLayout.LayoutParams.WRAP_CONTENT);

						int viewPagerWidth = Tabs.mPager.getWidth();
						float viewPagerHeight = height;

						layoutParams.width = viewPagerWidth;
						layoutParams.height = (int) viewPagerHeight;

						mPager.setLayoutParams(layoutParams);
						mPager.getViewTreeObserver()
								.removeGlobalOnLayoutListener(this);
						/*
						 * if (!first) { int flip = -1;
						 * ScreenSlidePageFragment.delta
						 * .setScaleType(ScaleType.MATRIX); // required
						 * matrix.postRotate(180f * flip,
						 * ScreenSlidePageFragment.delta.getDrawable()
						 * .getBounds().width() / 2,
						 * ScreenSlidePageFragment.delta.getDrawable()
						 * .getBounds().height() / 2);
						 * ScreenSlidePageFragment.delta
						 * .setImageMatrix(matrix); }
						 */
					}
				});
		mPager.setAdapter(mPagerAdapter);
	}

	/**
	 * This creates the PopupWindow used to add,remove,or change addresses for a
	 * user It uses a PopupWindow that references a layout xml file The layout
	 * holds a ListView that shows the addresses associated with an account
	 **/
	public static PopupWindow pwindo;
	ListView listView;

	private void initiatePopupWindow() {
		try {
			// We need to get the instance of the LayoutInflater
			LayoutInflater inflater = (LayoutInflater) Tabs.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View layout = inflater.inflate(R.layout.popup_address,
					(ViewGroup) findViewById(R.id.popup_element));
			listView = (ListView) layout.findViewById(R.id.addressListView);

			// Defined Array values to show in ListView
			String[] values = new String[addresses.length()];
			for (int i = 0; i < addresses.length(); i++) {
				values[i] = addresses.getJSONObject(i).getString("address");
			}
			final DisplayMetrics dm = getResources().getDisplayMetrics();
			final float dpInPxWidth = TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 75, dm);
			float dpInPxHeight = TypedValue.applyDimension(
					TypedValue.COMPLEX_UNIT_DIP, 40, dm);
			// Assign adapter to ListView
			listView.setAdapter(new PopUpAddressArrayAdapter(this, values,
					font, 0));
			listView.setDivider(null);
			listView.setOnItemClickListener(new OnItemClickListener() {
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					JSONObject firstAddress = new JSONObject();
					try {
						if (!showMinuses) {
							firstAddress = new JSONObject(addresses
									.getString(position));
							mainAddress = firstAddress.getString("address");
							currentAddress = position;
							meter = firstAddress.getJSONArray("meters").get(0)
									.toString();
							reloadData();
							pwindo.dismiss();
						} else {
							int size = addresses.length();
							if (size > 1) {
								initiateDelete(position);
							}
						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}
			});
			int popupWidth = (int) (slidingPanel.getWidth());
			int popupHeight = (int) (slidingPanel.getHeight());
			pwindo = new PopupWindow(layout, popupWidth, popupHeight, true);
			// pwindo.setAnimationStyle(R.style.Animation);
			pwindo.setOutsideTouchable(true);
			pwindo.setBackgroundDrawable(new BitmapDrawable());
			pwindo.setFocusable(true);
			pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

			Button done = (Button) layout.findViewById(R.id.btn_close_popup);
			done.setTypeface(font);
			final Button edit = (Button) layout.findViewById(R.id.edit);
			edit.setTypeface(font);
			edit.setWidth((int) dpInPxWidth);
			edit.setHeight((int) dpInPxHeight);
			edit.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					if (!editButton) {
						edit.setText("+");
						float dpInPxWidthNew = TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 40, dm);
						float dpInPxHeightNew = TypedValue.applyDimension(
								TypedValue.COMPLEX_UNIT_DIP, 40, dm);

						edit.setWidth((int) dpInPxWidthNew / 2);
						edit.setHeight((int) dpInPxHeightNew);
						editButton = true;
						showMinuses = true;
						listView.invalidateViews();
					} else {
						showMinuses = false;
						Intent intent = new Intent(Tabs.this, FindHome.class);
						startActivity(intent);
					}

				}

			});
			RelativeLayout popup_element = (RelativeLayout) layout
					.findViewById(R.id.popup_element);
			popup_element.setOnClickListener(cancel_button_click_listener);
			LinearLayout info = (LinearLayout) layout.findViewById(R.id.info);
			info.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {

				}
			});
			done.setOnClickListener(cancel_button_click_listener);

		} catch (Exception e) {
			e.printStackTrace();
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
			int popupHeight = (int) (topLevel.getHeight() * 1.10f);
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

	private static PopupWindow budgetoverlay;
	static int countBudgetTries = 0;
	static TextView budget_value;

	public static void initiateBudget() {
		try {
			if (countBudgetTries == 0) {
				int popupWidth = (int) ((int) (slidingPanel.getWidth()) * 1.0f);
				int popupHeight = (int) (topLevel.getHeight() * 1.05f);
				budgetoverlay = new PopupWindow(budgetOverlay, popupWidth,
						popupHeight, true);
				budgetoverlay.setTouchable(true);
				budgetoverlay.setFocusable(false);
				budgetoverlay.setOutsideTouchable(false);
				budgetoverlay.setBackgroundDrawable(new BitmapDrawable());
				budgetoverlay.showAtLocation(budgetOverlay, Gravity.CENTER, 0,
						0);
				// LinearLayout element = (LinearLayout) budgetOverlay
				// .findViewById(R.id.inside_element);
				// element.setOnClickListener(cancel_budget_click_listener);
				stringValue = "";
				Button cancel = (Button) budgetOverlay
						.findViewById(R.id.cancel_button);
				cancel.setOnClickListener(cancel_budget_click_listener);
				budget_value = (TextView) budgetOverlay
						.findViewById(R.id.budget_value);
				Button retry = (Button) budgetOverlay
						.findViewById(R.id.retry_button);
				TextView textView1 = (TextView) budgetOverlay
						.findViewById(R.id.textView1);
				textView1.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (stringValue.length() < 5) {
							stringValue = stringValue + "1";
							budget_value.setText("$" + updateDecimal());
						}
					}
				});
				TextView textView2 = (TextView) budgetOverlay
						.findViewById(R.id.textView2);
				textView2.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (stringValue.length() < 5) {
							stringValue = stringValue + "2";
							budget_value.setText("$" + updateDecimal());
						}
					}
				});
				TextView textView3 = (TextView) budgetOverlay
						.findViewById(R.id.textView3);
				textView3.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (stringValue.length() < 5) {
							stringValue = stringValue + "3";
							budget_value.setText("$" + updateDecimal());
						}
					}
				});
				TextView textView4 = (TextView) budgetOverlay
						.findViewById(R.id.textView4);
				textView4.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (stringValue.length() < 5) {
							stringValue = stringValue + "4";
							budget_value.setText("$" + updateDecimal());
						}
					}
				});
				TextView textView5 = (TextView) budgetOverlay
						.findViewById(R.id.textView5);
				textView5.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (stringValue.length() < 5) {
							stringValue = stringValue + "5";
							budget_value.setText("$" + updateDecimal());
						}
					}
				});
				TextView textView6 = (TextView) budgetOverlay
						.findViewById(R.id.textView6);
				textView6.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (stringValue.length() < 5) {
							stringValue = stringValue + "6";
							budget_value.setText("$" + updateDecimal());
						}
					}
				});
				TextView textView7 = (TextView) budgetOverlay
						.findViewById(R.id.textView7);
				textView7.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (stringValue.length() < 5) {
							stringValue = stringValue + "7";
							budget_value.setText("$" + updateDecimal());
						}
					}
				});
				TextView textView8 = (TextView) budgetOverlay
						.findViewById(R.id.textView8);
				textView8.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (stringValue.length() < 5) {
							stringValue = stringValue + "8";
							budget_value.setText("$" + updateDecimal());
						}
					}
				});
				TextView textView9 = (TextView) budgetOverlay
						.findViewById(R.id.textView9);
				textView9.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (stringValue.length() < 5) {
							stringValue = stringValue + "9";
							budget_value.setText("$" + updateDecimal());
						}
					}
				});
				TextView textView0 = (TextView) budgetOverlay
						.findViewById(R.id.textView0);
				textView0.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						if (stringValue.length() < 6) {
							stringValue = stringValue + "0";
							budget_value.setText("$" + updateDecimal());
						}
					}
				});
				LinearLayout textViewB = (LinearLayout) budgetOverlay
						.findViewById(R.id.test);
				textViewB.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						int length = stringValue.length();
						if (length != 0) {
							stringValue = stringValue.substring(0,
									stringValue.length() - 1);
							budget_value.setText("$" + updateDecimal());
						}

					}
				});

				retry.setOnClickListener(new OnClickListener() {

					public void onClick(View v) {
						try {
							int info = Integer.parseInt(stringValue);
							String z = stringValue.substring(0,
									stringValue.length() - 2)
									+ "."
									+ stringValue.substring(
											stringValue.length() - 2,
											stringValue.length());
							double newZ = Double.parseDouble(z);
							if (newZ == 0 || newZ >= 1000) {
								singleError(false, false);
							} else {
								countBudgetTries = 0;
								budgetoverlay.dismiss();
								budget_value.setText("");
								ScreenSlidePageFragment.updateBudget(false, ""
										+ newZ);
							}
						} catch (Exception e) {

						}
					}
				});
			}
			countBudgetTries++;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String updateDecimal() {
		String d = "";
		if (stringValue.length() <= 2) {
			d = "0." + stringValue;
		} else {
			d = stringValue.substring(0, stringValue.length() - 2)
					+ "."
					+ stringValue.substring(stringValue.length() - 2,
							stringValue.length());
		}
		return d;
	}

	private static PopupWindow deleteoverlay;

	private void initiateDelete(final int position) {
		try {
			Tabs.this.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					int width = topLevel.getWidth();
					int height = topLevel.getHeight();
					int popupHeight = (int) (height);
					deleteoverlay = new PopupWindow(layoutDelete, width,
							popupHeight, true);
					deleteoverlay.setTouchable(true);
					deleteoverlay.setFocusable(false);
					deleteoverlay.setOutsideTouchable(false);
					deleteoverlay.setBackgroundDrawable(new BitmapDrawable());
					deleteoverlay.showAtLocation(layoutDelete, Gravity.CENTER,
							0, 0);
					LinearLayout element = (LinearLayout) layoutDelete
							.findViewById(R.id.popup_element);
					element.setOnClickListener(cancel_delete_click_listener);
					TextView error_message = (TextView) layoutDelete
							.findViewById(R.id.error_message);
					error_message.setTypeface(font);
					Button cancel = (Button) layoutDelete
							.findViewById(R.id.cancel_button);
					cancel.setTypeface(font);
					cancel.setOnClickListener(cancel_delete_click_listener);
					Button retry = (Button) layoutDelete
							.findViewById(R.id.retry_button);
					retry.setTypeface(font);
					retry.setOnClickListener(new OnClickListener() {

						public void onClick(View v) {
							deleteAddress(position);
						}
					});
				}
			});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean deleteAddress(int position) {
		// Create a new HttpClient and Post Header

		boolean errorOut = false;
		try {
			HttpClient httpclient = new DefaultHttpClient();
			JSONObject j = addresses.getJSONObject(position);
			String premise_id = j.getString("premise_id");
			URL url = new URL(
					"https://mobileapi.lessutility.com/v1/user/premise/"
							+ premise_id + "?token="
							+ MainActivity.access_token);
			URI uri = url.toURI();
			HttpDelete httpPost = new HttpDelete(uri);
			InputStream inputStream = null;
			String resultString = "";
			JSONObject obj = null;
			// Adds the post data

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
			// initiateError(true);
		}
		if (result == null) {
			getUser();
			getJSON(0);
			reloadData();
		} else {

			if (errorOut) {
				// initiateError(false);
			} else {
			}

		}
		deleteoverlay.dismiss();
		pwindo.dismiss();
		return errorOut;
	}

	public static void getUser() {
		// TODO Auto-generated method stub
		JSONObject jObj = jGet.getJSONFromUrl(endpoint + "/v1/user?token="
				+ access_token);
		try {
			dailyData = new DailyData();
			MainActivity.result = jGet
					.getJSONFromUrl("https://mobileapi.lessutility.com/v1/user/premise?token="
							+ access_token);

			addresses = MainActivity.result.getJSONArray("premiseList");
		} catch (Exception e) {
			// Log.d("InputStream", e.getLocalizedMessage());

		}
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

	public static boolean reloadData() {
		try {
			dailyData = new DailyData();
			String url = endpoint + "/v1/meter/" + meter + "/usage?token="
					+ access_token + "&date=" + dateString + "&cycle_type=0";
			dailyJSON = jGet.getJSONFromUrl(url);

			dailyData = JSON_Parser.parseJSONObject(dailyJSON);
			ScreenSlidePageFragment.personalTextView.setText("$"
					+ df.format(dailyData.getTotalCost()));
			ScreenSlidePageFragment.cityTextView.setText("$"
					+ df.format(dailyData.getCityAverage()));
			amount.setText("$" + df.format(dailyData.getTotalCost()));
			mPager.setCurrentItem(0);
			yearTextView.setText(year + "");
			dayDate.setText(formatOriginalDate(dateString));
			killowattsHTextView.setText(df.format(dailyData.getTotalkWh())
					+ "kWh");
			address.setText(mainAddress);
			getData = true;
			showMinuses = false;
			mTabHost.setCurrentTab(0);
		} catch (Exception e) {
			singleError(true, false);
		}
		return getData;
	}

	public static boolean reloadDataMonth(int page) {
		try {
			cycleData = new DailyData();
			String url = endpoint + "/v1/meter/" + meter + "/usage?token="
					+ access_token + "&date=" + dateString + "&cycle_type=2";
			cycleJSON = jGet.getJSONFromUrl(url);

			cycleData = JSON_Parser.parseJSONObject(cycleJSON);
			ScreenSlidePageFragment.personalTextView.setText("$"
					+ cycleData.getTotalCost());
			ScreenSlidePageFragment.cityTextView.setText("$"
					+ cycleData.getCityAverage());
			amount.setText("$" + cycleData.getTotalCost());
			yearTextView.setText(formatOriginalDate(cycleData.getIntervals()
					.elementAt(0).getDate()));
			dayDate.setText(formatOriginalDate(cycleData.getIntervals()
					.elementAt(cycleData.getIntervals().size() - 1).getDate()));
			killowattsHTextView.setText(cycleData.getTotalkWh() + "kWh");
			address.setText(mainAddress);
			getData = true;
			showMinuses = false;
			mTabHost.setCurrentTab(page);
		} catch (Exception e) {
			singleError(true, false);
		}
		return getData;
	}

	@SuppressWarnings("deprecation")
	public static String formatCalendar(int year, int month, int day) {
		String dayZero = String.format("%02d", day);
		String text = "";
		if (month == 0) {
			text = "JAN" + dayZero;
			dateString = "01/" + dayZero + "/" + year;
		} else if (month == 1) {
			text = "FEB " + dayZero;
			dateString = "02/" + dayZero + "/" + year;
		} else if (month == 2) {
			text = "MAR " + dayZero;
			dateString = "03/" + dayZero + "/" + year;
		} else if (month == 3) {
			text = "APR " + dayZero;
			dateString = "04/" + dayZero + "/" + year;
		} else if (month == 4) {
			text = "MAY " + dayZero;
			dateString = "05/" + dayZero + "/" + year;
		} else if (month == 5) {
			text = "JUN " + dayZero;
			dateString = "06/" + dayZero + "/" + year;
		} else if (month == 6) {
			text = "JUL " + dayZero;
			dateString = "07/" + dayZero + "/" + year;
		} else if (month == 7) {
			text = "AUG " + dayZero;
			dateString = "08/" + dayZero + "/" + year;
		} else if (month == 8) {
			text = "SEP " + dayZero;
			dateString = "09/" + dayZero + "/" + year;
		} else if (month == 9) {
			text = "OCT " + dayZero;
			dateString = "10/" + dayZero + "/" + year;
		} else if (month == 10) {
			text = "NOV " + dayZero;
			dateString = "11/" + dayZero + "/" + year;
		} else if (month == 11) {
			text = "DEC " + dayZero;
			dateString = "12/" + dayZero + "/" + year;
		}

		return text;
	}

	public Calendar getCalendar(int day, int month, int year) {
		Calendar date = Calendar.getInstance();
		date.set(Calendar.YEAR, year);
		date.set(Calendar.MONTH, month);
		date.set(Calendar.DAY_OF_MONTH, day);

		return date;
	}

	private OnClickListener texts = new OnClickListener() {
		public void onClick(View v) {
			pwindo.dismiss();

		}
	};

	private OnClickListener cancel_button_click_listener = new OnClickListener() {
		public void onClick(View v) {
			pwindo.dismiss();
			editButton = false;
			showMinuses = false;
			listView.invalidateViews();
		}
	};

	private static OnClickListener cancel_overlay_click_listener = new OnClickListener() {
		public void onClick(View v) {
			toggleSlider();
		}
	};
	private static OnClickListener cancel_delete_click_listener = new OnClickListener() {
		public void onClick(View v) {
			deleteoverlay.dismiss();
		}
	};
	private static OnClickListener cancel_budget_click_listener = new OnClickListener() {
		public void onClick(View v) {
			budget_value.setText("");
			stringValue = "";
			countBudgetTries = 0;
			budgetoverlay.dismiss();
		}
	};

	private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
		public ScreenSlidePagerAdapter(android.support.v4.app.FragmentManager fm) {
			super(fm);
		}

		@Override
		public android.support.v4.app.Fragment getItem(int position) {
			return ScreenSlidePageFragment.create(position);
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public void showNotification() {

		// define sound URI, the sound to be played when there's a notification
		// Uri soundUri =
		// RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

		// intent triggered, you can add other intent for other actions
		Intent intent = new Intent(Tabs.this, Tabs.class);
		PendingIntent pIntent = PendingIntent.getActivity(Tabs.this, 0, intent,
				0);

		// this is it, we'll build the notification!
		// in the addAction method, if you don't want any icon, just set the
		// first param to 0
		Notification mNotification = new Notification.Builder(this)

		.setContentTitle("").setContentText(broadcastMessage)
				.setSmallIcon(R.drawable.less_icon).setContentIntent(pIntent)
				.setAutoCancel(true)
				// .setSound(soundUri)

				// .addAction(R.drawable.less_icon, "View", pIntent)
				// .addAction(0, "Remind", pIntent)

				.build();
		NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		// If you want to hide the notification after it was selected, do the
		// code below
		// myNotification.flags |= Notification.FLAG_AUTO_CANCEL;

		notificationManager.notify(0, mNotification);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(gcmReceiver, gcmFilter);

	}

	@Override
	public void onDestroy() {

		GCMRegistrar.onDestroy(this);

		super.onDestroy();
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {

		super.onRestoreInstanceState(savedInstanceState);

		broadcastMessage = savedInstanceState.getString("BroadcastMessage");

	}

	private static PopupWindow singleoverlay;
	static boolean singleError = false;

	public static void singleError(final boolean date, final boolean help) {
		try {
			if (!singleError) {
				int width = topLevel.getWidth();
				int height = topLevel.getHeight();
				int popupHeight = (int) (height * 1.1f);
				singleoverlay = new PopupWindow(layoutSingle, width,
						popupHeight, true);
				singleError = true;
				singleoverlay.setTouchable(true);
				singleoverlay.setFocusable(true);
				singleoverlay.setOutsideTouchable(true);
				singleoverlay.setBackgroundDrawable(new BitmapDrawable());
				singleoverlay
						.showAtLocation(layoutSingle, Gravity.CENTER, 0, 0);
				TextView error = (TextView) layoutSingle
						.findViewById(R.id.error);
				error.setTypeface(MainActivity.font);
				if (help) {
					error.setText("Receive alerts when you approach your spending threshold.");
				} else {
					if (date) {
						error.setText("Invalid date range");
					} else {
						error.setText("Spending Goal must be between $0.01 and $999.99");
					}
				}
				LinearLayout element = (LinearLayout) layoutSingle
						.findViewById(R.id.popup_element);
				element.setOnClickListener(cancel_single_click_listener);
				Button cancel = (Button) layoutSingle
						.findViewById(R.id.cancel_button);
				cancel.setText("okay");
				cancel.setOnClickListener(cancel_single_click_listener);

			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static OnClickListener cancel_single_click_listener = new OnClickListener() {
		public void onClick(View v) {
			singleoverlay.dismiss();
			DatePickerFragment.count = 0;
			singleError = false;
		}
	};

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {

		super.onSaveInstanceState(savedInstanceState);

		savedInstanceState.putString("BroadcastMessage", broadcastMessage);

	}

	public static void popupKeyboard() {

	}

	@Override
	public void onBackPressed() {
	}

}
