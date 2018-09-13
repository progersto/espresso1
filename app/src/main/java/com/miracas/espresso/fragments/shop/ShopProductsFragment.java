package com.miracas.espresso.fragments.shop;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.miracas.R;
import com.miracas.espresso.adapters.ShopProductsAdapter;
import com.miracas.espresso.fragments.BaseFragment;
import com.miracas.espresso.layouts.CustomLinearLayoutManager;
import com.miracas.espresso.listeners.RecyclerItemClickListener;
import com.miracas.espresso.model.ProductModel;
import com.miracas.espresso.rest.RestGetFreebiePictureInterface;
import com.miracas.espresso.rest.RestGetFreebiePictures;
import com.miracas.espresso.utils.SharedStorage;
import org.json.JSONArray;
import org.json.JSONException;
import java.util.ArrayList;
import static com.miracas.espresso.model.ProductModel.PRODUCT;
import static com.miracas.espresso.model.ShopModel.KEYWORD;

public class ShopProductsFragment extends BaseFragment implements RestGetFreebiePictureInterface{

    RelativeLayout loading;

    private ShopProductsAdapter adapter;
    private ArrayList<ProductModel> productsList;
    private int currentPage = 0;
    private boolean productsRequested = false;
    String keyword = "";

    RestGetFreebiePictures restGetFreebiePictures;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle s) {
        View view = inflater.inflate(R.layout.fragment_direct_purchase, container, false);

        if (getArguments() != null) {
            keyword = getArguments().getString(KEYWORD);
            onCreateToolbar(view, keyword, false);
        }

        restGetFreebiePictures = new RestGetFreebiePictures(this);
        restGetFreebiePictures.getFreebieImages(
                new SharedStorage(getActivity()).getValue(getString(R.string.shared_storage_jwt)),
                keyword,-1
        );

        loading = view.findViewById(R.id.loading);

        productsList = new ArrayList<>();
        adapter = new ShopProductsAdapter(getContext(), productsList);

        RecyclerView listView = view.findViewById(R.id.products_list);
        CustomLinearLayoutManager customLinearLayoutManager = new CustomLinearLayoutManager(getContext());
        listView.setLayoutManager(customLinearLayoutManager);
        listView.setAdapter(adapter);

        listView.addOnItemTouchListener(
                new RecyclerItemClickListener(getContext(), listView,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        ProductModel product = productsList.get(position);

                        FragmentManager fragmentManager = getFragmentManager();
                        if (fragmentManager != null) {
                            Bundle bundle = new Bundle();
                            bundle.putSerializable(PRODUCT, product);
                            bundle.putBoolean("SHOW_CART", true);
                            Fragment fragment = new ShopProductFrament();
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

        listView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
//                int lastPosition = customLinearLayoutManager.findLastVisibleItemPosition();
//                if (!productsRequested && lastPosition == items.size() - 1) {
//                    appendProducts();
//                }
            }
        });

        return view;
    }

    private void appendProducts() {
        currentPage += 10;
//        ProductsFeedClient client = new ProductsFeedClient(
//                this, activeKeyword.getBrandLinkOrKeyword(), activeKeyword.genders, currentPage);
//        client.execute();
        productsRequested = true;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        onCreateToolbar(view, activeKeyword.getBrandLinkOrKeyword(), false);
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {
//        View view = getView();
//        if (clientCode == ClientCodes.PRODUCT_FEED_CLIENT && view != null) {
//
//            List<Product> products = (List<Product>) response;
//            if (products != null) {
//                if (!products.isEmpty()) {
//                    items.addAll(products);
//                    adapter.notifyDataSetChanged();
//                    productsRequested = false;
//
//                } else {
//                    adapter.setReachedEnd();
//                }
//            }
//
//            view.findViewById(R.id.loading).setVisibility(View.GONE);
//        }
    }

    @Override
    public void onGetFreebiePicturesSuccess(JSONArray response) {
        Log.d("PRODUCTS_","response: "+response.toString());
        loading.findViewById(R.id.loading).setVisibility(View.GONE);

        try {
            for(int x = 0; x < response.length(); x++){
                ProductModel productModel = ProductModel.initProductModel(response.getJSONObject(x));

                if(productModel != null){
                    productsList.add(productModel);
                }else{
                    showProductLoadingError();
                    return;
                }
            }

            Log.d("PRODUCTS_","size: "+productsList.size());

//            adapter.clear();
            adapter.addAll(productsList);
            adapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
            showProductLoadingError();
        }
    }

    @Override
    public void onGetFreebiePictureError(VolleyError error) {
        loading.findViewById(R.id.loading).setVisibility(View.GONE);
        showProductLoadingError();
    }

    public void showProductLoadingError(){
        Toast.makeText(getContext(),R.string.error_loading_products,Toast.LENGTH_LONG).show();
    }
}
