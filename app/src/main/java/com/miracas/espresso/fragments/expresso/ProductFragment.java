package com.miracas.espresso.fragments.expresso;


import android.app.Activity;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.miracas.espresso.activity.MainActivity;
import com.miracas.R;
import com.miracas.espresso.adapters.CarouselAdapter;
import com.miracas.espresso.client.cart.AddToCartClient;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.responses.Product;
import com.miracas.espresso.config.Settings;
import com.miracas.espresso.fragments.BaseFragment;
import com.miracas.espresso.listeners.RecyclerItemClickListener;
import com.miracas.espresso.utils.FacebookManager;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

public class ProductFragment extends BaseFragment {

    private boolean colorOptionSelected = false;
    private boolean sizeOptionSelected = false;
    private boolean addedToCart = false;

    View selectedSizeView;
    View selectedColorView;

    String selectedColor;
    String selectedSize;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view = inflater.inflate(R.layout.fragment_product, container, false);

        if (getArguments() != null) {
            String productString = getArguments().getString("PRODUCT");
            boolean showCart = getArguments().getBoolean("SHOW_CART");

            final Product product = new Gson().fromJson(productString, Product.class);

            if (!showCart) {
                view.findViewById(R.id.bottomNav).setVisibility(View.GONE);
            }

            RecyclerView carousel = view.findViewById(R.id.carousel);
            CarouselAdapter carouselAdapter = new CarouselAdapter(this, product.popup_images);
            carousel.setLayoutManager(new LinearLayoutManager(
                    getContext(), LinearLayoutManager.HORIZONTAL, false));
            carousel.setAdapter(carouselAdapter);

            carousel.addOnItemTouchListener(
                    new RecyclerItemClickListener(getContext(), carousel ,new RecyclerItemClickListener.OnItemClickListener() {
                        @Override public void onItemClick(View view, int position) {
                            FragmentManager fragmentManager = getFragmentManager();
                            if (fragmentManager != null) {
                                Bundle bundle = new Bundle();
                                bundle.putString("IMAGES", new Gson().toJson(product.popup_images));
                                bundle.putInt("POSITION", position);
                                Fragment fragment = new ZoomedProductFragment();
                                fragment.setArguments(bundle);
                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.setCustomAnimations(R.anim.product_view_exit, R.anim.product_view);
                                transaction.add(R.id.content_frame, fragment).addToBackStack(null).commit();
                            }
                        }

                        @Override public void onLongItemClick(View view, int position) {
                            // Do nothing
                        }
                    })
            );

            TextView title = view.findViewById(R.id.title);
            TextView priceOld = view.findViewById(R.id.priceOld);
            TextView priceNew = view.findViewById(R.id.priceNew);

            NumberFormat nf = NumberFormat.getInstance(Locale.US);
            double price, discount;

            try {
                price = nf.parse(product.price).doubleValue();
                discount = nf.parse(product.discount).doubleValue();
            } catch (ParseException e) {
                price = 0;
                discount = 0;
            }

            String priceOldValue = "₹" + product.price;
            String priceNewValue = "₹" + String.valueOf(Math.round(price - discount));

            priceOld.setText(priceOldValue);
            priceNew.setText(priceNewValue);

            priceOld.setPaintFlags(priceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            title.setText(product.name);

            if (priceNewValue.equals(priceOldValue)) {
                priceOld.setVisibility(View.GONE);
            }

            GridLayout colorsLayout = view.findViewById(R.id.colorsLayout);
            LinearLayout colorsParentLayout = view.findViewById(R.id.colorsParentLayout);
            GridLayout sizesLayout = view.findViewById(R.id.sizesLayout);
            LinearLayout sizesParentLayout = view.findViewById(R.id.sizesParentLayout);

            if (product.colors.size() == 0) {
                colorsParentLayout.setVisibility(View.GONE);
                view.findViewById(R.id.colorDivider).setVisibility(View.GONE);
                colorOptionSelected = true;

            } else {
                for(String color: product.colors) {
                    final View colorView = LayoutInflater.from(getContext()).inflate(
                            R.layout.color_item, colorsLayout, false);
                    TextView colorValue = colorView.findViewById(R.id.colorValue);
                    ImageView colorImageView = colorView.findViewById(R.id.color);
                    GradientDrawable bgShape = (GradientDrawable)colorImageView.getBackground();

                    try {
                        bgShape.setColor(Color.parseColor(color));
                    } catch (IllegalArgumentException ignored) {}

                    colorValue.setText(color);
                    colorsLayout.addView(colorView);

                    colorView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (selectedColorView != null) {
                                selectedColorView.findViewById(R.id.background).setVisibility(View.INVISIBLE);
                            }
                            colorView.findViewById(R.id.background).setVisibility(View.VISIBLE);
                            selectedColorView = colorView;
                            selectedColor = ((TextView) colorView.findViewById(R.id.colorValue)).getText().toString();
                            colorOptionSelected = true;
                        }
                    });
                }
            }

            if (product.sizes.size() == 0) {
                sizesParentLayout.setVisibility(View.GONE);
                view.findViewById(R.id.sizeDivider).setVisibility(View.GONE);
                sizeOptionSelected = true;

            } else {
                for(String size: product.sizes) {
                    final View sizeView = LayoutInflater.from(getContext()).inflate(
                            R.layout.size_item, sizesLayout, false);
                    TextView sizeTextView = sizeView.findViewById(R.id.size);
                    TextView sizeTextDescription = sizeView.findViewById(R.id.sizeValue);
                    String sizeShorthand = "?";

                    try {
                        if (Settings.SIZE_MAP.containsKey(size))
                            sizeShorthand = Settings.SIZE_MAP.get(size);
                        else
                            sizeShorthand = size;
                    } catch (Exception ignored) {}

                    sizeTextView.setText(sizeShorthand);
                    sizeTextDescription.setText(size);
                    sizesLayout.addView(sizeView);

                    sizeView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (selectedSizeView != null) {
                                selectedSizeView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_size_item));
                            }
                            sizeView.setBackground(ContextCompat.getDrawable(getContext(), R.drawable.circle_size_item_selected));
                            selectedSizeView = sizeView;
                            selectedSize = ((TextView) sizeView.findViewById(R.id.sizeValue)).getText().toString();
                            sizeOptionSelected = true;
                        }
                    });
                }
            }

            final NestedScrollView scrollView = view.findViewById(R.id.scrollView);
            final View details = view.findViewById(R.id.details);

            final Button addToExpresso = view.findViewById(R.id.addToExpresso);
            double finalDiscount = discount;
            addToExpresso.setOnClickListener(view1 -> {
            if (!addedToCart) {
                if (colorOptionSelected && sizeOptionSelected) {

                    FacebookManager manager = new FacebookManager(getActivity());
                    if (!manager.checkLoginStatus()) {
                        MainActivity activity = (MainActivity) getActivity();
                        if (activity != null)
                            activity.showLoginDialog();
                        return;
                    }

                    showProgress(true);
                    AddToCartClient client = new AddToCartClient(ProductFragment.this,
                            selectedColor, "1", selectedSize, product.id_product_prestashop,
                            product.id_product, product.price, String.valueOf(finalDiscount));
                    client.execute();

                } else {
                    String message = "Please select ";
                    if (!colorOptionSelected && !sizeOptionSelected)
                        message += "color and size";
                    else if (!colorOptionSelected)
                        message += "color";
                    else
                        message += "size";
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    int top = details.getTop();
                    scrollView.smoothScrollTo(0, top);
                }

            } else {
                MainActivity activity = (MainActivity) getActivity();
                if (activity != null) {
                    activity.switchFragment(R.id.nav_cart, true,null);
                }
            }
            });

            view.findViewById(R.id.backButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Activity activity = getActivity();
                    if (activity != null)
                        activity.onBackPressed();
                }
            });

        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress(false);
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {
        View view = getView();

        if (clientCode == ClientCodes.ADD_TO_CART && response != null && view != null) {
            boolean added = (boolean) response;
            if (added) {
                Toast.makeText(getContext(), "Added To Cart", Toast.LENGTH_SHORT).show();
                Button addToExpresso = view.findViewById(R.id.addToExpresso);
                addToExpresso.setText("GO TO CART");
                addedToCart = true;
            } else {
                Toast.makeText(getContext(), "Some error occurred. Please try again later",
                        Toast.LENGTH_SHORT).show();
            }
            showProgress(false);
        }
    }

}
