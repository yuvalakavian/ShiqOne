package com.example.shiqone.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shiqone.model.FirebaseModel
import com.example.shiqone.model.Model
import com.example.shiqone.model.Weather
import com.example.shiqone.model.dao.AppLocalDb.database
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executors

class ProfileViewModel : ViewModel() {
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName
    private var executor = Executors.newSingleThreadExecutor()

    private val _profileImageUrl = MutableLiveData<String>()
    val profileImageUrl: LiveData<String> = _profileImageUrl

    init {
        fetchUserData()
    }

    fun fetchUserData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid ?: return

        // Fetch user data from local Room database first (if available)
        executor.execute {

            val firebaseModel = FirebaseModel()
            firebaseModel.getUser(userId) { user ->
                if (user != null) {
                    _userName.postValue(user.displayName)
                    _profileImageUrl.postValue(user.avatarUri)

                    // Save the fetched user locally for faster access next time
                    executor.execute {
                        database.userDao().insert(user)
                    }
                }
            }

        }
    }

    val weather: LiveData<Weather> = Model.shared.weather

    fun fetchWeather() {
        Model.shared.getWeatherForecast()
    }

    private val _text = MutableLiveData<String>().apply {
        value = "This is profile fragment"
    }
    val text: LiveData<String> = _text
}