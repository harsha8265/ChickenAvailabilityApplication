package com.mobile.chickenavailabilityapplication.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;

import android.os.Handler;
import android.os.Message;
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
 * Use the {@link UpdateNameFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateNameFragment extends Fragment {

    private EditText mFirstNameEditTextView;
    private TextView mFirstNameTextView;
    private EditText mLastNameEditTextView;
    private TextView mLastNameTextView;
    private Customer customer;
    private NavController navController;

    public UpdateNameFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateNameFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateNameFragment newInstance(String param1, String param2) {
        UpdateNameFragment fragment = new UpdateNameFragment();
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
        return inflater.inflate(R.layout.fragment_update_name, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = new NavController(view.getContext());
        mFirstNameEditTextView = view.findViewById(R.id.firstNameEditText);
        mLastNameEditTextView = view.findViewById(R.id.lastNameEditText);

        customer = Customer.getInstance();
        mFirstNameEditTextView.setText(customer.firstName);
        mLastNameEditTextView.setText(customer.lastName);

        customer.updateName(customer, new UpdateNameHandler());
    }

    public class UpdateNameHandler extends Handler {

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case NetworkConstants.CUSTOMER_DETAILS_UPDATE_SUCCESS:
                    Toast.makeText(getContext(),"Updated Name Successfully",Toast.LENGTH_LONG).show();
                    navController.navigate(R.id.action_updateNameFragment_to_profileItemFragment);
                    break;
                case NetworkConstants.CUSTOMER_DETAILS_UPDATE_FAILURE:
                    Toast.makeText(getContext(),"Failed Updating Name",Toast.LENGTH_LONG).show();
                    navController.navigate(R.id.action_updateNameFragment_to_profileItemFragment);
                    break;
                default:
                    break;
            }
        }
    }
}