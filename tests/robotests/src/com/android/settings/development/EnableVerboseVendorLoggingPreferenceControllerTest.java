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
package com.android.settings.development;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.hardware.dumpstate.V1_1.IDumpstateDevice;

import androidx.preference.PreferenceScreen;
import com.android.settings.custom.preference.SwitchPreference;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import java.lang.reflect.Field;

@RunWith(RobolectricTestRunner.class)
public final class EnableVerboseVendorLoggingPreferenceControllerTest {
    @Mock
    private SwitchPreference mPreference;
    @Mock
    private PreferenceScreen mPreferenceScreen;
    @Mock
    IDumpstateDevice mIDumpstateDevice;

    private Context mContext;
    private EnableVerboseVendorLoggingPreferenceController mController;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mContext = RuntimeEnvironment.application;
        mController = spy(new EnableVerboseVendorLoggingPreferenceController(mContext));
        doReturn(mIDumpstateDevice).when(mController).getDumpstateDeviceService();

        // mock with Dumpstate HAL v1.1
        Field f = EnableVerboseVendorLoggingPreferenceController.class
                .getDeclaredField("mDumpstateHalVersion");
        f.setAccessible(true);
        f.setInt(mController, 1 /* DUMPSTATE_HAL_VERSION_1_1 */);

        when(mPreferenceScreen.findPreference(mController.getPreferenceKey()))
                .thenReturn(mPreference);
        mController.displayPreference(mPreferenceScreen);
    }

    @Test
    public void onPreferenceChange_settingEnable_enableVendorLoggingShouldBeOn() throws Exception {
        doReturn(true).when(mIDumpstateDevice).getVerboseLoggingEnabled();

        mController.onPreferenceChange(mPreference, true /* new value */);

        final boolean enabled = mController.getVerboseLoggingEnabled();
        assertTrue(enabled);
    }

    @Test
    public void onPreferenceChange_settingDisable_enableVendorLoggingShouldBeOff()
            throws Exception {
        doReturn(false).when(mIDumpstateDevice).getVerboseLoggingEnabled();

        mController.onPreferenceChange(mPreference,  false /* new value */);

        final boolean enabled = mController.getVerboseLoggingEnabled();
        assertFalse(enabled);
    }

    @Test
    public void updateState_settingDisabled_preferenceShouldNotBeChecked() throws Exception {
        doReturn(false).when(mIDumpstateDevice).getVerboseLoggingEnabled();

        mController.setVerboseLoggingEnabled(false);
        mController.updateState(mPreference);

        verify(mPreference).setChecked(false);
    }

    @Test
    public void updateState_settingEnabled_preferenceShouldBeChecked() throws Exception {
        doReturn(true).when(mIDumpstateDevice).getVerboseLoggingEnabled();

        mController.setVerboseLoggingEnabled(true);
        mController.updateState(mPreference);

        verify(mPreference).setChecked(true);
    }

    @Test
    public void onDeveloperOptionDisabled_shouldDisablePreference() throws Exception {
        doReturn(false).when(mIDumpstateDevice).getVerboseLoggingEnabled();

        mController.onDeveloperOptionsSwitchDisabled();

        final boolean enabled = mController.getVerboseLoggingEnabled();
        assertFalse(enabled);
        verify(mPreference).setChecked(false);
        verify(mPreference).setEnabled(false);
    }
}
