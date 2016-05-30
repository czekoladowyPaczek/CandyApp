package com.candy.android.candyapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.MainActivity;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.managers.UserManager;

import javax.inject.Inject;

public class LoginActivity extends AppCompatActivity {

    @Inject
    UserManager userManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((CandyApplication) getApplication()).getActivityComponent().inject(this);
        if (userManager.isLoggedIn()) {
            onLoginSuccess();
            return;
        }

        setContentView(R.layout.activity_login);
    }

    public void onLoginSuccess() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
