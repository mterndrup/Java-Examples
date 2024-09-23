package com.array.adapters;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.beans.Addresses;
import com.lessutility.R;
import com.lessutility.SelectAddress;

public class ListViewArrayAdapter extends ArrayAdapter<Addresses> {
	private final Context context;
	private final ArrayList<Addresses> values;
	public Typeface font;
	View rowView;

	public ListViewArrayAdapter(Context context, ArrayList<Addresses> values,
			Typeface ttf) {
		super(context, R.layout.listview_address, values);
		this.context = context;
		this.values = values;
		this.font = ttf;
	}

	@Override
	public int getCount() {
		return values.size();
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		rowView = convertView;

		if (rowView == null) {
			LayoutInflater li = LayoutInflater.from(getContext());
			rowView = li.inflate(R.layout.listview_address, null);
		}
		final Addresses address = values.get(position);
		final TextView textView = (TextView) rowView
				.findViewById(R.id.selectAddressPt);
		textView.setText(address.getAddress());
		textView.setTypeface(font);
		if (SelectAddress.selectedRow == position) {
			textView.setBackgroundColor(0xFFFFFFFF);
			textView.setTextColor(R.color.menu_title_color);
		} else {
			if (position % 2 == 1) {
				textView.setBackgroundColor(0xFF4f9bd4);
			} else {
				textView.setBackgroundColor(0xFF5ebdeb);
			}
		}

		textView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// textView.setBackgroundColor(Color.WHITE);
				// textView.setTextColor(Color.BLUE);
				SelectAddress.selectedRow = position;
				SelectAddress.selectedFromList = address;
				SelectAddress.enableButton();
				SelectAddress.listView1.invalidateViews();
			}

		});

		return rowView;

	}

}