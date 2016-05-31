package com.candy.android.candyapp.helper;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;

import com.candy.android.candyapp.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Marcin
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class)
public class UiHelperTest {

    private Context mockContext;
    private Resources mockResources;
    private DisplayMetrics mockMetrics;
    private int density = 100;

    @Before
    public void setup() {
        mockContext = mock(Context.class);
        mockResources = mock(Resources.class);
        mockMetrics = mock(DisplayMetrics.class);
        when(mockContext.getResources()).thenReturn(mockResources);
        when(mockResources.getDisplayMetrics()).thenReturn(mockMetrics);
        mockMetrics.densityDpi = density;
    }

    @Test
    public void convertDpToPixel() throws Exception {
        int dp = 234;
        int expectedPixels = dp * density / 160;

        assertEquals(expectedPixels, UiHelper.convertDpToPixel(dp, mockContext));
    }
}