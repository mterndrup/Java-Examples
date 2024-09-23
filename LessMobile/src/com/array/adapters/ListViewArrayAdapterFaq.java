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

import com.lessutility.R;
import com.parsers.Item;

public class ListViewArrayAdapterFaq extends ArrayAdapter<Item> {
	private final Context context;
	private final ArrayList<Item> values;
	public Typeface font;
	boolean isExpanded;

	public ListViewArrayAdapterFaq(Context context, ArrayList<Item> items,
			Typeface ttf) {
		super(context, R.layout.listview_faq, items);
		this.context = context;
		this.values = items;
		this.font = ttf;
	}

	@Override
	public int getCount() {
		return values.size();
	}

	@SuppressLint("ResourceAsColor")
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			LayoutInflater li = LayoutInflater.from(getContext());
			rowView = li.inflate(R.layout.listview_faq, null);
		}

		final Item item = values.get(position);
		final TextView textView = (TextView) rowView.findViewById(R.id.top);
		final TextView bottom = (TextView) rowView.findViewById(R.id.bottom);
		textView.setText(item.getTitle());
		bottom.setText(item.getDescription());
		bottom.setTypeface(font);
		bottom.setBackgroundResource(R.color.button_type1_active);
		textView.setTypeface(font);

		if (position % 2 == 1) {
			textView.setBackgroundColor(0xFF4f9bd4);
			// textView.setBackgroundResource(R.drawable.listview_signup);
		} else {
			textView.setBackgroundColor(0xFF4f9bd4);
			// textView.setBackgroundResource(R.drawable.listview_signup_alt);
		}

		textView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (!isExpanded) {
					bottom.setVisibility(View.VISIBLE);
				} else {
					bottom.setVisibility(View.GONE);
				}
				isExpanded = !isExpanded;
			}

		});

		return rowView;

	}

}