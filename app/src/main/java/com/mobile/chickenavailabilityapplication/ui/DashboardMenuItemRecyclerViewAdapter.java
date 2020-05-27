package com.mobile.chickenavailabilityapplication.ui;

import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.MenuItem;
import com.mobile.chickenavailabilityapplication.ui.DashboardMenuItemFragment.OnListFragmentInteractionListener;
import com.mobile.chickenavailabilityapplication.dummy.DummyContent.DummyItem;
import com.mobile.chickenavailabilityapplication.util.ViewUtils;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class DashboardMenuItemRecyclerViewAdapter extends RecyclerView.Adapter<DashboardMenuItemRecyclerViewAdapter.ViewHolder> {

    public Context mContext;
    private final List<MenuItem> mValues;
    private final OnListFragmentInteractionListener mListener;
    private Picasso picasso;
    public DashboardMenuItemRecyclerViewAdapter(List<MenuItem> items,Context mContext, OnListFragmentInteractionListener listener) {
        mValues = items;
        this.mContext = mContext;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        picasso = Picasso.get();
        picasso.setIndicatorsEnabled(true);
        picasso.setLoggingEnabled(true);
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_dashboardmenu_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        MenuItem menuItem = mValues.get(position);

        holder.mItem = menuItem;
        holder.mItemHeadingText.setText(menuItem.itemHeading);
        holder.mItemPriceText.setText(mContext.getResources().getString(R.string.item_price_per_kg,menuItem.price));
        //holder.mItemAvailabilityText.setText("Availability :"+mValues.get(position).availableQuantity+"kgs");
        holder.mItemSubHeadingText.setText(menuItem.itemSubheading);
        //int resID = mContext.getResources().getIdentifier(mValues.get(position).itemImage , "drawable" , mContext.getPackageName());
       // Bitmap bitMap = ViewUtils.drawableToBitmap(mContext.getResources().getDrawable(resID));
        //holder.mItemImageView.setBackground(mContext.getResources().getDrawable(resID));
        Picasso.get().load(menuItem.imageUrl)
                //.memoryPolicy(MemoryPolicy.NO_CACHE)
                //.memoryPolicy(MemoryPolicy.NO_STORE)
                .into(holder.mItemImageView);
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem.productID);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mItemHeadingText;
        public final TextView mItemPriceText;
        //public final TextView mItemAvailabilityText;
        public final TextView mItemSubHeadingText;
        public final ImageView mItemImageView;

        public MenuItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mItemHeadingText = (TextView) view.findViewById(R.id.item_heading);
            mItemPriceText = (TextView) view.findViewById(R.id.item_price);
            //mItemAvailabilityText = (TextView) view.findViewById(R.id.item_subheading);
            mItemSubHeadingText = (TextView) view.findViewById(R.id.item_subheading);
            mItemImageView = (ImageView) view.findViewById(R.id.item_image);

        }

        @Override
        public String toString() {
            return super.toString() + " '" + mItemHeadingText.getText() + "'";
        }
    }
}
