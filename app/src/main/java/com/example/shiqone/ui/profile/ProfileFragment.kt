package com.example.shiqone.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shiqone.R
import com.example.shiqone.databinding.FragmentProfileBinding
import com.example.shiqone.ui.login.LoginFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Weather display (unchanged)
        profileViewModel.fetchWeather()
        profileViewModel.weather.observe(viewLifecycleOwner) { weather ->
            binding.weatherTemp.text = String.format(
                "%s °C",
                weather.hourlyWeather.temperature2m[0].toString()
            )
        }

        // Open the edit profile fragment when clicking the edit icon
        binding.editIcon.setOnClickListener {
            val editFragment = EditProfileFragment()
            editFragment.show(parentFragmentManager, "EditProfileFragment")
        }

        // Observe LiveData for username and update the UI accordingly
        profileViewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.userName.text = name
        }

        // Observe LiveData for the profile image URL and load via Picasso
        profileViewModel.profileImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            if (!imageUrl.isNullOrEmpty()) {
                Picasso.get().load(imageUrl).into(binding.profileImage)
            } else {
                binding.profileImage.setImageResource(R.drawable.ic_profile_black_24dp)
            }
        }

        return root
    }

    override fun onResume() {
        super.onResume()
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            binding.userName.text = currentUser.displayName

            // Load profile image from Firebase Realtime Database
            val userDatabaseRef = FirebaseDatabase.getInstance()
                .getReference("Users")
                .child(currentUser.uid)

            userDatabaseRef.child("profileImageUrl").addListenerForSingleValueEvent(object :
                ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val imageUrl = snapshot.getValue(String::class.java)
                    if (!imageUrl.isNullOrEmpty()) {
                        Picasso.get()
                            .load(imageUrl)
                            .placeholder(R.drawable.ic_profile_black_24dp) // Show placeholder while loading
                            .into(binding.profileImage)
                    } else {
                        // Load a default image if no URL is found
                        binding.profileImage.setImageResource(R.drawable.ic_profile_black_24dp)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle database error
                    Log.e("FirebaseDB", "Failed to load profile image: ${error.message}")
                    binding.profileImage.setImageResource(R.drawable.ic_profile_black_24dp)
                }
            })
        } else {
            // No user is logged in, redirect to login fragment
            val newFragment = LoginFragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, newFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
