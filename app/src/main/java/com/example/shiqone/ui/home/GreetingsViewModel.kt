package com.example.shiqone.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shiqone.model.FirebaseModel
import com.example.shiqone.model.User
import com.example.shiqone.model.dao.AppLocalDb.database
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class GreetingViewModel(application: Application) : AndroidViewModel(application) {

    private val _greeting = MutableLiveData<String>()
    val greeting: LiveData<String> = _greeting

    private val auth = FirebaseAuth.getInstance()

    // Listener to update the greeting when the auth state changes.
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        updateGreetingFromFirebase()
    }

    init {
        updateGreetingFromFirebase()
        auth.addAuthStateListener(authStateListener)
    }

    fun updateGreetingFromFirebase() {
        val currentUser = auth.currentUser
        val userId = currentUser?.uid ?: run {
            _greeting.postValue("Welcome, Guest")
            return
        }

        viewModelScope.launch {
            val firebaseModel = FirebaseModel()
            val user = fetchUserFromFirebase(firebaseModel, userId)

            if (user != null) {
                _greeting.postValue("Hi ${user.displayName},\nhave a shiq day")
                saveUserToLocalDatabase(user)
            } else {
                _greeting.postValue("Welcome, Guest")
            }
        }
    }

    private suspend fun fetchUserFromFirebase(firebaseModel: FirebaseModel, userId: String) =
        withContext(Dispatchers.IO) {
            return@withContext kotlinx.coroutines.suspendCancellableCoroutine { continuation ->
                firebaseModel.getUser(userId) { user ->
                    continuation.resume(user, null)
                }
            }
        }

    private fun saveUserToLocalDatabase(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            database.userDao().insert(user)
        }
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}
