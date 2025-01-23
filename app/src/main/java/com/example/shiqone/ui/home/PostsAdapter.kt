package com.example.shiqone.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.shiqone.R
import com.example.shiqone.ui.home.Post

class PostsAdapter(private val posts: List<Post>) :
    RecyclerView.Adapter<PostsAdapter.PostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.profileName.text = "${post.profileName}: ${post.postTitle}"
        holder.profileImage.setImageResource(post.profileImageResId) // Set profile image
        holder.postImage.setImageResource(post.imageResId)           // Set post image
    }

    override fun getItemCount(): Int = posts.size

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileName: TextView = itemView.findViewById(R.id.post_header)
        val profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        val postImage: ImageView = itemView.findViewById(R.id.post_image)
    }
}
