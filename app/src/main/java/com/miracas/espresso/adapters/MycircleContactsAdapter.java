package com.miracas.espresso.adapters;

import android.content.Context;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.miracas.R;
import com.miracas.espresso.model.ContactModel;
import com.miracas.espresso.widget.DTextView;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

public class MycircleContactsAdapter extends RecyclerView.Adapter<MycircleContactsAdapter.AlertHolder> {

    public EventListener mEventListener;
    Context context;
    private List<ContactModel> data = new ArrayList<>();

    public MycircleContactsAdapter(Context context) {
        this.context = context;
    }

    public void addAll(List<ContactModel> mData) {
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

    public ContactModel getItem(int position) {
        return data.get(position);
    }

    @Override
    public AlertHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        final View v = inflater.inflate(R.layout.item_mycircle_contacts_request, parent, false);
        return new AlertHolder(v);
    }

    @Override
    public void onBindViewHolder(final AlertHolder holder, final int position) {
        ContactModel cm = data.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        holder.name.setText(cm.getName()+"");
//        holder.deals.setText(cm.get);

        Picasso.with(context)
                .load(cm.getUrl()+"")
                .transform(new CropCircleTransformation())
                .fit()
                .into(holder.picture);


        holder.imgMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupMenu popup = new PopupMenu(context, holder.imgMore);

                //inflating menu from xml resource
                popup.inflate(R.menu.menu_mycircle_contact_more);

                try {
                    Field[] fields = popup.getClass().getDeclaredFields();
                    for (Field field : fields) {
                        if ("mPopup".equals(field.getName())) {
                            field.setAccessible(true);
                            Object menuPopupHelper = field.get(popup);
                            Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                            Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                            setForceIcons.invoke(menuPopupHelper, true);
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.menuInvite:
                                Toast.makeText(context, "Invite", Toast.LENGTH_SHORT).show();
                                break;
                            case R.id.menuGift:
                                Toast.makeText(context, "Gift", Toast.LENGTH_SHORT).show();
                                break;
                        }
                        return false;
                    }
                });
                popup.show();

            }


        });


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
        void onClick(View view);
    }


    static class AlertHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.imgMore)
        ImageView imgMore;
        @BindView(R.id.status)
        CircleImageView status;
        @BindView(R.id.picture)
        CircleImageView picture;
        @BindView(R.id.name)
        DTextView name;
        @BindView(R.id.deals)
        DTextView deals;

        AlertHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);

        }

    }

}