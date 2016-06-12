package com.candy.android.candyapp.shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.SparseArray;
import android.view.View;

import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.shop.holder.ShopUserHolder;
import com.candy.android.candyapp.ui.AdapterClickListener;
import com.candy.android.candyapp.ui.UserAdapter;

import java.util.ArrayList;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

/**
 * Created by marcingawel on 11.06.2016.
 */

public class ShopFriendActivity extends AppCompatActivity implements AdapterClickListener {
    public static final String SHOP = "com.candy.android.shop";

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

    private UserAdapter<ShopUserHolder> adapter;
    private LinearLayoutManager manager;
    private ModelShop shop;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            shop = savedInstanceState.getParcelable(SHOP);
        } else {
            shop = getIntent()!= null ? getIntent().getParcelableExtra(SHOP) : null;
        }
        if (shop == null) {
            finish();
            return;
        } else {
            shop = new ModelShop(shop.getId(), shop.getOwner(), new ArrayList<>(shop.getUsers()), shop.getName(), shop.getModificationDate());
        }

        setContentView(R.layout.activity_shop_users);
        ((CandyApplication) getApplication()).getActivityComponent().inject(this);
        ButterKnife.bind(this);
        root = findViewById(R.id.root);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(shop.getName());

        adapter = new UserAdapter<>(this, shop.getUsers(), new ShopUserHolder.Builder(), this);
        manager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(() -> presenter.refreshData());

        presenter.setParent(this, shop, getLastCustomNonConfigurationInstance());
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SHOP, shop);
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
        super.onDestroy();
    }

    @Override
    public void onClick(int position) {

    }
}
