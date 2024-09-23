package com.draw;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.beans.Intervals;
import com.lessutility.MainActivity;
import com.lessutility.Tabs;

public class DrawingWeek extends View {
	Paint paint = new Paint();
	int xStored, yStored, canvasHeight, canvasWidth, elementAt;
	boolean drawNew = false;
	List<Rect> rectangles;
	String degree = "\u00B0" + "F";
	Vector<Intervals> energyData;

	public DrawingWeek(Context context) {
		super(context);
	}

	public DrawingWeek(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int width = super.getWidth();
		int height = super.getHeight();
		canvasWidth = width;
		canvasHeight = height;
		super.onDraw(canvas);
		rectangles = new ArrayList<Rect>();

		try {
			energyData = Tabs.weeklyData.getIntervals();
			int maxPosition = 0;
			if (energyData.size() != 0) {
				for (int i = 0; i < energyData.size(); i++) {
					if (i != 0) {
						if (energyData.get(i).getCost() > energyData.get(
								maxPosition).getCost()) {
							maxPosition = i;
						}
					}
				}
			}
			Shader shader = null;

			int colWidth = width / 7;
			if (energyData.size() != 0) {
				for (int i = 0; i < energyData.size(); i++) {
					Float currentData = energyData.get(i).getCost();
					double splitInto = currentData
							/ energyData.get(maxPosition).getCost();
					int startColor = 0xFF1b8040;
					int endColor = 0xFF2dad5f;
					if (splitInto == 1) {

					} else if (splitInto >= 0.9 && splitInto < 1) {
						startColor = 0xEF1b8040;
						endColor = 0xEF2dad5f;
					} else if (splitInto < 0.9 && splitInto >= 0.8) {
						startColor = 0xDF1b8040;
						endColor = 0xDF2dad5f;
					} else if (splitInto < 0.8 && splitInto >= 0.7) {
						startColor = 0xCF1b8040;
						endColor = 0xCF2dad5f;
					} else if (splitInto <= 0.7) {
						startColor = 0xBF1b8040;
						endColor = 0xFF2dad5f;
					}

					int left = 2 + (i * colWidth);
					int right = (i * colWidth) + colWidth;
					double heightIndicator = 1;

					int bottom = (int) (height
							* (currentData / energyData.get(maxPosition)
									.getCost()) * heightIndicator);
					// adding a shader paint creates the gradient needed for the
					// chart
					shader = new LinearGradient(0, 0, 0,
							(float) (bottom * 0.5), endColor, startColor,
							TileMode.CLAMP);
					paint.setShader(shader);
					final Rect currentRect = new Rect(left, height - bottom,
							right, height);
					rectangles.add(currentRect);

					canvas.drawRect(currentRect, paint);
				}
			}
		} catch (Exception e) {
			// Log.d("InputStream", e.getLocalizedMessage());

		}
		
		int density = Tabs.density;
		int textSize = 40;
		int flip = 1;
		if (drawNew) {
			if(density==1){
				textSize = (int) (textSize * 1.25);
			}else if(density==3){
				textSize = (int) (textSize * 1.25);
			}
			paint.setShader(null);
			paint.setColor(0xFF2c3e50);
			Path path = new Path();
			int triangleValue = canvasWidth / 40;
			path.moveTo(xStored, height - triangleValue);
			if (elementAt < 4) {
				path.lineTo(xStored + triangleValue, height
						- (triangleValue * 2));
				path.lineTo(xStored + triangleValue, height
						- triangleValue);
				canvas.drawPath(path, paint);
			} else {
				flip = -1;
				path.lineTo(xStored - triangleValue, height
						- (triangleValue * 2));
				path.lineTo(xStored - triangleValue, height
						- triangleValue);
				canvas.drawPath(path, paint);
			}
			double cutValue = 3;
			double multiplyBy = 3;
			if(density==1||density==2){
				cutValue = 2.25;
				multiplyBy = 3.75;
			}
			int left = xStored + (triangleValue * flip);
			int top = canvasHeight / 5;
			int right = (int) (xStored + ((canvasWidth / cutValue)) * flip);
			int bottom = height - triangleValue;
			Rect newRect = new Rect(left, top, right, bottom);
			canvas.drawRect(newRect, paint);
			paint.setAntiAlias(true);
			paint.setColor(Color.WHITE);
			paint.setTextSize(textSize);
			paint.setTypeface(Tabs.font);
			double paddingRight = triangleValue;
			double paddingTop = triangleValue * multiplyBy;
			String time = formatOriginalDate(Tabs.weeklyData.getIntervals().get(elementAt)
					.getDate());
			float timeWidth = paint.measureText(time);
			int switchInt = right;
			if(flip==-1){
				switchInt = left;
			}
			canvas.drawText(time, (float) ((switchInt - timeWidth) - paddingRight),
					(float) (top + paddingTop), paint);
			String cost = "$"
					+ Tabs.weeklyData.getIntervals().get(elementAt)
							.getCost();
			float costWidth = paint.measureText(cost);
			canvas.drawText(cost, (float) ((switchInt - costWidth) - paddingRight),
					(float) (top + (paddingTop * 2)), paint);
			paint.setColor(0xFF909394);
			paint.setTextSize((float) (textSize*0.7));
			String killowatts = Tabs.weeklyData.getIntervals()
					.get(elementAt).getkW()
					+ "kW";
			float killowattWidth = paint.measureText(killowatts);
			canvas.drawText(killowatts, (float) ((switchInt - killowattWidth) - paddingRight),
					(float) (top + (paddingTop * 3)), paint);
			String temp = Tabs.weeklyData.getTemps().get(elementAt) + degree;
			float tempWidth = paint.measureText(temp);
			canvas.drawText(temp, (float) ((switchInt - tempWidth) - paddingRight),
					(float) (top + (paddingTop * 4)-(triangleValue/2)), paint);
		} else {

		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);

		int x = (int) event.getX();
		int y = (int) event.getY();
		yStored = y;
		if (event.getAction() == MotionEvent.ACTION_UP) {

		} else if (event.getAction() == MotionEvent.ACTION_DOWN) {
			System.out.println("Touching down!");
			if (!drawNew) {
				for (int i = 0; i < rectangles.size(); i++) {
					Rect rect = rectangles.get(i);
					if (rect.contains(x, y)) {
						drawNew = true;
						elementAt = i;
						xStored = rect.right;
						invalidate();
					} else {

					}
				}
			} else {
				drawNew = false;

				invalidate();
			}

		} else if (event.getAction() == MotionEvent.ACTION_MOVE) {

		}
		this.postInvalidate();
		return true;
	}

	private String formatOriginalDate(String dateString) {
		String newDate = "";
		String[] splitDate = dateString.split(" ");
		String month = splitDate[1].substring(1, 3);
		String day = splitDate[1].substring(4, 6);
		if ("01".equals(month)) {
			newDate = "JAN " + day;
		} else if ("02".equals(month)) {
			newDate = "FEB " + day;
		} else if ("03".equals(month)) {
			newDate = "MAR " + day;
		} else if ("04".equals(month)) {
			newDate = "APR " + day;
		} else if ("05".equals(month)) {
			newDate = "MAY " + day;
		} else if ("06".equals(month)) {
			newDate = "JUN " + day;
		} else if ("07".equals(month)) {
			newDate = "JUL " + day;
		} else if ("08".equals(month)) {
			newDate = "AUG " + day;
		} else if ("09".equals(month)) {
			newDate = "SEP " + day;
		} else if ("10".equals(month)) {
			newDate = "OCT " + day;
		} else if ("11".equals(month)) {
			newDate = "NOV " + day;
		} else if ("12".equals(month)) {
			newDate = "DEC " + day;
		}
		return newDate;
	}
}