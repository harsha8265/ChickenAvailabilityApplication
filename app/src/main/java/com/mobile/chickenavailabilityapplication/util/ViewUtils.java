package com.mobile.chickenavailabilityapplication.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by Harsha on 05/02/20.
 */
public class ViewUtils {

    public static void hideKeypad(View view) {
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) view.getContext()
                    .getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeypad(EditText editText) {
        if (editText != null) {
            editText.requestFocus();
            InputMethodManager imm = (InputMethodManager) editText.getContext()
                    .getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static String getFormatedDateString(String format, Date date, String timeZoneId) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        if (timeZoneId == null) {
            sdf.setTimeZone(TimeZone.getDefault());
        }
        else {
            sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        }
        return sdf.format(date);
    }

    public static Date getDateForFormat(String format, String dateString, String timeZoneId) {
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        if (timeZoneId == null) {
            sdf.setTimeZone(TimeZone.getDefault());
        }
        else {
            sdf.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        }
        Date date = new Date();
        try {
            date = sdf.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return date;
    }

    public static long getDateWithoutTimeInMillies(Date date, String timeZoneId) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        if (timeZoneId == null) {
            calendar.setTimeZone(TimeZone.getDefault());
        }
        else {
            calendar.setTimeZone(TimeZone.getTimeZone(timeZoneId));
        }

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    public static String getCurrentTimezoneId() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTimeZone().getID();
    }

    public static TimeZone getTimezoneForDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.getTimeZone();
    }

    public static boolean isOldEntry(Date date) {
        String currentDateString = ViewUtils.getFormatedDateString("MMddyy", new Date(), null);
        String selectedDateString = ViewUtils.getFormatedDateString("MMddyy", date, null);

        return !currentDateString.equals(selectedDateString);
    }

    public static Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString, Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static Bitmap getRoundedCornerImage(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;

    }
    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if (bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if (drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        } else  {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static String BitMapToString(Bitmap bitmap){
        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50, baos);
        byte [] b=baos.toByteArray();
        String temp= Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    public static Bitmap loadBitmapFromView(View v, int width, int height) {
        Bitmap b = Bitmap.createBitmap(width , height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(0, 0, v.getLayoutParams().width, v.getLayoutParams().height);
        v.draw(c);
        return b;
    }
}
