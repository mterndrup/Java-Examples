package com.array.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.lessutility.ContactUs;
import com.lessutility.R;
import com.lessutility.R.color;

public class PopUpContactUsArrayAdapter extends ArrayAdapter<String> {
private final Context context;
private final String[] values;
public Typeface font;
private int page;

public PopUpContactUsArrayAdapter(Context context, String[] values, Typeface ttf, int page) {
    super(context, R.layout.listview_address, values);
    this.context = context;
    this.values = values;  
    this.font=ttf;
    this.page=page;
}

@SuppressLint("ResourceAsColor")
@Override
public View getView(final int position, View convertView, ViewGroup parent) {
    LayoutInflater inflater = (LayoutInflater) context
            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.popup_contactus_listview, parent, false);
        final TextView textView = (TextView) rowView.findViewById(R.id.label);
        textView.setText(values[position]);
        textView.setTypeface(font);
        
        if(position%2==1){
        	if(page==1){
        		rowView.setBackgroundColor(0xFF5ebdeb);
        	}
            textView.setTextColor(Color.WHITE);
        }else{
        	if(page==1){
        		rowView.setBackgroundColor(0xFF4f9bd4);
        	}
        	textView.setTextColor(color.listview_address_text_color);
        }
        if (ContactUs.selectedRow == position) {
        	textView.setBackgroundColor(0xFFFFFFFF);
			textView.setTextColor(R.color.menu_title_color);
        }
        textView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				ContactUs.selectedRow = position;
				ContactUs.general_comment.setText(textView.getText().toString());
				ContactUs.listView.invalidateViews();
				ContactUs.pwindo.dismiss();
			}

		});
        

    return rowView;

}
}