package com.miracas.espresso.adapters;


import android.content.Intent;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miracas.R;
import com.miracas.espresso.client.AddProductToExpressoClient;
import com.miracas.espresso.client.ImageDownloader;
import com.miracas.espresso.client.responses.ActiveKeyword;
import com.miracas.espresso.client.responses.Product;
import com.miracas.espresso.client.shares.ProductSharedClient;
import com.miracas.espresso.fragments.home.DirectPurchaseFragment;
import com.miracas.espresso.fragments.home.HomePageFragment;
import com.miracas.espresso.utils.FacebookManager;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;
import java.util.UUID;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {

    private static final int VIEW_TYPE_BRAND_LINK = 100;
    private static final int VIEW_TYPE_PRODUCT = 101;

    private HomePageFragment homePageFragment;

    private List<Product> products;
    private List<String> bookedItems;
    private ActiveKeyword keyword;

    private String keyword_id;
    private double invertedDiscount;

    public ProductAdapter(HomePageFragment homePageFragment, List<Product> values,
                          String keyword_id, List<String> bookedItems,
                          double invertedDiscount, ActiveKeyword keyword) {
        super();
        this.homePageFragment = homePageFragment;
        this.products = values;
        this.keyword_id = keyword_id;
        this.bookedItems = bookedItems;
        this.invertedDiscount = invertedDiscount;
        this.keyword = keyword;
    }

    public void setInvertedDiscount(double invertedDiscount) {
        this.invertedDiscount = invertedDiscount;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(homePageFragment.getContext());
        if (viewType == VIEW_TYPE_BRAND_LINK) {
            View view = inflater.inflate(R.layout.list_item_product_brand_link, parent, false);
            return new BrandLinkViewHolder(view);
        }
        else {
            View view = inflater.inflate(R.layout.list_item_product, parent, false);
            return new ProductViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        final Product product = products.get(position);

        if (product.isBrandLink) {
            BrandLinkViewHolder holder = (BrandLinkViewHolder) viewHolder;
            holder.link.setOnClickListener(view -> {
                FragmentManager fragmentManager = homePageFragment.getFragmentManager();
                if (fragmentManager != null) {
                    Bundle bundle = new Bundle();
                    bundle.putString("KEYWORD", new Gson().toJson(keyword));
                    Fragment fragment = new DirectPurchaseFragment();
                    fragment.setArguments(bundle);
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.setCustomAnimations(R.anim.product_view_exit, R.anim.product_view);
                    transaction.add(R.id.content_frame, fragment).addToBackStack(null).commit();
                }
            });

        } else {
            ProductViewHolder holder = (ProductViewHolder) viewHolder;
            if (product.isSectionImage) {
                holder.productLayout.setVisibility(View.VISIBLE);
                holder.sectionImage.setVisibility(View.VISIBLE);
                holder.titleLayout.setVisibility(View.GONE);
                holder.carousel.setVisibility(View.GONE);
                try {
                    Picasso.with(homePageFragment.getContext())
                            .load(product.img_url)
                            .centerCrop()
                            .fit()
                            .priority(Picasso.Priority.HIGH)
                            .into(holder.sectionImage);
                } catch (Exception ignored) {
                }

            } else {
                holder.productLayout.setVisibility(View.VISIBLE);
                holder.sectionImage.setVisibility(View.GONE);
                holder.titleLayout.setVisibility(View.VISIBLE);
                holder.carousel.setVisibility(View.VISIBLE);

                CarouselAdapter carouselAdapter = new CarouselAdapter(
                        homePageFragment, product.popup_images);
                final LinearLayoutManager manager = new LinearLayoutManager(
                        homePageFragment.getContext(), LinearLayoutManager.HORIZONTAL, false);
                holder.carousel.setLayoutManager(manager);

                String priceNew = "₹" + String.valueOf(Math.round(
                        Float.valueOf(product.price) * invertedDiscount / 100));
                String priceOld = "₹" + product.price;

                holder.carousel.setAdapter(carouselAdapter);
                holder.title.setText(product.name);
                holder.price.setText(priceNew);
                holder.priceOld.setText(priceOld);
                holder.priceOld.setPaintFlags(holder.priceOld.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

                boolean booked = false;
                for (String i : bookedItems)
                    if (i.equals(product.id_product)) {
                        booked = true;
                        break;
                    }

                if (product.addedToPot || booked)
                    holder.productFab.setVisibility(View.INVISIBLE);
                else
                    holder.productFab.setVisibility(View.VISIBLE);

                holder.productFab.setOnClickListener(view -> {
                    FacebookManager manager1 = new FacebookManager(homePageFragment.getActivity());
                    if (!manager1.checkLoginStatus()) {
                        homePageFragment.showLogin();
                        return;
                    }

                    AddProductToExpressoClient client = new AddProductToExpressoClient(
                            homePageFragment, product, keyword_id);
                    client.execute();
                    int[] position1 = new int[2];
                    holder.productFab.getLocationInWindow(position1);
                    homePageFragment.animateProductFab(position1[0], position1[1] - 50);
                    holder.productFab.setVisibility(View.INVISIBLE);
                    product.addedToPot = true;
                });

                holder.share.setOnClickListener(view -> {

                    String shareID = UUID.randomUUID().toString().substring(0, 20);

                    ProductSharedClient client = new ProductSharedClient(homePageFragment,
                            shareID, product.id_product);
                    client.execute();

                    String link = "http://www.miracas.com/push?id_product=" + product.id_product
                            + "&id_product_prestashop=" + product.id_product_prestashop
                            + "&id_keyword=" + keyword_id + "&share_id=" + shareID;
                    String message = "Check out this product from Miracas - " + product.name + ". " + link;

                    File directory = new File(homePageFragment.getContext().getFilesDir(), "share");
                    directory.mkdirs();
                    ImageDownloader downloader = new ImageDownloader(
                            homePageFragment, product.img_url, message, directory);
                    downloader.execute();
                    homePageFragment.showProgress(true);

                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position != -1) {
            Product product = products.get(position);
            if (product.isBrandLink) return VIEW_TYPE_BRAND_LINK;
            return VIEW_TYPE_PRODUCT;
        }
        return super.getItemViewType(position);
    }

    class ProductViewHolder extends ViewHolder {
        CardView productLayout;

        RelativeLayout titleLayout;
        ImageView sectionImage;
        RecyclerView carousel;
        TextView title;
        ImageView productFab;
        TextView price;
        TextView priceOld;
        View share;

        ProductViewHolder(View itemView) {
            super(itemView);
            productLayout = itemView.findViewById(R.id.productLayout);
            sectionImage = itemView.findViewById(R.id.sectionImage);
            titleLayout = itemView.findViewById(R.id.titleLayout);
            carousel = itemView.findViewById(R.id.carousel);
            title = itemView.findViewById(R.id.title);
            productFab = itemView.findViewById(R.id.productFab);
            price = itemView.findViewById(R.id.price);
            priceOld = itemView.findViewById(R.id.priceOld);
            share = itemView.findViewById(R.id.share);
        }
    }

    class BrandLinkViewHolder extends ViewHolder {
        View link;
        public BrandLinkViewHolder(View itemView) {
            super(itemView);
            link = itemView.findViewById(R.id.link);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }

}
