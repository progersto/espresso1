package com.miracas.espresso.dialogs;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miracas.R;
import com.miracas.espresso.client.address.DeleteAddressClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.cart.DeleteCartItemClient;
import com.miracas.espresso.client.responses.Address;
import com.miracas.espresso.client.responses.CartItem;
import com.miracas.espresso.design.IOnRequestCompleted;

import static android.app.Activity.RESULT_OK;

public class DeleteAddressDialog extends DialogFragment implements IOnRequestCompleted {

    private View loading;

    public static DeleteAddressDialog newInstance() {
        return new DeleteAddressDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        View view = inflater.inflate(R.layout.dialog_delete_address, container, false);
        final IOnRequestCompleted fragment = this;
        loading = view.findViewById(R.id.loading);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String packet = bundle.getString("ADDRESS");
            final Address item = new Gson().fromJson(packet, Address.class);

            view.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loading.setVisibility(View.VISIBLE);
                    DeleteAddressClient client = new DeleteAddressClient(fragment, item);
                    client.execute();
                }
            });
        }

        view.findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

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

    @Override
    public void onRequestComplete(int clientCode, Object response) {
        if (clientCode == ClientCodes.DELETE_ADDRESS && response != null) {
            loading.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Address successfully removed",
                    Toast.LENGTH_SHORT).show();
            Fragment fragment = getTargetFragment();
            if (fragment != null)
                fragment.onActivityResult(getTargetRequestCode(), RESULT_OK, null);
            dismiss();
        }
    }
}
