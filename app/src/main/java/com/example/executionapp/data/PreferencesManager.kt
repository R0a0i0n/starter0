package com.example.executionapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val PRE_INPUT_KEY = stringPreferencesKey("pre_input")
    }

    val preInputFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PRE_INPUT_KEY] ?: ""
        }

    suspend fun savePreInput(preInput: String) {
        context.dataStore.edit { preferences ->
            preferences[PRE_INPUT_KEY] = preInput
        }
    }
}
