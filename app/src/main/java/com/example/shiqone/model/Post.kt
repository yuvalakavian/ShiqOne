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
    var name: String,
    val userID: String,
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
        const val NAME_KEY = "name"
        const val USER_ID_KEY = "userID"
        const val IS_DELETED_KEY = "isChecked"
        const val LAST_UPDATED = "lastUpdated"
        const val LOCAL_LAST_UPDATED = "locaStudentLastUpdated"
        
        fun fromJSON(json: Map<String, Any>): Post {
            val id = json[ID_KEY] as? String ?: ""
            val name = json[NAME_KEY] as? String ?: ""
            val avatarUrl = json[NAME_KEY] as? String ?: ""
            val isChecked = json[IS_DELETED_KEY] as? Boolean ?: false
            val timeStamp = json[LAST_UPDATED] as? Timestamp
            val lastUpdatedLongTimestamp = timeStamp?.toDate()?.time
            return Post(
                id = id,
                name = name,
                userID = avatarUrl,
                isDeleted = isChecked,
                lastUpdated = lastUpdatedLongTimestamp
            )
        }
    }

    val json: Map<String, Any>
        get() = hashMapOf(
                ID_KEY to id,
                NAME_KEY to name,
                USER_ID_KEY to userID,
                IS_DELETED_KEY to isDeleted,
                LAST_UPDATED to FieldValue.serverTimestamp()
            )
        
}
