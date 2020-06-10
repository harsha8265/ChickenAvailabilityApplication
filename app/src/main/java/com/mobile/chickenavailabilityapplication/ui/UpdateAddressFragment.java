package com.mobile.chickenavailabilityapplication.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.CartItemContainer;
import com.mobile.chickenavailabilityapplication.datamodel.Customer;
import com.mobile.chickenavailabilityapplication.network.NetworkConstants;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UpdateAddressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UpdateAddressFragment extends Fragment {

    public UpdateAddressFragment() {
        // Required empty public constructor
    }
    private EditText mDoorEditTextView;
    private TextView mDoorTextView;
    private EditText mStreetEditTextView;
    private TextView mStreetTextView;
    private EditText mAreaEditTextView;
    private TextView mAreaTextView;
    private EditText mPinCodeEditTextView;
    private TextView mPinCodeTextView;
    private EditText mStateEditTextView;
    private TextView mStateTextView;
    private EditText mTownEditTextView;
    private TextView mTownTextView;
    private Customer customer;
    private NavController navController;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UpdateAddressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UpdateAddressFragment newInstance(String param1, String param2) {
        UpdateAddressFragment fragment = new UpdateAddressFragment();
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
        return inflater.inflate(R.layout.fragment_update_address, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        navController = new NavController(view.getContext());
        mDoorEditTextView= view.findViewById(R.id.doorNumberEditText);
        mStreetEditTextView= view.findViewById(R.id.streetEditText);
        mAreaEditTextView= view.findViewById(R.id.areaEditText);
        mPinCodeEditTextView= view.findViewById(R.id.pinCodeEditText);
        mStateEditTextView= view.findViewById(R.id.stateEditText);
        mTownEditTextView= view.findViewById(R.id.townEditText);

        customer = Customer.getInstance();
        mDoorEditTextView.setText(customer.shippingAddress.DoorNumber);
        mStreetEditTextView.setText(customer.shippingAddress.Street);
        mAreaEditTextView.setText(customer.shippingAddress.Area);
        mTownEditTextView.setText(customer.shippingAddress.Town);
        mStateTextView.setText(customer.shippingAddress.State);
        mPinCodeEditTextView.setText(customer.shippingAddress.PinCode);

        customer.shippingAddress.DoorNumber = mDoorTextView.getText().toString();
        customer.shippingAddress.Area = mAreaTextView.getText().toString();
        customer.shippingAddress.Street = mStreetTextView.getText().toString();
        customer.shippingAddress.Town = mTownTextView.getText().toString();
        customer.shippingAddress.State = mStateTextView.getText().toString();
        customer.shippingAddress.PinCode = mPinCodeTextView.getText().toString();


        customer.updateShippingAddress(customer, new UpdateAddressHandler());

    }

    public class UpdateAddressHandler extends Handler{

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case NetworkConstants.CUSTOMER_DETAILS_UPDATE_SUCCESS:
                    Toast.makeText(getContext(),"Updated Address Successfully",Toast.LENGTH_LONG).show();
                    navController.navigate(R.id.action_updateAddressFragment_to_profileItemFragment);
                    break;
                case NetworkConstants.CUSTOMER_DETAILS_UPDATE_FAILURE:
                    Toast.makeText(getContext(),"Failed Updating Address",Toast.LENGTH_LONG).show();
                    navController.navigate(R.id.action_updateAddressFragment_to_profileItemFragment);
                    break;
                default:
                    break;
            }
        }
    }
}