package com.example.shiqone

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.shiqone.model.Model
import com.example.shiqone.model.Post
import com.google.firebase.auth.FirebaseAuth

class PostsListViewModel : ViewModel() {
    private val _showOnlyMine = MutableLiveData(false)

    // LiveData source for all posts
    private val allPosts: LiveData<List<Post>> = Model.shared.posts

    // Posts LiveData that updates dynamically
    val posts: LiveData<List<Post>> = _showOnlyMine.switchMap { showMine ->
        allPosts.map { posts ->
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid
            val filteredPosts = if (showMine && currentUserId != null) {
                posts.filter { it.userID == currentUserId }
            } else {
                posts
            }
            filteredPosts.sortedByDescending { it.lastUpdated }
        }
    }

    fun setPostsMode(showOnlyMine: Boolean) {
        _showOnlyMine.value = showOnlyMine
    }

    fun refreshAllPosts() {
        Model.shared.refreshAllPosts()
    }

    fun updatePost(updatedPost: Post) {
        Model.shared.update(updatedPost) {
            Log.d("PostsListViewModel", "Post Updated: $updatedPost")
            refreshAllPosts()  // Force refresh after updating
        }
    }

    fun deletePost(post: Post) {
        Model.shared.delete(post) {
            Log.d("PostsListViewModel", "Post Deleted: $post")
            refreshAllPosts()  // Force refresh after deleting
        }
    }
}
