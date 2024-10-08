/*  Created by Edward Akoto on 12/31/12.
 *  Email akotoe@aua.ac.ke
 * 	Free for modification and distribution
 */

package com.lessutility;

import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.RelativeLayout;

/**
 * The CollapseAnimation class is used by every page that has the sliding
 * hamburger menu
 * 
 * **/
public class CollapseAnimation extends TranslateAnimation implements
		TranslateAnimation.AnimationListener {

	private LinearLayout slidingLayout;
	int panelWidth;

	public CollapseAnimation(LinearLayout slidingPanel, int width,
			int fromXType, float fromXValue, int toXType, float toXValue,
			int fromYType, float fromYValue, int toYType, float toYValue) {

		super(fromXType, fromXValue, toXType, toXValue, fromYType, fromYValue,
				toYType, toYValue);

		slidingLayout = slidingPanel;
		panelWidth = width;
		setDuration(200);
		setFillAfter(false);
		setInterpolator(new AccelerateDecelerateInterpolator());
		setAnimationListener(this);

		// Clear left and right margins
		LayoutParams params = (LayoutParams) slidingLayout.getLayoutParams();
		params.rightMargin = 0;
		params.leftMargin = 0;
		slidingLayout.setLayoutParams(params);
		slidingLayout.requestLayout();
		slidingLayout.startAnimation(this);

	}

	public void onAnimationEnd(Animation animation) {

	}

	public void onAnimationRepeat(Animation animation) {

	}

	public void onAnimationStart(Animation animation) {

	}

}
