package com.miracas.espresso.adapters;

import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.miracas.R;
import com.miracas.espresso.activity.MainActivity;
import com.miracas.espresso.model.ShopModel;
import com.miracas.espresso.widget.DTextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

import static com.miracas.espresso.model.ShopModel.KEYWORD;

public class ShopAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public EventListener mEventListener;
    private List<ShopModel> data = new ArrayList<>();
    FragmentActivity activity;

    public ShopAdapter(FragmentActivity activity) {
        this.activity = activity;
    }

    public void addAll(List<ShopModel> mData) {
        data.clear();
        data.addAll(mData);
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public boolean checkSize() {
        if (data.size() == 0)
            return true;
        else
            return false;
    }

    public void removeAt(int position) {
        data.remove(position);
        notifyDataSetChanged();
    }

    public ShopModel getItem(int position) {
        return data.get(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = null;

        v = inflater.inflate(R.layout.item_category, parent, false);
        return new CategoryHolder(v);

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.placeholder(R.drawable.ic_placeholder);
        requestOptions.transforms(new RoundedCorners(13));

        ShopModel shopModel = data.get(position);
        CategoryHolder categoryHolder = (CategoryHolder) holder;

        categoryHolder.category_name.setText(shopModel.getKeyword()+"");
        categoryHolder.category_price.setText(shopModel.getBooking_threshold()+"");

        categoryHolder.category_frame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!shopModel.isLocked()) {
                    Bundle bundle = new Bundle();
                    bundle.putString(KEYWORD, shopModel.getKeyword());
                    ((MainActivity) activity).switchFragment(R.id.nav_bronz_products, false, bundle);
                }else{
                    Toast.makeText(activity,R.string.you_must_be_platinum,Toast.LENGTH_LONG).show();
                }
            }
        });

        categoryHolder.category_price.setPaintFlags(categoryHolder.category_price.getPaintFlags()
                | Paint.STRIKE_THRU_TEXT_FLAG);

        if(!shopModel.isLocked()){
            categoryHolder.multiple_lock.setVisibility(View.GONE);
            categoryHolder.single_lock.setVisibility(View.GONE);
        }else{
            categoryHolder.multiple_lock.setVisibility(View.VISIBLE);
            categoryHolder.single_lock.setVisibility(View.VISIBLE);
        }

        if(shopModel.getImage1() != null) {
            //if getImage1 is not empty it has multiple pictures
            categoryHolder.multiple_frame.setVisibility(View.VISIBLE);
            categoryHolder.single_frame.setVisibility(View.GONE);
            if (shopModel.getImage1() != null) {
                loadPicture(shopModel.getImage1(), categoryHolder.multiple_picture1);
            }

            if (shopModel.getImage2() != null) {
                loadPicture(shopModel.getImage2(), categoryHolder.multiple_picture2);
            }

            if (shopModel.getImage3() != null) {
                loadPicture(shopModel.getImage3(), categoryHolder.multiple_picture3);
            }

            if (shopModel.getImage4() != null) {
                loadPicture(shopModel.getImage4(), categoryHolder.multiple_picture4);
            }
        }else{
            //else if it is empty it has one picture only
            categoryHolder.single_frame.setVisibility(View.VISIBLE);
            categoryHolder.multiple_frame.setVisibility(View.GONE);

            loadPicture(shopModel.getImage(),categoryHolder.single_picture);
        }


    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public EventListener getEventListener() {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener) {
        mEventListener = eventListener;
    }



    public interface EventListener {
        void onClick(int position);
    }

    static class CategoryHolder extends RecyclerView.ViewHolder{

        @BindView(R.id.category_name)
        DTextView category_name;
        @BindView(R.id.category_price1)
        DTextView category_price;
        @BindView(R.id.category_frame)
        RelativeLayout category_frame;

        @BindView(R.id.single_picture)
        ImageView single_picture;
        @BindView(R.id.single_dot)
        ImageView single_dot;
        @BindView(R.id.single_lock)
        ImageView single_lock;
        @BindView(R.id.single_frame)
        RelativeLayout single_frame;

        @BindView(R.id.multiple_frame)
        RelativeLayout multiple_frame;
        @BindView(R.id.multiple_lock)
        ImageView multiple_lock;
        @BindView(R.id.multiple_picture_1)
        ImageView multiple_picture1;
        @BindView(R.id.multiple_picture_2)
        ImageView multiple_picture2;
        @BindView(R.id.multiple_picture_3)
        ImageView multiple_picture3;
        @BindView(R.id.multiple_picture_4)
        ImageView multiple_picture4;

        public CategoryHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }

    public void loadPicture(String url, ImageView holder){
        Picasso.with(activity)
                .load(url)
                .into(holder);
    }

}