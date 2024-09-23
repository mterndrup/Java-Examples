package com.date.slider;

import java.util.Calendar;

import com.lessutility.Tabs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

public class DatePickerFragment extends DialogFragment implements
		DatePickerDialog.OnDateSetListener {
	String date;
	int year, month, day;
	public static int count = 0;

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current date as the default date in the picker
		if (!Tabs.changedDate) {
			final Calendar c = Calendar.getInstance();
			c.add(Calendar.DATE, -1);
			year = c.get(Calendar.YEAR);
			month = c.get(Calendar.MONTH);
			day = c.get(Calendar.DAY_OF_MONTH);
			date = Tabs.dateString;
		} else {
			year = Tabs.year;
			month = Tabs.month;
			day = Tabs.day;
		}

		// Create a new instance of DatePickerDialog and return it
		return new DatePickerDialog(getActivity(), this, year, month, day);
	}

	public void onDateChanged(DatePicker view, int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
	}

	public void onDateSet(DatePicker view, int year, int month, int day) {
		final Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 0);
		int yearL = c.get(Calendar.YEAR);
		int monthL = c.get(Calendar.MONTH);
		int dayL = c.get(Calendar.DAY_OF_MONTH);
		boolean tooHigh = false;
		if (year <= yearL) {
			if (month < monthL) {
				processInfo(year, month, day);
			} else {
				if (day < dayL) {
					processInfo(year, month, day);
				} else {
					tooHigh = true;
				}
			}
		} else {
			if (month <= monthL) {
				if (day < dayL) {
					processInfo(year, month, day);
				} else {
					tooHigh = true;
				}
			} else {
				tooHigh = true;
			}
		}
		if (tooHigh) {
			if (count == 0) {
				Tabs.singleError(true, false);
				count++;
			}
		}

	}

	void processInfo(int year, int month, int day) {
		this.year = year;
		this.month = month;
		this.day = day;
		Tabs.year = year;
		Tabs.month = month;
		Tabs.day = day;
		Tabs.changedDate = true;
		Tabs.formatCalendar(year, month, day);
		boolean getData = Tabs.reloadData();
		if (getData) {
			Tabs.dayDate.setText(Tabs.formatCalendar(year, month, day));
		}
		Tabs.gotoPage(0);
		Tabs.mTabHost.invalidate();
		Tabs.mTabHost.setCurrentTab(Tabs.mTabHost.getCurrentTab());
	}
}