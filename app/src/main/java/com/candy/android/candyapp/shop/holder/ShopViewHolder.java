package com.candy.android.candyapp.shop.holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.ui.AdapterClickListener;
import com.candy.android.candyapp.ui.ShopAdapter;
import com.candy.android.candyapp.view.ColorGenerator;
import com.candy.android.candyapp.view.TextDrawable;

import butterknife.BindView;

/**
 * Created by marcingawel on 12.06.2016.
 */

public class ShopViewHolder extends ShopAdapter.ViewHolder<ModelShop> {
    @BindView(R.id.image) ImageView image;
    @BindView(R.id.name) TextView name;
    @BindView(R.id.creator) TextView creator;

    public ShopViewHolder(View itemView, AdapterClickListener listener) {
        super(itemView, listener);
    }

    @Override
    public void bind(ModelShop item, TextDrawable.IBuilder builder, ColorGenerator color) {
        image.setImageDrawable(builder.build(String.valueOf(item.getName().charAt(0)), color.getColor(item.getName())));
        name.setText(item.getName());
        creator.setText(item.getOwner().getName());
    }

    public static class Builder implements ShopAdapter.ViewHolder.Builder<ShopViewHolder> {
        @Override
        public ShopViewHolder newInstance(View view, AdapterClickListener listener) {
            return new ShopViewHolder(view, listener);
        }

        @Override
        public int getLayout() {
            return R.layout.view_list_shop;
        }
    }
}
