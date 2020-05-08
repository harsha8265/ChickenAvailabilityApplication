package com.mobile.chickenavailabilityapplication.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;
import com.mobile.chickenavailabilityapplication.R;


/**
 * Created by Harsha on 05/02/20.
 */

public class CustomTextView extends TextView {

	public CustomTextView(Context context) {
		super(context);
	}

	public CustomTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		setCustomFont(context, attrs);
	}

	public CustomTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		setCustomFont(context, attrs);
	}

	private void setCustomFont(Context ctx, AttributeSet attrs) {
		TypedArray a = ctx.obtainStyledAttributes(attrs, R.styleable.CustomTextView);
		String customFont = a.getString(R.styleable.CustomTextView_customFont);
		setCustomFont(ctx, customFont);
		a.recycle();
	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		super.setText(text, type);
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