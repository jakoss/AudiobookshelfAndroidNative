package pl.jsyty.audiobookshelfnative.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import org.koin.core.annotation.Single

@Single
class Settings(private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

    private val store by lazy {
        context.dataStore
    }

    val token = NullableCachedSettingEntry<String>(store, stringPreferencesKey("token"))
    val serverAddress = NullableCachedSettingEntry<String>(store, stringPreferencesKey("server_address"))
}
