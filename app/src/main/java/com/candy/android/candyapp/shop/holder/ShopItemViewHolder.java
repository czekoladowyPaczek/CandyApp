package com.candy.android.candyapp.shop.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.model.ModelShopItem;
import com.candy.android.candyapp.ui.AdapterClickListener;
import com.candy.android.candyapp.ui.ShopAdapter;
import com.candy.android.candyapp.view.ColorGenerator;
import com.candy.android.candyapp.view.TextDrawable;

import butterknife.BindView;

/**
 * Created by marcingawel on 13.06.2016.
 */

public class ShopItemViewHolder extends ShopAdapter.ViewHolder<ModelShopItem> {
    @BindView(R.id.image) ImageView image;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.creator) TextView creator;

    public ShopItemViewHolder(View itemView, AdapterClickListener listener) {
        super(itemView, listener);
    }

    @Override
    public void bind(ModelShopItem item, TextDrawable.IBuilder builder, ColorGenerator color) {
        image.setImageDrawable(builder.build(String.valueOf(item.getName().charAt(0)), color.getColor(item.getName())));
        name.setText(item.getName());
        creator.setText(item.getCount() + " " + item.getMetric());
    }

    public static class Builder implements ShopAdapter.ViewHolder.Builder<ShopItemViewHolder> {
        @Override
        public ShopItemViewHolder newInstance(View view, AdapterClickListener listener) {
            return new ShopItemViewHolder(view, listener);
        }

        @Override
        public int getLayout() {
            return R.layout.view_list_shop;
        }
    }
}
