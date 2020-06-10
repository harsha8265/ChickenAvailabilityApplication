package com.mobile.chickenavailabilityapplication.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.Customer;
import com.mobile.chickenavailabilityapplication.network.NetworkConstants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdatePasswordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdatePasswordFragment extends Fragment {

    private EditText mPasswordEditTextView;
    private TextView mPasswordTextView;
    private EditText mConfirmEditTextView;
    private TextView mConfirmTextView;
    private Customer customer;
    private NavController navController;

    public UpdatePasswordFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdatePasswordFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdatePasswordFragment newInstance(String param1, String param2) {
        UpdatePasswordFragment fragment = new UpdatePasswordFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_update_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = new NavController(view.getContext());

        mConfirmEditTextView = view.findViewById(R.id.firstNameEditText);
        mPasswordEditTextView = view.findViewById(R.id.lastNameEditText);

        customer = Customer.getInstance();
        mPasswordEditTextView.setText(customer.password);

        Editable confirmPassword = mConfirmEditTextView.getText();
        final String confirmPasswordText = confirmPassword.toString();

        Editable password = mPasswordEditTextView.getText();
        String passwordText = password.toString();

        if(passwordText.equals(confirmPasswordText)){
            Toast.makeText(view.getContext(),"Password Matched",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(view.getContext(),"Password Not Matched",Toast.LENGTH_LONG).show();
        }
    }

    public class UpdatePasswordHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case NetworkConstants.CUSTOMER_DETAILS_UPDATE_SUCCESS:
                    Toast.makeText(getContext(),"Updated Password Successfully",Toast.LENGTH_LONG).show();
                    navController.navigate(R.id.action_profileItemFragment_to_updatePasswordFragment);
                    break;
                case NetworkConstants.CUSTOMER_DETAILS_UPDATE_FAILURE:
                    Toast.makeText(getContext(),"Failed Updating Password",Toast.LENGTH_LONG).show();
                    navController.navigate(R.id.action_profileItemFragment_to_updatePasswordFragment);
                    break;
                default:
                    break;
            }
        }
    }
}