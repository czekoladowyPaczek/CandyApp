package com.candy.android.candyapp.shop.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.view.ColorGenerator;
import com.candy.android.candyapp.view.TextDrawable;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marcingawel on 02.06.2016.
 */

public class ShopAdapter extends RecyclerView.Adapter<ShopAdapter.ViewHolder> {

    private List<ModelShop> shops;
    private TextDrawable.IBuilder drawableBuilder = TextDrawable.builder().round();
    private ColorGenerator colorGenerator = ColorGenerator.MATERIAL;
    private LayoutInflater inflater;

    public ShopAdapter(Context context, List<ModelShop> shops) {
        this.shops = shops;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.view_list_shop, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String name = shops.get(position).getName();
        holder.bind(shops.get(position), drawableBuilder.build(String.valueOf(name.charAt(0)), colorGenerator.getColor(name)));
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.image)
        ImageView image;
        @BindView(R.id.name)
        TextView name;
        @BindView(R.id.creator)
        TextView creator;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void bind(ModelShop shop, Drawable drawable) {
            image.setImageDrawable(drawable);
            name.setText(shop.getName());
            creator.setText(shop.getCreator());
        }
    }
}
