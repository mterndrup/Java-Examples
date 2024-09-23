package com.array.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lessutility.R;
import com.lessutility.R.color;
import com.lessutility.Tabs;

public class PopUpAddressArrayAdapter extends ArrayAdapter<String> {
	private final Context context;
	private final String[] values;
	public Typeface font;
	private int page;

	public PopUpAddressArrayAdapter(Context context, String[] values,
			Typeface ttf, int page) {
		super(context, R.layout.listview_address, values);
		this.context = context;
		this.values = values;
		this.font = ttf;
		this.page = page;
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View rowView = convertView;
		ViewHolder holder = new ViewHolder();
		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.popup_address_listview, parent,
					false);
			holder.textView = (TextView) rowView.findViewById(R.id.label);
			holder.minus = (ImageView) rowView.findViewById(R.id.minus);
			rowView.setTag(holder);
		} else {
			holder = (ViewHolder) rowView.getTag();
		}

		holder.textView.setText(values[position]);
		holder.textView.setTypeface(font);
		

		if (!Tabs.showMinuses) {
			if(Tabs.currentAddress==position){
				holder.minus.setVisibility(View.VISIBLE);
				holder.minus.setImageResource(R.drawable.check);
			}else{
				holder.minus.setVisibility(View.GONE);
				holder.minus.setImageResource(R.drawable.minus);
			}
		} else {
			holder.minus.setImageResource(R.drawable.minus);
			holder.minus.setVisibility(View.VISIBLE);
		}

		holder.textView.setTextColor(Color.WHITE);
		if (position % 2 == 1) {
			if (page == 1) {
				rowView.setBackgroundColor(color.listview_alt);
			}
		} else {
			if (page == 1) {
				rowView.setBackgroundColor(color.listview_original);
			}
			// holder.textView.setTextColor(color.listview_address_text_color);
		}

		

		return rowView;

	}

	private class ViewHolder {
		ImageView minus;
		TextView textView;

	}
}