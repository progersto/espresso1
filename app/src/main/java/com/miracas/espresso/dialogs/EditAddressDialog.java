package com.miracas.espresso.dialogs;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miracas.R;
import com.miracas.espresso.client.address.AddAddressClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.address.EditAddressClient;
import com.miracas.espresso.client.responses.Address;
import com.miracas.espresso.design.IOnRequestCompleted;

import static android.app.Activity.RESULT_OK;


public class EditAddressDialog extends DialogFragment implements IOnRequestCompleted {

    public static final String ADDRESS = "ADDRESS";
    public static final String MODE = "MODE";

    private View loading;

    public enum MODE_TYPE {
        ADD, EDIT
    };

    private Address address;
    ViewHolder holder;

    public static EditAddressDialog newInstance() {
        return new EditAddressDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        View view = inflater.inflate(R.layout.dialog_edit_address, container, false);
        loading = view.findViewById(R.id.loading);
        holder = new ViewHolder(view);
        Bundle bundle = getArguments();
        if (bundle != null) {
            int mode = bundle.getInt(MODE);
            if (mode == MODE_TYPE.ADD.ordinal()) {
                address = new Address();
                initAddButton(view);

            } else if (mode == MODE_TYPE.EDIT.ordinal()) {
                String packet = bundle.getString(ADDRESS);
                address = new Gson().fromJson(packet, Address.class);
                setFields(view);
                initSaveButton(view);
            }

            setButtons(view);
        }
        return view;
    }

    @Override
    public void onResume() {
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = LinearLayoutCompat.LayoutParams.MATCH_PARENT;
        params.height = LinearLayoutCompat.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        super.onResume();
    }

    private void initAddButton(View view) {
        final IOnRequestCompleted fragment = this;
        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillAddressFromTextFields();
                if (validateFields()) {
                    loading.setVisibility(View.VISIBLE);
                    AddAddressClient client = new AddAddressClient(fragment, address);
                    client.execute();
                }
            }
        });
    }

    private void initSaveButton(View view) {
        final IOnRequestCompleted fragment = this;
        view.findViewById(R.id.save).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fillAddressFromTextFields();
                if (validateFields()) {
                    loading.setVisibility(View.VISIBLE);
                    EditAddressClient client = new EditAddressClient(fragment, address);
                    client.execute();
                }
            }
        });
    }

    private boolean validateFields() {
        String emailPattern= "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
        if (!address.email.matches(emailPattern)) {
            Toast.makeText(getContext(), "Please enter correct email address", Toast.LENGTH_LONG).show();
            return false;
        }

        if (address.name.isEmpty() || address.mobile_number.isEmpty() ||
                address.address.isEmpty() || address.landmark.isEmpty() || address.city.isEmpty() ||
                address.state.isEmpty() || address.pin_code.isEmpty() || address.email.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all the fields to proceed", Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    private void fillAddressFromTextFields() {
        address.name = holder.name.getText().toString();
        address.mobile_number = holder.phone.getText().toString();
        address.address = holder.address.getText().toString();
        address.landmark = holder.locality.getText().toString();
        address.city = holder.city.getText().toString();
        address.state = holder.state.getText().toString();
        address.pin_code = holder.pincode.getText().toString();
        address.email = holder.email.getText().toString();
    }

    private void setFields(View view) {
        if (view != null) {
            holder.name.setText(address.name);
            holder.address.setText(address.address);
            holder.city.setText(address.city);
            holder.state.setText(address.address);
            holder.phone.setText(address.mobile_number);
            holder.email.setText(address.email);
            holder.locality.setText(address.landmark);
            holder.pincode.setText(address.pin_code);
        }
    }

    private void setButtons(View view) {
        if (view != null) {
            holder.cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dismiss();
                }
            });
        }
    }

    class ViewHolder {
        TextView name;
        TextView address;
        TextView city;
        TextView state;
        TextView phone;
        TextView email;
        TextView locality;
        TextView pincode;
        ImageView cancel;

        ViewHolder(View view) {
            name = view.findViewById(R.id.name);
            address = view.findViewById(R.id.address);
            city = view.findViewById(R.id.city);
            state = view.findViewById(R.id.state);
            phone = view.findViewById(R.id.phone);
            email = view.findViewById(R.id.email);
            locality = view.findViewById(R.id.locality);
            pincode = view.findViewById(R.id.pinCode);
            cancel = view.findViewById(R.id.close);
        }
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {
        if (clientCode == ClientCodes.ADD_ADDRESS  || clientCode == ClientCodes.EDIT_ADDRESS) {
            loading.setVisibility(View.GONE);
            Fragment fragment = getTargetFragment();
            if (fragment != null) {
                Intent intent = new Intent();
                intent.putExtra("ADDRESS", new Gson().toJson(address));
                fragment.onActivityResult(getTargetRequestCode(), RESULT_OK, intent);
            }
            dismiss();
        }
    }

}
