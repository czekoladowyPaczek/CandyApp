package com.candy.android.candyapp.shop;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;

import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopUser;
import com.candy.android.candyapp.shop.holder.ShopUserHolder;
import com.candy.android.candyapp.ui.AdapterClickListener;
import com.candy.android.candyapp.ui.UserAdapter;

import java.util.Iterator;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by marcingawel on 11.06.2016.
 */

public class ShopFriendActivity extends AppCompatActivity implements AdapterClickListener {
    public static final String SHOP = "com.candy.android.shop";
    public static final String SELECTED_USER = "com.candy.android.selected_user";

    @Inject
    ShopFriendPresenter presenter;

    private View root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.shop_users_list)
    RecyclerView recyclerView;
    @BindView(R.id.add_friend_button)
    FloatingActionButton addUser;

    private Dialog acceptDialog;
    private Dialog removingProgressDialog;
    private ModelShopUser selectedUser;

    private UserAdapter<ShopUserHolder> adapter;
    private LinearLayoutManager manager;
    private ModelShop shop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            shop = savedInstanceState.getParcelable(SHOP);
            selectedUser = savedInstanceState.getParcelable(SELECTED_USER);
        } else {
            shop = getIntent() != null ? getIntent().getParcelableExtra(SHOP) : null;
        }
        if (shop == null) {
            finish();
            return;
        }

        setContentView(R.layout.activity_shop_users);
        ((CandyApplication) getApplication()).getActivityComponent().inject(this);
        ButterKnife.bind(this);
        root = findViewById(R.id.root);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.users_title);

        adapter = new UserAdapter<>(this, shop.getUsers(), new ShopUserHolder.Builder(), this);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(() -> presenter.refreshData());

        presenter.setParent(this, shop, getLastCustomNonConfigurationInstance());

        if (selectedUser != null) {
            acceptDialog = createAcceptDialog(selectedUser);
            acceptDialog.show();
        }
    }

    public void onDataRefresh(ModelShop shop) {
        int oldCount = this.shop.getUsers().size();
        this.shop.getUsers().clear();
        adapter.notifyItemRangeRemoved(0, oldCount);
        this.shop.getUsers().addAll(shop.getUsers());
        adapter.notifyItemRangeInserted(0, shop.getUsers().size());
        refreshLayout.setRefreshing(false);
    }

    public void showFabButton(boolean visible) {
        if (visible) {
            addUser.show();
        } else {
            addUser.hide();
        }
    }

    public void setRefreshing(boolean refreshing) {
        refreshLayout.post(() -> refreshLayout.setRefreshing(refreshing));
    }

    public void showError(@StringRes int res) {
        Snackbar.make(root, res, Snackbar.LENGTH_LONG).show();
    }

    public void showRemovingProgressDialog() {
        removingProgressDialog = new AlertDialog.Builder(this)
                .setMessage(R.string.users_removing_user)
                .setCancelable(false)
                .create();
        removingProgressDialog.show();
    }

    public void removeRemovingProgressDialog() {
        if (removingProgressDialog != null && removingProgressDialog.isShowing()) {
            try {
                removingProgressDialog.dismiss();
            } catch (IllegalArgumentException ignored) {

            }
        }
    }

    public void removeUserFromList(ModelShopUser user) {
        int position = 0;
        int userPosition = -1;
        for (Iterator<ModelShopUser> it = shop.getUsers().iterator(); it.hasNext(); position++) {
            final ModelShopUser u = it.next();
            if (u.getId() == user.getId()) {
                it.remove();
                userPosition = position;
                break;
            }
        }
        if (userPosition > -1) {
            adapter.notifyItemRemoved(userPosition);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SHOP, shop);
        if (selectedUser != null)
            outState.putParcelable(SELECTED_USER, selectedUser);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return presenter.onRetainCustomNonConfigurationInstance();
    }

    @SuppressWarnings("unchecked")
    @Override
    public SparseArray<Observable> getLastCustomNonConfigurationInstance() {
        return (SparseArray<Observable>) super.getLastCustomNonConfigurationInstance();
    }

    @Override
    protected void onDestroy() {
        presenter.removeParent();
        removeAcceptDialog();
        removeRemovingProgressDialog();
        super.onDestroy();
    }

    @Override
    public void onClick(int position) {
        if (!presenter.isListOwner()) {
            return;
        }

        ModelShopUser user = shop.getUsers().get(position);
        if (user.getId() != shop.getOwner().getId()) {
            selectedUser = user;
            acceptDialog = createAcceptDialog(selectedUser);
            acceptDialog.show();
        } else {
            showError(R.string.users_error_remove_owner);
        }
    }

    private void removeAcceptDialog() {
        if (acceptDialog != null && acceptDialog.isShowing()) {
            try {
                acceptDialog.dismiss();
            } catch (IllegalArgumentException ignored) {

            }
        }
    }

    private AlertDialog createAcceptDialog(ModelShopUser selectedUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.users_remove_title, selectedUser.getName()));
        builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            showRemovingProgressDialog();
            presenter.removeUser(selectedUser);
        });
        builder.setOnDismissListener(dialog -> {
            ShopFriendActivity.this.selectedUser = null;
            acceptDialog = null;
        });
        builder.setNegativeButton(android.R.string.cancel, (dialog, which) -> {
        });
        return builder.create();
    }
}
