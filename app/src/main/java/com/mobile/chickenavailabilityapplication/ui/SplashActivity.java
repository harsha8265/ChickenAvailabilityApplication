package com.mobile.chickenavailabilityapplication.ui;

import android.content.Intent;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.Customer;
import com.mobile.chickenavailabilityapplication.datamodel.MenuItemContainer;
import com.mobile.chickenavailabilityapplication.util.CustomTextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        View decorView = getWindow().getDecorView();
        // Hide the status bar.
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onSplashDurationComplete();
            }
        }, 2000);

        TextView version = (TextView) findViewById(R.id.app_version);
        version.setText("Version " + com.mobile.chickenavailabilityapplication.BuildConfig.VERSION_NAME);


    }

    private void onSplashDurationComplete() {
        if(MenuItemContainer.readMenuItemContainer().menuItems.isEmpty()){
            MenuItemContainer.readMenuItemContainer().populateMenuItemObject();
        }
        //Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        //startActivity(intent);
        if (Customer.getInstance().cellNumber == null ) {
            Intent intent = new Intent(getApplicationContext(), CustomerSignupActivity.class);
            //Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            //Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(getApplicationContext(), PinAuthenticationActivity.class);
            startActivity(intent);
        }

        finish();
    }
}
