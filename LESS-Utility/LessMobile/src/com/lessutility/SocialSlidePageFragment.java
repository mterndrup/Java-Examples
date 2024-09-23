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

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

/**
 * Created by Matthew Terndrup - Jan 27, 2014. This Fragment holds the
 * additional information in the ContactUs page. There are 3 slides in total.
 * 
 * <p>
 * This class is used by the {@link CardFlipActivity} and
 * {@link ScreenSlideActivity} samples.
 * </p>
 */
public class SocialSlidePageFragment extends Fragment {
	/**
	 * The argument key for the page number this fragment represents.
	 */
	public static final String ARG_PAGE = "page";
	public static TextView personalTextView, cityTextView;

	/**
	 * The fragment's page number, which is set to the argument value for
	 * {@link #ARG_PAGE}.
	 */
	private int mPageNumber;

	/**
	 * Factory method for this fragment class. Constructs a new fragment for the
	 * given page number.
	 */
	public static Fragment create(int position) {
		SocialSlidePageFragment fragment = new SocialSlidePageFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PAGE, position);
		fragment.setArguments(args);
		return fragment;
	}

	public SocialSlidePageFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPageNumber = getArguments().getInt(ARG_PAGE);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		ViewGroup rootView = null;
		final SlidingDrawer slidingDrawer;
		final Button slideButton;
		Typeface font = Typeface.createFromAsset(this.getActivity().getAssets(),
				"Raleway-Thin.ttf");
		if (mPageNumber + 1 == 1) {
			rootView = (ViewGroup) inflater.inflate(R.layout.socialviewpager,
					container, false);
			TextView textView = (TextView) rootView
					.findViewById(R.id.texttop);
			textView.setTypeface(font);

			ImageView arrow1 = (ImageView) rootView.findViewById(R.id.arrow);
			arrow1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					ContactUs.MoveNext();
				}
			});
			ImageView twitter = (ImageView) rootView.findViewById(R.id.imageView1);
			twitter.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.twitter.com/LESSutility/"));
					startActivity(browserIntent);
				}
			});
			ImageView facebook = (ImageView) rootView.findViewById(R.id.imageView2);
			facebook.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://m.facebook.com/lessutility"));
					startActivity(browserIntent);
				}
			});

		} else if (mPageNumber + 1 == 2) {
			rootView = (ViewGroup) inflater.inflate(R.layout.socialviewpager2,
					container, false);
			TextView textView = (TextView) rootView
					.findViewById(R.id.texttop);
			TextView utility = (TextView) rootView
					.findViewById(R.id.utility);
			TextView phone = (TextView) rootView
					.findViewById(R.id.phone);
			textView.setTypeface(font);
			utility.setTypeface(font);
			phone.setTypeface(font);
			ImageView arrow1 = (ImageView) rootView.findViewById(R.id.arrow);
			arrow1.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					ContactUs.MoveNext();
				}
			});
		} else if (mPageNumber + 1 == 3) {
			rootView = (ViewGroup) inflater.inflate(R.layout.socialviewpager3,
					container, false);
			TextView textView = (TextView) rootView
					.findViewById(R.id.texttop);
			TextView techsupport = (TextView) rootView
					.findViewById(R.id.techsupport);
			TextView email = (TextView) rootView
					.findViewById(R.id.email);
			TextView phone = (TextView) rootView
					.findViewById(R.id.phone);
			TextView office = (TextView) rootView
					.findViewById(R.id.office);
			textView.setTypeface(font);
			techsupport.setTypeface(font);
			email.setTypeface(font);
			phone.setTypeface(font);
			office.setTypeface(font);
		}

		return rootView;
	}

	/**
	 * Returns the page number represented by this fragment object.
	 */
	public int getPageNumber() {
		return mPageNumber;
	}

}
