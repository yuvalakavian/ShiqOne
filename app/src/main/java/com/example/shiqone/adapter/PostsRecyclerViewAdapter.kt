package com.example.shiqone.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.shiqone.R
import com.example.shiqone.model.Post
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
        holder.postHeader.text = post.content
        holder.postImage.let { imageView ->
            if (post.avatarUri.isNotEmpty()) {
                Picasso.get()
                    .load(post.avatarUri)
                    .placeholder(R.drawable.ic_profile_placeholder)
                    .into(imageView)
            } else {
                // Optionally, load a default image if avatarUri is empty
                Picasso.get()
                    .load(R.drawable.ic_profile_placeholder)
                    .into(imageView)
            }
        }


        holder.editPostIcon.setOnClickListener { v: View? -> listener.onEditPost(post) }
        holder.deletePostIcon.setOnClickListener { v: View? ->
            listener.onDeletePost(
                post
            )
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    fun updatePosts(updatedPosts: List<Post>) {
        val oldPosts = this.posts
        val diff = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            override fun getOldListSize(): Int = oldPosts.size
            override fun getNewListSize(): Int = updatedPosts.size
            override fun areItemsTheSame(oldPos: Int, newPos: Int): Boolean =
                oldPosts[oldPos].id == updatedPosts[newPos].id
            override fun areContentsTheSame(oldPos: Int, newPos: Int): Boolean =
                oldPosts[oldPos] == updatedPosts[newPos]
        })
        this.posts = updatedPosts
        diff.dispatchUpdatesTo(this)
    }

    class PostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var postHeader: TextView = itemView.findViewById(R.id.post_header)
        var profileImage: ImageView = itemView.findViewById(R.id.profile_image)
        var postImage: ImageView = itemView.findViewById(R.id.post_image)
        var editPostIcon: ImageView = itemView.findViewById(R.id.edit_post_icon)
        var deletePostIcon: ImageView = itemView.findViewById<ImageView>(R.id.delete_post_icon)
    }
}