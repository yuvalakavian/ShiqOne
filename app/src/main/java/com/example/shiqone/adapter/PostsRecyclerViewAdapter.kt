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

        // Clear the image before loading a new one
        holder.postImage.setImageResource(R.drawable.ic_profile_placeholder)

       holder.postHeader.text = post.userName

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
                        Log.e("PostsRecyclerViewAdapter", "Failed to load image: ${e?.message}")
                    }
                })
        } else {
            Picasso.get()
                .load(R.drawable.ic_profile_placeholder)
                .into(holder.postImage)
        }

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
        var postHeader: TextView = itemView.findViewById(R.id.post_header)
        val postContent: TextView = itemView.findViewById(R.id.post_content)
        var postImage: ImageView = itemView.findViewById(R.id.post_image)
        var editPostIcon: ImageView = itemView.findViewById(R.id.edit_post_icon)
        var deletePostIcon: ImageView = itemView.findViewById<ImageView>(R.id.delete_post_icon)
    }
}