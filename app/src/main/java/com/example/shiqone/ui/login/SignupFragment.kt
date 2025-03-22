package com.example.shiqone.ui.login

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.shiqone.R
import com.example.shiqone.model.FirebaseModel
import com.example.shiqone.model.Model
import com.example.shiqone.model.User
import com.google.firebase.auth.FirebaseAuth
import java.io.IOException

class SignupFragment : Fragment() {

    private lateinit var auth: FirebaseAuth
    private val firebaseModel = FirebaseModel()
    private var selectedImageUri: Uri? = null
    private var userBitmap: Bitmap? = null
    private lateinit var avatarImageView: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup, container, false)

        auth = FirebaseAuth.getInstance()

        val fullNameEditText: EditText = view.findViewById(R.id.full_name_edit_text)
        val emailEditText: EditText = view.findViewById(R.id.email_edit_text)
        val passwordEditText: EditText = view.findViewById(R.id.password_edit_text)
        val confirmPasswordEditText: EditText = view.findViewById(R.id.confirm_password_edit_text)
        val signupButton: Button = view.findViewById(R.id.sign_up_button)
        val signInButton: TextView = view.findViewById(R.id.sign_in_text)

        signupButton.setOnClickListener {
            val fullName = fullNameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(context, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create the user in Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser = auth.currentUser
                        firebaseUser?.let { user ->
                            val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                                .setDisplayName(fullName)
                                .build()

                            user.updateProfile(profileUpdates).addOnCompleteListener {
                                saveUserToDatabase(user.uid, fullName, "")
                            }
                            loadLogin()
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Signup Failed: ${task.exception?.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        signInButton.setOnClickListener {
            loadLogin()
        }

        return view
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, selectedImageUri)
                avatarImageView.setImageBitmap(bitmap)
                userBitmap = bitmap
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun saveUserToDatabase(userId: String, fullName: String, avatarUri: String) {
        val user = User(id = userId, displayName = fullName, avatarUri = avatarUri)

        Model.shared.addUser(user)
    }

    private fun loadLogin() {
        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val newFragment = LoginFragment()
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, newFragment)
            .addToBackStack(null)
            .commit()
    }

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }
}
