/*
 * Copyright (C) 2022, Kasem S.M
 * All rights reserved.
 */
package kasem.sm.data.session

import android.content.SharedPreferences
import androidx.core.content.edit
import javax.inject.Inject
import kasem.sm.core.domain.SlimeDispatchers
import kasem.sm.core.interfaces.Session
import kasem.sm.data.db.SlimeDatabase
import kasem.sm.data.util.observeKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.withContext

class SessionImpl @Inject constructor(
    private val pref: SharedPreferences,
    private val db: SlimeDatabase,
    private val slimeDispatchers: SlimeDispatchers,
) : Session {
    override suspend fun storeUserToken(token: String?) {
        pref.edit {
            putString(AUTH_TOKEN_KEY, token).apply()
        }
    }

    override fun getUserToken(): String? {
        return pref.getString(AUTH_TOKEN_KEY, null)
    }

    override suspend fun storeUserId(id: String?) {
        pref.edit {
            putString(AUTH_ID_KEY, id).apply()
        }
    }

    override fun getUserId(): String? {
        return pref.getString(AUTH_ID_KEY, null)
    }

    override fun observeAuthenticationState(): Flow<Boolean> = channelFlow {
        pref.observeKey(AUTH_TOKEN_KEY, defValue = "").collectLatest {
            send(it.isNotBlank())
        }
    }

    override suspend fun clear() {
        pref.edit { clear().commit() }
        withContext(slimeDispatchers.io) {
            db.categoryDao().clearSubscription()
        }
    }

    companion object {
        const val AUTH_TOKEN_KEY = "kasem.sm.slime.user_auth_token"
        const val AUTH_ID_KEY = "kasem.sm.slime.user_id"
    }
}