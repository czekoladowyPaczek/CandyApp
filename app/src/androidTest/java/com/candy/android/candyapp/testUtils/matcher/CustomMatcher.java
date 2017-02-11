package com.candy.android.candyapp.testUtils.matcher;

import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.TextInputLayout;
import android.support.test.espresso.UiController;
import android.support.test.espresso.ViewAction;
import android.view.View;
import android.widget.ProgressBar;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static android.support.test.espresso.action.ViewActions.actionWithAssertions;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;

/**
 * @author Marcin
 */
public class CustomMatcher {
    public static Matcher<View> hasTextInputLayoutErrorText(final String expectedErrorText) {
        return new TypeSafeMatcher<View>() {

            @Override
            public boolean matchesSafely(View view) {
                if (!(view instanceof TextInputLayout)) {
                    return false;
                }

                CharSequence error = ((TextInputLayout) view).getError();

                if (error == null) {
                    return false;
                }

                String hint = error.toString();

                return expectedErrorText.equals(hint);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with error: ");
            }
        };
    }

    public static ViewAction replaceProgressBarDrawable() {
        return actionWithAssertions(new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(ProgressBar.class);
            }

            @Override
            public String getDescription() {
                return "replace the ProgressBar drawable";
            }

            @Override
            public void perform(final UiController uiController, final View view) {
                // Replace the indeterminate drawable with a static red ColorDrawable
                ProgressBar progressBar = (ProgressBar) view;
                progressBar.setIndeterminateDrawable(new ColorDrawable(0xffff0000));
                uiController.loopMainThreadUntilIdle();
            }
        });
    }
}
