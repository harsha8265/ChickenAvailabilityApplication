package com.mobile.chickenavailabilityapplication.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.Customer;
import com.mobile.chickenavailabilityapplication.network.NetworkConstants;
import com.mobile.chickenavailabilityapplication.util.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button mLoginButton;
    private Button mForgotPasswordButton;
    private Button mSignUpButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorRed,null));
            setSystemBarTheme(this,true);
        }
        setContentView(R.layout.activity_login);

        mEmailView =  findViewById(R.id.editTextEmail);
        mPasswordView =  findViewById(R.id.editTextPassword);
        mLoginButton =  findViewById(R.id.login_button);
        mForgotPasswordButton=findViewById(R.id.forgot_pwd_button);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mSignUpButton =  findViewById(R.id.sign_up_button);
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), CustomerSignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mForgotPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    private void attemptLogin() {

        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString().trim();
        String password = mPasswordView.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            mEmailView.setError(getString(R.string.error_field_required));
//            mPinView.setError(getString(R.string.error_field_required));
            mPasswordView.setHintTextColor(Color.RED);
            return;
        }

        Customer.getInstance().loginUser(email, email, new LoginActivityHandler());
        showProgress(true);
    }

    private void showProgress(final boolean show) {
        ViewUtils.hideKeypad(this.getCurrentFocus());
        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    /** Changes the System Bar Theme. */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void setSystemBarTheme(final Activity pActivity, final boolean pIsDark) {
        // Fetch the current flags.
        final int lFlags = pActivity.getWindow().getDecorView().getSystemUiVisibility();
        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        pActivity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
    }

    private class LoginActivityHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
            switch (message.what) {
                case NetworkConstants.CUSTOMER_LOGIN_SUCCESS:
                    Customer user = (Customer) message.obj;
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                    finish();
                    break;
                case NetworkConstants.CUSTOMER_OPERATION_FAILURE:
                    showProgress(false);
                    Toast.makeText(getApplicationContext(), "Login Failed: Either your cell phone number or PIN is wrong.", Toast.LENGTH_LONG).show();
                    break;
                default:
                    break;
            }
        }
    }
}
