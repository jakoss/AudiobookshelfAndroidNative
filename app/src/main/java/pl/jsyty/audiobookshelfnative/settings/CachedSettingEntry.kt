package pl.jsyty.audiobookshelfnative.settings

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.*

class CachedSettingEntry<T>(
    private val dataStore: DataStore<Preferences>,
    private val key: Preferences.Key<T>,
    private val defaultValue: T
) {
    private var cachedEntry: T? = null

    suspend fun get(): T {
        val cached = cachedEntry
        if (cached != null) return cached
        return dataStore.data.map { preferences -> preferences[key] ?: defaultValue }.first()
    }

    suspend fun put(value: T) {
        cachedEntry = value
        dataStore.edit { preferences ->
            preferences[key] = value
        }
    }

    fun listen(): Flow<T> {
        return dataStore.data.map { preferences ->
            preferences[key] ?: defaultValue
        }
    }
}

class NullableCachedSettingEntry<T>(
    private val dataStore: DataStore<Preferences>,
    private val key: Preferences.Key<T>,
) {
    private var cachedEntry: T? = null

    suspend fun get(): T? {
        val cached = cachedEntry
        if (cached != null) return cached
        return dataStore.data.map { preferences -> preferences[key] }.first()
    }

    suspend fun put(value: T?) {
        cachedEntry = value
        dataStore.edit { preferences ->
            if (value == null) {
                preferences.remove(key)
            } else {
                preferences[key] = value
            }
        }
    }

    fun listen(): Flow<T?> {
        return dataStore.data.map { preferences ->
            preferences[key]
        }
    }
}
