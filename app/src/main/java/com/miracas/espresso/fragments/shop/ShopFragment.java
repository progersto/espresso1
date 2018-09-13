package com.miracas.espresso.fragments.shop;

import android.content.res.ColorStateList;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.volley.VolleyError;
import com.miracas.R;
import com.miracas.espresso.Application;
import com.miracas.espresso.adapters.ShopAdapter;
import com.miracas.espresso.fragments.BaseFragment;
import com.miracas.espresso.model.BronzResponse;
import com.miracas.espresso.model.ShopModel;
import com.miracas.espresso.rest.RestGetFreebiePictureInterface;
import com.miracas.espresso.rest.RestGetFreebiePictures;
import com.miracas.espresso.rest.RestGetFreebies;
import com.miracas.espresso.rest.RestGetFreebiesInterface;
import com.miracas.espresso.utils.SharedStorage;
import com.warkiz.widget.ColorCollector;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import static com.miracas.espresso.model.ShopModel.KEYWORDS;
import static com.miracas.espresso.model.ShopModel.SUBTYPE_ALL;
import static com.miracas.espresso.model.ShopModel.SUBTYPE_BEGGINER;
import static com.miracas.espresso.model.ShopModel.SUBTYPE_BRONZE;
import static com.miracas.espresso.model.ShopModel.SUBTYPE_GOLD;
import static com.miracas.espresso.model.ShopModel.SUBTYPE_PLATINUM;
import static com.miracas.espresso.model.ShopModel.SUBTYPE_SILVER;
import static com.miracas.espresso.rest.RestGetFreebiePictures.IMG_URL;
import static com.miracas.espresso.rest.RestGetFreebiePictures.REST_GET_FREEBIE_PICTURE;
import static com.miracas.espresso.rest.RestGetFreebies.REST_GET_FREEBIES;

public class ShopFragment extends BaseFragment implements RestGetFreebiesInterface,
        RestGetFreebiePictureInterface {

    Unbinder unbinder;
    @BindView(R.id.rvBronz)
    RecyclerView rvBronz;
    @BindView(R.id.sbBar)
    IndicatorSeekBar sbBar;
    @BindView(R.id.loading)
    RelativeLayout loading;

    ShopAdapter adapter;
    BronzResponse response;

    RestGetFreebies restGetFreebies;
    RestGetFreebiePictures restGetFreebiePictures;

    ArrayList<ShopModel> originalList = new ArrayList<>();
    ArrayList<ShopModel> list = new ArrayList<>();

    String token = "";
    int position = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_shop, container, false);
        unbinder = ButterKnife.bind(this, rootView);

        token = new SharedStorage(getActivity()).getValue(SharedStorage.JWT);

        restGetFreebies = new RestGetFreebies(this);
        restGetFreebiePictures = new RestGetFreebiePictures(this);

        initRecyclerView();

        sbBar.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                list= new ArrayList<>();

                if(originalList != null && position >= originalList.size()) {

                    if (seekParams.tickText.equalsIgnoreCase(getString(R.string.subtype_bronz))) {

                        updateColor(takeColor(R.color.colorBronz),
                                takeStateListener(R.color.selector_tick_bronz),
                                SUBTYPE_BRONZE);

                        search(SUBTYPE_BRONZE);

                    }else if(seekParams.tickText.equalsIgnoreCase(getString(R.string.subtype_silver))){

                        updateColor(takeColor(R.color.colorSilver),
                                takeStateListener(R.color.selector_tick_silver),
                                SUBTYPE_SILVER);

                        search(SUBTYPE_SILVER);

                    } else if(seekParams.tickText.equalsIgnoreCase(getString(R.string.subtype_gold))){

                        updateColor(takeColor(R.color.colorGold),
                                takeStateListener(R.color.selector_tick_gold),
                                SUBTYPE_GOLD);

                        search(SUBTYPE_GOLD);

                    }else if(seekParams.tickText.equalsIgnoreCase(getString(R.string.subtype_platinum))){

                        updateColor(takeColor(R.color.colorPlatinum),
                                takeStateListener(R.color.selector_tick_platinum),
                                SUBTYPE_PLATINUM);

                        search(SUBTYPE_PLATINUM);
                    }else if(seekParams.tickText.equalsIgnoreCase(getString(R.string.subtype_begginer))){
                        updateColor(takeColor(R.color.colorBegginer),
                                takeStateListener(R.color.selector_tick_begginer),
                                SUBTYPE_BEGGINER);

                        search(SUBTYPE_BEGGINER);
                    }else{

                        updateColor(takeColor(R.color.green),
                                takeStateListener(R.color.selector_tick_all),
                                SUBTYPE_ALL);
                        adapter.clear(); adapter.addAll(originalList);
                    }
                }else{
                    Toast.makeText(getContext(),R.string.loading_shop,Toast.LENGTH_SHORT).show();
                    sbBar.setProgress(0);
                }
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {}
        });

        restGetFreebies.getFreebies(token);

        return rootView;
    }

    private void initRecyclerView() {
        response = new BronzResponse();
        rvBronz.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        adapter = new ShopAdapter(getActivity());
        adapter.addAll(list);
        rvBronz.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        onCreateToolbar(getView() ,"Shop", false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Application.getInstance().cancelPendingRequest(REST_GET_FREEBIE_PICTURE+position);
        Application.getInstance().cancelPendingRequest(REST_GET_FREEBIES);
        unbinder.unbind();
    }

    @Override
    public void onRequestComplete(int clientCode, Object response) {}

    @Override
    public void onGetFreebiesSuccess(JSONObject response) {
        Log.d("FREEBIES","success: "+response.toString());
        loading.setVisibility(View.GONE);

        try {
            if(!response.isNull(KEYWORDS)){

                JSONArray jsonArray = response.getJSONArray(KEYWORDS);

                for(int i = 0; i < jsonArray.length(); i++){
                    ShopModel shopModel = ShopModel.initShopModel(jsonArray.getJSONObject(i));
                    if(shopModel != null){
                        originalList.add(shopModel);
                    }else{
                        return;
                    }
                }

                list = originalList;
                adapter.clear(); adapter.addAll(list);
                adapter.notifyDataSetChanged();

                if(originalList.size() > 0) {
                    restGetFreebiePictures.getFreebieImages(token,
                            originalList.get(position).getKeyword(), position);
                }
            }else{
                showFreebiesLoadingError();
            }
        } catch (JSONException e) {
            showFreebiesLoadingError();
        }
    }

    @Override
    public void onGetFreebiesError(VolleyError error) {
        Log.d("FREEBIES","error: "+error.toString());
        showFreebiesLoadingError();
        loading.setVisibility(View.GONE);
    }

    public void showFreebiesLoadingError(){
        Toast.makeText(getContext(),R.string.error_loading_freebies,Toast.LENGTH_LONG).show();
    }

    public String getSubtype(int subtype){
        switch (subtype){
            case SUBTYPE_BEGGINER:
                return getString(R.string.subtype_all);
            case SUBTYPE_BRONZE:
                return getString(R.string.subtype_bronz);
            case SUBTYPE_SILVER:
                return getString(R.string.subtype_silver);
            case SUBTYPE_GOLD:
                return getString(R.string.subtype_gold);
            case SUBTYPE_PLATINUM:
                return getString(R.string.subtype_platinum);
                default:
                    return getString(R.string.subtype_all);
        }
    }

    public void updateColor(int color, ColorStateList colorStateList,int subtype){
        sbBar.thumbColor(color);
        sbBar.tickMarksColor(colorStateList);

        int greenColor = getResources().getColor(R.color.green);

        sbBar.customSectionTrackColor(new ColorCollector() {
            @Override
            public boolean collectSectionTrackColor(int[] colorIntArr) {
                //the length of colorIntArray equals section count
                if(subtype == SUBTYPE_BEGGINER){
                    colorIntArr[0] = color; colorIntArr[1] = greenColor;
                    colorIntArr[2] = greenColor; colorIntArr[3] = greenColor;
                    colorIntArr[4] = greenColor;
                }else if(subtype == SUBTYPE_BRONZE){
                    colorIntArr[0] = color; colorIntArr[1] = color;
                    colorIntArr[2] = greenColor; colorIntArr[3] = greenColor;
                    colorIntArr[4] = greenColor;
                }else if(subtype == SUBTYPE_SILVER){
                    colorIntArr[0] = color; colorIntArr[1] = color;
                    colorIntArr[2] = color; colorIntArr[3] = greenColor;
                    colorIntArr[4] = greenColor;
                }else if(subtype == SUBTYPE_GOLD){
                    colorIntArr[0] = color; colorIntArr[1] = color;
                    colorIntArr[2] = color; colorIntArr[3] = color;
                    colorIntArr[4] = greenColor;
                }else if(subtype == SUBTYPE_PLATINUM){
                    colorIntArr[0] = color; colorIntArr[1] = color;
                    colorIntArr[2] = color; colorIntArr[3] = color;
                    colorIntArr[4] = color;
                }else{
                    colorIntArr[0] = greenColor; colorIntArr[1] = greenColor;
                    colorIntArr[2] = greenColor; colorIntArr[3] = greenColor;
                    colorIntArr[4] = greenColor;
                }


                return true; //True if apply color , otherwise no change
            }
        });
    }

    @Override
    public void onGetFreebiePicturesSuccess(JSONArray response) {
        //getItem(position).setImages_obtained(true);
        Log.d("IMAGES_",position + " : "+originalList.get(position).getKeyword()+
                " : "+response.toString());

        if(response.length() > 0){
            Log.d("IMAGES_",response.length()+"");

            for(int i = 0; i < response.length(); i ++){
                try {
                    if(i == 0){
                        originalList.get(position).setImage1(response.getJSONObject(i).getString(IMG_URL));
                    }else if(i == 1){
                        originalList.get(position).setImage2(response.getJSONObject(i).getString(IMG_URL));
                    }else if(i == 2){
                        originalList.get(position).setImage3(response.getJSONObject(i).getString(IMG_URL));
                    }else if(i == 3){
                        originalList.get(position).setImage4(response.getJSONObject(i).getString(IMG_URL));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("IMAGES_","onGetImages cathc: "+e.toString());
                    break;
                }
            }

            adapter.clear();adapter.addAll(originalList);
            adapter.notifyDataSetChanged();

        }else{
            Log.d("IMAGES_",response.toString());
        }
        position++;
        callNextPictureApi();

        //notifyDataSetChanged();
    }

    @Override
    public void onGetFreebiePictureError(VolleyError error) {
        Log.d("IMAGES_",error.toString());
        Log.d("IMAGES_",position + " : "+originalList.get(position).getKeyword()+
                " : "+response.toString());

        position++;

        callNextPictureApi();
    }

    public void callNextPictureApi(){
        Log.d("IMAGES_","POSITION: "+position);
        Log.d("IMAGES_","condition: "+(position < originalList.size())+"");
        for(int x = position; x < originalList.size(); x++){
            if(position < originalList.size()) {
                restGetFreebiePictures.getFreebieImages(token,originalList.get(x).getKeyword(),
                        x);
            }
            break;
        }
    }

    public int takeColor(int color){
        return getResources().getColor(color);
    }

    public ColorStateList takeStateListener(int resource){
                return ContextCompat.getColorStateList(getContext(),resource);
    }

    public void search(int subtype){
        for (int i = 0; i < originalList.size(); i++) {
            if (originalList.get(i).getKeyword_subtype() <= subtype) {
                list.add(originalList.get(i));
            }
        }
        adapter.clear(); adapter.addAll(list);
    }
}
