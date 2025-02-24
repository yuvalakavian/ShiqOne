package com.example.shiqone.ui.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.shiqone.R
import com.example.shiqone.ui.profile.ProfileFragment
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the login layout
        val view = inflater.inflate(R.layout.fragment_login, container, false)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Reference views from the layout
        val emailEditText: EditText = view.findViewById(R.id.email_edit_text)
        val passwordEditText: EditText = view.findViewById(R.id.password_edit_text)
        val loginButton: Button = view.findViewById(R.id.sign_in_button)
        val signUpText: TextView = view.findViewById(R.id.sign_up_text)

        // Set click listener for login
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(context, "Login Successful", Toast.LENGTH_SHORT).show()
                            // Navigate to your main activity or next fragment here
                            val result = Bundle()
                            result.putString("username", "JohnDoe")
                            parentFragmentManager.setFragmentResult("loginKey", result)
                            requireActivity().supportFragmentManager.popBackStackImmediate()
                        } else {
                            Toast.makeText(
                                context,
                                "Login Failed: ${task.exception?.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            } else {
                Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT)
                    .show()
            }
        }


        // Set click listener for signup text to open SignupFragment
        signUpText.setOnClickListener {
            val signupFragment = SignupFragment()  // Assumes SignupFragment is accessible in your project
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_activity_main, signupFragment)
                .addToBackStack(null)
                .commit()
        }


        return view
    }
}
