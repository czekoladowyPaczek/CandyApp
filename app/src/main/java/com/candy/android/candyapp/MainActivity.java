package com.candy.android.candyapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.candy.android.candyapp.profile.ProfileActivity;
import com.candy.android.candyapp.shop.ShopListFragment;

/**
 * Created by marcingawel on 29.05.2016.
 */

public class MainActivity extends AppCompatActivity {
    public static final String TAG_SHOP_LIST = "tag_shop_list";
    private ShopListFragment fragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            fragment = new ShopListFragment();
            getSupportFragmentManager().beginTransaction().add(R.id.container, fragment, TAG_SHOP_LIST).commit();
        } else {
            fragment = (ShopListFragment) getSupportFragmentManager().findFragmentByTag(TAG_SHOP_LIST);
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
