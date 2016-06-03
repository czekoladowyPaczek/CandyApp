package com.candy.android.candyapp.shop;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;

import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.shop.adapter.ShopAdapter;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by marcingawel on 02.06.2016.
 */

public class ShopListFragment extends Fragment {

    private View root;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.shop_list)
    RecyclerView shopList;
    private ShopAdapter adapter;
    private List<ModelShop> shops;

    @Inject
    ShopListPresenter presenter;

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
        adapter = new ShopAdapter(getContext(), shops);
        shopList.setLayoutManager(new LinearLayoutManager(getContext()));
        shopList.setAdapter(adapter);

        presenter.setParent(this);
    }

    @Override
    public void onDestroyView() {
        adapter = null;
        shopList = null;
        root = null;
        toolbar = null;
        presenter.removeParent();
        super.onDestroyView();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);
    }

    public void showListLoading() {

    }

    public void showLoadingDialog(@StringRes int res) {

    }
}
