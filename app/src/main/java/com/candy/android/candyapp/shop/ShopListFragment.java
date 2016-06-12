package com.candy.android.candyapp.shop;

import android.animation.Animator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Build;
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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.EditText;

import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.helper.UiHelper;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.shop.holder.ShopViewHolder;
import com.candy.android.candyapp.ui.ShopAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marcingawel on 02.06.2016.
 */

public class ShopListFragment extends Fragment {
    public static final String SAVE_NAME = "save_name";

    public interface OnShopItemSelected {
        void onItemSelected(ModelShop shop);
    }

    private View root;
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
    @BindView(R.id.create_shop_list_layout)
    View container;
    @BindView(R.id.create_shop_accept)
    FloatingActionButton createShopAccept;
    @BindView(R.id.create_shop_name)
    EditText shopNameView;

    private ShopAdapter<ShopViewHolder> adapter;
    private LinearLayoutManager layoutManager;
    private List<ModelShop> shops;

    @Inject
    ShopListPresenter presenter;

    private Dialog shopCreateDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        setHasOptionsMenu(true);

        CandyApplication.getApplication().getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_shop_list, container, false);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        ButterKnife.bind(this, root);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        shops = new ArrayList<>();
        adapter = new ShopAdapter<>(getContext(), shops, new ShopViewHolder.Builder(),
                position -> ((OnShopItemSelected) getActivity()).onItemSelected(shops.get(position)));
        layoutManager = new LinearLayoutManager(getContext());
        shopList.setLayoutManager(layoutManager);
        shopList.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(() -> presenter.getShopLists(false));
        createShopButton.setOnClickListener(v -> createShoppingList(true));
        createShopAccept.setOnClickListener(v -> {
            String name = shopNameView.getText().toString();
            if (!TextUtils.isEmpty(name)) {
                presenter.createShopList(name);
                onBackPressed();
            }
        });
        if (savedInstanceState != null) {
            String name = savedInstanceState.getString(SAVE_NAME, null);
            if (name != null) {
                shopNameView.setText(name);
                createShoppingList(false);
            }
        }

        presenter.setParent(this);
    }

    @Override
    public void onDestroyView() {
        adapter = null;
        shopList = null;
        root = null;
        toolbar = null;
        presenter.removeParent();
        refreshLayout = null;
        emptyView = null;
        createShopButton = null;
        container = null;
        createShopAccept = null;
        shopNameView = null;
        layoutManager = null;

        hideLoadingDialog();
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (container != null && container.getVisibility() == View.VISIBLE) {
            outState.putString(SAVE_NAME, shopNameView.getText().toString());
        }
    }

    public void showListLoading(boolean refreshing) {
        refreshLayout.setRefreshing(refreshing);
    }

    public void showLoadingDialog(@StringRes int res) {
        shopCreateDialog = ProgressDialog.show(getContext(), "", getString(res), true, true);
        shopCreateDialog.setOnCancelListener(dialog -> presenter.cancelShopCreating());
        shopCreateDialog.show();
    }

    public void hideLoadingDialog() {
        if (shopCreateDialog != null) {
            try {
                shopCreateDialog.dismiss();
                shopCreateDialog = null;
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void setData(List<ModelShop> shops) {
        int oldSize = adapter.getItemCount();
        this.shops.clear();
        adapter.notifyItemRangeRemoved(0, oldSize);
        this.shops.addAll(shops);
        adapter.notifyItemRangeInserted(0, shops.size());
        changeEmptyViewVisibility();
    }

    private void changeEmptyViewVisibility() {
        if (shops.size() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    public void addData(ModelShop shop) {
        this.shops.add(0, shop);
        adapter.notifyItemInserted(0);

        if (layoutManager.findFirstCompletelyVisibleItemPosition() == 0) {
            layoutManager.scrollToPosition(0);
        }

        changeEmptyViewVisibility();
    }

    public void showError(@StringRes int resId) {
        Snackbar.make(root, resId, Snackbar.LENGTH_LONG).show();
    }

    public boolean onBackPressed() {
        if (container != null && container.getVisibility() == View.VISIBLE) {
            container.setVisibility(View.GONE);
            createShopButton.setVisibility(View.VISIBLE);
            return true;
        }
        return false;
    }

    private void createShoppingList(boolean animate) {
        container.setVisibility(View.VISIBLE);
        if (animate && Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int width = UiHelper.getScreenWidth(getContext());
            int margin = UiHelper.convertDpToPixel(45, getContext());
            int x = UiHelper.getScreenWidth(getContext()) - margin;
            Animator anim = ViewAnimationUtils.createCircularReveal(container, x, margin, margin * 0.70f, width);
            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    createShopButton.setVisibility(View.GONE);
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
            createShopButton.setVisibility(View.GONE);
        }
    }
}
