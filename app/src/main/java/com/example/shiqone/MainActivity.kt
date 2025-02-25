package com.example.shiqone

import android.os.Bundle
import android.view.View
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.shiqone.databinding.ActivityMainBinding
import com.example.shiqone.ui.login.LoginFragment
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_upload, R.id.navigation_profile
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)


        binding.logoutButton.setOnClickListener {
            auth.signOut()
            binding.logoutButton.visibility = View.GONE

            val newFragment = LoginFragment() // Replace with your target fragment instance

            this.supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            this.supportFragmentManager.beginTransaction().replace(
                R.id.nav_host_fragment_activity_main, newFragment
            )  // 'fragment_container' is your activity's container view
                .addToBackStack(null)  // Optional: adds this transaction to the back stack for navigation
                .commit()
        }
    }

    override fun onResume() {
        super.onResume()
        // Check if a user is logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            binding.logoutButton.visibility = View.VISIBLE
            // A user is logged in
            // You can use currentUser.uid, currentUser.email, etc.
        } else {
            binding.logoutButton.visibility = View.GONE
            // No user is logged in
            // Redirect to login or show a message accordingly
            // For example, inside a button click listener in your current fragment:
            val newFragment = LoginFragment() // Replace with your target fragment instance

            this.supportFragmentManager.beginTransaction().replace(
                R.id.nav_host_fragment_activity_main, newFragment
            )  // 'fragment_container' is your activity's container view
                .addToBackStack(null)  // Optional: adds this transaction to the back stack for navigation
                .commit()

            supportFragmentManager.setFragmentResultListener("loginKey", this) { _, bundle ->
                val username = bundle.getString("username")
                binding.logoutButton.visibility = View.VISIBLE
                binding.navView.visibility = View.VISIBLE

            }
        }
    }
}