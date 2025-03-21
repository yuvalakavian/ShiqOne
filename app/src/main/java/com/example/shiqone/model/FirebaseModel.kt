package com.example.shiqone.model

import EmptyCallback
import PostsCallback
import android.graphics.Bitmap
import android.util.Log
import com.example.shiqone.model.Post
import com.google.firebase.firestore.firestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.memoryCacheSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.example.shiqone.utils.extensions.toFirebaseTimestamp
import com.google.firebase.Timestamp
import java.io.ByteArrayOutputStream

class FirebaseModel {

    private val database = Firebase.firestore
    private val storage = Firebase.storage

    init {

        val settings = firestoreSettings {
            setLocalCacheSettings(memoryCacheSettings {  })
        }
        database.firestoreSettings = settings
    }

    fun getAllPosts(sinceLastUpdated: Long, callback: PostsCallback) {
        database.collection(Constants.Collections.POSTS)
            .whereGreaterThanOrEqualTo(Post.LAST_UPDATED, sinceLastUpdated.toFirebaseTimestamp)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val posts = mutableListOf<Post>()
                    for (document in task.result) {
                        // Create Post with Firebase document ID + data
                        val post = Post(
                            id = document.id, // Firebase document ID as primary key
                            content = document.getString(Post.CONTENT_KEY) ?: "",
                            userID = document.getString(Post.USER_ID_KEY) ?: "",
                            avatarUri = document.getString(Post.AVATAR_URL_KEY) ?: "",
                            isDeleted = document.getBoolean(Post.IS_DELETED_KEY) ?: false,
                            lastUpdated = document.getTimestamp(Post.LAST_UPDATED)?.toDate()?.time,
                            userName= document.getString(Post.USERNAME_KEY) ?: "",
                        )
                        posts.add(post)
                    }
                    Log.d("Firestore", "Fetched ${posts.size} posts")
                    callback(posts)
                } else {
                    Log.e("Firestore", "Error getting posts", task.exception)
                    callback(emptyList())
                }
            }
    }

    fun add(post: Post, callback: EmptyCallback) {
        database.collection(Constants.Collections.POSTS).document(post.id.toString()).set(post.json)
            .addOnCompleteListener {
                callback()
            }
            .addOnFailureListener {
                Log.e("BAD", it.toString() + it.message)
            }
    }

    fun delete(post: Post, callback: EmptyCallback) {
        database.collection(Constants.Collections.POSTS).document(post.id.toString()).delete()
        .addOnCompleteListener {
            callback()
        }
        .addOnFailureListener {
            Log.e("BAD", it.toString() + it.message)
        }
    }

    fun uploadImage(image: Bitmap, name: String, callback: (String?) -> Unit) {
        val storageRef = storage.reference
        val imageRef = storageRef.child("images/$name.jpg")
        val baos = ByteArrayOutputStream()
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var uploadTask = imageRef.putBytes(data)
        uploadTask.addOnFailureListener {
            callback(null)
        }.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { uri ->
                callback(uri.toString())
            }
        }
    }

    fun update(post: Post, callback: EmptyCallback) {
        post.lastUpdated = Timestamp.now().seconds
        database.collection(Constants.Collections.POSTS).document(post.id.toString()).set(post.json)
            .addOnCompleteListener {
                callback()
            }
            .addOnFailureListener {
                Log.e("BAD", it.toString() + it.message)
            }
    }

    fun getUser(userId: String, callback: (User?) -> Unit) {
        database.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val user = User(
                        id = document.id,
                        displayName = document.getString(User.DISPLAY_NAME_KEY) ?: "",
                        avatarUri = document.getString(User.AVATAR_URL_KEY) ?: "",
                        lastUpdated = document.getTimestamp(User.LAST_UPDATED)?.toDate()?.time
                    )
                    callback(user)
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error fetching user", it)
                callback(null)
            }
    }

    fun addUser(user: User, callback: EmptyCallback) {
        database.collection("users").document(user.id).set(user.json)
            .addOnCompleteListener {
                callback()
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error adding user", it)
            }
    }

    fun updateUser(user: User, callback: EmptyCallback) {
        database.collection("users").document(user.id).set(user.json)
            .addOnCompleteListener {
                callback()
            }
            .addOnFailureListener {
                Log.e("Firebase", "Error updating user", it)
            }
    }
}