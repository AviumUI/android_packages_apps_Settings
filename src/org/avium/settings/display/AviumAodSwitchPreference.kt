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

import android.content.Context
import com.android.settingslib.datastore.KeyValueStore
import com.android.settingslib.datastore.SettingsSystemStore
import com.android.settingslib.metadata.ReadWritePermit
import com.android.settingslib.metadata.SensitivityLevel
import com.android.settingslib.metadata.SwitchPreference
import com.android.settingslib.preference.SwitchPreferenceBinding

class AviumAodSwitchPreference(
    context: Context,
    key: String,
    title: Int,
    summary: Int,
    private val settingsKey: String,
) : SwitchPreference(key, title, summary),
    SwitchPreferenceBinding {

    private val dataStore = AviumAodBooleanStore(context, settingsKey)

    override fun storage(context: Context): KeyValueStore = dataStore

    override fun getReadPermissions(context: Context) = SettingsSystemStore.getReadPermissions()

    override fun getWritePermissions(context: Context) = SettingsSystemStore.getWritePermissions()

    override fun getReadPermit(context: Context, callingPid: Int, callingUid: Int) =
        ReadWritePermit.ALLOW

    override fun getWritePermit(
        context: Context,
        value: Boolean?,
        callingPid: Int,
        callingUid: Int
    ) = ReadWritePermit.ALLOW

    override val sensitivityLevel
        get() = SensitivityLevel.NO_SENSITIVITY

    companion object {
        const val KEY_SCHEDULE = "avium_aod_schedule_enabled"
        const val KEY_SHAKE = "avium_aod_shake_enabled"
    }
}
