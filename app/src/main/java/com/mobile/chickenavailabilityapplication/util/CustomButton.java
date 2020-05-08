package com.mobile.chickenavailabilityapplication.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;
import com.mobile.chickenavailabilityapplication.R;


public class CustomButton extends Button {

	public CustomButton(Context context) {
		super(context);
	}

	public CustomButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomFont(context, attrs);
	}

	public CustomButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCustomFont(context, attrs);
	}

	private void setCustomFont(Context ctx, AttributeSet attrs) {
		TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomButton);
		String customFont = a.getString(R.styleable.CustomButton_customButtonFont);
		setCustomFont(ctx, customFont);
		a.recycle();
	}

	private boolean setCustomFont(Context ctx, String asset) {
		Typeface tf = null;
		try {
			tf = Typeface.createFromAsset(ctx.getAssets(), asset);
		} catch (Exception e) {
			return false;
		}

		setTypeface(tf);
		return true;
	}

}