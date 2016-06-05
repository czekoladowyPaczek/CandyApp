package com.candy.android.candyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.candy.android.candyapp.model.ModelShop;
import com.candy.android.candyapp.profile.ProfileActivity;
import com.candy.android.candyapp.shop.ShopDetailFragment;
import com.candy.android.candyapp.shop.ShopListFragment;

/**
 * Created by marcingawel on 29.05.2016.
 */

public class MainActivity extends AppCompatActivity implements ShopListFragment.OnShopItemSelected {
    public static final String TAG_SHOP_LIST = "tag_shop_list";
    public static final String TAG_SHOP_DETAIL = "tag_shop_detail";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            ShopListFragment fragment = new ShopListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment, TAG_SHOP_LIST).commit();
        }
    }

    @Override
    public void onItemSelected(ModelShop shop) {
        ShopDetailFragment fragment = new ShopDetailFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment, TAG_SHOP_DETAIL)
                .addToBackStack(TAG_SHOP_DETAIL)
                .commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag(TAG_SHOP_LIST);
        boolean onBackConsumed = false;
        if (fragment != null && fragment instanceof ShopListFragment) {
            onBackConsumed = ((ShopListFragment) fragment).onBackPressed();
        }
        if (!onBackConsumed) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile:
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
