package com.example.shiqone.ui.profile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.shiqone.databinding.FragmentEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso

class EditProfileFragment : DialogFragment() {
    private var binding: FragmentEditProfileBinding? = null
    private var auth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private var userDatabaseRef: DatabaseReference? = null
    private var selectedImageUri: Uri? = null

    private val imagePickerLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data!!.data
            binding?.profileImageView?.setImageURI(selectedImageUri)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root: View = binding?.root!!

        auth = FirebaseAuth.getInstance()
        currentUser = auth!!.currentUser
        userDatabaseRef =
            FirebaseDatabase.getInstance().getReference("Users").child(currentUser!!.uid)

        // Cloudinary MediaManager should be initialized once (for example, in your Application class)

        loadUserProfile()

        binding?.profileImageView?.setOnClickListener { pickImageFromGallery() }
        binding?.saveButton?.setOnClickListener { saveProfileChanges() }

        return root
    }

    private fun loadUserProfile() {
        // Load the user's name from Realtime Database
        userDatabaseRef?.child("name")?.get()?.addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                binding?.nameEditText?.setText(snapshot.getValue(String::class.java))
            }
        }
        // Load the profile image URL from Realtime Database and use Picasso to display it
        userDatabaseRef?.child("profileImageUrl")?.get()?.addOnSuccessListener { snapshot ->
            if (snapshot.exists()) {
                val imageUrl = snapshot.getValue(String::class.java)
                if (!imageUrl.isNullOrEmpty()) {
                    Picasso.get().load(imageUrl).into(binding?.profileImageView)
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        imagePickerLauncher.launch(intent)
    }

    private fun saveProfileChanges() {
        val newName: String = binding?.nameEditText?.text.toString().trim()
        if (newName.isNotEmpty()) {
            // Update the name in the Realtime Database
            userDatabaseRef?.child("name")?.setValue(newName)

            // Also update the Firebase Authentication display name
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(newName)
                .build()
            currentUser?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Toast.makeText(context, "Failed to update display name", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        // Upload the image using Cloudinary if a new image has been selected
        if (selectedImageUri != null) {
            MediaManager.get().upload(selectedImageUri)
                .option("resource_type", "image")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String?) {
                        // Optionally show upload progress
                    }

                    override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                        // Optionally update progress
                    }

                    override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                        val url = resultData?.get("secure_url") as? String
                        if (url != null) {
                            // Save the Cloudinary URL to the Realtime Database
                            userDatabaseRef?.child("profileImageUrl")?.setValue(url)
                        }
                    }

                    override fun onError(requestId: String?, error: ErrorInfo?) {
                        Toast.makeText(context, "Failed to upload image", Toast.LENGTH_SHORT).show()
                    }

                    override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                        // Handle rescheduling if needed
                    }
                }).dispatch()
        }
        dismiss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}
