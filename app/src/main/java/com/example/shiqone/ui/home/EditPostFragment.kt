package com.example.shiqone.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.shiqone.R
import com.example.shiqone.model.Post
import com.squareup.picasso.Picasso

class EditPostFragment : DialogFragment() {
    private var editTextPost: EditText? = null
    private var buttonSave: Button? = null
    private var buttonCancel: Button? = null
    private var imageSelector: ImageView? = null
    private var updatedUri: String = ""
    private var post: Post? = null
    private var listener: EditPostListener? = null

    // Launcher for picking an image from the gallery
    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>

    interface EditPostListener {
        fun onPostEdited(updatedPost: Post?)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                updatedUri = it.toString()
                loadImage(updatedUri) // Load the selected image properly
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_edit_post, container, false)
        editTextPost = view.findViewById(R.id.edit_text_post)
        buttonSave = view.findViewById(R.id.button_save)
        buttonCancel = view.findViewById(R.id.button_cancel)
        imageSelector = view.findViewById(R.id.image_selector)

        // Retrieve passed post data, if any
        post = arguments?.getSerializable(ARG_POST) as? Post
        post?.let {
            editTextPost?.setText(it.content)
            updatedUri = it.avatarUri ?: ""
            loadImage(updatedUri)
        }

        // Set a click listener to launch the image picker when the ImageView is tapped
        imageSelector?.setOnClickListener {
            imagePickerLauncher.launch("image/*")
        }

        // Save button listener
        buttonSave?.setOnClickListener {
            post?.content = editTextPost?.text.toString()
            post?.avatarUri = updatedUri
            listener?.onPostEdited(post)
            dismiss()
        }

        // Cancel button listener
        buttonCancel?.setOnClickListener { dismiss() }

        return view
    }

    private fun loadImage(uri: String) {
        if (uri.isNotEmpty()) {
            Picasso.get()
                .load(uri)
                .placeholder(R.drawable.ic_profile_black_24dp) // Add a placeholder while loading
                .error(R.drawable.ic_profile_black_24dp) // Add an error placeholder if loading fails
                .into(imageSelector!!)
        } else {
            imageSelector?.setImageResource(R.drawable.ic_profile_black_24dp)
        }
    }

    fun setEditPostListener(listener: EditPostListener) {
        this.listener = listener
    }

    companion object {
        private const val ARG_POST = "post"
        fun newInstance(post: Post?): EditPostFragment {
            val fragment = EditPostFragment()
            val args = Bundle()
            args.putSerializable(ARG_POST, post)
            fragment.arguments = args
            return fragment
        }
    }
}
