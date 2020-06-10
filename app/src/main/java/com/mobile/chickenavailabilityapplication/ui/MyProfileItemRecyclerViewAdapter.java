package com.mobile.chickenavailabilityapplication.ui;

import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mobile.chickenavailabilityapplication.R;
import com.mobile.chickenavailabilityapplication.datamodel.ProfileKeyValue;
import com.mobile.chickenavailabilityapplication.dummy.DummyContent.DummyItem;
import com.mobile.chickenavailabilityapplication.ui.ProfileItemFragment.OnListFragmentInteractionListener;

import java.util.ArrayList;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyProfileItemRecyclerViewAdapter extends RecyclerView.Adapter<MyProfileItemRecyclerViewAdapter.ViewHolder> {

    private final ArrayList<ProfileKeyValue> profileKeyValues;
    private final OnListFragmentInteractionListener mListener;
    public MyProfileItemRecyclerViewAdapter(ArrayList<ProfileKeyValue> profileKeyValues, OnListFragmentInteractionListener listener) {
        this.profileKeyValues = profileKeyValues;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_profile_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = profileKeyValues.get(position);
        holder.mKeyView.setText(holder.mItem.Key);
        if(holder.mItem.Key.equals("Password") || holder.mItem.Key.equals("Shipping Address")){
            holder.mValueView.setInputType(InputType.TYPE_MASK_CLASS);
        }
        holder.mValueView.setText(holder.mItem.Value);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onProfileListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return profileKeyValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mKeyView;
        public final TextView mValueView;
        public ProfileKeyValue mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mKeyView = (TextView) view.findViewById(R.id.text_view_key);
            mValueView = (TextView) view.findViewById(R.id.text_view_value);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mValueView.getText() + "'";
        }
    }
}
