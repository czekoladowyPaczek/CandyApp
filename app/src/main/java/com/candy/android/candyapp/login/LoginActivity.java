package com.candy.android.candyapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.candy.android.candyapp.MainActivity;
import com.candy.android.candyapp.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    public void onLoginSuccess() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
