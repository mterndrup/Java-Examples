package com.lessutility;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.array.adapters.ListViewArrayAdapter;
import com.beans.Addresses;
import com.crashlytics.android.Crashlytics;

/**
 * Created by Matthew Terndrup - Jan 27, 2014. This is where a user chooses from
 * a list of addresses returned during the signup process This page comes up
 * after FindHome
 * **/
public class SelectAddress extends ListActivity {

	public static Button continue_button;
	public static Addresses selectedFromList;
	public static int selectedRow = -1;
	public static ListView listView1;

	@SuppressLint("ResourceAsColor")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.selectaddress);

		Crashlytics.start(this);
		Typeface font = Typeface.createFromAsset(getAssets(),
				"Raleway-Thin.ttf");

		TextView pageTitle = (TextView) findViewById(R.id.pageTitle);
		TextView selectaddress = (TextView) findViewById(R.id.selectAddressText);
		pageTitle.setTypeface(font);
		selectaddress.setTypeface(font);
		listView1 = getListView();
		
		ArrayList<Addresses> values = FindHome.addresses;

		ListViewArrayAdapter adapter = new ListViewArrayAdapter(getApplicationContext(), values, font);
		listView1.setAdapter(adapter);
		listView1.setClickable(true);
		listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
 
		public void onItemClick(AdapterView<?> adapter, View view, int position, long arg3) {
			//TextView current = (TextView) listView1.getItemAtPosition(position);
			//current.setBackgroundColor(Color.WHITE);
			
		  }
		});
		
		
		//ArrayAdapter<String> adp=new ArrayAdapter<String> (getBaseContext(),
			//	android.R.layout.simple_dropdown_item_1line,values);
		//adp.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		
		//listView1.setAdapter(adp);

		continue_button = (Button) findViewById(R.id.continue_button);
		continue_button.setEnabled(false);
		continue_button.setTextColor(R.color.button_type3_disabled_textColor);
		continue_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				selectedRow = -1;
				Intent intent = new Intent(SelectAddress.this, SignUp.class);
				startActivity(intent);
			}

		});
		continue_button.setTypeface(font);

		Button back_button = (Button) findViewById(R.id.back_button);
		back_button.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				selectedRow = -1;
				Intent intent = new Intent(SelectAddress.this, FindHome.class);
				startActivity(intent);
			}

		});
		back_button.setTypeface(font);

	}
	
	

	public static void enableButton() {
		continue_button.setEnabled(true);
		continue_button.setTextColor(Color.WHITE);
	}
}
