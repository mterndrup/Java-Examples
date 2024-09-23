package com.lessutility;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;

import com.beans.Intervals;

public class Tab3 extends Fragment {
	public static HorizontalScrollView scrollView;
	Draw draw;
	int hh, canvasHeight, canvasWidth, elementAt;
	static boolean drawTemp;
	boolean stopAnimation = false;
	boolean reselect = false, debug = false;
	String degree = "\u00B0" + "F";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		Context context = getActivity();
		Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();

		DisplayMetrics metrics = new DisplayMetrics();
		display.getMetrics(metrics);
		Point size = new Point();
		//display.getSize(size);
		int width = metrics.widthPixels;;
		hh = (int) (width * 2.5);
		canvasWidth = width;
		draw = new Draw(this.getActivity());
		scrollView = new HorizontalScrollView(this.getActivity());
		scrollView.addView(draw);
		return scrollView;
	}

	public class Draw extends View implements Runnable {
		Paint paint = new Paint();
		List<Rect> rectangles;
		Canvas mCanvas;
		boolean drawNew = false;
		int xStored, yStored;
		int currentRectangle = 0;
		Handler handler = new Handler() {
			// The bar graph is draw over and over, limiting the amount of bars
			// This is based on the number of bars drawn
			// A sleep method is initiated for 200 miliseconds
			public void handleMessage(android.os.Message msg) {
				invalidate();
				System.out.println("redraw" + currentRectangle);
				currentRectangle++;
				if (currentRectangle > 0) {
					stopAnimation = true;
					reselect = true;
				}
			};
		};

		public Draw(Context context) {
			super(context);
			new Thread(this).start();
		}

		@Override
		protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			// Compute the height required to render the view
			int width = hh;
			canvasHeight = MeasureSpec.getSize(heightMeasureSpec);
			setMeasuredDimension(width, canvasHeight);
		}

		int x = 0;
		int height = 0;

		@SuppressLint("DrawAllocation")
		@Override
		/**This method where the bar graph is drawn**/
		public void onDraw(final Canvas canvas) {
			int width = super.getWidth();
			height = super.getHeight();
			mCanvas = canvas;
			super.onDraw(canvas);
			int colWidth = super.getWidth() / 32;
			rectangles = new ArrayList<Rect>();

			if (!debug) {
				try {
					// This array is where the energy data is stored
					Vector<Intervals> energyData = Tabs.cycleData
							.getIntervals();
					if (energyData != null) {

						int maxPosition = 0;
						if (energyData.size() != 0) {
							for (int i = 0; i < energyData.size(); i++) {
								if (i != 0) {
									if (energyData.get(i).getCost() > energyData
											.get(maxPosition).getCost()) {
										maxPosition = i;
									}
								}
							}
						}
						Shader shader = null;
						// The energyData array is looped
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
										* (currentData / energyData.get(
												maxPosition).getCost()) * heightIndicator);
								// adding a shader paint creates the gradient
								// needed
								// for
								// the
								// chart
								shader = new LinearGradient(0, 0, 0,
										(float) (bottom * 0.5), endColor,
										startColor, TileMode.CLAMP);
								paint.setShader(shader);
								final Rect currentRect = new Rect(left, height
										- bottom, right, height);
								rectangles.add(currentRect);
								if (!reselect) {
									if (x == 0) {
										Tabs.playSound();
										x++;
									}
									if (currentRectangle >= i) {
										canvas.drawRect(currentRect, paint);
									}
								} else {
									canvas.drawRect(currentRect, paint);
								}
							}
						}
					}
				} catch (Exception e) {

				}
			}

			if (drawTemp) {
				for (int i = 0; i < rectangles.size(); i++) {
					Rect rect = rectangles.get(i);
					int centerX = rect.centerX();
					int centerY = rect.centerY();
					paint.setColor(Color.RED);
					canvas.drawCircle(centerX, centerY, 2, paint);
				}
			}

			/**
			 * When a user selects a bar in the graph, the following code is
			 * initiated This is the popup that is drawn that shows more user
			 * information It also shows weather data as well
			 **/
			int density = Tabs.density;
			int textSize = 40;
			int flip = 1;
			if (drawNew) {
				if (density == 1) {
					textSize = (int) (textSize * 1.25);
				} else if (density == 3) {
					textSize = (int) (textSize * 1.25);
				}
				paint.setShader(null);
				paint.setColor(0xFF2c3e50);
				Path path = new Path();
				int triangleValue = canvasWidth / 40;
				path.moveTo(xStored, height - triangleValue);
				if (elementAt < 25) {
					path.lineTo(xStored + triangleValue, height
							- (triangleValue * 2));
					path.lineTo(xStored + triangleValue, height - triangleValue);
					canvas.drawPath(path, paint);
				} else {
					flip = -1;
					path.lineTo(xStored - triangleValue, height
							- (triangleValue * 2));
					path.lineTo(xStored - triangleValue, height - triangleValue);
					canvas.drawPath(path, paint);
				}
				double cutValue = 2.75;
				double multiplyBy = 3;
				if (density == 1) {
					cutValue = 2.25;
					multiplyBy = 3.75;
				} else if (density == 3) {
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
				int paddingRight = triangleValue;
				double paddingTop = triangleValue * multiplyBy;
				String time = formatOriginalDate(Tabs.cycleData.getIntervals()
						.get(elementAt).getDate());
				float timeWidth = paint.measureText(time);
				int switchInt = right;
				if (flip == -1) {
					switchInt = left;
				}
				canvas.drawText(time, (switchInt - timeWidth) - paddingRight,
						(float) (top + paddingTop), paint);
				String cost = "$"
						+ Tabs.cycleData.getIntervals().get(elementAt)
								.getCost();
				float costWidth = paint.measureText(cost);
				canvas.drawText(cost, (switchInt - costWidth) - paddingRight,
						(float) (top + (paddingTop * 2)), paint);
				paint.setColor(0xFF909394);
				paint.setTextSize((float) (textSize * 0.7));
				String killowatts = Tabs.cycleData.getIntervals()
						.get(elementAt).getkW()
						+ "kW";
				float killowattWidth = paint.measureText(killowatts);
				canvas.drawText(killowatts, (switchInt - killowattWidth)
						- paddingRight, (float) (top + (paddingTop * 3)), paint);
				String temp = Tabs.cycleData.getTemps().get(elementAt) + degree;
				float tempWidth = paint.measureText(temp);
				canvas.drawText(temp, (switchInt - tempWidth) - paddingRight,
						(float) (top + (paddingTop * 4) - (triangleValue / 2)),
						paint);
			} else {

			}
		}

		/** This is where the program recognizes a user's click on the bar graph **/
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

		public void run() {
			while (!stopAnimation) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				handler.sendEmptyMessage(0);
			}
		}
	}

	private String formatOriginalDate(String dateString) {
		String newDate = "";
		String[] splitDate = dateString.split("/");
		if ("01".equals(splitDate[0])) {
			newDate = "JAN " + splitDate[1];
		} else if ("02".equals(splitDate[0])) {
			newDate = "FEB " + splitDate[1];
		} else if ("03".equals(splitDate[0])) {
			newDate = "MAR " + splitDate[1];
		} else if ("04".equals(splitDate[0])) {
			newDate = "APR " + splitDate[1];
		} else if ("05".equals(splitDate[0])) {
			newDate = "MAY " + splitDate[1];
		} else if ("06".equals(splitDate[0])) {
			newDate = "JUN " + splitDate[1];
		} else if ("07".equals(splitDate[0])) {
			newDate = "JUL " + splitDate[1];
		} else if ("08".equals(splitDate[0])) {
			newDate = "AUG " + splitDate[1];
		} else if ("09".equals(splitDate[0])) {
			newDate = "SEP " + splitDate[1];
		} else if ("10".equals(splitDate[0])) {
			newDate = "OCT " + splitDate[1];
		} else if ("11".equals(splitDate[0])) {
			newDate = "NOV " + splitDate[1];
		} else if ("12".equals(splitDate[0])) {
			newDate = "DEC " + splitDate[1];
		}
		return newDate;
	}
}