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
import android.widget.Toast;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.CartItem;
import com.mobile.chickenavailabilityapplication.datamodel.CartItemContainer;
import com.mobile.chickenavailabilityapplication.datamodel.MenuItem;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class CartFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView recyclerView;
    private RelativeLayout checkOutLayout;
    private Button checkOutButton;
    SwipeController swipeController=null;
    private ArrayList<CartItem> cartItems;
    private CartMenuItemRecyclerViewAdapter cartMenuItemRecyclerViewAdapter;
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
        if(!cartItems.isEmpty()){
            view=  inflater.inflate(R.layout.fragment_cart, container, false);
        }
        else{
            view = inflater.inflate(R.layout.fragment_empty_cart, container, false);
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (view instanceof RelativeLayout) {
            Context context = view.getContext();
            recyclerView = (RecyclerView) view.findViewById(R.id.list);
            checkOutLayout=(RelativeLayout) view.findViewById(R.id.cart_checkout_layout);
            checkOutButton=(Button) view.findViewById(R.id.cart_checkout_button);
            recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));

            cartMenuItemRecyclerViewAdapter = new CartMenuItemRecyclerViewAdapter(cartItems, view.getContext());
            recyclerView.setAdapter(cartMenuItemRecyclerViewAdapter);
            checkOutButton.setText(view.getResources().getString(R.string.cart_checkout,cartMenuItemRecyclerViewAdapter.calculateFees()));

            swipeController = new SwipeController(new SwipeControllerActions() {
                @Override
                public void onRightClicked(int position) {
                    //super.onRightClicked(position);
                    CartItemContainer.readCartItemContainer().deleteCartItemByCartID(cartItems.get(position).cartID);
                    //cartMenuItemRecyclerViewAdapter.mValues.remove(position);
                    cartMenuItemRecyclerViewAdapter.notifyItemRemoved(position);
                    cartMenuItemRecyclerViewAdapter.notifyItemRangeChanged(position, cartMenuItemRecyclerViewAdapter.getItemCount());
                    checkOutButton.setText(view.getResources().getString(R.string.cart_checkout,cartMenuItemRecyclerViewAdapter.calculateFees()));
                    if(!cartItems.isEmpty()){
                        checkOutLayout.setVisibility(View.VISIBLE);
                    }
                    else{
                        checkOutLayout.setVisibility(View.GONE);
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
    public void onAttach(Context context) {
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
        //void onCartItemFragmentInteraction(CartItem item);
    }
}
