package com.candy.android.candyapp.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
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

    private View root;
    private ProgressDialog loadingDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        ((CandyApplication) getActivity().getApplication()).getActivityComponent().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, root);

        loginPresenter.setParent(this);

        loginButton.setOnClickListener(v -> loginPresenter.startFacebookLogin());

        return root;
    }

    @Override
    public void onDestroyView() {
        loginPresenter.removeParent();
        removeLoadingView();
        root = null;
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loginPresenter.onActivityResult(requestCode, resultCode, data);
    }

    public void showLoadingView() {
        loadingDialog = ProgressDialog.show(getActivity(), null, getString(R.string.login_login_in_progress), true);
        loadingDialog.show();
    }

    public void removeLoadingView() {
        if (loadingDialog != null) {
            try {
                loadingDialog.dismiss();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
        }
    }

    public void showError(@StringRes int errorMessage) {
        Snackbar.make(root, errorMessage, Snackbar.LENGTH_LONG).show();
    }

    public void onLoginSuccess() {
        ((LoginActivity) getActivity()).onLoginSuccess();
    }
}
