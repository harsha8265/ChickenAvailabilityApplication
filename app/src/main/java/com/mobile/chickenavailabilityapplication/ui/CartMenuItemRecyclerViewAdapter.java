package com.mobile.chickenavailabilityapplication.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.CartItem;
import com.mobile.chickenavailabilityapplication.ui.CartFragment.OnListFragmentInteractionListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * {@link RecyclerView.Adapter} that can display a {@link CartItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class CartMenuItemRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int Normal = 1;
    public static final int Footer = 2;
    public static final int EmptyCart = 3;

    public Context mContext;
    public List<CartItem> mValues;

    public LinearLayout customOptionsLayout;
    public LayoutInflater inflater;

    public double itemsSubTotal=0.00;
    public double itemsDeliveryFees=0.00;
    public double itemsTaxesFees=0.00;
    public double itemsTotal=0.00;
    private final EmptyCartButtonPressed mListener;


    public CartMenuItemRecyclerViewAdapter(List<CartItem> items, Context mContext, EmptyCartButtonPressed mListener) {
        mValues = items;
        this.mContext = mContext;
        this.mListener=mListener;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view;
        /*if(!mValues.isEmpty()){
            if(viewType==Footer){
                view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_cart_item_footer, parent, false);
                return new FooterViewHolder(view);
            }
            else{
                view=LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_cart_item, parent, false);
                return new ItemViewHolder(view);
            }

        }
        else{
            view=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_empty_cart, parent, false);
            return new EmptyCartViewHolder(view);
        }*/

       if(viewType==Footer){
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_cart_item_footer, parent, false);
                return new FooterViewHolder(view);


        }
        else{
            view=LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.fragment_cart_item, parent, false);
           return new ItemViewHolder(view);
        }
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
    public void onBindViewHolder(@NotNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position)==Normal){
            ItemViewHolder itemHolder=(ItemViewHolder)holder;
            itemHolder.mItem = mValues.get(position);
            itemHolder.itemNumber.setText(String.valueOf(position+1));
            itemHolder.itemTitle.setText(mValues.get(position).productName);
            itemHolder.itemQuantity.setText(itemHolder.mView.getResources().getString(R.string.item_quantity,mValues.get(position).quantitySelected));
            itemHolder.itemSubTotal.setText(itemHolder.mView.getResources().getString(R.string.item_price,mValues.get(position).subTotal));
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
        else if(getItemViewType(position)==Footer){
            FooterViewHolder footerHolder=(FooterViewHolder) holder;
            if(!mValues.isEmpty()){
                calculateFees();
                footerHolder.footerView.setVisibility(View.VISIBLE);
                footerHolder.itemsSubtotal.setText(footerHolder.mView.getResources().getString(R.string.item_price,itemsSubTotal));
                footerHolder.itemsDeliveryFee.setText(footerHolder.mView.getResources().getString(R.string.item_price,itemsDeliveryFees));
                footerHolder.itemsTaxesFees.setText(footerHolder.mView.getResources().getString(R.string.item_price,itemsTaxesFees));
                footerHolder.itemsTotal.setText(footerHolder.mView.getResources().getString(R.string.item_price,itemsTotal));
                footerHolder.emptyCartButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //clear();
                        mListener.onEmptyCart(true);

                    }
                });
            }
            else{
                footerHolder.footerView.setVisibility(View.GONE);
            }
        }
        else{

        }
        /*if(position < mValues.size()){
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
                holder.mView.setVisibility(View.VISIBLE);
                holder.itemsSubtotal.setText(holder.mView.getResources().getString(R.string.item_price,itemsSubTotal));
                holder.itemsDeliveryFee.setText(holder.mView.getResources().getString(R.string.item_price,itemsDeliveryFees));
                holder.itemsTaxesFees.setText(holder.mView.getResources().getString(R.string.item_price,itemsTaxesFees));
                holder.itemsTotal.setText(holder.mView.getResources().getString(R.string.item_price,itemsTotal));
                holder.emptyCartButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //clear();
                        mListener.onEmptyCart(true);

                    }
                });
            }
            else{
                holder.mView.setVisibility(View.GONE);
            }

        }*/




    }

    @Override
    public int getItemCount() {
        if (mValues.isEmpty()) return 1;
        else return mValues.size() + 1;
        //return mValues.size() + 1;
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
        public Button emptyCartButton=null;
        public RelativeLayout footerView=null;


        public CartItem mItem;

        public ViewHolder(View view,int viewType) {
            super(view);
            mView = view;
            if(viewType==Normal){
                mView.setTag("Item");
                itemNumber = (TextView) view.findViewById(R.id.item_number);
                itemTitle = (TextView) view.findViewById(R.id.item_title);
                itemQuantity = (TextView) view.findViewById(R.id.item_quantity);
                itemSubTotal = (TextView) view.findViewById(R.id.item_subtotal);
                customOptionsLayout = (LinearLayout) view.findViewById(R.id.custom_options_layout);
            }
            else{
                mView.setTag("Footer");
                itemsSubtotal = (TextView) view.findViewById(R.id.item_subtotal_price);
                itemsDeliveryFee = (TextView) view.findViewById(R.id.item_deliveryfee_price);
                itemsTaxesFees = (TextView) view.findViewById(R.id.item_tax_fee_price);
                itemsTotal = (TextView) view.findViewById(R.id.item_total_fee_price);
                emptyCartButton = (Button) view.findViewById(R.id.items_empty_cart_button);
                footerView=(RelativeLayout) view.findViewById(R.id.item_view) ;

            }


        }

    }
    public class ItemViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView itemNumber;
        TextView itemTitle;
        TextView itemQuantity;
        TextView itemSubTotal;
        public CartItem mItem;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            itemNumber = (TextView) itemView.findViewById(R.id.item_number);
            itemTitle = (TextView) itemView.findViewById(R.id.item_title);
            itemQuantity = (TextView) itemView.findViewById(R.id.item_quantity);
            itemSubTotal = (TextView) itemView.findViewById(R.id.item_subtotal);
            customOptionsLayout = (LinearLayout) itemView.findViewById(R.id.custom_options_layout);
        }
    }
    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        TextView itemsSubtotal;
        TextView itemsDeliveryFee;
        TextView itemsTaxesFees;
        TextView itemsTotal;
        Button emptyCartButton;
        RelativeLayout footerView;
        View mView;

        public FooterViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            itemsSubtotal = (TextView) itemView.findViewById(R.id.item_subtotal_price);
            itemsDeliveryFee = (TextView) itemView.findViewById(R.id.item_deliveryfee_price);
            itemsTaxesFees = (TextView) itemView.findViewById(R.id.item_tax_fee_price);
            itemsTotal = (TextView) itemView.findViewById(R.id.item_total_fee_price);
            emptyCartButton = (Button) itemView.findViewById(R.id.items_empty_cart_button);
            footerView=(RelativeLayout) itemView.findViewById(R.id.item_view) ;

        }
    }

    public static class EmptyCartViewHolder extends RecyclerView.ViewHolder {

        EmptyCartViewHolder(@NonNull View itemView) {
            super(itemView);

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
        valueTV.setTextColor(context.getResources().getColor(R.color.colorLightGray));
        valueTV.setGravity(Gravity.CENTER_HORIZONTAL);
        valueTV.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT));

        customOptionsLayout.addView(valueTV);
    }
}
