package com.example.shiqone.ui.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shiqone.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignupFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the signup layout
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Reference views from the layout using the new IDs from the XML
        val fullNameEditText: EditText = view.findViewById(R.id.full_name_edit_text)
        val emailEditText: EditText = view.findViewById(R.id.email_edit_text)
        val passwordEditText: EditText = view.findViewById(R.id.password_edit_text)
        val confirmPasswordEditText: EditText = view.findViewById(R.id.confirm_password_edit_text)
        val signupButton: Button = view.findViewById(R.id.sign_up_button)

        // Set click listener for signup
        signupButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            // Check if all fields are filled
            if (email.isNotEmpty() && password.isNotEmpty() && confirmPassword.isNotEmpty()) {
                // Check if password and confirm password match
                if (password == confirmPassword) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Signup Successful", Toast.LENGTH_SHORT).show()
                                // User created successfully.
                                val user = FirebaseAuth.getInstance().currentUser
                                val profileUpdates = UserProfileChangeRequest.Builder()
                                    .setDisplayName(fullName)  // Set the user's full name here
                                    .build()

                                user?.updateProfile(profileUpdates)
                                    ?.addOnCompleteListener { updateTask ->
                                        if (updateTask.isSuccessful) {
                                            Log.d("FirebaseAuth", "User profile updated with name: $fullName")
                                        }
                                    }
                                // Navigate to your next fragment or activity here
                                val newFragment = LoginFragment() // Replace with your target fragment instance

                                requireActivity().supportFragmentManager.beginTransaction()
                                    .replace(R.id.nav_host_fragment_activity_main, newFragment)  // 'fragment_container' is your activity's container view
                                    .addToBackStack(null)  // Optional: adds this transaction to the back stack for navigation
                                    .commit()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Signup Failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                } else {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "Please enter all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return view
    }
}
