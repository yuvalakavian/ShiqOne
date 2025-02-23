package com.example.shiqone.model

import android.content.Context
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.shiqone.base.MyApplication
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import java.io.Serializable

/*
1. Update json and add updated timestamp
2. Update fromJson serialise the updated date
3. Store locally the lastUpdated timestamp
 */
@Entity
data class Post(
    @PrimaryKey val id: String,
    var content: String,
    val userID: String,
    val avatarUri: String,
    var isDeleted: Boolean,
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
        const val CONTENT_KEY = "content"
        const val USER_ID_KEY = "userID"
        const val AVATAR_URL_KEY = "avatarUrl"
        const val IS_DELETED_KEY = "isChecked"
        const val LAST_UPDATED = "lastUpdated"
        const val LOCAL_LAST_UPDATED = "locaStudentLastUpdated"

        fun fromJSON(json: Map<String, Any>): Post {
            // Get Firebase document ID as String
            val firebaseId = json[ID_KEY] as? String ?: ""

            return Post(
                id = firebaseId, // Set Firebase ID as primary key
                content = json[CONTENT_KEY] as? String ?: "",
                userID = json[USER_ID_KEY] as? String ?: "",
                avatarUri = json[AVATAR_URL_KEY] as? String ?: "",
                isDeleted = json[IS_DELETED_KEY] as? Boolean ?: false,
                lastUpdated = (json[LAST_UPDATED] as? Timestamp)?.toDate()?.time
            )
        }
    }

    val json: Map<String, Any>
        get() = hashMapOf(
                ID_KEY to id,
            CONTENT_KEY to content,
            USER_ID_KEY to userID,
                AVATAR_URL_KEY to avatarUri,
                IS_DELETED_KEY to isDeleted,
                LAST_UPDATED to FieldValue.serverTimestamp()
            )

}
