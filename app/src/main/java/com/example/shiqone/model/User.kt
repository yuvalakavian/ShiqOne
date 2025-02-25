package com.example.shiqone.model

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shiqone.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.io.Serializable

@Entity
data class User(
    @PrimaryKey val id: String,
    val displayName: String,
    var avatarUri: String,
    val lastUpdated: Long? = null
) : Serializable {

    companion object {
        var lastUpdated: Long
            get() = MyApplication.Globals.context?.getSharedPreferences("TAG", Context.MODE_PRIVATE)
                ?.getLong(LOCAL_LAST_UPDATED, 0) ?: 0

            set(value) {
                MyApplication.Globals.context
                    ?.getSharedPreferences("TAG", Context.MODE_PRIVATE)?.apply {
                        edit().putLong(LOCAL_LAST_UPDATED, value).apply()
                    }
            }

        const val ID_KEY = "id"
        const val DISPLAY_NAME_KEY = "displayName"
        const val AVATAR_URL_KEY = "avatarUrl"
        const val LAST_UPDATED = "lastUpdated"
        const val LOCAL_LAST_UPDATED = "localUserLastUpdated"

        fun fromJSON(json: Map<String, Any>): User {
            return User(
                id = json[ID_KEY] as? String ?: "",
                displayName = json[DISPLAY_NAME_KEY] as? String ?: "",
                avatarUri = json[AVATAR_URL_KEY] as? String ?: "",
                lastUpdated = (json[LAST_UPDATED] as? Timestamp)?.toDate()?.time
            )
        }
    }

    val json: Map<String, Any>
        get() = hashMapOf(
            ID_KEY to id,
            DISPLAY_NAME_KEY to displayName,
            AVATAR_URL_KEY to avatarUri,
            LAST_UPDATED to FieldValue.serverTimestamp()
        )
}
