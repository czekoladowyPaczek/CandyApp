package com.candy.android.candyapp.profile;

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
import com.candy.android.candyapp.model.ModelFriend;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.List;

/**
 * @author Marcin
 */

public class FriendRecyclerAdapter extends RecyclerView.Adapter<FriendRecyclerAdapter.ViewHolder> {

    public interface ClickListener {
        void onClick(int position);
    }

    private List<ModelFriend> friends;
    private LayoutInflater inflater;
    private int imageSize;
    private ClickListener listener;

    public FriendRecyclerAdapter(Context context, List<ModelFriend> friends, ClickListener listener) {
        this.friends = friends;
        this.listener = listener;

        inflater = LayoutInflater.from(context);
        imageSize = UiHelper.convertDpToPixel(40, context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.view_list_friend, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(friends.get(position), imageSize);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView userImage;
        private TextView userName;
        private TextView userStatus;

        public ViewHolder(View itemView, ClickListener listener) {
            super(itemView);

            userImage = (ImageView) itemView.findViewById(R.id.userImage);
            userName = (TextView) itemView.findViewById(R.id.userName);
            userStatus = (TextView) itemView.findViewById(R.id.userStatus);

            itemView.setOnClickListener((v) -> listener.onClick(getAdapterPosition()));
        }

        public void bind(ModelFriend friend, int imageSize) {
            userName.setText(friend.getName());
            switch (friend.getStatus()) {
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
            if (!TextUtils.isEmpty(friend.getPicture())) {
                ImageLoader.getInstance().displayImage(friend.getPicture(), userImage, new ImageSize(imageSize, imageSize));
            }
        }
    }
}
