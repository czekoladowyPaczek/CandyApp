package com.candy.android.candyapp.profile;

import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.model.ModelFriend;
import com.candy.android.candyapp.ui.AdapterClickListener;
import com.candy.android.candyapp.ui.UserAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import butterknife.BindView;

/**
 * Created by marcingawel on 12.06.2016.
 */

public class FriendViewHolder extends UserAdapter.ViewHolder<ModelFriend> {
    @BindView(R.id.userImage) ImageView userImage;
    @BindView(R.id.userName) TextView userName;
    @BindView(R.id.userStatus) TextView userStatus;

    public FriendViewHolder(View itemView, AdapterClickListener listener) {
        super(itemView, listener);
    }

    @Override
    public void bind(ModelFriend item, int imageSize) {
        userName.setText(item.getName());
        switch (item.getStatus()) {
            case ModelFriend.STATUS_ACCEPTED:
                userStatus.setText(R.string.friend_accepted);
                break;
            case ModelFriend.STATUS_INVITED:
                userStatus.setText(R.string.friend_invited);
                break;
            case ModelFriend.STATUS_WAITING:
                userStatus.setText(R.string.friend_waiting);
        }
        userImage.setImageResource(R.drawable.user_default);
        if (!TextUtils.isEmpty(item.getPicture())) {
            ImageLoader.getInstance().displayImage(item.getPicture(), userImage, new ImageSize(imageSize, imageSize));
        }
    }

    public static class Builder implements UserAdapter.ViewHolder.Builder<FriendViewHolder> {
        @Override
        public FriendViewHolder newInstance(View view, AdapterClickListener listener) {
            return new FriendViewHolder(view, listener);
        }

        @Override
        public int getLayout() {
            return R.layout.view_list_friend;
        }
    }
}
