package com.example.shiqone.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shiqone.model.Model
import com.example.shiqone.model.Weather
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileViewModel : ViewModel() {
    private val _userName = MutableLiveData<String>()
    val userName: LiveData<String> = _userName

    private val _profileImageUrl = MutableLiveData<String>()
    val profileImageUrl: LiveData<String> = _profileImageUrl

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId)

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Update name
                val name = snapshot.child("name").getValue(String::class.java) ?: ""
                _userName.postValue(name)

                // Update image URL
                val imageUrl = snapshot.child("profileImageUrl").getValue(String::class.java) ?: ""
                _profileImageUrl.postValue(imageUrl)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
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