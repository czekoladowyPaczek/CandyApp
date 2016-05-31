package com.candy.android.candyapp.profile;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.model.ModelFriend;
import com.candy.android.candyapp.model.ModelUser;
import com.candy.android.zlog.ZLog;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Marcin
 */

public class ProfileActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.user_profile_image)
    ImageView userImage;
    @BindView(R.id.user_profile_name)
    TextView userName;
    @BindView(R.id.loadingLayout)
    SwipeRefreshLayout loadingLayout;
    @BindView(R.id.friendsView)
    RecyclerView friendsView;

    @Inject
    ProfilePresenter presenter;

    private List<ModelFriend> friends;
    private FriendRecyclerAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        ((CandyApplication) getApplication()).getActivityComponent().inject(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        friends = new ArrayList<>();
        adapter = new FriendRecyclerAdapter(this, friends);
        friendsView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        friendsView.setAdapter(adapter);

        loadingLayout.setOnRefreshListener(() -> presenter.loadProfile(false));

        presenter.setParent(this, savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        presenter.removeParent();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        presenter.onSaveInstanceState(outState);
    }

    public void setUserData(ModelUser user) {
        ZLog.e("set data");
        userName.setText(user.getName());

        if (!TextUtils.isEmpty(user.getPicture())) {

        }

        int oldSize = friends.size();
        friends.clear();
        adapter.notifyItemRangeRemoved(0, oldSize);
        friends.addAll(user.getFriends());
        adapter.notifyItemRangeInserted(0, friends.size());
    }

    public void showLoading() {
        loadingLayout.setRefreshing(true);
    }

    public void cancelLoading() {
        loadingLayout.setRefreshing(false);
    }

    public void showError() {

    }
}
