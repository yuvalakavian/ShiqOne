package com.example.shiqone.model

import EmptyCallback
import android.graphics.Bitmap
import android.os.Looper
import android.util.Log
import androidx.core.os.HandlerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shiqone.model.dao.AppLocalDb
import com.example.shiqone.model.dao.AppLocalDbRepository
import com.example.shiqone.model.networking.WeatherClient
import com.example.shiqone.ui.CloudinaryModel
import com.example.shiqone.model.FirebaseModel
import java.util.concurrent.Executors

class Model private constructor() {

    enum class LoadingState {
        LOADING,
        LOADED
    }

    enum class Storage {
        FIREBASE,
        CLOUDINARY
    }

    private val database: AppLocalDbRepository = AppLocalDb.database
    private var executor = Executors.newSingleThreadExecutor()
    private var mainHandler = HandlerCompat.createAsync(Looper.getMainLooper())
    val posts: LiveData<List<Post>> = database.postDao().getAllPost()
    val loadingState: MutableLiveData<LoadingState> = MutableLiveData<LoadingState>()
    val weather: MutableLiveData<Weather> = MutableLiveData()

    private val firebaseModel = FirebaseModel()
    private val cloudinaryModel = CloudinaryModel()

    companion object {
        val shared = Model()
    }

    fun refreshAllPosts(userID: String = "") {
        loadingState.postValue(LoadingState.LOADING)
        val lastUpdated: Long = Post.lastUpdated
        firebaseModel.getAllPosts(lastUpdated) { posts ->
            executor.execute {
                var currentTime = lastUpdated
                for (post in posts) {
                    // Only process posts that match the userID filter (if specified)
                    if (userID.isEmpty() || post.userID == userID) {
                        database.postDao().insert(post)

                        // Update lastUpdated timestamp only for relevant posts
                        post.lastUpdated?.let {
                            if (currentTime < it) {
                                currentTime = it
                            }
                        }
                    }
                }

                Post.lastUpdated = currentTime
                loadingState.postValue(LoadingState.LOADED)
            }
        }
    }
    fun update(post: Post, callback: EmptyCallback) {
        firebaseModel.update(post, callback)
    }

    fun add(post: Post, image: Bitmap?, storage: Storage, callback: EmptyCallback) {
        firebaseModel.add(post) {
            image?.let {
                uploadTo(
                    storage,
                    image = image,
                    name = post.id.toString(),
                    callback = { uri ->
                        if (!uri.isNullOrBlank()) {
                            val st = post.copy(avatarUri = uri)
                            firebaseModel.add(st, callback)
                        } else {
                            callback()
                        }
                    },
                )
            } ?: callback()
        }
    }

    private fun uploadTo(
        storage: Storage,
        image: Bitmap,
        name: String,
        callback: (String?) -> Unit
    ) {
        when (storage) {
            Storage.FIREBASE -> {
                uploadImageToFirebase(image, name, callback)
            }

            Storage.CLOUDINARY -> {
                uploadImageToCloudinary(
                    bitmap = image,
                    name = name,
                    onSuccess = callback,
                    onError = { callback(null) }
                )
            }
        }
    }

    fun delete(post: Post, callback: EmptyCallback) {
        firebaseModel.delete(post) {
            executor.execute {
                database.postDao().delete(post)
            }
            callback()
        }
    }

    private fun uploadImageToFirebase(
        image: Bitmap,
        name: String,
        callback: (String?) -> Unit
    ) {
        firebaseModel.uploadImage(image, name, callback)
    }

    private fun uploadImageToCloudinary(
        bitmap: Bitmap,
        name: String,
        onSuccess: (String?) -> Unit,
        onError: (String?) -> Unit
    ) {
        cloudinaryModel.uploadImage(
            bitmap = bitmap,
            name = name,
            onSuccess = onSuccess,
            onError = onError
        )
    }

    fun getWeatherForecast() {
        executor.execute {
            try {
                val request = WeatherClient.weatherApiClient.getCurrentWeather()
                val response = request.execute()

                if (response.isSuccessful) {
                    val weather_response_body = response.body()
                    Log.d("TAG", "Fetched weather!")
                    this.weather.postValue(weather_response_body)
                } else {
                    Log.e("TAG", "Failed to fetch weather!")
                }
            } catch (e: Exception) {
                Log.e("TAG", "Failed to fetch weather! with exception ${e}")
            }
        }
    }
}
