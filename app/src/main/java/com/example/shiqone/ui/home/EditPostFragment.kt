package com.example.shiqone.ui

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
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.example.shiqone.R
import com.example.shiqone.model.Post
import com.squareup.picasso.Picasso
import java.io.IOException

class EditPostFragment : DialogFragment() {
    private var editTextPost: EditText? = null
    private var buttonSave: Button? = null
    private var buttonCancel: Button? = null
    private var imageSelector: ImageView? = null
    private var selectedImageUri: Uri? = null
    private var post: Post? = null
    private var listener: EditPostListener? = null

    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            selectedImageUri = result.data!!.data
            imageSelector?.setImageURI(selectedImageUri)
        }
    }

    interface EditPostListener {
        fun onPostEdited(updatedPost: Post?, bitmap: Bitmap?)
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

        post = arguments?.getSerializable(ARG_POST) as? Post
        post?.let {
            editTextPost?.setText(it.content)
            it.avatarUri?.let { uri -> loadImage(uri) }
        }

        imageSelector?.setOnClickListener { pickImageFromGallery() }
        buttonSave?.setOnClickListener { savePostChanges() }
        buttonCancel?.setOnClickListener { dismiss() }

        return view
    }

    private fun loadImage(uri: String) {
        if (uri != "") {
            Picasso.get().load(uri).placeholder(R.drawable.ic_profile_black_24dp).into(imageSelector!!)
        } else {
            imageSelector?.setImageResource(R.drawable.ic_profile_black_24dp)
        }
    }

    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        imagePickerLauncher.launch(intent)
    }

    private fun savePostChanges() {
        post?.content = editTextPost?.text.toString()

        if (selectedImageUri != null) {
            try {
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(
                    requireActivity().contentResolver, selectedImageUri
                )
                post?.avatarUri = selectedImageUri.toString()
                listener?.onPostEdited(post, bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(context, "Failed to process image", Toast.LENGTH_SHORT).show()
            }
        } else {
            // Ensure the post updates even if no new image is selected
            listener?.onPostEdited(post, null)
        }

        dismiss()
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

    override fun dismiss() {
        parentFragmentManager.setFragmentResult("edit_post_result", Bundle())
        super.dismiss()
    }
}
