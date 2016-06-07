package com.candy.android.candyapp.testUtils;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.candy.android.candyapp.R;
import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.shop.ShopDetailFragment;
import com.candy.android.candyapp.shop.ShopListFragment;

/**
 * Created by marcingawel on 03.06.2016.
 */

public class DummyActivity extends AppCompatActivity implements ShopListFragment.OnShopItemSelected, ShopDetailFragment.OnListDeletedCallback {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onItemSelected(ModelShop shop) {

    }

    @Override
    public void onListDeleted() {

    }
}
