package uk.co.baconi.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

val Context.oAuthDataStore by preferencesDataStore(name = "oAuth")

fun DataStore<Preferences>.get(key: Preferences.Key<String>): Flow<String> {
    return data.mapNotNull { it[key] }
}

suspend fun DataStore<Preferences>.set(key: Preferences.Key<String>, value: String?) {
    edit {
        when (value) {
            null -> it.remove(key)
            else -> it[key] = value
        }
    }
}