package com.mobile.chickenavailabilityapplication.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.CartItem;
import com.mobile.chickenavailabilityapplication.datamodel.CartItemContainer;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CartFragment extends Fragment implements EmptyCartButtonPressed {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private RelativeLayout checkOutLayout;
    private RelativeLayout deliveryLayout;
    private Button checkOutButton;
    private Button addMoreItemsButton;
    SwipeController swipeController=null;
    private ArrayList<CartItem> cartItems;
    private CartMenuItemRecyclerViewAdapter cartMenuItemRecyclerViewAdapter;
    private Context context;
    private TextView emptyMessage;
    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CartFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static CartFragment newInstance(int columnCount) {
        CartFragment fragment = new CartFragment();
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
        // Inflate the layout for this fragment
        View view ;
        cartItems=CartItemContainer.readCartItemContainer().cartItems;
        view=  inflater.inflate(R.layout.fragment_cart, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view instanceof RelativeLayout) {
            context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            emptyMessage = (TextView) view.findViewById(R.id.cart_empty_message);
            checkOutLayout=(RelativeLayout) view.findViewById(R.id.cart_checkout_layout);
            deliveryLayout=(RelativeLayout) view.findViewById(R.id.deliveryLayout);
            addMoreItemsButton=(Button) view.findViewById(R.id.addMoreItemsButton);
            checkOutButton=(Button) view.findViewById(R.id.cart_checkout_button);
            addMoreItemsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    NavController navController = Navigation.findNavController((Activity) view.getContext(), R.id.fragNavHost);
                    navController.navigate(R.id.action_global_mainFragment);
                }
            });
            if (cartItems.isEmpty()) {
                hideDeliveryView();
            }
            else {
                showDeliveryView();
                recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
                cartMenuItemRecyclerViewAdapter = new CartMenuItemRecyclerViewAdapter(cartItems, view.getContext(), this);
                recyclerView.setAdapter(cartMenuItemRecyclerViewAdapter);
                checkOutButton.setText(view.getResources().getString(R.string.cart_checkout,cartMenuItemRecyclerViewAdapter.calculateFees()));

            }


            swipeController = new SwipeController(new SwipeControllerActions() {
                @Override
                public void onRightClicked(int position) {
                    //super.onRightClicked(position);
                    CartItemContainer.readCartItemContainer().deleteCartItemByCartID(cartItems.get(position).cartID);
                    //    cartMenuItemRecyclerViewAdapter.mValues.remove(position);
                    cartMenuItemRecyclerViewAdapter.notifyItemRemoved(position);
                    cartMenuItemRecyclerViewAdapter.notifyItemRangeChanged(position, cartMenuItemRecyclerViewAdapter.getItemCount());
                    checkOutButton.setText(view.getResources().getString(R.string.cart_checkout,cartMenuItemRecyclerViewAdapter.calculateFees()));

                    if(!cartItems.isEmpty()){
                        showDeliveryView();
                    }
                    else{
                        ((HomeActivity)context).removeNotificationBadge(R.id.cartFragment);
                        hideDeliveryView();
                    }
                }

                @Override
                public void onLeftClicked(int position) {
                    NavController navController = Navigation.findNavController((Activity) view.getContext(), R.id.fragNavHost);
                    Bundle bundle = new Bundle();
                    bundle.putString("cartID", String.valueOf(cartItems.get(position).cartID));
                    navController.navigate(R.id.action_cartFragment_to_itemDescriptionFragment, bundle);
                    checkOutButton.setText(view.getResources().getString(R.string.cart_checkout,cartMenuItemRecyclerViewAdapter.calculateFees()));

                }
            });
            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
            itemTouchHelper.attachToRecyclerView(recyclerView);

            recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
                @Override
                public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                    swipeController.onDraw(c);
                }
            });
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        //mFragmentCallBack = (EmptyCartButtonPressed) context;
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

    @Override
    public void onEmptyCart(boolean emptyCartButtonPresed) {
        CartItemContainer.readCartItemContainer().emptyCart();
        ((HomeActivity)context).removeNotificationBadge(R.id.cartFragment);
        cartMenuItemRecyclerViewAdapter.notifyItemRangeRemoved(0,cartItems.size());
        hideDeliveryView();
    }

    private void showDeliveryView(){
        recyclerView.setVisibility(View.VISIBLE);
        checkOutLayout.setVisibility(View.VISIBLE);
        emptyMessage.setVisibility(View.GONE);
        deliveryLayout.setVisibility(View.VISIBLE);
    }

    private void hideDeliveryView(){
        recyclerView.setVisibility(View.GONE);
        checkOutLayout.setVisibility(View.GONE);
        emptyMessage.setVisibility(View.VISIBLE);
        deliveryLayout.setVisibility(View.GONE);
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
        //void onCartItemFragmentInteraction(CartItem item);
    }

}
