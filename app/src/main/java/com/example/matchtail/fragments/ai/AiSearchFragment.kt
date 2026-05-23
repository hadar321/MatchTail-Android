package com.example.matchtail.fragments.ai

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.matchtail.R
import com.example.matchtail.adapters.OnPostItemClickListener
import com.example.matchtail.adapters.PostsRecyclerAdapter
import com.example.matchtail.data.models.InflatedPost
import com.example.matchtail.databinding.FragmentAiSearchBinding
import com.example.matchtail.utils.ImageLoaderViewModel
import com.example.matchtail.data.repositories.InflatedPostRepository

class AiSearchFragment : Fragment(R.layout.fragment_ai_search) {

    private var _binding: FragmentAiSearchBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AiSearchViewModel by viewModels()
    private val imageLoaderViewModel: ImageLoaderViewModel by viewModels()
    private lateinit var adapter: PostsRecyclerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentAiSearchBinding.bind(view)

        adapter = PostsRecyclerAdapter(emptyList(), imageLoaderViewModel)
        binding.postsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.postsRecyclerView.adapter = adapter

        adapter.restaurantListener = object : OnPostItemClickListener {
            override fun onClickListener(post: InflatedPost) {
                // Navigate to post details if needed
            }
        }

        binding.btnSearch.setOnClickListener {
            val query = binding.searchQuery.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchPosts(query)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.searchResults.observe(viewLifecycleOwner) { posts ->
            adapter.updatePosts(posts)
        }

        // Keep local database posts synchronized so value isn't empty on search
        InflatedPostRepository.getInstance().getAll().observe(viewLifecycleOwner) {
            // Keep DB observer active to load posts
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}