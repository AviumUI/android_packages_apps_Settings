/*
 * Copyright (C) 2025 The AviumUI Project
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
package org.avium.settings.aboutphone;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;
import com.android.settingslib.deviceinfo.StorageManagerVolumeProvider;

import java.io.File;

public class AviumDeviceInfoCardController extends AbstractPreferenceController {

    private LayoutPreference mLayoutPreference;
    private static final String UNKNOWN = "Unknown"; 

    public AviumDeviceInfoCardController(Context context, String key) {
        super(context);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public String getPreferenceKey() {
        return "avium_device_info_card";
    }

    @Override
    public void displayPreference(PreferenceScreen screen) {
        super.displayPreference(screen);
        mLayoutPreference = screen.findPreference(getPreferenceKey());
        if (mLayoutPreference != null) {
            updateState(mLayoutPreference);
        }
    }

    @Override
    public void updateState(Preference preference) {
        if (mLayoutPreference == null) {
            return;
        }

        // model
        TextView modelView = mLayoutPreference.findViewById(R.id.device_model_value);
        modelView.setText(SystemProperties.get("ro.product.model", Build.MODEL));

        // soc
        TextView processorView = mLayoutPreference.findViewById(R.id.processor_value);
        processorView.setText(SystemProperties.get("ro.soc.model", UNKNOWN));

        // android version
        TextView androidVersionView = mLayoutPreference.findViewById(R.id.android_version_value);
        androidVersionView.setText(Build.VERSION.RELEASE);

        // ram
        TextView ramView = mLayoutPreference.findViewById(R.id.ram_value);
        ramView.setText(getTotalRam());

        // rom
        TextView storageView = mLayoutPreference.findViewById(R.id.storage_value);
        storageView.setText(getStorageInfo());

        // ssystem version
        TextView systemVersionView = mLayoutPreference.findViewById(R.id.system_version_value);
        systemVersionView.setText(getSystemDisplayVersion());
        
        // screen
        TextView resolutionView = mLayoutPreference.findViewById(R.id.screen_resolution_value);
        resolutionView.setText(getScreenResolution());
    }

    private String getTotalRam() {
        ActivityManager actManager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
        actManager.getMemoryInfo(memInfo);
        double totalRamBytes = memInfo.totalMem;
        double totalRamGb = totalRamBytes / (1024 * 1024 * 1024);
        long ramGb = (long) Math.ceil(totalRamGb);
        return ramGb + " GB";
    }

    private String getStorageInfo() {
        try {
            long totalBytes = 0L;
            StorageManager sm = mContext.getSystemService(StorageManager.class);
            if (sm != null) {
                StorageManagerVolumeProvider smvp = new StorageManagerVolumeProvider(sm);
                totalBytes = smvp.getPrimaryStorageSize();
            }
               
            if (totalBytes <= 0) {
                File totalPath = Environment.getDataDirectory();
                StatFs totalStat = new StatFs(totalPath.getPath());
                totalBytes = totalStat.getBlockSizeLong() * totalStat.getBlockCountLong();
            }

            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long freeBytes = stat.getAvailableBytes();

            long totalG = Math.round(totalBytes / 1_000_000_000d);
            long freeG = (long) Math.floor(freeBytes / 1_000_000_000d);
            if (freeG < 0) freeG = 0;
            long usedG = totalG - freeG;

            return usedG + "/" + totalG + "G";
        } catch (Exception e) {
            return UNKNOWN;
        }
    }

    private String getSystemDisplayVersion() {
        return SystemProperties.get("ro.avium.display.version", UNKNOWN);
    }

    private String getScreenResolution() {
        try {
            WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
            final DisplayMetrics metrics = new DisplayMetrics();
            if (wm != null) {
                wm.getDefaultDisplay().getRealMetrics(metrics);
                return metrics.heightPixels + " x " + metrics.widthPixels;
            }
        } catch (Exception e) {
        }
        return UNKNOWN;
    }
}