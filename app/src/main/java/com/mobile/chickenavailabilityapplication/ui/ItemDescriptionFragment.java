package com.mobile.chickenavailabilityapplication.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.CartItem;
import com.mobile.chickenavailabilityapplication.datamodel.CartItemContainer;
import com.mobile.chickenavailabilityapplication.datamodel.MenuItem;
import com.mobile.chickenavailabilityapplication.datamodel.MenuItemContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ItemDescriptionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ItemDescriptionFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemDescriptionFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "productID";
    private static final String ARG_PARAM2 = "cartID";

    // TODO: Rename and change types of parameters
    private String productID;
    private String cartID;

    ImageView itemImage;
    TextView itemTitle;
    TextView itemPrice;
    TextView itemQuantity;
    LinearLayout customOptionsMainLinearLayout;
    LinearLayout customOptionsLinearLayout;
    LayoutInflater layoutInflater;
    EditText specialInstructionsText;
    View customOptionsSeperator;
    Button increementButton;
    Button decreementButton;
    Button addCartButton;

    private static final double MIN_QUANTITY = 1.00;
    MenuItem menuItem;
    CartItem cartItem;
    double requestedQuantity;
    double subTotal;
    HashMap<String,String> optionsSelected=null;
    String requestedSpecialInstructions="";
    private OnFragmentInteractionListener mListener;

    public ItemDescriptionFragment() {
        // Required empty public constructor
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemDescriptionFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemDescriptionFragment newInstance(String param1, String param2) {
        ItemDescriptionFragment fragment = new ItemDescriptionFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback= new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Bundle bundle = new Bundle();
                bundle.putBoolean("itemadded", false);
                NavController navController = Navigation.findNavController( requireActivity(),R.id.fragNavHost);
                navController.setGraph(R.navigation.bottom_home_nav,bundle);
                //navController.popBackStack(R.id.itemDescriptionFragment,false);

                //navController.navigate(R.id.action_global_mainFragment, bundle);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            productID = getArguments().getString(ARG_PARAM1);
            cartID = getArguments().getString(ARG_PARAM2);
            if(cartID != null){
                cartItem= CartItemContainer.readCartItemContainer().getCartItemForCartID(cartID);
            }
            if(productID == null){
                productID=cartItem.productID;
            }
            menuItem= MenuItemContainer.readMenuItemContainer().getMenuListForProductId(productID);
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_item_description, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        itemImage = (ImageView) view.findViewById(R.id.headerImage);
        itemTitle = (TextView) view.findViewById(R.id.item_heading);
        itemPrice = (TextView) view.findViewById(R.id.item_price);
        increementButton = (Button) view.findViewById(R.id.increementButton);
        decreementButton = (Button) view.findViewById(R.id.decreementButton);
        addCartButton = (Button) view.findViewById(R.id.add_cart_button);
        customOptionsMainLinearLayout = (LinearLayout) view.findViewById(R.id.custom_options_main_layout);
        customOptionsLinearLayout = (LinearLayout) view.findViewById(R.id.custom_options_layout);
        customOptionsSeperator = (View) view.findViewById(R.id.quantity_separator);
        itemQuantity = (TextView) view.findViewById(R.id.quantity_text_view);
        specialInstructionsText=(EditText) view.findViewById(R.id.special_instruction_edittext);

        int resID = view.getResources().getIdentifier(menuItem.itemImage , "drawable" , view.getContext().getPackageName());
        itemImage.setBackground(getResources().getDrawable(resID));
        itemTitle.setText(menuItem.itemHeading);
        addCartButton.setText(view.getResources().getString(R.string.add_to_cart,menuItem.price));
        customOptionsLinearLayout.removeAllViews();
        layoutInflater=getLayoutInflater();
        HashMap<String, ArrayList<String>> customOptionsMap = menuItem.optionsMap;
        //This if block is for generating choices layout
        if(!customOptionsMap.isEmpty()){
            optionsSelected=new HashMap<String,String>();
            customOptionsMainLinearLayout.setVisibility(View.VISIBLE);
            customOptionsSeperator.setVisibility(View.VISIBLE);
            for (Map.Entry<String, ArrayList<String>> entry : customOptionsMap.entrySet()) {
                addCustomOptiontoView(layoutInflater, entry,view.getContext());
            }
        }
        else{
            customOptionsMainLinearLayout.setVisibility(View.GONE);
            customOptionsSeperator.setVisibility(View.GONE);
        }
        if(cartID!=null){
            subTotal=cartItem.subTotal;
            requestedQuantity=cartItem.quantitySelected;
            itemPrice.setText(view.getResources().getString(R.string.item_price,cartItem.subTotal));
            itemQuantity.setText(String.valueOf(cartItem.quantitySelected));
            for(int i=0;i<customOptionsLinearLayout.getChildCount();i++){
                RelativeLayout optionRelativeLayout = (RelativeLayout) customOptionsLinearLayout.getChildAt(i);
                TextView optionLabel = (TextView) optionRelativeLayout.getChildAt(0);
                RadioGroup optionRadioGroup = (RadioGroup) optionRelativeLayout.getChildAt(1);
                for(int j=0;j<optionRadioGroup.getChildCount();j++){
                    RadioButton radioButton= (RadioButton)optionRadioGroup.getChildAt(j);
                    if(radioButton.getText().toString().equals(cartItem.optionsSelected.get(optionLabel.getText().toString()))){
                        radioButton.setChecked(true);
                    }
                }
            }

            specialInstructionsText.setText(cartItem.specialInstructionsrequested);
        }
        else{
            subTotal=(double) Math.round(menuItem.price*100)/100;
            requestedQuantity=MIN_QUANTITY;
            itemPrice.setText(view.getResources().getString(R.string.item_price,menuItem.price));
            itemQuantity.setText(String.valueOf(MIN_QUANTITY));
            specialInstructionsText.setText(requestedSpecialInstructions);
        }
        increementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double curQuantity = Double.parseDouble(itemQuantity.getText().toString());

                if((curQuantity+0.25) <= menuItem.availableQuantity){
                    itemQuantity.setText(String.valueOf(curQuantity+0.25));
                }
                else{
                    itemQuantity.setText(String.valueOf(curQuantity));
                }
            }


        });

        decreementButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                double curQuantity = Double.parseDouble(itemQuantity.getText().toString());

                if((curQuantity-0.25) >= MIN_QUANTITY){
                    itemQuantity.setText(String.valueOf(curQuantity-0.25));
                }
                else{
                    itemQuantity.setText(String.valueOf(curQuantity));
                }
            }

        });
        itemQuantity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                requestedQuantity = Double.parseDouble(editable.toString());
                subTotal = (double) Math.round(requestedQuantity*menuItem.price*100)/100;
                itemPrice.setText(view.getResources().getString(R.string.item_price,subTotal));
                addCartButton.setText(view.getResources().getString(R.string.add_to_cart,subTotal));

            }
        });
        addCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putBoolean("itemadded", true);
                optionsSelected=new HashMap<>();
                NavController navController = Navigation.findNavController((Activity) view.getContext(),R.id.fragNavHost);
                for(int i=0;i<customOptionsLinearLayout.getChildCount();i++){
                    RelativeLayout optionRelativeLayout = (RelativeLayout) customOptionsLinearLayout.getChildAt(i);
                    TextView optionLabel = (TextView) optionRelativeLayout.getChildAt(0);
                    RadioGroup optionRadioGroup = (RadioGroup) optionRelativeLayout.getChildAt(1);
                    optionsSelected.put(optionLabel.getText().toString(),((RadioButton)optionRadioGroup.findViewById(optionRadioGroup.getCheckedRadioButtonId())).getText().toString());

                }
                requestedSpecialInstructions=specialInstructionsText.getText().toString();
                navController.popBackStack(R.id.itemDescriptionFragment,false);
                if(cartID !=null){
                    CartItemContainer.readCartItemContainer().editCartItems(cartID,requestedQuantity,subTotal,optionsSelected,requestedSpecialInstructions);
                    navController.navigate(R.id.action_itemdescription_to_cartfragment,bundle);
                }
                else{
                    CartItemContainer.readCartItemContainer().saveCartItems(menuItem.productID,menuItem.itemHeading,requestedQuantity,subTotal,optionsSelected,requestedSpecialInstructions);
                    navController.navigate(R.id.action_itemDescriptionFragment_to_dashboardMenuItemFragment,bundle);
                }
                //navController.navigate(R.id.action_global_mainFragment, bundle);Thats a nice flowto understand ..

            }
        });
    }


    private void addCustomOptiontoView(LayoutInflater layoutInflater, Map.Entry<String,ArrayList<String>> optionEntry,Context context) {
        final View dataView = layoutInflater.inflate(R.layout.option_layout, null);
        TextView optionsLabel = (TextView) dataView.findViewById(R.id.option_heading);
        RadioGroup optionsGroup = (RadioGroup) dataView.findViewById(R.id.options_group);
        optionsGroup.setTag(optionEntry.getKey());
        optionsLabel.setText(dataView.getResources().getString(R.string.item_options,optionEntry.getKey()));
        optionsGroup.setClickable(true);
        ArrayList<String> optionsList=optionEntry.getValue();
        final RadioButton[] rb = new RadioButton[optionsList.size()];
        for(int i=0;i<optionsList.size();i++){
            rb[i]  = new RadioButton(context);
            rb[i].setText(optionsList.get(i));
            rb[i].setTextColor(context.getResources().getColor(R.color.colorLightBlack));
            if(Build.VERSION.SDK_INT>=21)
            {

                ColorStateList colorStateList = new ColorStateList(
                        new int[][]{

                                new int[]{-android.R.attr.state_enabled}, //disabled
                                new int[]{android.R.attr.state_enabled} //enabled
                        },
                        new int[] {

                                Color.WHITE //disabled
                                ,Color.BLACK //enabled

                        }
                );
                rb[i].setButtonTintList(colorStateList);//set the color tint list
                if(i==0){
                    rb[0].setChecked(true);
                }
                rb[i].invalidate(); //could not be necessary
            }
            rb[i].setBackgroundTintList(context.getResources().getColorStateList(R.color.colorLightBlack));
            rb[i].setId(i + 100);
            optionsGroup.addView(rb[i]);
        }

        customOptionsLinearLayout.addView(dataView);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
