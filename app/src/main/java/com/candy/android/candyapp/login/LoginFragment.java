package com.candy.android.candyapp.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @author Marcin
 */

public class LoginFragment extends Fragment {

    @Inject
    LoginPresenter loginPresenter;

    @BindView(R.id.loginButton)
    Button loginButton;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        ((CandyApplication) getActivity().getApplication()).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, root);

        loginPresenter.setParent(this);

        loginButton.setOnClickListener(v -> loginPresenter.startFacebookLogin());

        return root;
    }

    @Override
    public void onDestroyView() {
        loginPresenter.removeParent();
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginPresenter.onActivityResult(requestCode, resultCode, data);
    }
}
