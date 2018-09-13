package com.miracas.espresso.adapters;

import android.content.Context;
import android.media.FaceDetector;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.miracas.R;
import com.miracas.espresso.model.FacebookModel;
import com.miracas.espresso.widget.DTextView;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class FacebookAdapter extends RecyclerView.Adapter<FacebookAdapter.AlertHolder> {

    public EventListener mEventListener;
    Context context;
    private List<FacebookModel> data = new ArrayList<>();

    public FacebookAdapter(Context context) {
        this.context = context;
    }

    public void addAll(List<FacebookModel> mData)
    {
        data.addAll(mData);
        notifyDataSetChanged();
    }

    public void clear() {
        data.clear();
        notifyDataSetChanged();
    }

    public boolean checkSize()
    {
        if (data.size() == 0)
            return true;
        else
            return false;
    }

    public void removeAt(int position) {
        data.remove(position);
        notifyDataSetChanged();
    }

    public FacebookModel getItem(int position)
    {
        return data.get(position);
    }

    @Override
    public AlertHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.item_facebook, parent, false);
        return new AlertHolder(v);
    }

    @Override
    public void onBindViewHolder(final AlertHolder holder, final int position)
    {
        AlertHolder alertHolder = (AlertHolder) holder;
        FacebookModel facebookModel = data.get(position);

        alertHolder.item_name.setText(facebookModel.getName()+"");
        Picasso.with(context)
                .load(facebookModel.getUrl()+"")
                .transform(new CropCircleTransformation())
                .fit()
                .into(alertHolder.item_image);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (mEventListener != null)
                {
                    mEventListener.onClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return data.size();
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    public EventListener getEventListener()
    {
        return mEventListener;
    }

    public void setEventListener(EventListener eventListener)
    {
        mEventListener = eventListener;
    }


    public interface EventListener
    {
        void onClick(int position);
    }


    static class AlertHolder extends RecyclerView.ViewHolder
    {
        ImageView item_image;
        DTextView item_name;

        AlertHolder(View view)
        {
            super(view);
            item_image = view.findViewById(R.id.item_facebook_picture);
            item_name = view.findViewById(R.id.item_facebook_name);

            //ButterKnife.bind(this, view);
        }
    }
}