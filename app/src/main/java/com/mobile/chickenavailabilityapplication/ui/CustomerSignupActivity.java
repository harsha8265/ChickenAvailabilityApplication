package com.mobile.chickenavailabilityapplication.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.Customer;
import com.mobile.chickenavailabilityapplication.network.NetworkConstants;
import com.mobile.chickenavailabilityapplication.util.ViewUtils;

import org.json.JSONException;
import org.json.JSONObject;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

public class CustomerSignupActivity extends AppCompatActivity {

    private final int REGISTRATION_STEP_PERSONAL_INFO = 1;
    private final int REGISTRATION_STEP_CREATE_PIN = 2;

    // UI references.
    private TextView mHeaderView;
    private Button mLoginButton;
    private EditText mFirstNameView;
    private EditText mLastNameView;
    private EditText mCellNumberView;
    private EditText mCreatePin;
    private EditText mConfirmPin;
    private EditText mEmail;
    private EditText mPassword;
    private Spinner mSecurityQuestionView;
    private EditText mSecurityAnswerView;
    private Button mSignUpButton;
    private View mProgressView;
    private View mLoginFormView;
    private int mRegistrationStep;
    public enum StatusBarState
    {
        Light,
        Dark
    }
    private StatusBarState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_signup);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorRed,null));
            setSystemBarTheme(this,true);
        }


        mHeaderView = (TextView) findViewById(R.id.registration_header);
        mLoginButton = (Button) findViewById(R.id.login_button);
        mFirstNameView = (EditText) findViewById(R.id.first_name);
        mLastNameView = (EditText) findViewById(R.id.last_name);
        mCellNumberView = (EditText) findViewById(R.id.cell_number);
        mPassword = (EditText) findViewById(R.id.password);
        mEmail = (EditText) findViewById(R.id.email_id);
        mCreatePin = (EditText) findViewById(R.id.create_pin);
        mConfirmPin = (EditText) findViewById(R.id.confirm_pin);
        mSecurityQuestionView = (Spinner) findViewById(R.id.security_question_spinner);
        mSecurityAnswerView = (EditText) findViewById(R.id.security_answer);
        mSignUpButton = (Button) findViewById(R.id.sign_up_button);
        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        mFirstNameView.setEnabled(true);
        updateViewPersonalInfo();

        ArrayAdapter<CharSequence> adapterSecurityQuestion = ArrayAdapter.createFromResource(this, R.array.security_question_array,
                R.layout.spinner_textview);
        adapterSecurityQuestion.setDropDownViewResource(R.layout.spinner_textview);
        mSecurityQuestionView.setAdapter(adapterSecurityQuestion);
        mSecurityQuestionView.setOnItemSelectedListener(new SpinnerSelection());

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });

        mLastNameView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mCellNumberView.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mCellNumberView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mEmail.requestFocus();
                    return true;
                }
                return false;
            }
        });
        mEmail.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    mPassword.requestFocus();
                    return true;
                }
                return false;
            }
        });

        mCreatePin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 4) {
                    mConfirmPin.requestFocus();
                }
            }
        });

        mConfirmPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length() == 4) {
                    if (!mConfirmPin.getText().toString().equals(mCreatePin.getText().toString())) {
                        mConfirmPin.setError(getString(R.string.error_pin_mismatch));
                    }
                    else {
                        ViewUtils.hideKeypad(mConfirmPin);
                        mSecurityQuestionView.performClick();
                    }
                }
            }
        });

        mSecurityAnswerView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    ViewUtils.hideKeypad(mSecurityAnswerView);
                    attemptSignUp();
                    return true;
                }
                return false;
            }
        });

    }

    private void attemptSignUp() {
        switch (mRegistrationStep) {

            case REGISTRATION_STEP_PERSONAL_INFO:
                if (validatePersonalInfo()) {
                    Customer user = Customer.getInstance();
                    user.firstName = mFirstNameView.getText().toString();
                    user.lastName = mLastNameView.getText().toString();
                    user.cellNumber = mCellNumberView.getText().toString();
                    user.email=mEmail.getText().toString();
                    user.password=mPassword.getText().toString();
                    updateViewCreatePin();
                    //showProgress(true);
                }
                break;
            case REGISTRATION_STEP_CREATE_PIN:
                if (validateCreatePin()) {
                    Customer user = Customer.getInstance();
                    user.pin = mCreatePin.getText().toString();
                    user.securityQuestionId = mSecurityQuestionView.getSelectedItemPosition();
                    user.securityAnswer = mSecurityAnswerView.getText().toString();
                    user.saveUserDetails(new SignUpActivityHandler());
                    showProgress(true);
                }
                break;
        }
    }

    private boolean validatePersonalInfo() {
        boolean returnValue = true;
        if (mFirstNameView.getText().length() == 0) {
            mFirstNameView.setError(getString(R.string.error_field_required));
            returnValue = false;
        }
        if (mLastNameView.getText().length() == 0) {
            mLastNameView.setError(getString(R.string.error_field_required));
            returnValue = false;
        }
        if (mCellNumberView.getText().length() == 0) {
            mCellNumberView.setError(getString(R.string.error_field_required));
            returnValue = false;
        } if (mEmail.getText().length() == 0) {
            mEmail.setError(getString(R.string.error_field_required));
            returnValue = false;
        } if (mPassword.getText().length() == 0) {
            mPassword.setError(getString(R.string.error_field_required));
            returnValue = false;
        }

        return returnValue;
    }

    private boolean validateCreatePin() {
        boolean returnValue = true;
        if (mCreatePin.getText().length() != 4) {
            mCreatePin.setError(getString(R.string.error_pin_length));
            returnValue = false;
        }
        if (!mConfirmPin.getText().toString().equals(mCreatePin.getText().toString())) {
            mConfirmPin.setError(getString(R.string.error_pin_mismatch));
            returnValue = false;
        }
        if (mSecurityQuestionView.getSelectedItemPosition() == 0) {
            ((TextView)mSecurityQuestionView.getSelectedView()).setError(getString(R.string.error_field_required));
            returnValue = false;
        }
        if (mSecurityAnswerView.getText().length() == 0) {
            mSecurityAnswerView.setError(getString(R.string.error_field_required));
            returnValue = false;
        }

        return returnValue;
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

    private void showErrorDialog(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            message = jsonObject.getString("statusDescription");
        } catch (JSONException e) {
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CustomerSignupActivity.this);
        alertDialogBuilder.setTitle("Error");
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("OK", null);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private void updateViewPersonalInfo() {

        mRegistrationStep = REGISTRATION_STEP_PERSONAL_INFO;
        mHeaderView.setText(getString(R.string.prompt_header_personal_info));
        mSignUpButton.setText(R.string.action_next);
        ((View)mFirstNameView.getParent()).setVisibility(View.VISIBLE);
        ((View)mLastNameView.getParent()).setVisibility(View.VISIBLE);
        ((View)mCellNumberView.getParent()).setVisibility(View.VISIBLE);
        ((View)mEmail.getParent()).setVisibility(View.VISIBLE);
        ((View)mPassword.getParent()).setVisibility(View.VISIBLE);
        ((View)mCreatePin.getParent()).setVisibility(View.GONE);
        ((View)mConfirmPin.getParent()).setVisibility(View.GONE);
        mSecurityQuestionView.setVisibility(View.GONE);
        ((View)mSecurityAnswerView.getParent()).setVisibility(View.GONE);
        mLoginButton.setVisibility(View.GONE);
    }
    private void updateViewCreatePin() {

        mRegistrationStep = REGISTRATION_STEP_CREATE_PIN;
        mHeaderView.setText(getString(R.string.prompt_header_create_pin));
        mSignUpButton.setText(R.string.action_register);
        ((View)mFirstNameView.getParent()).setVisibility(View.GONE);
        ((View)mLastNameView.getParent()).setVisibility(View.GONE);
        ((View)mCellNumberView.getParent()).setVisibility(View.GONE);
        ((View)mEmail.getParent()).setVisibility(View.GONE);
        ((View)mPassword.getParent()).setVisibility(View.GONE);
        ((View)mCreatePin.getParent()).setVisibility(View.VISIBLE);
        ((View)mConfirmPin.getParent()).setVisibility(View.VISIBLE);
        mSecurityQuestionView.setVisibility(View.VISIBLE);
        ((View)mSecurityAnswerView.getParent()).setVisibility(View.VISIBLE);
        mLoginButton.setVisibility(View.GONE);
    }

    private class SpinnerSelection implements AdapterView.OnItemSelectedListener {

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            if (position == 0) {
                ((TextView) view).setTextColor(Color.BLACK);
                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22f);
            }
            else {
                ((TextView) view).setTextColor(getResources().getColor(R.color.primaryTextColor));
                ((TextView) view).setTextSize(TypedValue.COMPLEX_UNIT_DIP, 22f);
                mSecurityAnswerView.requestFocus();
                ViewUtils.showKeypad(mSecurityAnswerView);
            }
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    @Override
    public void onBackPressed() {
        switch (mRegistrationStep) {
            case REGISTRATION_STEP_CREATE_PIN:
                updateViewPersonalInfo();
                break;
            /*case REGISTRATION_STEP_PERSONAL_INFO:
                //updateViewActivation();
                break;*/
            default:
                super.onBackPressed();
        }
    }

    /** Changes the System Bar Theme. */
    @RequiresApi(api = Build.VERSION_CODES.M)
    public static final void setSystemBarTheme(final Activity pActivity, final boolean pIsDark) {
        // Fetch the current flags.
        final int lFlags = pActivity.getWindow().getDecorView().getSystemUiVisibility();
        // Update the SystemUiVisibility dependening on whether we want a Light or Dark theme.
        pActivity.getWindow().getDecorView().setSystemUiVisibility(pIsDark ? (lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR) : (lFlags | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR));
    }

    private class SignUpActivityHandler extends Handler {

        @Override
        public void handleMessage(Message message) {
            showProgress(false);
            switch (message.what) {
                case NetworkConstants.CUSTOMER_DETAILS_SAVE_SUCCESS:
                     showProgress(true);
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    SharedPreferences preferences = getApplicationContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putBoolean("NewAccount", true);
                    editor.commit();
                    startActivity(intent);
                    finish();
                     break;
                default:
                    break;
            }
        }
    }
}
