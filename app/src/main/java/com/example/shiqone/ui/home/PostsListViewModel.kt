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
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // Original posts source
    private val allPosts = Model.shared.posts

    // Filtered LiveData using extensions
    val posts: LiveData<List<Post>> = _showOnlyMine.switchMap { showMine ->
        if (showMine) {
            allPosts.map { posts ->
                posts.filter {
                    it.userID == currentUserId
                }
            }
        } else {
            allPosts
        }
    }

    fun refreshAllPosts() {
        _showOnlyMine.value = false
        Model.shared.refreshAllPosts()
        _showOnlyMine.value = _showOnlyMine.value // Force update
    }

    fun loadMyPosts() {
        _showOnlyMine.value = true
        Model.shared.refreshAllPosts()
        _showOnlyMine.value = _showOnlyMine.value // Force update
    }



    fun updatePost(updatedPost: Post) {
        Model.shared.update(updatedPost){
            Log.d("Post Updated", updatedPost.toString())
        }
    }

    fun deletePost(post: Post) {
        Model.shared.delete(post){
            Log.d("Post Deleted", post.toString())
        }
    }
}