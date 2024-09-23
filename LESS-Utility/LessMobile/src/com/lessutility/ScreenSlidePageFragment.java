/*
 * Copyright 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.lessutility;

import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Checkable;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView.ScaleType;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.array.adapters.MobileArrayAdapter;
import com.beans.Intervals;
import com.parsers.JSON.JSON_Parser;

/**
 * This class is used for the ViewPager that lays at the bottom of the Tabs (aka
 * Dashboard) page A fragment representing a single step in a wizard. The
 * fragment shows the page title along with data based specialized for the user.
 * 
 */
public class ScreenSlidePageFragment extends Fragment {
	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";
	public static TextView personalTextView, cityTextView;
	public static ImageView delta;
	public static EditText budget;
	public static com.draw.SwitchButton switchButton;
	public static String budget_enabled, bill_to_date, budget_goal, days_left;
	boolean isOn = true;

	ViewGroup rootView = null;

	/**
	 * The fragment's page number, which is set to the argument value for
	 * {@link #ARG_PAGE}.
	 */
	private int mPageNumber;
	float xCoordinate, yCoordinate;
	private boolean isExanded, debug = false;
	Typeface font;
	ImageView arrow_orig, arrow2;
	private Handler mHandler = new Handler();
	int count;

	/**
	 * Factory method for this fragment class. Constructs a new fragment for the
	 * given page number.
	 */
	public static Fragment create(int position) {
		ScreenSlidePageFragment fragment = new ScreenSlidePageFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, position);
		fragment.setArguments(args);
		return fragment;
	}

	public ScreenSlidePageFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
		font = MainActivity.font;
		
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflates the layout containing a title and body text.

		if (mPageNumber + 1 == 1) {
			/** This is the 1st page that shows social comparisons **/
			rootView = (ViewGroup) inflater.inflate(
					R.layout.fragment_screen_slide_page, container, false);

			TextView texttop = (TextView) rootView.findViewById(R.id.texttop);
			personalTextView = (TextView) rootView.findViewById(R.id.textView1);
			cityTextView = (TextView) rootView.findViewById(R.id.textView2);
			texttop.setTypeface(Tabs.font);

			arrow_orig = (ImageView) rootView.findViewById(R.id.arrow);
			arrow_orig.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Tabs.MoveNext();
				}
			});

			if (Tabs.currentTab == 0) {
				if (Tabs.access_token != null) {
					try {
						float cost = Tabs.dailyData.getTotalCost();
						float cityAvg = Tabs.dailyData.getCityAverage();

						personalTextView.setText("$" + cost);
						cityTextView.setText("$" + Float.toString(cityAvg));
					} catch (Exception e) {
						// Log.d("InputStream", e.getLocalizedMessage());

					}
				}
			} else if (Tabs.currentTab == 1) {
				if (Tabs.access_token != null) {
					float cost = Tabs.weeklyData.getTotalCost();
					if (cost != 0.0f) {
						personalTextView.setText("$" + cost);
					}
					cityTextView.setText("$"
							+ Float.toString(Tabs.weeklyData.getCityAverage()));
				}
			}

			personalTextView.setTypeface(font);
			cityTextView.setTypeface(font);

		} else if (mPageNumber + 1 == 2) {
			/** This page shows tips on how to better manage electricity use **/
			rootView = (ViewGroup) inflater.inflate(
					R.layout.fragment_screen_slide_page2, container, false);
			TextView texttop = (TextView) rootView.findViewById(R.id.texttop);

			TextView tip = (TextView) rootView.findViewById(R.id.tip);
			texttop.setTypeface(font);
			tip.setTypeface(font);

			arrow2 = (ImageView) rootView.findViewById(R.id.arrow2);

			arrow2.setOnClickListener(new OnClickListener() {

				public void onClick(View v) {
					Tabs.MoveNext();
				}

			});
			String degree = "\u00B0";
			int value = (int) ((Math.random() * (10 - 0)) + 0);
			if (value == 0) {
				tip.setText("Prevent air leaks by caulking, sealing, or using weather strips on all seams, cracks, and openings to the outside.");
			} else if (value == 1) {
				tip.setText("Clean or replace filters on furnaces and air conditioners once a month or as recommended.");
			} else if (value == 2) {
				tip.setText("Eliminate trapped air from hot-water radiators once or twice a season.");
			} else if (value == 3) {
				tip.setText("Place heat-resistant radiator reflectors between exterior walls and any radiators in your house.");
			} else if (value == 4) {
				tip.setText("Consider using an interior fan along with your window air conditioner to spread the cooled air through your home without greatly increasing your power use.");
			} else if (value == 5) {
				tip.setText("Avoid placing appliances that give off heat such as lamps or TVs near a thermostat.");
			} else if (value == 6) {
				tip.setText("Repair leaky faucets promptly; a leaky faucet wastes gallons of water in a short period of time.");
			} else if (value == 7) {
				tip.setText("Set the thermostat on your water heater to 120"+degree+" F to get comfortable hot water for most uses.");
			} else if (value == 8) {
				tip.setText("Insulate the first 6 feet of the hot and cold water pipes connected to the water heater.");
			} else if (value == 9) {
				tip.setText("ENERGY STAR qualified CFL bulbs use about 75% less energy and last up to 10 times longer than traditional incandescents.");
			} else if (value == 10) {
				// tip.setText("");
			}

		} else if (mPageNumber + 1 == 3) {
			/**
			 * This is the 'Budget Planner' page The bar that shows the current
			 * amount is draw onto the Canvas
			 **/
			rootView = (ViewGroup) inflater.inflate(
					R.layout.fragment_screen_slide_page3, container, false);
			budget = (EditText) rootView.findViewById(R.id.budget_edittext);
			budget.setTypeface(font);
			getJSON();
			ImageView arrow1 = (ImageView) rootView.findViewById(R.id.arrow);
			ImageView help = (ImageView) rootView
					.findViewById(R.id.help);
			help.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Tabs.singleError(false, true);
				}
			});

			// budget.setText(DrawBudget.budget_goal);

			TextView texttop = (TextView) rootView.findViewById(R.id.texttop);
			TextView spendingGoal = (TextView) rootView
					.findViewById(R.id.textView1);
			TextView days = (TextView) rootView.findViewById(R.id.days);
			spendingGoal.setTypeface(font);
			days.setTypeface(font);
			if (days_left != null) {
				if ("1".equals(days_left)) {
					days.setText(days_left + " day left");
				} else {
					days.setText(days_left + " days left");
				}

			}
			texttop.setTypeface(font);
			View checkboxView = rootView.findViewById(R.id.toggle);
			switchButton = (com.draw.SwitchButton) checkboxView;
			arrow1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Tabs.MoveNext();
				}
			});

			budget.requestFocus();
			count = 0;
			budget.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View arg0, MotionEvent arg1) {
					if (count == 0) {
						Tabs.initiateBudget();
						count++;
					} else {
						count = 0;
					}
					return true;
				}
			});
			if (checkboxView != null && checkboxView instanceof Checkable) {

				com.draw.SwitchButton switchButton = (com.draw.SwitchButton) checkboxView;
				switchButton
						.setOnCheckedChangeListener(new OnCheckedChangeListener() {

							public void onCheckedChanged(
									CompoundButton buttonView, boolean isChecked) {
								if (!isChecked) {
									Tabs.initiateBudget();
								} else {
									budget.setText("");
									updateBudget(isChecked, "0");
								}
								isOn = isChecked;
							}
						});
			}

			if ("0".equals(budget_goal)) {
				checkboxView.performClick();
			}
		} else if (mPageNumber + 1 == 4) {
			/**
			 * This is where the 'Period Details' page is inflated It references
			 * an xml file that contains a listview A custom listview adapter is
			 * set The delta image, when selected, changes the height of the
			 * ViewPager The arrow image, when selected, moves the current page
			 * to the next one
			 **/
			rootView = (ViewGroup) inflater.inflate(R.layout.detailslist,
					container, false);
			TextView texttop = (TextView) rootView
					.findViewById(R.id.period_details);
			texttop.setTypeface(font);

			ListView listView1 = (ListView) rootView
					.findViewById(R.id.sampleListView);

			if (!debug) {
				try 
				{
					Vector<Intervals> values = Tabs.dailyData.getIntervals();

					MobileArrayAdapter adapter = new MobileArrayAdapter(
							this.getActivity(), values);
					listView1.setAdapter(adapter);
					listView1.setDivider(null);
					final Matrix matrix = new Matrix();
					/*
					delta = (ImageView) rootView.findViewById(R.id.delta);
					delta.setOnClickListener(new OnClickListener() {
						@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
						public void onClick(View v) {
							if (!isExanded) {
								Tabs.mPager.setPagingEnabled(false);
								Tabs.mTabHost.getTabWidget()
										.getChildTabViewAt(0).setEnabled(false);
								Tabs.mTabHost.getTabWidget()
										.getChildTabViewAt(1).setEnabled(false);
								Tabs.mTabHost.getTabWidget()
										.getChildTabViewAt(2).setEnabled(false);
								Tabs.changeViewPager(Tabs.viewPagerHeight * 2,
										true, true, false);
							} else {
								Tabs.mPager.setPagingEnabled(true);
								Tabs.mTabHost.getTabWidget()
										.getChildTabViewAt(0).setEnabled(true);
								Tabs.mTabHost.getTabWidget()
										.getChildTabViewAt(1).setEnabled(true);
								Tabs.mTabHost.getTabWidget()
										.getChildTabViewAt(2).setEnabled(true);
								Tabs.changeViewPager(Tabs.viewPagerHeight,
										false, true, false);
							}
							Tabs.gotoPage(3);
							isExanded = !isExanded;
						}
					});
					*/
				} catch (Exception e) {
					// Log.d("InputStream", e.getLocalizedMessage());

				}
			}

		}

		return rootView;
	}

	public static void showKeyboard(Activity activity) {
		if (activity != null) {
			activity.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
		}
	}

	public static void hideKeyboard(Activity activity) {
		if (activity != null) {
			activity.getWindow().setSoftInputMode(
					WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		}
	}

	public static void updateBudget(boolean isOn, String value) {
		boolean failed = false;
		try {
			URL url = new URL("https://mobileapi.lessutility.com/v1/meter/"
					+ Tabs.meter + "/budget?token=" + MainActivity.access_token);
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setRequestMethod("PUT");
			connection.setDoOutput(true);
			connection.setRequestProperty("Content-Type", "application/json");
			connection.setRequestProperty("Accept", "application/json");
			OutputStreamWriter osw = new OutputStreamWriter(
					connection.getOutputStream());
			JSONObject jsonObject = new JSONObject();
			budget_goal = value;
			String budget_enabled = "1";
			if (!isOn) {

			} else {
				budget_enabled = "0";
				// switchButton.setChecked(true);
			}
			jsonObject.accumulate("budget_goal", budget_goal);
			jsonObject.accumulate("budget_enabled", budget_enabled);

			// convert JSONObject to JSON to String
			String json = jsonObject.toString();
			osw.write(json);
			osw.flush();
			osw.close();
			System.err.println(connection.getResponseCode());
			Tabs.reloadViewPager(value);
		} catch (Exception e) {
			failed = true;
		}
	}

	public void setData(String budget_goal) {
		this.budget_goal = budget_goal;
	}

	/**
	 * Returns the page number represented by this fragment object.
	 */
	public int getPageNumber() {
		return mPageNumber;
	}

	private void getJSON() {
		JSON_Parser jGet = new JSON_Parser();

		try {
			JSONObject firstAddress = new JSONObject(
					Tabs.addresses.getString(Tabs.currentAddress));
			JSONArray meters = firstAddress.getJSONArray("meters");
			String meter = meters.get(0).toString();
			JSONObject jObj = jGet
					.getJSONFromUrl("https://mobileapi.lessutility.com/v1/meter/"
							+ meter + "/budget?token=" + Tabs.access_token);
			budget_enabled = jObj.getString("budget_enabled");
			bill_to_date = jObj.getString("bill_to_date");
			budget_goal = jObj.getString("budget_goal");
			days_left = jObj.getString("days_left");
		} catch (Exception e) {
			// Log.d("InputStream", e.getLocalizedMessage());

		}
	}

}
