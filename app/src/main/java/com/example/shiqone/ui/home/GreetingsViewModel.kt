package com.example.shiqone.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.shiqone.model.FirebaseModel
import com.example.shiqone.model.dao.AppLocalDb.database
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executors

// Assuming FirebaseModel is defined in your project and has the method:
// fun getUser(userId: String, callback: (User?) -> Unit)
class GreetingViewModel(application: Application) : AndroidViewModel(application) {

    private val _greeting = MutableLiveData<String>()
    val greeting: LiveData<String> = _greeting

    private val auth = FirebaseAuth.getInstance()
    private val executor = Executors.newSingleThreadExecutor()

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

        // Use executor to fetch user data similarly to your fetchUserData snippet.
        executor.execute {
            val firebaseModel = FirebaseModel()
            firebaseModel.getUser(userId) { user ->
                if (user != null) {
                    _greeting.postValue("Hi ${user.displayName},\nhave a shiq day")
                    // Save the fetched user locally for faster access next time.
                    executor.execute {
                        database.userDao().insert(user)
                    }
                } else {
                    _greeting.postValue("Welcome, Guest")
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
        executor.shutdown()
    }
}
