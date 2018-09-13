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
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.cart.DeleteCartItemClient;
import com.miracas.espresso.client.responses.CartItem;
import com.miracas.espresso.design.IOnRequestCompleted;

import static android.app.Activity.RESULT_OK;

public class DeleteCartItemDialog extends DialogFragment implements IOnRequestCompleted {

    View loading;

    public static DeleteCartItemDialog newInstance() {
        return new DeleteCartItemDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        View view = inflater.inflate(R.layout.dialog_delete_cart_item, container, false);
        final IOnRequestCompleted fragment = this;
        loading = view.findViewById(R.id.loading);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String packet = bundle.getString("CART_ITEM");
            final CartItem item = new Gson().fromJson(packet, CartItem.class);

            String text = "Are you sure you want to remove " + item.product_name + " from your cart?";
            TextView productName = view.findViewById(R.id.productName);
            productName.setText(text);

            view.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    loading.setVisibility(View.VISIBLE);
                    DeleteCartItemClient client = new DeleteCartItemClient(fragment, item.id_order_detail);
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
        if (clientCode == ClientCodes.DELETE_CART_ITEM && response != null) {
            loading.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Product successfully removed from cart",
                    Toast.LENGTH_SHORT).show();
            Fragment fragment = getTargetFragment();
            if (fragment != null)
                fragment.onActivityResult(getTargetRequestCode(), RESULT_OK, null);
            dismiss();
        }
    }
}
