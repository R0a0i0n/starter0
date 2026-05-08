package com.example.executionapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.random.Random

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class PreferencesManager(private val context: Context) {

    companion object {
        val PRE_INPUT_KEY = stringPreferencesKey("pre_input")
        val AB_TEST_GROUP_KEY = booleanPreferencesKey("ab_test_group")
    }

    val preInputFlow: Flow<String> = context.dataStore.data
        .map { preferences ->
            preferences[PRE_INPUT_KEY] ?: ""
        }

    val abTestGroupFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[AB_TEST_GROUP_KEY] ?: false
        }

    suspend fun savePreInput(preInput: String) {
        context.dataStore.edit { preferences ->
            preferences[PRE_INPUT_KEY] = preInput
        }
    }

    suspend fun getOrInitializeAbTestGroup(): Boolean {
        var isTestGroup = false
        context.dataStore.edit { preferences ->
            val existing = preferences[AB_TEST_GROUP_KEY]
            if (existing == null) {
                isTestGroup = Random.nextBoolean()
                preferences[AB_TEST_GROUP_KEY] = isTestGroup
            } else {
                isTestGroup = existing
            }
        }
        return isTestGroup
    }
}
