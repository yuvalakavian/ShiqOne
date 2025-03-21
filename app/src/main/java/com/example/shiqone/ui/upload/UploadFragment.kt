package com.example.shiqone.ui.upload

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation.findNavController
import com.example.shiqone.R
import com.example.shiqone.databinding.FragmentUploadBinding
import com.example.shiqone.model.Model
import com.example.shiqone.model.Post
import com.example.shiqone.ui.login.LoginFragment
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class UploadFragment : Fragment() {
    private var binding: FragmentUploadBinding? = null
    private var didSetImage = false
    private val imagePickerLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            var selectedImageUri = result.data!!.data
            binding?.imageView?.setImageURI(selectedImageUri)
            didSetImage =true
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @Deprecated("Deprecated in Java")
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val uploadViewModel = ViewModelProvider(this).get(
            UploadViewModel::class.java
        )

        binding = FragmentUploadBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        val textView = binding!!.titleText
        uploadViewModel.text.observe(
            viewLifecycleOwner
        ) { text: String? ->
            textView.text =
                text
        }

        binding!!.saveButton.setOnClickListener { view: View ->
            this.onSaveClicked(
                view
            )
        }
        binding!!.takePhotoButton.setOnClickListener { v -> pickImageFromGallery() }


        val auth = FirebaseAuth.getInstance()

        // Check if a user is logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // A user is logged in
            // You can use currentUser.uid, currentUser.email, etc.
        } else {
            // No user is logged in
            // Redirect to login or show a message accordingly
            // For example, inside a button click listener in your current fragment:
            val newFragment = LoginFragment() // Replace with your target fragment instance

            requireActivity().supportFragmentManager.beginTransaction()
                .replace(
                    R.id.nav_host_fragment_activity_main,
                    newFragment
                )  // 'fragment_container' is your activity's container view
                .addToBackStack(null)  // Optional: adds this transaction to the back stack for navigation
                .commit()
        }

        return root
    }

    private fun onSaveClicked(view: View) {
        val description: String = binding?.descriptionEditText?.getText().toString()
        val curr_user = FirebaseAuth.getInstance().currentUser
        val userId = curr_user?.uid.toString()
        val displayName = curr_user?.displayName.toString()
        // Pass the userId to the Post constructor
        val myUuid = UUID.randomUUID()
        val myUuidAsString = myUuid.toString()

        val post = Post(
            id=myUuidAsString,
            content = description,
            userID = userId,
            isDeleted = false,
            avatarUri = "",
            userName =displayName,
        )

        binding?.progressBar?.setVisibility(View.VISIBLE)

        if (didSetImage) {
            binding?.imageView?.setDrawingCacheEnabled(true)
            binding?.imageView?.buildDrawingCache()
            val drawable = binding?.imageView?.getDrawable() as BitmapDrawable

            Model.shared.add(post, drawable.bitmap, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.setVisibility(View.GONE)
                findNavController(view).popBackStack()
            }
        } else {
            Model.shared.add(post, null, Model.Storage.CLOUDINARY) {
                binding?.progressBar?.setVisibility(View.GONE)
                findNavController(view).popBackStack()
            }
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setType("image/*")
        imagePickerLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}