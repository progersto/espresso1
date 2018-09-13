package com.miracas.espresso.adapters;

import android.content.Context;
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
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class FriendRequestAdapter extends RecyclerView.Adapter<FriendRequestAdapter.AlertHolder> {

    public static final int FRIEND_REJECT = 0;
    public static final int FRIEND_APPROVE = 1;

    public EventListener mEventListener;
    Context context;
    private List<FacebookModel> data = new ArrayList<>();

    public FriendRequestAdapter(Context context) {
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
        final View v = inflater.inflate(R.layout.item_friend_request, parent, false);
        return new AlertHolder(v);
    }

    @Override
    public void onBindViewHolder(final AlertHolder holder, final int position)
    {
        FacebookModel facebookModel = data.get(position);

        holder.name.setText(facebookModel.getName()+"");
        Picasso.with(context)
                .load(facebookModel.getUrl()+"")
                .transform(new CropCircleTransformation())
                .fit()
                .into(holder.picture);

        holder.approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (mEventListener != null)
                {
                    mEventListener.onClick(facebookModel,position,FRIEND_APPROVE);
                }
            }
        });

        holder.reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if (mEventListener != null)
                {
                    mEventListener.onClick(facebookModel,position,FRIEND_REJECT);
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
        void onClick(FacebookModel facebookModel, int position, int action);
    }


    static class AlertHolder extends RecyclerView.ViewHolder
    {
        CircleImageView picture;
        DTextView name;
        ImageView approve, reject;

        AlertHolder(View view)
        {
            super(view);

            picture = view.findViewById(R.id.item_friend_request_picture);
            name = view.findViewById(R.id.item_friend_request_name);
            approve = view.findViewById(R.id.approve);
            reject = view.findViewById(R.id.reject);

        }
    }
}