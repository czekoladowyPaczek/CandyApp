package com.candy.android.candyapp.shop;

import android.Manifest;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.widget.ProgressBar;

import com.candy.android.candyapp.CandyApplication;
import com.candy.android.candyapp.R;
import com.candy.android.candyapp.graph.component.ActivityComponent;
import com.candy.android.candyapp.managers.ImageUploadManager;
import com.candy.android.candyapp.managers.UserManager;
import com.candy.android.candyapp.model.UploadedImage;
import com.candy.android.candyapp.testUtils.graph.DaggerFakeActivityComponent;
import com.candy.android.candyapp.testUtils.graph.FakePresenterModule;
import com.candy.android.candyapp.testUtils.graph.FakeUserManagerModule;
import com.candy.android.candyapp.testUtils.graph.FakeUtilModule;
import com.candy.android.candyapp.testUtils.matcher.CustomMatcher;
import com.candy.android.candyapp.util.PermissionsHelper;
import com.candy.android.candyapp.util.PictureSelectHelper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import rx.Observable;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withSpinnerText;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.Assert.fail;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Marcin
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class AddItemActivityTest {

    @Mock UserManager userManager;
    @Mock ImageUploadManager imageManager;
    @Mock PictureSelectHelper pictureSelectHelper;
    @Mock PermissionsHelper permissionsHelper;

    @Rule
    public ActivityTestRule<AddItemActivity> activityRule = new ActivityTestRule<>(AddItemActivity.class, false, false);

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        ActivityComponent component = DaggerFakeActivityComponent.builder()
                .fakeUserManagerModule(new FakeUserManagerModule(userManager, imageManager))
                .fakePresenterModule(new FakePresenterModule())
                .fakeUtilModule(new FakeUtilModule(pictureSelectHelper, permissionsHelper))
                .build();

        Instrumentation instrumentation = InstrumentationRegistry.getInstrumentation();
        CandyApplication app = (CandyApplication) instrumentation.getTargetContext().getApplicationContext();
        app.setActivityComponent(component);

        activityRule.launchActivity(new Intent());

        onView(isAssignableFrom(ProgressBar.class)).perform(CustomMatcher.replaceProgressBarDrawable());
    }

    @Test
    public void new_shouldKeepDataAfterRecreate() {
        onView(withId(R.id.item_name)).perform(typeText("test name"));
        onView(withId(R.id.item_quantity)).perform(typeText("1"));
        onView(withId(R.id.item_quantity_type)).perform(click());
        String selectedText = activityRule.getActivity().getResources().getStringArray(R.array.shop_create_quantity_type)[2];
        onData(allOf(is(instanceOf(String.class)), is(selectedText))).perform(click());

        activityRule.getActivity().runOnUiThread(() -> {
            activityRule.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        });

        onView(withId(R.id.item_quantity_type)).check(matches(withSpinnerText(containsString(selectedText))));
        onView(withText("test name")).check(matches(isDisplayed()));
        onView(withText("1")).check(matches(isDisplayed()));
    }

    @Test
    public void new_shouldShowNameErrorWhenNameIsMissing() {
        onView(withId(R.id.item_quantity)).perform(typeText("1"));

        onView(withId(R.id.save_item)).perform(click());

        onView(withText("1")).check(matches(isDisplayed()));
        Activity a = activityRule.getActivity();
        onView(withId(R.id.item_name_layout)).check(matches(CustomMatcher.hasTextInputLayoutErrorText(a.getString(R.string.shop_create_name_error))));
    }

    @Test
    public void new_shouldShowQuantityErrorWhenQuantityIsMissing() {
        onView(withId(R.id.item_name)).perform(typeText("test name"));

        onView(withId(R.id.save_item)).perform(click());

        Activity a = activityRule.getActivity();
        onView(withId(R.id.item_quantity_layout)).check(matches(CustomMatcher.hasTextInputLayoutErrorText(a.getString(R.string.shop_create_quantity_error))));
        onView(withText("test name")).check(matches(isDisplayed()));
    }

    @Test
    public void new_shouldShowBothErrorsWhenBothAreMissing() {
        onView(withText(R.string.shop_create_save)).perform(click());

        Activity a = activityRule.getActivity();
        onView(withId(R.id.item_name_layout)).check(matches(CustomMatcher.hasTextInputLayoutErrorText(a.getString(R.string.shop_create_name_error))));
        onView(withId(R.id.item_quantity_layout)).check(matches(CustomMatcher.hasTextInputLayoutErrorText(a.getString(R.string.shop_create_quantity_error))));
    }

    @Test
    public void new_shouldAllowOnlyNumberOnQuantityView() {
        onView(withId(R.id.item_quantity)).perform(typeText("8888a"));

        onView(withText("8888a")).check(doesNotExist());
        onView(withText("8888")).check(matches(isDisplayed()));
    }

    @Test
    public void new_shouldShowCameraViewIfPermissionsAreGranted() throws Exception {
        when(permissionsHelper.hasPermissions(any(Context.class), anyString())).thenReturn(true);
        when(pictureSelectHelper.getCameraIntent()).thenReturn(new Intent(activityRule.getActivity(), AddItemActivity.class));

        onView(withId(R.id.item_image)).perform(click());

        verify(permissionsHelper).hasPermissions(any(Context.class), eq(Manifest.permission.CAMERA));
        verify(permissionsHelper).hasPermissions(any(Context.class), eq(Manifest.permission.WRITE_EXTERNAL_STORAGE));
        verify(pictureSelectHelper).getCameraIntent();
        verify(permissionsHelper, never()).requestPermissions(any(Activity.class), any(String[].class), anyInt());
    }

    @Test
    public void new_shouldRequestPermissionsIfPermissionsAreNotGranted() throws Exception {
        when(permissionsHelper.hasPermissions(any(Context.class), anyString())).thenReturn(false);
        when(pictureSelectHelper.getCameraIntent()).thenReturn(new Intent(activityRule.getActivity(), AddItemActivity.class));

        onView(withId(R.id.item_image)).perform(click());

        verify(permissionsHelper, atLeast(1)).hasPermissions(any(Context.class), anyString());
        verify(pictureSelectHelper, never()).getCameraIntent();
        verify(permissionsHelper).requestPermissions(any(Activity.class), any(String[].class), anyInt());
    }

    @Test
    public void shouldShowCameraActivityAfterOnSelectImageClicked() throws Exception {
        when(pictureSelectHelper.getCameraIntent()).thenReturn(new Intent(activityRule.getActivity(), AddItemActivity.class));

        onView(withId(R.id.item_image)).perform(click());

        verify(pictureSelectHelper, times(1)).getCameraIntent();
    }

    @Test
    public void shouldSaveImageWhenImageWasSelectedAndDisablePickingImage() throws Exception {
        when(imageManager.uploadImage(anyString())).thenReturn(Observable.just(new UploadedImage()));
        when(pictureSelectHelper.getPath(any(Uri.class))).thenReturn("path");
        when(pictureSelectHelper.getThumbnail(any(Uri.class))).thenReturn(null);

        activityRule.getActivity().runOnUiThread(() -> {
            Intent intent = new Intent();
            intent.setData(Uri.EMPTY);
            activityRule.getActivity().onActivityResult(PictureSelectHelper.CODE_CAMERA, Activity.RESULT_OK, intent);
        });
        onView(withId(R.id.item_image)).perform(click());

        verify(imageManager).uploadImage(anyString());
        verify(pictureSelectHelper, never()).getCameraIntent();
    }

    @Test
    public void shouldDoNothingWhenUserCancelledCamera() {
        when(imageManager.uploadImage(anyString())).thenReturn(Observable.just(new UploadedImage()));
        activityRule.getActivity().onActivityResult(PictureSelectHelper.CODE_CAMERA, Activity.RESULT_CANCELED, null);

        verify(imageManager, never()).uploadImage(anyString());
    }

    @Test
    public void shouldShowErrorWhenErrorOccurredWhilePickingImage() throws Exception {
        when(imageManager.uploadImage(anyString())).thenReturn(Observable.just(new UploadedImage()));
        when(pictureSelectHelper.getPath(any(Uri.class))).thenReturn("path");
        when(pictureSelectHelper.getThumbnail(any(Uri.class))).thenReturn(null);

        activityRule.getActivity().runOnUiThread(() -> {
            Intent intent = new Intent();
            intent.setData(Uri.EMPTY);
            activityRule.getActivity().onActivityResult(PictureSelectHelper.CODE_CAMERA, Activity.RESULT_OK, null);
        });

        verify(imageManager, never()).uploadImage(anyString());
        onView(withText(R.string.shop_create_image_error)).check(matches(isDisplayed()));
    }

    @Test
    public void shouldNotCloseActivityWhenSaveClickedButImageNotYetSaved() {
        fail();
    }

    @Test
    public void shouldResubscribeToPendingImageSaveAfterRecreate() {
        fail();
    }

    private void fillData() {
        onView(withId(R.id.item_quantity)).perform(typeText("1"));
        onView(withId(R.id.item_name)).perform(typeText("name"));
    }

}