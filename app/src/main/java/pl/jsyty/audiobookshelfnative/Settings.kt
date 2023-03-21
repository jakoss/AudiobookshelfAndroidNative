package pl.jsyty.audiobookshelfnative

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import org.koin.core.annotation.Single

@Single
class Settings(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val store by lazy {
        context.dataStore
    }
    private var tokenCache: String? = null

    suspend fun getToken(): String {
        val cached = tokenCache
        if (cached != null) return cached
        return store.data.map { preferences -> preferences[tokenKey] ?: "" }.first()
    }

    suspend fun saveToken(token: String) {
        tokenCache = token
        store.edit { preferences ->
            preferences[tokenKey] = token
        }
    }

    private val tokenKey = stringPreferencesKey("token")
}
