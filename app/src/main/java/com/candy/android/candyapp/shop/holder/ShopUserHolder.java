package com.candy.android.candyapp.shop.holder;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.model.ModelShopUser;
import com.candy.android.candyapp.ui.AdapterClickListener;
import com.candy.android.candyapp.ui.UserAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import butterknife.BindView;

/**
 * Created by marcingawel on 12.06.2016.
 */

public class ShopUserHolder extends UserAdapter.ViewHolder<ModelShopUser> {
    @BindView(R.id.userName) TextView userName;
    @BindView(R.id.userImage) ImageView userImage;

    public ShopUserHolder(View itemView, AdapterClickListener listener) {
        super(itemView, listener);
    }

    @Override
    public void bind(ModelShopUser item, int imageSize) {
        userName.setText(item.getName());

        userImage.setImageResource(R.drawable.user_default);
        if (!TextUtils.isEmpty(item.getPicture())) {
            ImageLoader.getInstance().displayImage(item.getPicture(), userImage, new ImageSize(imageSize, imageSize));
        }
    }

    public static class Builder implements UserAdapter.ViewHolder.Builder<ShopUserHolder> {
        @Override
        public ShopUserHolder newInstance(View view, AdapterClickListener listener) {
            return new ShopUserHolder(view, listener);
        }

        @Override
        public int getLayout() {
            return R.layout.view_list_shop_user;
        }
    }
}
