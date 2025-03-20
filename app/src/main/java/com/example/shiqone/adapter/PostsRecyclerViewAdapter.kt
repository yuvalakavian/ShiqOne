package com.example.shiqone.adapter

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shiqone.R
import com.example.shiqone.base.MyApplication.Globals.context
import com.example.shiqone.model.FirebaseModel
import com.example.shiqone.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
class PostsRecyclerViewAdapter(
    private var posts: List<Post>,
    private val listener: PostActionListener
) :
    RecyclerView.Adapter<PostsRecyclerViewAdapter.PostViewHolder>() {

    interface PostActionListener {
        fun onEditPost(post: Post?)
        fun onDeletePost(post: Post?)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.postContent.text = post.content

        // Clear the images before loading new ones
        holder.postImage.setImageResource(R.drawable.ic_profile_placeholder)
        holder.profileImage.setImageResource(R.drawable.ic_profile_placeholder)

        // Fetch avatarUri from Firebase based on the userId for profile image
        val firebaseModel = FirebaseModel()
        firebaseModel.getUser(post.userID) { user ->
            if (user != null) {
                // Load the user's profile image (avatar)
                if (user.avatarUri.isNotEmpty()) {
                    Picasso.get()
                        .load(user.avatarUri)
                        .placeholder(R.drawable.ic_profile_placeholder)
                        .error(R.drawable.ic_profile_placeholder)
                        .into(holder.profileImage, object : Callback {
                            override fun onSuccess() {
                                // Image loaded successfully
                            }

                            override fun onError(e: Exception?) {
                                Log.e("PostsRecyclerViewAdapter", "Failed to load image: ${e?.message}")
                            }
                        })
                } else {
                    Picasso.get()
                        .load(R.drawable.ic_profile_placeholder)
                        .into(holder.profileImage)
                }

                // Set the user displayName to postHeader (if needed)
                holder.postHeader.text = user.displayName
            }
        }

        // Set the post image (if there's an image in the post)
        if (post.avatarUri.isNotEmpty()) {
            Picasso.get()
                .load(post.avatarUri)
                .placeholder(R.drawable.ic_profile_placeholder)
                .error(R.drawable.ic_profile_placeholder)
                .into(holder.postImage, object : Callback {
                    override fun onSuccess() {
                        // Image loaded successfully
                    }

                    override fun onError(e: Exception?) {
                        Log.e("PostsRecyclerViewAdapter", "Failed to load post image: ${e?.message}")
                    }
                })
        } else {
            holder.postImage.setImageResource(R.drawable.ic_profile_placeholder)
        }

        // Edit and delete icons visibility logic
        holder.editPostIcon.setOnClickListener { listener.onEditPost(post) }
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser

        if (currentUser != null) {
            if (post.userID != currentUser.uid) {
                holder.deletePostIcon.visibility = View.GONE
                holder.editPostIcon.visibility = View.GONE
            } else {
                holder.deletePostIcon.visibility = View.VISIBLE
                holder.editPostIcon.visibility = View.VISIBLE
            }
        }

        holder.deletePostIcon.setOnClickListener { listener.onDeletePost(post) }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun updatePosts(updatedPosts: List<Post>) {
        this.posts = updatedPosts
        notifyDataSetChanged() // Notify the adapter that the data has changed
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        var postHeader: TextView = itemView.findViewById(R.id.post_header)
        val postContent: TextView = itemView.findViewById(R.id.post_content)
        var postImage: ImageView = itemView.findViewById(R.id.post_image)
        var editPostIcon: ImageView = itemView.findViewById(R.id.edit_post_icon)
        var deletePostIcon: ImageView = itemView.findViewById(R.id.delete_post_icon)
    }
}

