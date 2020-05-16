package com.mobile.chickenavailabilityapplication.ui;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.CartItem;
import com.mobile.chickenavailabilityapplication.ui.CartFragment.OnListFragmentInteractionListener;
import com.mobile.chickenavailabilityapplication.util.ViewUtils;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CartItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class CartMenuItemRecyclerViewAdapter extends RecyclerView.Adapter<CartMenuItemRecyclerViewAdapter.ViewHolder> {

    public static final int Normal = 1;
    public static final int Footer = 2;

    public Context mContext;
    public List<CartItem> mValues;

    public LinearLayout customOptionsLayout;
    public LayoutInflater inflater;

    public double itemsSubTotal=0.00;
    public double itemsDeliveryFees=0.00;
    public double itemsTaxesFees=0.00;
    public double itemsTotal=0.00;



    public CartMenuItemRecyclerViewAdapter(List<CartItem> items, Context mContext) {
        mValues = items;
        this.mContext = mContext;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view;
        ViewHolder holder;
        if(viewType==Footer){
            if(!mValues.isEmpty()){
                view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_cart_item_footer, parent, false);
                holder=new ViewHolder(view,viewType);
            }
            else{
                view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_empty_cart, parent, false);
                holder=new ViewHolder(view,viewType);
            }
        }
        else{
            view=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_cart_item, parent, false);
            holder=new ViewHolder(view,viewType);
        }

        return holder;

    }

    @Override
    public int getItemViewType(int position) {

        if(position==mValues.size()){
           return Footer;
        }
        else{
            return Normal;
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if(position < mValues.size()){
            holder.mItem = mValues.get(position);
            holder.itemNumber.setText(String.valueOf(position+1));
            holder.itemTitle.setText(mValues.get(position).productName);
            holder.itemQuantity.setText(holder.mView.getResources().getString(R.string.item_quantity,mValues.get(position).quantitySelected));
            holder.itemSubTotal.setText(holder.mView.getResources().getString(R.string.item_price,mValues.get(position).subTotal));
            customOptionsLayout.removeAllViews();
            inflater = LayoutInflater.from(mContext);

            ArrayList<String> optionEntries = new ArrayList<>();
            if(!mValues.get(position).optionsSelected.isEmpty()) {
                optionEntries.addAll(mValues.get(position).optionsSelected.values());
            }
            if(!mValues.get(position).specialInstructionsrequested.isEmpty()){
                optionEntries.add(mValues.get(position).specialInstructionsrequested);
            }
            if(!optionEntries.isEmpty()){
                for(int i=0;i<optionEntries.size();i++){
                    addCustomOptiontoView(inflater,optionEntries.get(i),mContext,i);
                }
            }

        }
        else{
            if(!mValues.isEmpty()){
                calculateFees();
                holder.itemsSubtotal.setText(holder.mView.getResources().getString(R.string.item_price,itemsSubTotal));
                holder.itemsDeliveryFee.setText(holder.mView.getResources().getString(R.string.item_price,itemsDeliveryFees));
                holder.itemsTaxesFees.setText(holder.mView.getResources().getString(R.string.item_price,itemsTaxesFees));
                holder.itemsTotal.setText(holder.mView.getResources().getString(R.string.item_price,itemsTotal));
            }
            else{

            }

        }


    }

    @Override
    public int getItemCount() {
        return mValues.size()+1;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public  TextView itemNumber=null;
        public  TextView itemTitle=null;
        public  TextView itemQuantity=null;
        public  TextView itemSubTotal=null;
        public  TextView itemsSubtotal=null;
        public  TextView itemsDeliveryFee=null;
        public TextView itemsTaxesFees=null;
        public  TextView itemsTotal=null;


        public CartItem mItem;

        public ViewHolder(View view,int viewType) {
            super(view);
            mView = view;
            if(viewType==Normal){
                itemNumber = (TextView) view.findViewById(R.id.item_number);
                itemTitle = (TextView) view.findViewById(R.id.item_title);
                itemQuantity = (TextView) view.findViewById(R.id.item_quantity);
                itemSubTotal = (TextView) view.findViewById(R.id.item_subtotal);
                customOptionsLayout = (LinearLayout) view.findViewById(R.id.custom_options_layout);
            }
            else{
                itemsSubtotal = (TextView) view.findViewById(R.id.item_subtotal_price);
                itemsDeliveryFee = (TextView) view.findViewById(R.id.item_deliveryfee_price);
                itemsTaxesFees = (TextView) view.findViewById(R.id.item_tax_fee_price);
                itemsTotal = (TextView) view.findViewById(R.id.item_total_fee_price);


            }


        }

    }

    public double calculateFees(){
        itemsSubTotal=0;
        for(CartItem cartItem:mValues){
            itemsSubTotal+=cartItem.subTotal;
        }
        itemsDeliveryFees= itemsSubTotal*(5.0f/100.0f);
        itemsTaxesFees=itemsSubTotal*(2.0f/100.0f);
        itemsTotal=itemsSubTotal+itemsDeliveryFees+itemsTaxesFees;
        return itemsTotal;
    }

    private void addCustomOptiontoView( LayoutInflater layoutInflater,String optionEntry,Context context,int optionId) {
        TextView valueTV = new TextView(context);
        valueTV.setText(context.getResources().getString(R.string.selected_option,optionEntry));
        valueTV.setId(optionId);
        valueTV.setTextColor(context.getResources().getColor(R.color.colorLightBlack));
        valueTV.setGravity(Gravity.CENTER_HORIZONTAL);
        valueTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

        customOptionsLayout.addView(valueTV);
    }
}
