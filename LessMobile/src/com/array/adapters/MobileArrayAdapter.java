package com.array.adapters;

import java.util.Vector;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.beans.Intervals;
import com.lessutility.MainActivity;
import com.lessutility.R;

public class MobileArrayAdapter extends ArrayAdapter<Intervals> {
	private final Context context;
	private final Vector<Intervals> values;

	public MobileArrayAdapter(Context context, Vector<Intervals> values) {
		super(context, R.layout.detailslistinfo, values);
		this.context = context;
		this.values = values;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			LayoutInflater li = LayoutInflater.from(getContext());
			rowView = li.inflate(R.layout.detailslistinfo, null);
		}
		TextView textView = (TextView) rowView.findViewById(R.id.label1);
		TextView textView2 = (TextView) rowView.findViewById(R.id.label2);
		TextView textView3 = (TextView) rowView.findViewById(R.id.label3);
		String time = "AM";

		if (position % 2 == 0) {
			rowView.setBackgroundColor(0xFF5ebdeb);
		} else {
			rowView.setBackgroundColor(0xFF4f9bd4);
		}
		if (position == 0) {
			textView.setText("12AM");
			textView2.setText("$" + values.get(position).getCost());
			textView3.setText(values.get(position).getkW() + " kWh");
		} else {
			if (position < 12) {
				textView.setText("" + position + time);
			} else {
				if (position == 12) {
					textView.setText("12PM");
				} else {
					textView.setText("" + (position - 12) + "PM");
				}
			}

			textView2.setText("$" + values.get(position).getCost());
			textView3.setText(values.get(position).getkW() + " kWh");
		}
		textView.setTextColor(Color.WHITE);
		textView2.setTextColor(Color.WHITE);
		textView3.setTextColor(Color.WHITE);
		textView.setTypeface(MainActivity.font);
		textView2.setTypeface(MainActivity.font);
		textView3.setTypeface(MainActivity.font);

		return rowView;
	}
}