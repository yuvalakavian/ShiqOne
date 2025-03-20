package com.example.shiqone.ui.home;

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.shiqone.PostsListViewModel
import com.example.shiqone.R
import com.example.shiqone.adapter.PostsRecyclerViewAdapter
import com.example.shiqone.model.Post
import com.example.shiqone.ui.EditPostFragment
import com.example.shiqone.ui.login.LoginFragment
import com.google.firebase.auth.FirebaseAuth


class PostsFragment : Fragment(), PostsRecyclerViewAdapter.PostActionListener {
    private lateinit var recyclerView: RecyclerView;
    private lateinit var myPostsButton: Button;
    private lateinit var allPostsButton: Button;
    private lateinit var progressBar: ProgressBar;
    private lateinit var swipeRefresh: SwipeRefreshLayout;
    private lateinit var postsAdapter: PostsRecyclerViewAdapter;
    private lateinit var greetingsTextView: TextView
    private val greetingViewModel: GreetingViewModel by viewModels()


    // Obtain the PostsListViewModel (similar to StudentsListViewModel)
    private val viewModel: PostsListViewModel by viewModels();

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout
        val view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize views from layout
        recyclerView = view.findViewById(R.id.posts_recycler_view);
        myPostsButton = view.findViewById(R.id.my_posts_button);
        allPostsButton = view.findViewById(R.id.all_posts_button);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        greetingsTextView = view.findViewById(R.id.greeting_text)

        // Set up RecyclerView with an initially empty adapter.
        recyclerView.layoutManager = LinearLayoutManager(requireContext());
        postsAdapter = PostsRecyclerViewAdapter(emptyList(), this);
        recyclerView.adapter = postsAdapter;

        // Observe the posts LiveData from the ViewModel.
        viewModel.posts.observe(viewLifecycleOwner) { posts ->
            postsAdapter.updatePosts(posts);
            progressBar.visibility = View.GONE;
        }

        // Button listeners to filter posts.
        myPostsButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE;
            viewModel.loadMyPosts(); // This method should update the posts LiveData with only the user's posts.
        }
        allPostsButton.setOnClickListener {
            progressBar.visibility = View.VISIBLE;
            viewModel.refreshAllPosts(); // This method should update the posts LiveData with all posts.
        }

        // Swipe-to-refresh listener to reload posts.
        swipeRefresh.setOnRefreshListener {
            viewModel.refreshAllPosts();
            swipeRefresh.isRefreshing = false
        }

        // Initialize the GreetingViewModel.
        greetingViewModel.greeting.observe(viewLifecycleOwner) { greeting ->
            greetingsTextView.text = greeting
        }


        return view;
    }

    override fun onResume() {
        super.onResume();
        requireActivity().supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        // Retrieve the FirebaseAuth instance
        val auth = FirebaseAuth.getInstance()

        // Check if a user is logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // A user is logged in
            // You can use currentUser.uid, currentUser.email, etc.
            greetingViewModel.updateGreetingFromFirebase()
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

        viewModel.refreshAllPosts(); // Reload all posts when the fragment resumes.
    }

    override fun onEditPost(post: Post?) {
        val editFragment = EditPostFragment.newInstance(post)
        editFragment.setEditPostListener(object : EditPostFragment.EditPostListener {
            override fun onPostEdited(updatedPost: Post?) {
                if (post != null) {
                    viewModel.updatePost(post)
                }
            }
        })
        editFragment.show(parentFragmentManager, "EditPostFragment")
    }


    override fun onDeletePost(post: Post?) {
        if (post != null) {
            viewModel.deletePost(post)
        };
    }
}
