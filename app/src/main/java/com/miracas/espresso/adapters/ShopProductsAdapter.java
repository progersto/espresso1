package com.miracas.espresso.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.miracas.R;
import com.miracas.espresso.model.ProductModel;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;

public class ShopProductsAdapter extends RecyclerView.Adapter<ShopProductsAdapter.ViewHolder> {

    ArrayList<ProductModel> productList = new ArrayList<>();
    private Context context;;
    private boolean reachedEnd = false;

    public void addAll(ArrayList<ProductModel> mData)
    {
        productList.addAll(mData);
        notifyDataSetChanged();
    }

    public void clear() {
        productList.clear();
        notifyDataSetChanged();
    }

    public ShopProductsAdapter(Context context, ArrayList<ProductModel> items) {
        super();
        this.context = context;
        this.productList = items;
    }

    public void setReachedEnd() {
        reachedEnd = true;
        notifyItemChanged(getItemCount()-1);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.list_item_purchase_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        final ProductModel item = productList.get(position);

        Log.d("PRODUCTS_","onBindViewHolder: " +position);

        holder.title.setText(item.getName()+"");
        Picasso.with(context)
                .load(item.getImg_url()+"")
                .fit()
                .into(holder.image);
        holder.priceNew.setText(item.getPrice()+"");

        if (position == getItemCount() - 1 && reachedEnd)
            holder.footerMessage.setVisibility(View.VISIBLE);
        else
            holder.footerMessage.setVisibility(View.GONE);

    }

    private void scaleColor(ExpressoBrewedAdapter.ViewHolder holder) {
        int color = ContextCompat.getColor(context, R.color.pastel_red);
        holder.timerHours.setTextColor(color);
        holder.timerMinutes.setTextColor(color);
        holder.timerSeconds.setTextColor(color);
    }

    @Override
    public int getItemCount() {
        Log.d("PRODUCTS_","getItemCount: "+productList.size());
        return productList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView priceOld;
        TextView priceNew;
        View footerMessage;

        ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            title = view.findViewById(R.id.title);
            priceOld = view.findViewById(R.id.priceOld);
            priceNew = view.findViewById(R.id.priceNew);
            footerMessage = view.findViewById(R.id.footerMessage);
        }
    }

}
