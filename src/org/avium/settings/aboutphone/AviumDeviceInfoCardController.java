package org.avium.settings.aboutphone;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;

import com.android.settings.R;
import com.android.settingslib.core.AbstractPreferenceController;
import com.android.settingslib.widget.LayoutPreference;

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
            File path = Environment.getDataDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = stat.getBlockSizeLong();
            long totalBlocks = stat.getBlockCountLong();
            long availableBlocks = stat.getAvailableBlocksLong();

            long totalSizeGb = totalBlocks * blockSize / (1024 * 1024 * 1024);
            long availableSizeGb = availableBlocks * blockSize / (1024 * 1024 * 1024);
            long usedSizeGb = totalSizeGb - availableSizeGb;

            return usedSizeGb + "/" + totalSizeGb + " GB";
        } catch (Exception e) {
            return UNKNOWN;
        }
    }

    private String getSystemDisplayVersion() {
        return SystemProperties.get("ro.lineage.display.version", UNKNOWN);
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