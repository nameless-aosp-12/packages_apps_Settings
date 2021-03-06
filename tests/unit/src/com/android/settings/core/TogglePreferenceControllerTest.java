/*
 * Copyright (C) 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.settings.core;

import static com.google.common.truth.Truth.assertThat;

import android.content.Context;

import com.android.settings.custom.preference.SwitchPreference;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.android.settings.slices.SliceData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TogglePreferenceControllerTest {

    private FakeToggle mToggleController;

    private Context mContext;
    private SwitchPreference mPreference;

    @Before
    public void setUp() {
        mContext = ApplicationProvider.getApplicationContext();
        mPreference = new SwitchPreference(mContext);
        mToggleController = new FakeToggle(mContext, "key");
    }

    @Test
    public void testSetsPreferenceValue_setsChecked() {
        mToggleController.setChecked(true);
        mPreference.setChecked(false);

        mToggleController.updateState(mPreference);

        assertThat(mPreference.isChecked()).isTrue();
    }

    @Test
    public void testSetsPreferenceValue_setsNotChecked() {
        mToggleController.setChecked(false);
        mPreference.setChecked(true);

        mToggleController.updateState(mPreference);

        assertThat(mPreference.isChecked()).isFalse();
    }

    @Test
    public void testUpdatesPreferenceOnChange_turnsOn() {
        boolean newValue = true;
        mToggleController.setChecked(!newValue);

        mToggleController.onPreferenceChange(mPreference, newValue);

        assertThat(mToggleController.isChecked()).isEqualTo(newValue);
    }

    @Test
    public void testUpdatesPreferenceOnChange_turnsOff() {
        boolean newValue = false;
        mToggleController.setChecked(!newValue);

        mToggleController.onPreferenceChange(mPreference, newValue);

        assertThat(mToggleController.isChecked()).isEqualTo(newValue);
    }

    @Test
    public void testSliceType_returnsSliceType() {
        assertThat(mToggleController.getSliceType()).isEqualTo(
                SliceData.SliceType.SWITCH);
    }

    @Test
    public void isSliceable_returnTrue() {
        assertThat(mToggleController.isSliceable()).isTrue();
    }

    @Test
    public void isPublicSlice_returnFalse() {
        assertThat(mToggleController.isPublicSlice()).isFalse();
    }

    private static class FakeToggle extends TogglePreferenceController {

        private boolean mCheckedFlag;

        private FakeToggle(Context context, String preferenceKey) {
            super(context, preferenceKey);
        }

        @Override
        public boolean isChecked() {
            return mCheckedFlag;
        }

        @Override
        public boolean setChecked(boolean isChecked) {
            mCheckedFlag = isChecked;
            return true;
        }

        @Override
        public int getSliceHighlightMenuRes() {
            return 5678;
        }

        @Override
        public int getAvailabilityStatus() {
            return AVAILABLE;
        }
    }
}
