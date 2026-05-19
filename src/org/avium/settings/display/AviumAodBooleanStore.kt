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
import android.provider.Settings
import com.android.settingslib.datastore.AbstractKeyedDataObservable
import com.android.settingslib.datastore.KeyValueStore
import com.android.settingslib.datastore.KeyedObserver

@Suppress("UNCHECKED_CAST")
class AviumAodBooleanStore(
    private val context: Context,
    private val settingsKey: String,
    private val default: Boolean = false,
) : AbstractKeyedDataObservable<String>(), KeyedObserver<String>, KeyValueStore {

    override fun contains(key: String) = true

    override fun <T : Any> getDefaultValue(key: String, valueType: Class<T>) = default as T

    override fun <T : Any> getValue(key: String, valueType: Class<T>): T? {
        val intVal = Settings.Secure.getInt(context.contentResolver, settingsKey, if (default) 1 else 0)
        return (intVal != 0) as? T
    }

    override fun <T : Any> setValue(key: String, valueType: Class<T>, value: T?) {
        Settings.Secure.putInt(context.contentResolver, settingsKey, if (value as? Boolean == true) 1 else 0)
    }

    override fun onFirstObserverAdded() {
    }

    override fun onLastObserverRemoved() {
    }

    override fun onKeyChanged(key: String, reason: Int) {
    }
}
