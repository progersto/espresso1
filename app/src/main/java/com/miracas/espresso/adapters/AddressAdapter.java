package com.miracas.espresso.adapters;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miracas.R;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.address.DeleteAddressClient;
import com.miracas.espresso.client.responses.Address;
import com.miracas.espresso.design.IOnRequestCompleted;
import com.miracas.espresso.dialogs.DeleteAddressDialog;
import com.miracas.espresso.dialogs.EditAddressDialog;
import com.miracas.espresso.dialogs.EditCartItemDialog;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.ViewHolder> {

    private Fragment fragment;
    private View lastAddressSelected;
    private String lastAddressSelectedID;
    private List<Address> addresses;

    public AddressAdapter(Fragment fragment, List<Address> values) {
        super();
        this.fragment = fragment;
        this.addresses = values;
    }

    public String getLastAddressSelectedID() {
        return lastAddressSelectedID;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(fragment.getContext());
        View view = inflater.inflate(R.layout.list_item_address, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final Address address = addresses.get(position);
        holder.name.setText(address.name);
        holder.address.setText(address.address);
        holder.landmark.setText(address.landmark + " " + address.pin_code);
        holder.city.setText(address.city + " " + address.state);
        holder.email.setText(address.email);
        holder.phone.setText(address.mobile_number);

        if (address.selected) {
            Drawable background = ContextCompat.getDrawable(
                    fragment.getContext(),  R.drawable.selected_address_background);
            holder.addressLayout.setBackground(background);
        } else {
            holder.addressLayout.setBackground(null);
        }

        holder.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment editAddressFragment = EditAddressDialog.newInstance();
                editAddressFragment.setTargetFragment(fragment, ClientCodes.EDIT_ADDRESS_DIALOG);
                editAddressFragment.show(fragment.getFragmentManager(), "dialog");
                Bundle bundle = new Bundle();
                bundle.putInt(EditAddressDialog.MODE, EditAddressDialog.MODE_TYPE.EDIT.ordinal());
                bundle.putString(EditAddressDialog.ADDRESS, new Gson().toJson(address));
                editAddressFragment.setArguments(bundle);
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment editCartItemDialog = DeleteAddressDialog.newInstance();
                editCartItemDialog.setTargetFragment(fragment, ClientCodes.DELETE_ADDRESS_DIALOG);
                editCartItemDialog.show(fragment.getFragmentManager(), "dialog");
                Bundle bundle = new Bundle();
                bundle.putString("ADDRESS", new Gson().toJson(address));
                editCartItemDialog.setArguments(bundle);
            }
        });

        holder.addressLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = fragment.getContext();
                if (context != null) {
                    if (lastAddressSelected != null) {
                        lastAddressSelected.setBackground(null);
                        clearSelection();
                    }
                    address.selected = true;
                    Drawable background = ContextCompat.getDrawable(
                            fragment.getContext(),  R.drawable.selected_address_background);
                    holder.addressLayout.setBackground(background);
                    lastAddressSelected = holder.addressLayout;
                    lastAddressSelectedID = address.id_address;
                }
            }
        });

        if (position == 0 && lastAddressSelected == null && fragment.getContext() != null) {
            Drawable background = ContextCompat.getDrawable(
                    fragment.getContext(),  R.drawable.selected_address_background);
            holder.addressLayout.setBackground(background);
            address.selected = true;
            lastAddressSelected = holder.addressLayout;
            lastAddressSelectedID = address.id_address;
        }
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    private void clearSelection() {
        for (Address address : addresses)
            address.selected = false;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout addressLayout;
        TextView name;
        TextView address;
        TextView city;
        TextView email;
        TextView phone;
        RelativeLayout edit;
        RelativeLayout delete;
        TextView landmark;

        ViewHolder(View itemView) {
            super(itemView);
            addressLayout = itemView.findViewById(R.id.addressLayout);
            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
            city = itemView.findViewById(R.id.city);
            email = itemView.findViewById(R.id.email);
            phone = itemView.findViewById(R.id.phone);
            landmark = itemView.findViewById(R.id.landmark);
            edit = itemView.findViewById(R.id.edit);
            delete = itemView.findViewById(R.id.delete);
        }
    }



}
