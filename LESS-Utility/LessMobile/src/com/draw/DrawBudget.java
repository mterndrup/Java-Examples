package com.draw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import com.beans.DailyData;
import com.beans.User;
import com.lessutility.MainActivity;
import com.lessutility.ScreenSlidePageFragment;
import com.lessutility.Tabs;
import com.parsers.JSON.JSON_Parser;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.widget.RelativeLayout;

public class DrawBudget extends RelativeLayout {

	Paint paint = new Paint();
	Canvas mCanvas;
	Context context;
	int widthScreen, textSize, density = -1, height;
	public static JSONObject result;
	boolean gotData;
	int count = 0;

	public DrawBudget(Context context) {
		super(context);
		setWillNotDraw(false);
		this.density = Tabs.density;
		this.context = context;
		setHeight();
		if (!gotData) {
		}
	}

	public DrawBudget(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
		this.density = Tabs.density;
		this.context = context;
		setHeight();

	}

	private void setHeight() {
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		widthScreen = (int) (metrics.widthPixels * 0.95);
		textSize = 22;
		height = 40;
		if (density == 1) {
			height = 50;
			textSize = 34;
		} else if (density == 3) {
			height = 50;
			textSize = 34;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mCanvas = canvas;
		super.onDraw(canvas);
		try {
			float bdget = Float.parseFloat(ScreenSlidePageFragment.budget_goal);
			float bill = Float.parseFloat(ScreenSlidePageFragment.bill_to_date);
			double cutValue = (bill / bdget);
			paint.setColor(0xFF2dad5f);
			canvas.drawRect(0, 0, widthScreen, height, paint);
			paint.setColor(Color.WHITE);
			paint.setAntiAlias(true);
			paint.setTextSize(textSize);
			paint.setTypeface(Tabs.font);
			String spent = "$" + ScreenSlidePageFragment.bill_to_date;
			float spentWidth = paint.measureText(spent);
			int xValue = 0;
			if (cutValue > 1) {
				if (bdget != 0) {
					cutValue = 0;
					xValue = (int) (((widthScreen * 0.9) * cutValue) - spentWidth - 10);
				}else{
					cutValue = 0;
				}
				
			}
			
			int yValue = 25;
			if (density == 1) {
				// xValue = (int) (xValue - spentWidth);
				yValue = yValue + 12;
			} else if (density == 2) {
				yValue = yValue + 2;
			} else if (density == 3) {
				// xValue = (int) (xValue - spentWidth);
				yValue = yValue + 15;
			}
			float left = (float) ((widthScreen * 1) * cutValue);
			if (left < spentWidth) {
				// left = spentWidth;
				xValue = 15;
			} else {
				xValue = (int) (left - spentWidth - 5);
			}
			if (cutValue > 1) {
				paint.setColor(Color.parseColor("#950000"));
			} else {
				paint.setColor(Color.parseColor("#84b0dd"));
			}
			canvas.drawRect(left, 0, widthScreen, height, paint);
			paint.setColor(Color.BLACK);
			canvas.drawText(spent, xValue, yValue, paint);
			ScreenSlidePageFragment.budget.setText("$"
					+ ScreenSlidePageFragment.budget_goal);
		} catch (Exception e) {

		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(widthScreen, height);

	}

}