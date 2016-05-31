package com.candy.android.candyapp.profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.helper.UiHelper;
import com.candy.android.candyapp.login.LoginActivity;
import com.candy.android.candyapp.model.ModelFriend;
import com.candy.android.candyapp.model.ModelUser;
import com.candy.android.zlog.ZLog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

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
    @BindView(R.id.user_profile_email)
    TextView userEmail;

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
        userEmail.setText(user.getEmail());
        ZLog.e(user.getPicture());
        if (!TextUtils.isEmpty(user.getPicture())) {
            int imgSize = UiHelper.convertDpToPixel(110, this);
            ImageLoader.getInstance().displayImage(user.getPicture(), userImage, new ImageSize(imgSize, imgSize));
        }

        int oldSize = friends.size();
        friends.clear();
        adapter.notifyItemRangeRemoved(0, oldSize);
        friends.addAll(user.getFriends());
        adapter.notifyItemRangeInserted(0, friends.size());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;
    }

    public void showLoading() {
        loadingLayout.setRefreshing(true);
    }

    public void cancelLoading() {
        loadingLayout.setRefreshing(false);
    }

    public void showError() {

    }

    public void logout() {
        presenter.logout();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
