package com.candy.android.candyapp.profile;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.model.ModelFriend;

import java.util.List;

/**
 * @author Marcin
 */

public class FriendRecyclerAdapter extends RecyclerView.Adapter<FriendRecyclerAdapter.ViewHolder> {

    private List<ModelFriend> friends;
    private LayoutInflater inflater;

    public FriendRecyclerAdapter(Context context, List<ModelFriend> friends) {
        this.friends = friends;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(inflater.inflate(R.layout.view_list_friend, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(friends.get(position));
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView userImage;
        private TextView userName;
        private TextView userStatus;
        private Button button;

        public ViewHolder(View itemView) {
            super(itemView);

            userImage = (ImageView) itemView.findViewById(R.id.userImage);
            userName = (TextView) itemView.findViewById(R.id.userName);
            userStatus = (TextView) itemView.findViewById(R.id.userStatus);
            button = (Button) itemView.findViewById(R.id.friendStatus);
        }

        public void bind(ModelFriend friend) {
            userName.setText(friend.getName());
            switch (friend.getStatus()) {
                case ModelFriend.STATUS_ACCEPTED:
                    userStatus.setText(R.string.friend_accepted);
                    button.setVisibility(View.VISIBLE);
                    break;
                case ModelFriend.STATUS_INVITED:
                    userStatus.setText(R.string.friend_invited);
                    button.setVisibility(View.VISIBLE);
                    break;
                case ModelFriend.STATUS_WAITING:
                    userStatus.setText(R.string.friend_waiting);
                    button.setVisibility(View.GONE);
            }
        }
    }
}
