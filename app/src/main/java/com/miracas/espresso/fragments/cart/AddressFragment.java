package com.miracas.espresso.fragments.cart;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.miracas.R;
import com.miracas.espresso.adapters.AddressAdapter;
import com.miracas.espresso.client.address.SelectAddressForOrderClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.address.GetAddressesClient;
import com.miracas.espresso.client.responses.Address;
import com.miracas.espresso.design.IOnRequestCompleted;
import com.miracas.espresso.dialogs.EditAddressDialog;
import com.miracas.espresso.fragments.BaseFragment;

import java.util.ArrayList;
import java.util.List;

public class AddressFragment extends BaseFragment {

    private Fragment fragment;
    private List<Address> addresses;
    private AddressAdapter adapter;
    private String cartTotalValue;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view = inflater.inflate(R.layout.fragment_address, container, false);
        onCreateToolbar(view, "Addresses", false);

        Bundle bundle = getArguments();
        if (bundle != null) {
            TextView cartTotal = view.findViewById(R.id.cartTotal);
            cartTotalValue = bundle.getString("CART_TOTAL");
            cartTotal.setText(cartTotalValue);
        }

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        onCreateToolbar(getView(), "Addresses", false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragment = this;
        getAddresses();
        initButtons(view);
    }

    private void getAddresses() {
        View view = getView();
        if (view !=  null) {
            addresses = new ArrayList<>();
            adapter = new AddressAdapter(this, addresses);
            RecyclerView listView = view.findViewById(R.id.address_list);
            listView.setLayoutManager(new LinearLayoutManager(getContext()));
            listView.setAdapter(adapter);
            GetAddressesClient client = new GetAddressesClient(this);
            client.execute();
        }
    }

    private void initButtons(View view) {
        ImageView addButton = view.findViewById(R.id.addAddress);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment editAddressFragment = EditAddressDialog.newInstance();
                editAddressFragment.setTargetFragment(fragment, ClientCodes.EDIT_ADDRESS_DIALOG);
                editAddressFragment.show(fragment.getFragmentManager(), "dialog");
                Bundle bundle = new Bundle();
                bundle.putInt(EditAddressDialog.MODE, EditAddressDialog.MODE_TYPE.ADD.ordinal());
                editAddressFragment.setArguments(bundle);
            }
        });

        Button continueButton = view.findViewById(R.id.btn_continue);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress(true);
                SelectAddressForOrderClient client = new SelectAddressForOrderClient(
                        (IOnRequestCompleted) fragment, adapter.getLastAddressSelectedID());
                client.execute();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ClientCodes.EDIT_ADDRESS_DIALOG ||
                requestCode == ClientCodes.DELETE_ADDRESS_DIALOG ) {
            showProgress(true);
            GetAddressesClient client = new GetAddressesClient(this);
            client.execute();
        }
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {
        if (clientCode == ClientCodes.GET_ADDRESSES) {
            GetAddressesClient.Response addressResponse = (GetAddressesClient.Response) response;
            View view = getView();
            if (response != null && view != null) {
                if (addressResponse.addresses != null) {
                    addresses.clear();
                    addresses.addAll(addressResponse.addresses);
                    adapter.notifyDataSetChanged();
                    if (!addressResponse.addresses.isEmpty())
                        view.findViewById(R.id.bottomNav).setVisibility(View.VISIBLE);
                    else
                        view.findViewById(R.id.bottomNav).setVisibility(View.INVISIBLE);
                }
                showProgress(false);
            }

        } else if (clientCode == ClientCodes.DELETE_ADDRESS) {
            GetAddressesClient client = new GetAddressesClient(this);
            client.execute();

        } else if (clientCode == ClientCodes.SELECT_ADDRESS_FOR_ORDER) {
            if (response != null) {
                FragmentManager fragmentManager = getFragmentManager();
                if (fragmentManager != null) {
                    Fragment fragment = new PaymentFragment();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.product_view_exit, R.anim.product_view);
                    transaction.add(R.id.content_frame, fragment).addToBackStack(null).commit();
                    Bundle bundle = new Bundle();
                    bundle.putString("CART_TOTAL", cartTotalValue);
                    fragment.setArguments(bundle);
                }
            }
            showProgress(false);
        }
    }
}
