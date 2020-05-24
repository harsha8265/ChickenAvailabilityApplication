package com.mobile.chickenavailabilityapplication.ui;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.CartItem;
import com.mobile.chickenavailabilityapplication.datamodel.CartItemContainer;
import com.mobile.chickenavailabilityapplication.datamodel.ReviewCartItemSection;
import com.mobile.chickenavailabilityapplication.dummy.DummyContent;
import com.mobile.chickenavailabilityapplication.dummy.DummyContent.DummyItem;

import java.util.ArrayList;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ReviewCartItemFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";

    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private RelativeLayout purchaseOrderLayout;
    private Button purchaseOrderButton;
    private ArrayList<CartItem> cartItems;
    private ArrayList<ReviewCartItemSection> reviewCartItemSections;
    private MyReviewCartItemRecyclerViewAdapter myReviewCartItemRecyclerViewAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReviewCartItemFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static ReviewCartItemFragment newInstance(int columnCount) {
        ReviewCartItemFragment fragment = new ReviewCartItemFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getReviewItemsList();
        cartItems=CartItemContainer.readCartItemContainer().cartItems;
        return inflater.inflate(R.layout.fragment_review_cart_item_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Context context = view.getContext();
        cartItems = CartItemContainer.readCartItemContainer().cartItems;
        purchaseOrderLayout= view.findViewById(R.id.cart_checkout_layout);
        purchaseOrderButton=view.findViewById(R.id.cart_checkout_button);
        recyclerView = view.findViewById(R.id.list);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        myReviewCartItemRecyclerViewAdapter = new MyReviewCartItemRecyclerViewAdapter(reviewCartItemSections,cartItems, mListener,context);
        recyclerView.setAdapter(myReviewCartItemRecyclerViewAdapter);
        purchaseOrderButton.setText(view.getResources().getString(R.string.purchase_cart,myReviewCartItemRecyclerViewAdapter.calculateFees()));
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
       void onReviewCartListFragmentInteraction(String category);
    }

    private void getReviewItemsList(){
        reviewCartItemSections=new ArrayList<>();
        reviewCartItemSections.add(new ReviewCartItemSection(true,"Your information"));
        reviewCartItemSections.add(new ReviewCartItemSection(false,"Contact info:"));
        reviewCartItemSections.add(new ReviewCartItemSection(false,"Deliver to:"));
        reviewCartItemSections.add(new ReviewCartItemSection(true,"Your payment"));
        reviewCartItemSections.add(new ReviewCartItemSection(false,"Payment:"));
        reviewCartItemSections.add(new ReviewCartItemSection(true,"Your order"));
        reviewCartItemSections.add(new ReviewCartItemSection(false,"Order Details:"));

    }
}
