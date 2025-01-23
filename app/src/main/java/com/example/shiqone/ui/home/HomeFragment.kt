package com.example.shiqone.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.shiqone.R
import com.example.shiqone.ui.adapters.PostsAdapter

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var myPostsButton: Button
    private lateinit var allPostsButton: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Initialize views
        recyclerView = view.findViewById(R.id.posts_recycler_view)
        myPostsButton = view.findViewById(R.id.my_posts_button)
        allPostsButton = view.findViewById(R.id.all_posts_button)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = PostsAdapter(getDummyPosts())

        // Button Listeners
        myPostsButton.setOnClickListener { loadMyPosts() }
        allPostsButton.setOnClickListener { loadAllPosts() }

        return view
    }

    private fun loadMyPosts() {
        // TODO: Replace with logic to load user's posts
    }

    private fun loadAllPosts() {
        // TODO: Replace with logic to load all posts
    }

    private fun getDummyPosts(): List<Post> {
        return listOf(
            Post("Kristin Hennessy", "my outfit", R.drawable.post_image_1,R.drawable.ic_profile_placeholder),
            Post("Sara Israeli", "my fit today", R.drawable.post_image_2, R.drawable.ic_profile_placeholder)
        )
    }
}
