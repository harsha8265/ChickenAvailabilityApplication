package com.mobile.chickenavailabilityapplication.ui;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

    private static final int Normal = 1;
    private static final int Footer = 2;

    private Context mContext;
    private List<CartItem> mValues;

    private LinearLayout customOptionsLayout;
    private LayoutInflater inflater;

    private double itemsSubTotal=0.00;
    private double itemsDeliveryFees=0.00;
    private double itemsTaxesFees=0.00;
    private double itemsTotal=0.00;
    private final IAdapterItemListener mListener;


     CartMenuItemRecyclerViewAdapter(List<CartItem> items, Context mContext, IAdapterItemListener mListener) {
        mValues = items;
        this.mContext = mContext;
        this.mListener=mListener;
    }

    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NotNull ViewGroup parent, int viewType) {
        View view;
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
        else {
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
                        mListener.onEmptyCart(true);
                    }
                });
            }
            else{
                footerHolder.footerView.setVisibility(View.GONE);
            }
        }


    }

    @Override
    public int getItemCount() {
        if (mValues.isEmpty()) return 1;
        else return mValues.size() + 1;
    }


    public  class ItemViewHolder extends RecyclerView.ViewHolder {
        View mView;
        TextView itemNumber;
        TextView itemTitle;
        TextView itemQuantity;
        TextView itemSubTotal;
        public CartItem mItem;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            mView=itemView;
            itemNumber =  itemView.findViewById(R.id.item_number);
            itemTitle =  itemView.findViewById(R.id.item_title);
            itemQuantity =  itemView.findViewById(R.id.item_quantity);
            itemSubTotal =  itemView.findViewById(R.id.item_subtotal);
            customOptionsLayout =  itemView.findViewById(R.id.custom_options_layout);
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
            itemsSubtotal =  itemView.findViewById(R.id.item_subtotal_price);
            itemsDeliveryFee =  itemView.findViewById(R.id.item_deliveryfee_price);
            itemsTaxesFees =  itemView.findViewById(R.id.item_tax_fee_price);
            itemsTotal =  itemView.findViewById(R.id.item_total_fee_price);
            emptyCartButton =  itemView.findViewById(R.id.items_empty_cart_button);
            footerView= itemView.findViewById(R.id.item_view) ;

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
