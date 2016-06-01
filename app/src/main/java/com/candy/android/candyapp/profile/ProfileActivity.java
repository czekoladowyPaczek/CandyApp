package com.candy.android.candyapp.profile;

import android.animation.Animator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
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
    public static final String FRIEND_VIEW_VISIBLE = "com.candy.android.friend_visible";
    public static final String FRIEND_EMAIL = "com.candy.android.friend_email";

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
    @BindView(R.id.add_friend_button)
    FloatingActionButton addFriendButton;
    @BindView(R.id.add_friend_container)
    View container;
    @BindView(R.id.add_friend_email)
    TextInputEditText friendEmail;
    @BindView(R.id.add_friend_email_container)
    TextInputLayout friendEmailLayout;
    @BindView(R.id.add_friend_accept)
    View friendAccept;

    @Inject
    ProfilePresenter presenter;

    private List<ModelFriend> friends;
    private FriendRecyclerAdapter adapter;

    private Dialog friendDialog;

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
        addFriendButton.setOnClickListener(v -> showAddFriendView(true));
        friendAccept.setOnClickListener(v -> {
            String email = friendEmail.getText().toString();
            if (!TextUtils.isEmpty(email)) {
                presenter.inviteFriend(email, false);
                friendEmail.setText("");
                container.setVisibility(View.GONE);
                addFriendButton.setVisibility(View.VISIBLE);
            }
        });

        if (savedInstanceState != null) {
            if (savedInstanceState.getBoolean(FRIEND_VIEW_VISIBLE, false)) {
                showAddFriendView(false);
                friendEmail.setText(savedInstanceState.getString(FRIEND_EMAIL, ""));
            }
        }

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

        if (container.getVisibility() == View.VISIBLE) {
            outState.putBoolean(FRIEND_VIEW_VISIBLE, true);
            outState.putString(FRIEND_EMAIL, friendEmail.getText().toString());
        }
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

    @Override
    public void onBackPressed() {
        if (container.getVisibility() == View.VISIBLE) {
            container.setVisibility(View.GONE);
            addFriendButton.setVisibility(View.VISIBLE);
        } else {
            super.onBackPressed();
        }
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

    public void showLoadingDialog(@StringRes int res) {
        friendDialog = ProgressDialog.show(this, null, getString(res), true, true, v -> presenter.friendDialogCancelled());
    }

    private void showAddFriendView(boolean animate) {
        container.setVisibility(View.VISIBLE);
        if (animate && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int width = UiHelper.getScreenWidth(this);
            int margin = UiHelper.convertDpToPixel(45, this);
            int x = UiHelper.getScreenWidth(this) - margin;
            Animator anim = ViewAnimationUtils.createCircularReveal(container, x, margin, margin * 0.70f, width);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    addFriendButton.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationEnd(Animator animation) {

                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            anim.setDuration(400);
            anim.start();
        } else {
            addFriendButton.setVisibility(View.GONE);
        }
    }
}
