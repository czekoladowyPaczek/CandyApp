package com.candy.android.candyapp.shop;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.model.ModelShopItem;
import com.candy.android.candyapp.shop.adapter.ShopItemsAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marcingawel on 05.06.2016.
 */

public class ShopDetailFragment extends Fragment {
    public static final String LIST_ID = "list_id";
    private View root;

    @Inject
    ShopDetailPresenter presenter;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.shop_list)
    RecyclerView shopList;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.empty_layout)
    View emptyView;

    @BindView(R.id.createShopButton)
    FloatingActionButton createShopButton;

    private ShopItemsAdapter adapter;
    private List<ModelShopItem> items;

    private Dialog removingDialog;

    public static ShopDetailFragment getInstance(ModelShop shop) {
        Bundle args = new Bundle(1);
        args.putParcelable(LIST_ID, shop);
        ShopDetailFragment fragment = new ShopDetailFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CandyApplication.getApplication().getActivityComponent().inject(this);
        setRetainInstance(true);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_shop_detail, container, false);

        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, root);

        ModelShop shop = getArguments().getParcelable(LIST_ID);

        AppCompatActivity activity = ((AppCompatActivity) getActivity());
        activity.setSupportActionBar(toolbar);
        activity.getSupportActionBar().setTitle(shop.getName());
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setDisplayShowHomeEnabled(true);

        items = new ArrayList<>();
        adapter = new ShopItemsAdapter(getContext(), items, null);
        shopList.setLayoutManager(new LinearLayoutManager(getContext()));
        shopList.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(() -> presenter.getShopListItems(false));

        presenter.setParent(shop.getId(), this);
    }

    @Override
    public void onDestroyView() {
        adapter = null;
        shopList = null;
        root = null;
        toolbar = null;
        refreshLayout = null;
        emptyView = null;
        createShopButton = null;
        presenter.removeParent();
        hideRemovingDialog();
        super.onDestroyView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case R.id.menu_delete:
                presenter.deleteList();
                showRemovingDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showRemovingDialog() {
        removingDialog = ProgressDialog.show(getContext(), null, getString(R.string.detail_deleting_list), true, false);
        removingDialog.show();
    }

    public void hideRemovingDialog() {
        if (removingDialog != null) {
            try {
                removingDialog.dismiss();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } finally {
                removingDialog = null;
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.shop_detail_menu, menu);
    }

    public void setData(List<ModelShopItem> items) {
        int count = this.items.size();
        this.items.clear();
        adapter.notifyItemRangeRemoved(0, count);
        if (items.size() > 0) {
            this.items.addAll(items);
            adapter.notifyItemRangeInserted(0, items.size());
            emptyView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    public void showListLoading(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    public void showError(@StringRes int res) {
        Snackbar.make(root, res, Snackbar.LENGTH_LONG).show();
    }
}
