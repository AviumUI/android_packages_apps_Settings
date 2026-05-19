/*
 * Copyright (C) 2025-2026 The AviumUI Project
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
package org.avium.settings.display

import android.app.TimePickerDialog
import android.content.Context
import android.database.ContentObserver
import android.provider.Settings
import android.text.format.DateFormat
import android.widget.TimePicker
import androidx.preference.Preference
import com.android.settingslib.metadata.PreferenceLifecycleContext
import com.android.settingslib.metadata.PreferenceLifecycleProvider
import com.android.settingslib.metadata.PreferenceMetadata
import com.android.settingslib.metadata.PreferenceSummaryProvider
import com.android.settingslib.preference.PreferenceBinding
import java.util.Calendar

class AviumAodScheduleTimePreference(
    override val key: String,
    override val title: Int,
    private val settingKey: String
) : PreferenceMetadata,
    PreferenceBinding,
    PreferenceSummaryProvider,
    PreferenceLifecycleProvider,
    Preference.OnPreferenceClickListener {

    private lateinit var lifecycleContext: PreferenceLifecycleContext
    private var contentObserver: ContentObserver? = null

    override val indexable
        get() = false

    override fun bind(preference: Preference, metadata: PreferenceMetadata) {
        super.bind(preference, metadata)
        preference.onPreferenceClickListener = this
    }

    override fun onCreate(context: PreferenceLifecycleContext) {
        super.onCreate(context)
        lifecycleContext = context
    }

    override fun onStart(context: PreferenceLifecycleContext) {
        val observer = object : ContentObserver(null) {
            override fun onChange(selfChange: Boolean) {
                lifecycleContext.notifyPreferenceChange(key)
            }
        }
        contentObserver = observer
        context.contentResolver.registerContentObserver(
            Settings.Secure.getUriFor(settingKey), false, observer
        )
    }

    override fun onStop(context: PreferenceLifecycleContext) {
        contentObserver?.let { context.contentResolver.unregisterContentObserver(it) }
        contentObserver = null
    }

    override fun getSummary(context: Context): CharSequence {
        return Settings.Secure.getString(context.contentResolver, settingKey) ?: ""
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        val context = preference.context
        val savedTime = Settings.Secure.getString(context.contentResolver, settingKey)
        val calendar = Calendar.getInstance()

        if (savedTime != null) {
            val parts = savedTime.split(":")
            if (parts.size == 2) {
                try {
                    calendar.set(Calendar.HOUR_OF_DAY, parts[0].toInt())
                    calendar.set(Calendar.MINUTE, parts[1].toInt())
                } catch (_: NumberFormatException) {
                }
            }
        }

        TimePickerDialog(
            context,
            { _: TimePicker?, hourOfDay: Int, minute: Int ->
                val time = String.format("%02d:%02d", hourOfDay, minute)
                Settings.Secure.putString(context.contentResolver, settingKey, time)
                lifecycleContext.notifyPreferenceChange(key)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            DateFormat.is24HourFormat(context)
        ).show()
        return true
    }
}
