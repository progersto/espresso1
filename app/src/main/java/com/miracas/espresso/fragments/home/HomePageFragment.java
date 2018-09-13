package com.miracas.espresso.fragments.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.miracas.espresso.activity.MainActivity;
import com.miracas.R;
import com.miracas.espresso.adapters.ProductAdapter;
import com.miracas.espresso.client.AddProductToExpressoClient;
import com.miracas.espresso.client.ImageDownloader;
import com.miracas.espresso.client.base.ClientCodes;
import com.miracas.espresso.client.GetBookedItemsClient;
import com.miracas.espresso.client.ProductsFeedClient;
import com.miracas.espresso.client.responses.ActiveKeyword;
import com.miracas.espresso.client.responses.Product;
import com.miracas.espresso.config.Settings;
import com.miracas.espresso.fragments.BaseFragment;
import com.miracas.espresso.fragments.expresso.MyExpressoFragment;
import com.miracas.espresso.layouts.CustomLinearLayoutManager;
import com.miracas.espresso.utils.FacebookManager;
import com.miracas.espresso.utils.SharedStorageHelper;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION;
import static android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION;

public class HomePageFragment extends BaseFragment {

    private List<Product> productList;
    private List<String> bookedItems;
    private Product product;

    private RecyclerView listView;
    private ProductAdapter adapter;
    private CustomLinearLayoutManager customLinearLayoutManager;

    private TextView yourBookings;
    private TextView bookingsRemaining;
    private TextView runningDiscount;

    private View tint;
    private View tooltip;
    //private View tooltipBook;
    private View discountParent;
    private View totalBookingsParent;

    private int yourBookingsCount = 0;

    private ActiveKeyword activeKeyword;
    private int currentPage;

    private boolean productsRequested = false;
    private boolean userLoggedInWhileLoading;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view = inflater.inflate(R.layout.fragment_home_page, container, false);

        if (getArguments() != null) {
            String keywordString = getArguments().getString("KEYWORD");
            String productString = getArguments().getString("PRODUCT");

            activeKeyword = new Gson().fromJson(keywordString, ActiveKeyword.class);
            if (productString != null && !productString.isEmpty())
                product = new Gson().fromJson(productString, Product.class);

            onCreateToolbar(view, activeKeyword.keyword, false);

            bookingsRemaining = view.findViewById(R.id.bookingsRemaining);
            runningDiscount = view.findViewById(R.id.runningDiscount);
            yourBookings = view.findViewById(R.id.yourBookings);

            bookingsRemaining.setText(getBookingsPercent());

            String runningDiscountValue = String.valueOf(activeKeyword.current_discount) + "%";
            runningDiscount.setText(runningDiscountValue);

            yourBookings.setText("0");
        }

        totalBookingsParent = view.findViewById(R.id.totalBookingsParent);
        discountParent = view.findViewById(R.id.discountParent);
        tint = view.findViewById(R.id.tint);
        tooltip = view.findViewById(R.id.tooltip);
        //tooltipBook = view.findViewById(R.id.tooltipBook);

        userLoggedInWhileLoading = false;

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        onCreateToolbar(getView() , activeKeyword.keyword, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Picasso.with(getContext()).cancelTag(Settings.CAROUSEL_TAG);
        if (listView != null)
            listView.clearOnScrollListeners();
        if (productList != null) {
            productList.clear();
            productList = null;
            listView = null;
        }
    }

    private String getBookingsPercent() {
        int bookingsPercent;
        if (activeKeyword.booking_threshold == 0.0F) {
            bookingsPercent = 0;
        } else {
            bookingsPercent = ( activeKeyword.booking_count * 100 ) / activeKeyword.booking_threshold;
        }
        return String.valueOf(Math.round(bookingsPercent)) + "%";
    }

    public void userLoggedIn() {
        showProgress(true);
        userLoggedInWhileLoading = true;
        fetchBookedItems();
    }

    private void setHeaderValues(String discount, String bookings) {
        activeKeyword.booking_count = Integer.valueOf(bookings);
        yourBookings.setText(String.valueOf(yourBookingsCount));
        bookingsRemaining.setText(getBookingsPercent());
        runningDiscount.setText(discount + "%");
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgress(false);
        fetchBookedItems();
        initProductsFeed();
        initClickables();
    }

    private void fetchBookedItems() {
        FacebookManager manager = new FacebookManager(getActivity());
        if (manager.checkLoginStatus()) {
            GetBookedItemsClient client = new GetBookedItemsClient(this, activeKeyword.id_keyword);
            client.execute();
        }
    }

    public void animateProductFab(int fromX, int fromY) {
        new SharedStorageHelper(getActivity()).setUserAddedProduct();
        View view = getView();
        if (view != null) {

            final boolean wasFirstProduct = (yourBookingsCount == 0);
            int[] position = new int[2];
            yourBookings.getLocationInWindow(position);

            final ImageView productFab = view.findViewById(R.id.productFabMove);
            productFab.setVisibility(View.VISIBLE);

            AnimationSet animationSet = new AnimationSet(true);
            animationSet.setDuration(1000);
            animationSet.setInterpolator(new DecelerateInterpolator());
            animationSet.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    productFab.setVisibility(View.INVISIBLE);
                    MainActivity activity = (MainActivity) getActivity();
                    if (activity != null)
                        activity.setMenuBrewingCount(true);
                    if (wasFirstProduct)
                        showTint();
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });

            TranslateAnimation translateAnimation = new TranslateAnimation(
                    fromX, position[0], fromY, 0 );

            ScaleAnimation scaleAnimation = new ScaleAnimation(1.0F, 0.25F, 1.0F, 0.25F);
            animationSet.addAnimation(scaleAnimation);
            animationSet.addAnimation(translateAnimation);
            productFab.startAnimation(animationSet);
        }
    }

    public void showTint() {
        tint.setVisibility(View.VISIBLE);
        tooltip.setVisibility(View.VISIBLE);
        discountParent.setAlpha(0.5F);
        totalBookingsParent.setAlpha(0.5F);
        yourBookings.bringToFront();
        tint.setOnClickListener(view -> hideTint());
    }

    public void hideTint() {
        tint.setVisibility(View.GONE);
        tooltip.setVisibility(View.GONE);
        discountParent.setAlpha(1F);
        totalBookingsParent.setAlpha(1F);
        tint.setOnClickListener(null);
    }

    private void initClickables() {
        View view = getView();
        if (view != null) {
            view.findViewById(R.id.myBookings).setOnClickListener(view1 -> {
                FacebookManager manager = new FacebookManager(getActivity());
                if (manager.checkLoginStatus()) {
                    FragmentManager fragmentManager = getFragmentManager();
                    if (fragmentManager != null) {
                        Activity activity = getActivity();
                        if (activity != null)
                            activity.onBackPressed();
                        Bundle bundle = new Bundle();
                        bundle.putBoolean("BREWING", true);
                        Fragment fragment = new MyExpressoFragment();
                        fragment.setArguments(bundle);
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.setCustomAnimations(R.anim.product_view_exit, R.anim.product_view);
                        transaction.add(R.id.content_frame, fragment).addToBackStack(null).commit();
                    }
                } else {
                    showLogin();
                }
            });
        }
    }

    private void initProductsFeed() {

        Product productHeader = new Product();
        productHeader.isSectionImage = true;
        productHeader.img_url = activeKeyword.image;

        currentPage = 0;
        productList = new ArrayList<>();
        productList.add(productHeader);

        if (product != null)
            productList.add(product);

        bookedItems = new ArrayList<>();

        final View view = getView();

        if (view != null) {
            adapter = new ProductAdapter(this, productList,
                    activeKeyword.id_keyword, bookedItems,
                    100.0F - activeKeyword.discount, activeKeyword);
            listView = view.findViewById(R.id.products_list);
            final View discountHeader = view.findViewById(R.id.discountHeader);
            final View mainContentProducts = view.findViewById(R.id.mainContentProducts);

            TranslateAnimation translateAnimation = new TranslateAnimation(
                    0, 0, 0, -250);
            translateAnimation.setFillAfter(true);
            translateAnimation.setInterpolator(new DecelerateInterpolator());
            translateAnimation.setDuration(1);
            //translateAnimation.setStartOffset(500);
            mainContentProducts.startAnimation(translateAnimation);

            AlphaAnimation animation = new AlphaAnimation(0.0F, 0.0F);
            animation.setDuration(1);
            animation.setFillAfter(true);
            discountHeader.startAnimation(animation);

            customLinearLayoutManager = new CustomLinearLayoutManager(getContext());
            listView.setLayoutManager(customLinearLayoutManager);
            listView.setAdapter(adapter);

            listView.clearOnScrollListeners();
            listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                boolean hidden = false;
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    int position = customLinearLayoutManager.findLastVisibleItemPosition();
                    int lastPosition = customLinearLayoutManager.findLastVisibleItemPosition();

                    // Hide curtain when start scrolling
                    if (!hidden && position > 1) {
                        AlphaAnimation animation = new AlphaAnimation(0.0F, 1.0F);
                        animation.setDuration(750);
                        animation.setStartOffset(300);
                        animation.setFillAfter(true);
                        discountHeader.startAnimation(animation);
                        view.findViewById(R.id.parentHeader).setVisibility(View.GONE);
                        TranslateAnimation translateAnimation = new TranslateAnimation(
                                0, 0, -250, 0);
                        translateAnimation.setFillAfter(true);
                        translateAnimation.setDuration(1000);
                        mainContentProducts.startAnimation(translateAnimation);
                        hidden = true;
                    }

                    // Scroll effect for product carousel
                    if (position > 0) {
                        View child = customLinearLayoutManager.getChildAt(1);
                        if (child != null) {
                            RecyclerView carousel = child.findViewById(R.id.carousel);
                            if (carousel != null)
                                carousel.scrollBy(2, 0);
                        }
                    }

                    // Paginate products
                    if (!productsRequested && lastPosition == productList.size() - 1) {
                        appendProducts();
                    }
                }
            });

            appendProducts();
        }
    }

    private void appendProducts() {
        ProductsFeedClient client = new ProductsFeedClient(
                this, activeKeyword.keyword, activeKeyword.genders, currentPage);
        client.execute();
        currentPage += 10;
        productsRequested = true;
    }

    public void showLogin() {
        Activity activity = getActivity();
        if (activity != null) {
            ((MainActivity) activity).showLoginDialog();
        }
    }

    @Override
    public void onRequestComplete(int code, Object response) {
        View view = getView();
        if (view != null) {

            if (code == ClientCodes.PRODUCT_FEED_CLIENT) {
                List<Product> productFeedResponse = (List<Product>) response;
                if (productFeedResponse != null && !productFeedResponse.isEmpty()) {
                    productList.addAll(productFeedResponse);
                    adapter.notifyDataSetChanged();
                    productsRequested = false;
                    showProgress(false);

                } else if (productList.size() > 0 && activeKeyword.brand_link != null
                        && !activeKeyword.brand_link.isEmpty()) {
                    Product product = new Product();
                    product.isBrandLink = true;
                    productList.add(product);
                    adapter.notifyDataSetChanged();
                }

            } else if (code == ClientCodes.GET_BOOKED_ITEMS && response != null) {
                GetBookedItemsClient.Response bookedItemsResponse = (GetBookedItemsClient.Response) response;
                if (bookedItemsResponse.products != null) {
                    if (!bookedItemsResponse.products.isEmpty()) {
                        bookedItems.addAll(bookedItemsResponse.products);
                        yourBookings.setText(String.valueOf(bookedItems.size()));
                        yourBookingsCount = bookedItems.size();
                        if (productList != null && !productList.isEmpty()) {
                            int firstPosition = customLinearLayoutManager.findFirstVisibleItemPosition();
                            int lastPosition = customLinearLayoutManager.findLastVisibleItemPosition();
                            for (int i=firstPosition; i<=lastPosition; ++i) {
                                adapter.notifyItemChanged(i);
                            }
                        }
                    }
                    if (userLoggedInWhileLoading)
                        showProgress(false);
                }

            } else if (code == ClientCodes.ADD_PRODUCT_TO_EXPRESSO_CLIENT && response != null) {
                AddProductToExpressoClient.Response addResponse = (AddProductToExpressoClient.Response) response;
                if (addResponse.message.equals("done")) {
                    yourBookingsCount ++;
                    setHeaderValues(addResponse.keyword_info.current_discount, addResponse.keyword_info.booking_count);
                }

            } else if (code == ClientCodes.IMAGE_DOWNLOADER && response != null) {
                ImageDownloader.Response imageResponse = (ImageDownloader.Response) response;

                File file = imageResponse.output;

                Uri uri = FileProvider.getUriForFile(getContext(),
                        getActivity().getString(R.string.file_provider_authority), file);

                Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                        .setText(imageResponse.message)
                        .setStream(uri)
                        .setType("image/*")
                        .getIntent();

                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                getContext().grantUriPermission("com.miracas", uri,
                        FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);

                shareIntent.addFlags(FLAG_GRANT_READ_URI_PERMISSION | FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivity(shareIntent);
                showProgress(false);

            }

        }
    }

}
