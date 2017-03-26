package com.candy.android.candyapp.shop;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.managers.ImageUploadManager;
import com.candy.android.candyapp.model.UploadedImage;
import com.candy.android.candyapp.util.PermissionsHelper;
import com.candy.android.candyapp.util.PictureSelectHelper;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;

/**
 * @author Marcin
 */
public class AddItemActivity extends AppCompatActivity {
    private static final String SAVE_PATH = "path";
    private static final int PERMISSION_REQUEST_CODE = 1;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.item_name_layout)
    TextInputLayout nameLayout;
    @BindView(R.id.item_name)
    TextInputEditText name;
    @BindView(R.id.item_image)
    ImageView image;

    @BindView(R.id.item_quantity_layout)
    TextInputLayout quantityLayout;
    @BindView(R.id.item_quantity)
    TextInputEditText quantity;
    @BindView(R.id.item_image_saving)
    View imageLoader;

    @Inject
    PictureSelectHelper pictureHelper;
    @Inject
    ImageUploadManager uploadManager;
    @Inject
    PermissionsHelper permissionsHelper;

    private String localPath;
    private Observable<UploadedImage> imageObservable;
    private Subscription imageSubscription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        ButterKnife.bind(this);
        ((CandyApplication) getApplication()).getActivityComponent().inject(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        image.setOnClickListener((v) -> {
            if (!permissionsHelper.hasPermissions(this, Manifest.permission.CAMERA) ||
                    !permissionsHelper.hasPermissions(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionsHelper.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                        PERMISSION_REQUEST_CODE);
                return;
            }
            try {
                startActivityForResult(pictureHelper.getCameraIntent(), PictureSelectHelper.CODE_CAMERA);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        if (savedInstanceState != null) {
            localPath = savedInstanceState.getString(SAVE_PATH, null);

            if (localPath != null) {
                setThumbnail(localPath);
                image.setOnClickListener(null);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_idem_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                return true;
            case R.id.save_item:
                if (validateViews()) {

                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case PictureSelectHelper.CODE_CAMERA: {
                if (resultCode == RESULT_OK) {
                    if (data == null || data.getData() == null) {
                        Toast.makeText(this, R.string.shop_create_image_error, Toast.LENGTH_LONG).show();
                        return;
                    }

                    localPath = pictureHelper.getPath(data.getData());
                    setThumbnail(localPath);
                    image.setOnClickListener(null);
                    imageLoader.setVisibility(View.VISIBLE);
                    imageObservable = uploadManager.uploadImage(localPath).cache();
                    subscribeToImage(imageObservable);
                }
            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    image.performClick();
                } else {
                    Toast.makeText(this, R.string.no_permissions, Toast.LENGTH_LONG).show();
                }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (localPath != null) {
            outState.putString(SAVE_PATH, localPath);
        }
    }

    private Subscription subscribeToImage(Observable<UploadedImage> obs) {
        return obs.subscribe(image -> {
            imageObservable = null;

        }, error -> {
            imageObservable = null;
            localPath = null;
            Toast.makeText(this, R.string.shop_create_image_error, Toast.LENGTH_LONG).show();
        });
    }

    private boolean validateViews() {
        boolean isEmpty = false;
        if (TextUtils.isEmpty(name.getText())) {
            nameLayout.setError(getString(R.string.shop_create_name_error));
            isEmpty = true;
        } else {
            nameLayout.setError(null);
        }
        if (TextUtils.isEmpty(quantity.getText())) {
            quantityLayout.setError(getString(R.string.shop_create_quantity_error));
            isEmpty = true;
        } else {
            quantityLayout.setError(null);
        }

        return isEmpty;
    }

    private void setThumbnail(String path) {
        image.setImageBitmap(pictureHelper.getThumbnail(path));
    }
}
