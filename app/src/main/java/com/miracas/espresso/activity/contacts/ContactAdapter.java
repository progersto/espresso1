package com.miracas.espresso.activity.contacts;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.miracas.R;
import java.util.ArrayList;
import de.hdodenhof.circleimageview.CircleImageView;

public class ContactAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    ArrayList<User> contacts;
    AppCompatActivity activity;
    ContactSelectedInterface listener;

    public ContactAdapter(ArrayList<User> contacts,AppCompatActivity activity,
                          ContactSelectedInterface listener) {
        this.contacts = contacts;
        this.activity = activity;
        this.listener = listener;
    }

    public class Item extends RecyclerView.ViewHolder {

        CircleImageView item_url;
        TextView item_name;
        LinearLayout item_backgorund;
        CheckBox item_check;

        public Item(View itemView) {
            super(itemView);

            item_name = itemView.findViewById(R.id.friend_name);
            item_url = itemView.findViewById(R.id.friend_picture);
            item_backgorund = itemView.findViewById(R.id.item_background);
            item_check = itemView.findViewById(R.id.item_check);
        }
    }

    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Item(LayoutInflater.from(
                parent.getContext()).inflate(R.layout.item_contact
                , parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item p_item = (Item) holder;
        final User um = contacts.get(position);

        if(p_item instanceof Item){
            p_item.item_name.setText(um.getName());
            Glide.with(activity).load(um.getUrl()+"").into(p_item.item_url);

            if(um.isSelected()){
                p_item.item_check.setChecked(true);
            }else{
                p_item.item_check.setChecked(false);
            }

            p_item.item_backgorund.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(um.isSelected()) {
                        p_item.item_check.setChecked(false);
                    }else{
                        p_item.item_check.setChecked(true);
                    }
                }
            });

            p_item.item_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(!isChecked){
                        um.setSelected(false);
                        p_item.item_check.setChecked(false);
                        listener.onContactSelected(um,false);
                    }else{
                        um.setSelected(true);
                        p_item.item_check.setChecked(true);
                        listener.onContactSelected(um,true);
                    }
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return contacts.size();
    }
}
