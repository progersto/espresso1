package com.miracas.espresso.dialogs;


import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miracas.R;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.cart.EditCartItemClient;
import com.miracas.espresso.client.GetProductOptionDetailsClient;
import com.miracas.espresso.client.responses.CartItem;
import com.miracas.espresso.design.IOnRequestCompleted;

import static android.app.Activity.RESULT_OK;
import static com.miracas.espresso.config.Settings.DEFAULT_COLOR_CODE;
import static com.miracas.espresso.config.Settings.SELECTED_COLOR_CODE;


public class EditCartItemDialog extends DialogFragment implements IOnRequestCompleted {

    CartItem item;

    View selectedColorView;
    View selectedSizeView;
    View loading;

    public static EditCartItemDialog newInstance() {
        return new EditCartItemDialog();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getDialog().getWindow() != null) {
            getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        }
        View view = inflater.inflate(R.layout.dialog_edit_cart_item, container, false);
        loading = view.findViewById(R.id.loading);

        Bundle bundle = getArguments();

        if (bundle != null) {
            String packet = bundle.getString("CART_ITEM");
            item = new Gson().fromJson(packet, CartItem.class);

            GetProductOptionDetailsClient client = new GetProductOptionDetailsClient(
                    this, item.id_product_prestashop);
            client.execute();
        }

        view.findViewById(R.id.save).setOnClickListener(view1 -> save());
        view.findViewById(R.id.close).setOnClickListener(view2 -> dismiss());

        return view;
    }

    @Override
    public void onResume() {
        Window window = getDialog().getWindow();
        if (window != null) {
            ViewGroup.LayoutParams params = window.getAttributes();
            params.width = LinearLayoutCompat.LayoutParams.MATCH_PARENT;
            params.height = LinearLayoutCompat.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
        }
        super.onResume();
    }

    private void save() {
        loading.setVisibility(View.VISIBLE);
        EditCartItemClient client = new EditCartItemClient(this, item);
        client.execute();
    }

    private void parseOptions(GetProductOptionDetailsClient.Response response) {
        View view = getView();
        if (view == null) return;

        LinearLayout colorsLayout = view.findViewById(R.id.colorsLayout);
        LinearLayout sizesLayout = view.findViewById(R.id.sizesLayout);

        if (item.color != null && !item.color.isEmpty() && !item.color.equals("NA")
                && response.colors != null && !response.colors.isEmpty()) {

            for(String color: response.colors) {
                View separator = LayoutInflater.from(getContext()).inflate(
                        R.layout.separator, colorsLayout, false);

                final View colorView = LayoutInflater.from(getContext()).inflate(
                        R.layout.dialog_edit_cart_color_row, colorsLayout, false);
                TextView colorValue = colorView.findViewById(R.id.colorValue);
                ImageView colorImageView = colorView.findViewById(R.id.colorCircle);
                GradientDrawable bgShape = (GradientDrawable)colorImageView.getBackground();

                try {
                    bgShape.setColor(Color.parseColor(color));
                } catch (IllegalArgumentException ignored) {}

                colorValue.setText(color);
                colorsLayout.addView(colorView);
                colorsLayout.addView(separator);

                if (color.equals(item.color)) {
                    selectedColorView = colorView;
                    colorView.findViewById(R.id.check).setVisibility(View.VISIBLE);
                    colorView.setBackgroundColor(Color.parseColor(SELECTED_COLOR_CODE));
                }

                colorView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedColorView != null) {
                            selectedColorView.findViewById(R.id.check).setVisibility(View.INVISIBLE);
                            selectedColorView.setBackgroundColor(Color.parseColor(DEFAULT_COLOR_CODE));
                        }
                        colorView.findViewById(R.id.check).setVisibility(View.VISIBLE);
                        colorView.setBackgroundColor(Color.parseColor(SELECTED_COLOR_CODE));
                        selectedColorView = colorView;
                        item.color = ((TextView) colorView.findViewById(R.id.colorValue)).getText().toString();
                    }
                });
            }
        } else {
            colorsLayout.setVisibility(View.GONE);
        }

        if (item.size != null && !item.size.isEmpty() && !item.size.equals("NA")
                && response.sizes != null && !response.sizes.isEmpty()) {

            for(String size: response.sizes) {
                View separator = LayoutInflater.from(getContext()).inflate(
                        R.layout.separator, sizesLayout, false);

                final View sizeView = LayoutInflater.from(getContext()).inflate(
                        R.layout.dialog_edit_cart_size_row, sizesLayout, false);
                TextView sizeValue = sizeView.findViewById(R.id.sizeValue);
                sizeValue.setText(size);
                sizesLayout.addView(sizeView);
                sizesLayout.addView(separator);

                if (size.equals(item.size)) {
                    selectedSizeView = sizeView;
                    sizeView.findViewById(R.id.check).setVisibility(View.VISIBLE);
                    sizeView.setBackgroundColor(Color.parseColor(SELECTED_COLOR_CODE));
                }

                sizeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (selectedSizeView != null) {
                            selectedSizeView.findViewById(R.id.check).setVisibility(View.INVISIBLE);
                            selectedSizeView.setBackgroundColor(Color.parseColor(DEFAULT_COLOR_CODE));
                        }
                        sizeView.findViewById(R.id.check).setVisibility(View.VISIBLE);
                        sizeView.setBackgroundColor(Color.parseColor(SELECTED_COLOR_CODE));
                        selectedSizeView = sizeView;
                        item.size = ((TextView) sizeView.findViewById(R.id.sizeValue)).getText().toString();
                    }
                });
            }

        } else {
            sizesLayout.setVisibility(View.GONE);
        }

    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {
        if (clientCode == ClientCodes.EDIT_CART_ITEM && response != null) {
            loading.setVisibility(View.GONE);
            boolean added = (boolean) response;
            if (added) {
                Toast.makeText(getContext(), "Order modified successfully", Toast.LENGTH_SHORT).show();
                Fragment fragment = getTargetFragment();
                if (fragment != null)
                    fragment.onActivityResult(getTargetRequestCode(), RESULT_OK, null);
                dismiss();
            } else {
                Toast.makeText(getContext(), "Some error occurred. Please try again later",
                        Toast.LENGTH_SHORT).show();
            }

        } else if (clientCode == ClientCodes.GET_PRODUCT_OPTIONS && response != null && getView() != null) {
            parseOptions((GetProductOptionDetailsClient.Response) response);

        }
    }
}
