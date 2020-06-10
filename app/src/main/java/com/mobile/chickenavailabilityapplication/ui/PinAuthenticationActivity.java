package com.mobile.chickenavailabilityapplication.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.Customer;
import com.mobile.chickenavailabilityapplication.datamodel.MenuItemContainer;
import com.mobile.chickenavailabilityapplication.network.NetworkConstants;
import com.mobile.chickenavailabilityapplication.util.PFCodeView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PinAuthenticationActivity extends AppCompatActivity {
    /**
     * Whether or not the system UI should be auto-hidden after
     * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
     */
    private static final boolean AUTO_HIDE = true;

    /**
     * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
     * user interaction before hiding the system UI.
     */
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private View mDeleteButton;
    private TextView mLeftButton;
    private PFCodeView mCodeView;
    private String mCode = "";


    private View mRootView;

    private View.OnClickListener mOnLeftButtonClickListener = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorRed,null));
            setSystemBarTheme(this,true);
        }
        setContentView(R.layout.activity_pin_authentication);

        mDeleteButton = findViewById(R.id.button_delete);
        mLeftButton = findViewById(R.id.button_left);
        mDeleteButton.setOnClickListener(mOnDeleteButtonClickListener);
        mDeleteButton.setOnLongClickListener(mOnDeleteButtonOnLongClickListener);
        mCodeView = findViewById(R.id.code_view);
        mCodeView.setListener(mCodeListener);
        initKeyViews();

    }

    private void initKeyViews() {
        findViewById(R.id.button_0).setOnClickListener(mOnKeyClickListener);
        findViewById(R.id.button_1).setOnClickListener(mOnKeyClickListener);
        findViewById(R.id.button_2).setOnClickListener(mOnKeyClickListener);
        findViewById(R.id.button_3).setOnClickListener(mOnKeyClickListener);
        findViewById(R.id.button_4).setOnClickListener(mOnKeyClickListener);
        findViewById(R.id.button_5).setOnClickListener(mOnKeyClickListener);
        findViewById(R.id.button_6).setOnClickListener(mOnKeyClickListener);
        findViewById(R.id.button_7).setOnClickListener(mOnKeyClickListener);
        findViewById(R.id.button_8).setOnClickListener(mOnKeyClickListener);
        findViewById(R.id.button_9).setOnClickListener(mOnKeyClickListener);
    }

    private final View.OnClickListener mOnKeyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof TextView) {
                final String string = ((TextView) v).getText().toString();
                if (string.length() != 1) {
                    return;
                }
                final int codeLength = mCodeView.input(string);
                //if()
                configureRightButton(codeLength);
            }
        }
    };

    private final View.OnClickListener mOnDeleteButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final int codeLength = mCodeView.delete();
            configureRightButton(codeLength);
        }
    };

    private final View.OnLongClickListener mOnDeleteButtonOnLongClickListener
            = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            mCodeView.clearCode();
            configureRightButton(0);
            return true;
        }
    };

    private void configureRightButton(int codeLength) {

        mDeleteButton.setVisibility(View.INVISIBLE);
        mDeleteButton.setEnabled(false);

        if (codeLength > 0) {
            mDeleteButton.setVisibility(View.VISIBLE);
            mDeleteButton.setEnabled(true);
            return;
        }
    }

    private void cleanCode() {
        mCode = "";
        mCodeView.clearCode();
    }

    private void errorAction() {
        final Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        if (v != null) {
            v.vibrate(400);
        }
        final Animation animShake = AnimationUtils.loadAnimation(this,R.anim.shake_pf);
        mCodeView.startAnimation(animShake);
    }

    public void setOnLeftButtonClickListener(View.OnClickListener onLeftButtonClickListener) {
        this.mOnLeftButtonClickListener = onLeftButtonClickListener;
    }

    private final PFCodeView.OnPFCodeListener mCodeListener = new PFCodeView.OnPFCodeListener() {
        @Override
        public void onCodeCompleted(String code) {
            mCode = code;
            if(mCode.equals(Customer.getInstance().pin)){
                Toast.makeText(getApplicationContext(), "Pin Successful and Fetching Menu Items", Toast.LENGTH_LONG).show();
                if(MenuItemContainer.readMenuItemContainer().menuItems.isEmpty()){
                    MenuItemContainer.readMenuItemContainer().getMenuItems(new PinAuthenticationActivity.PinActivityHandler());
                }
            }
            else{
                mCodeView.clearCode();
                errorAction();
            }

            //Toast.makeText(getApplicationContext(), mCode, Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onCodeNotCompleted(String code) {
           // mNextButton.setVisibility(View.INVISIBLE);
            return;

        }

    };

        /** Changes the System Bar Theme. */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static final void setSystemBarTheme(final Activity pActivity, final boolean pIsDark) {
        // Fetch the current flags.
        final int lFlags = pActivity.getWindow().getDecorView().getSystemUiVisibility();
        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        pActivity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
    }


    private class PinActivityHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case NetworkConstants.GET_MENUITEMS_SUCCESS:
                {
                    Toast.makeText(getApplicationContext(), "Success in Loading Menu Items", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
                case NetworkConstants.CUSTOMER_OPERATION_FAILURE:
                    Toast.makeText(getApplicationContext(), "Error in Loading Menu Items", Toast.LENGTH_LONG).show();
                    break;
                case NetworkConstants.ACCESS_CODE_SUCCESS:
                {
                    Toast.makeText(getApplicationContext(), "Success in Account Menu Items", Toast.LENGTH_LONG).show();
                }
                default:
                    break;
            }
        }
    }


}
