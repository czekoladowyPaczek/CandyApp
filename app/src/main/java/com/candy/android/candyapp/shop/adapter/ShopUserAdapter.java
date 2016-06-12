package com.candy.android.candyapp.shop.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.helper.UiHelper;
import com.candy.android.candyapp.model.ModelShopUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.List;

/**
 * Created by marcingawel on 12.06.2016.
 */

public class ShopUserAdapter extends RecyclerView.Adapter<ShopUserAdapter.ViewHolder> {

    public interface ClickListener {
        void onClick(int position);
    }

    private LayoutInflater inflater;
    private List<ModelShopUser> users;
    private int imageSize;
    private ClickListener listener;

    public ShopUserAdapter(Context context, List<ModelShopUser> users, ClickListener listener) {
        this.inflater = LayoutInflater.from(context);
        this.users = users;
        this.listener = listener;
        imageSize = UiHelper.convertDpToPixel(40, context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.view_list_shop_user, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(users.get(position), imageSize);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView userName;
        private ImageView userImage;

        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);

            userImage = (ImageView) itemView.findViewById(R.id.userImage);
            userName = (TextView) itemView.findViewById(R.id.userName);

            itemView.setOnClickListener((v) -> listener.onClick(getAdapterPosition()));
        }

        public void bind(ModelShopUser user, int imageSize) {
            userName.setText(user.getName());

            userImage.setImageResource(R.drawable.user_default);
            if (!TextUtils.isEmpty(user.getPicture())) {
                ImageLoader.getInstance().displayImage(user.getPicture(), userImage, new ImageSize(imageSize, imageSize));
            }
        }
    }
}
