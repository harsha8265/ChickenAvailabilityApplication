package com.mobile.chickenavailabilityapplication.ui;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.CartItem;
import com.mobile.chickenavailabilityapplication.datamodel.CartItemContainer;
import com.mobile.chickenavailabilityapplication.datamodel.ReviewCartItemSection;
import com.mobile.chickenavailabilityapplication.ui.ReviewCartItemFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * {@link RecyclerView.Adapter} that can display a {@link ReviewCartItemSection} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyReviewCartItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int SECTION_VIEW = 0;
    private static final int ITEM_VIEW = 1;
    private static final int FOOTER_VIEW = 2;
    private double itemsSubTotal=0.00;
    private double itemsDeliveryFees=0.00;
    private double itemsTaxesFees=0.00;
    private double itemsTotal=0.00;
    private final List<ReviewCartItemSection> mValues;
    private final List<CartItem> cartItems;
    private final OnListFragmentInteractionListener mListener;
    private LinearLayout addordersLinearLayout;
    private Context mContext;
    public MyReviewCartItemRecyclerViewAdapter(List<ReviewCartItemSection> items,ArrayList<CartItem> cartItems, OnListFragmentInteractionListener listener, Context mContext) {
        mValues = items;
        mListener = listener;
        this.cartItems=cartItems;
        this.mContext=mContext;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType==FOOTER_VIEW){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_cart_item_footer, parent, false);
            return new FooterViewHolder(view);
        }
        else if(viewType==SECTION_VIEW){
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_review_cart_item, parent, false);
            return new SectionViewHolder(view);
        }
        else{
            view=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_review_item_section, parent, false);
            return new ItemViewHolder(view);
        }
    }



    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==SECTION_VIEW){
            SectionViewHolder sectionHolder=(SectionViewHolder) holder;
            sectionHolder.reviewCartItemSection = mValues.get(position);
            sectionHolder.mSectionheader.setText(sectionHolder.reviewCartItemSection.category);
        }
        else if(getItemViewType(position)==FOOTER_VIEW){
            FooterViewHolder footerHolder=(FooterViewHolder) holder;
            calculateFees();
            footerHolder.footerView.setVisibility(View.VISIBLE);
            footerHolder.emptyCartButton.setVisibility(View.GONE);
            footerHolder.itemsSubtotal.setText(footerHolder.mView.getResources().getString(R.string.item_price,itemsSubTotal));
            footerHolder.itemsDeliveryFee.setText(footerHolder.mView.getResources().getString(R.string.item_price,itemsDeliveryFees));
            footerHolder.itemsTaxesFees.setText(footerHolder.mView.getResources().getString(R.string.item_price,itemsTaxesFees));
            footerHolder.itemsTotal.setText(footerHolder.mView.getResources().getString(R.string.item_price,itemsTotal));
            //footerHolder.reviewCartItemSection = mValues.get(position);
            //footerHolder.mSectionheader.setText(sectionHolder.reviewCartItemSection.category);
        }
        else {
            ItemViewHolder itemHolder=(ItemViewHolder)holder;
            itemHolder.reviewCartItemSection = mValues.get(position);
            itemHolder.mItemHeader.setText(itemHolder.reviewCartItemSection.category);
            if(itemHolder.reviewCartItemSection.category.equals("Order Details:")){
                addordersLinearLayout.setVisibility(View.VISIBLE);
                LayoutInflater layoutInflater= LayoutInflater.from(mContext);
                if(!cartItems.isEmpty()){
                    for(int i=0;i<cartItems.size();i++){
                        addOrderstoView(layoutInflater,cartItems.get(i),mContext,i);
                    }
                }

            }
            else{
                addordersLinearLayout.setVisibility(View.GONE);
            }

        }
        /*
        itemHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onReviewCartListFragmentInteraction(itemHolder.reviewCartItemSection.category);
                }
            }
        });*/
    }



    @Override
    public int getItemCount() {
        return mValues.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if(position==mValues.size()){
            return FOOTER_VIEW;
        }
        else{
            if (mValues.get(position).isSection) {
                return SECTION_VIEW;
            }
            else {
                return ITEM_VIEW;
            }
        }
    }

    public  class ItemViewHolder extends RecyclerView.ViewHolder {
        View mView;
        ReviewCartItemSection reviewCartItemSection;
        CardView mItemLayout;
        TextView mItemHeader;


        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            mItemLayout= mView.findViewById(R.id.item_header_layout);
            mItemHeader= mView.findViewById(R.id.item_header);
            addordersLinearLayout= mView.findViewById(R.id.addOrdersLayout);
        }
    }

    public static class SectionViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView mSectionheader;
        ReviewCartItemSection reviewCartItemSection;

        SectionViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            mSectionheader=mView.findViewById(R.id.header_title);

        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView itemsSubtotal;
        TextView itemsDeliveryFee;
        TextView itemsTaxesFees;
        TextView itemsTotal;
        TextView emptyCartButton;
        RelativeLayout footerView;
        View mView;

        FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            itemsSubtotal = (TextView) itemView.findViewById(R.id.item_subtotal_price);
            itemsDeliveryFee = (TextView) itemView.findViewById(R.id.item_deliveryfee_price);
            itemsTaxesFees = (TextView) itemView.findViewById(R.id.item_tax_fee_price);
            itemsTotal = (TextView) itemView.findViewById(R.id.item_total_fee_price);
            emptyCartButton = (TextView) itemView.findViewById(R.id.items_empty_cart_button);
            footerView=(RelativeLayout) itemView.findViewById(R.id.item_view) ;

        }
    }

    public double calculateFees(){
        itemsSubTotal=0;
        for(CartItem cartItem: cartItems){
            itemsSubTotal+=cartItem.subTotal;
        }
        itemsDeliveryFees= itemsSubTotal*(5.0f/100.0f);
        itemsTaxesFees=itemsSubTotal*(2.0f/100.0f);
        itemsTotal=itemsSubTotal+itemsDeliveryFees+itemsTaxesFees;
        return itemsTotal;
    }

    private void addOrderstoView(LayoutInflater layoutInflater, CartItem cartItem, Context context,int orderIndex) {
        final View dataView = layoutInflater.inflate(R.layout.fragment_cart_item, null);
        TextView itemNumber =  dataView.findViewById(R.id.item_number);
        TextView itemTitle =  dataView.findViewById(R.id.item_title);
        TextView itemQuantity =  dataView.findViewById(R.id.item_quantity);
        TextView itemSubTotal =  dataView.findViewById(R.id.item_subtotal);
        RelativeLayout cartItemLayout =  dataView.findViewById(R.id.order_layout);
        View divider = dataView.findViewById(R.id.settings_Text_bottom_border);
        cartItemLayout.setBackgroundColor(Color.WHITE);
        divider.setVisibility(View.GONE);
        LinearLayout optionsLayout =  dataView.findViewById(R.id.custom_options_layout);
        itemNumber.setText(String.valueOf(orderIndex+1));
        itemTitle.setText(cartItem.productName);
        itemQuantity.setText(dataView.getResources().getString(R.string.item_quantity,cartItem.quantitySelected));
        itemSubTotal.setText(dataView.getResources().getString(R.string.item_price,cartItem.subTotal));
        optionsLayout.setVisibility(View.GONE);

        addordersLinearLayout.addView(dataView);
    }


}
