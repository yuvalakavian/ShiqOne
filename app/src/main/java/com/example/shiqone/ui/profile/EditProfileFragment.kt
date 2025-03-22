package com.example.shiqone.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.shiqone.databinding.FragmentEditProfileBinding
import com.example.shiqone.model.FirebaseModel
import com.example.shiqone.model.Model
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.squareup.picasso.Picasso
import java.io.IOException

class EditProfileFragment : DialogFragment() {
    private var binding: FragmentEditProfileBinding? = null
    private lateinit var auth: FirebaseAuth
    private var currentUser: FirebaseUser? = null
    private var selectedImageUri: Uri? = null
    private val firebaseModel = FirebaseModel()

    private val imagePickerLauncher = registerForActivityResult(
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
    ): View {
        binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser

        loadUserProfile()

        binding?.profileImageView?.setOnClickListener { pickImageFromGallery() }
        binding?.saveButton?.setOnClickListener { saveProfileChanges() }

        return root
    }

    private fun loadUserProfile() {
        val userId = currentUser?.uid ?: return

        firebaseModel.getUser(userId) { user ->
            user?.let {
                binding?.nameEditText?.setText(it.displayName)
                if (it.avatarUri.isNotEmpty()) {
                    Picasso.get().load(it.avatarUri).into(binding?.profileImageView)
                }
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun saveProfileChanges() {
        val newName: String = binding?.nameEditText?.text.toString().trim()
        val userId = currentUser?.uid ?: return

        firebaseModel.getUser(userId) { user ->
            if (user != null) {
                val updatedUser =
                    user.copy(displayName = if (newName.isNotEmpty()) newName else user.displayName)

                selectedImageUri?.let {
                    try {
                        val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                            requireActivity().contentResolver, it
                        )

                        // Upload image and update user profile
                        Model.shared.updateUser(
                            storage = Model.Storage.CLOUDINARY,
                            image = bitmap,
                            user = updatedUser,
                            callback = {
                                Log.d("Firebase", "User updated successfully")
                                Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT)
                                    .show()
                                dismiss()
                            }
                        )
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                } ?: firebaseModel.updateUser(updatedUser) {
                    Log.d("Firebase", "User updated successfully (without avatar)")
                    Toast.makeText(context, "Profile updated!", Toast.LENGTH_SHORT).show()
                    dismiss()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    override fun dismiss() {
        parentFragmentManager.setFragmentResult("edit_profile_result", Bundle())
        super.dismiss()
    }
}
