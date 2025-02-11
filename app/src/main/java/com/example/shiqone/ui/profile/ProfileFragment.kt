package com.example.shiqone.ui.profile

import com.example.shiqone.LoginActivity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shiqone.databinding.FragmentNotificationsBinding

class ProfileFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val profileViewModel =
            ViewModelProvider(this).get(ProfileViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        profileViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        val intent = Intent(getActivity(), LoginActivity::class.java)

        // Optionally, pass data to the second Activity
        intent.putExtra("key", "value")

        // Start the second Activity
        startActivity(intent)
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}