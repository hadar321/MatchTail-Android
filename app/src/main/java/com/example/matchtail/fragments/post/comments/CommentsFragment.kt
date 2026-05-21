package com.example.matchtail.fragments.post.comments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.matchtail.R
import com.example.matchtail.databinding.FragmentCommentsBinding
import com.example.matchtail.utils.BaseAlert

class CommentsFragment : Fragment() {
    private val args: CommentsFragmentArgs by navArgs()
    private val viewModel: CommentsViewModel by viewModels()
    private var _binding: FragmentCommentsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCommentsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupInput()
        setupRefresh()

        viewModel.setPostId(args.postId)
    }

    private fun setupToolbar() {
        binding.commentsToolbar.setNavigationIcon(R.drawable.arrow_back)
        binding.commentsToolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        val adapter = CommentsAdapter(emptyList(), viewModel)
        binding.commentsRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.commentsRecyclerView.adapter = adapter

        viewModel.comments.observe(viewLifecycleOwner) { comments ->
            adapter.updateComments(comments)
        }
    }

    private fun setupInput() {
        binding.commentSendButton.setOnClickListener {
            val content = binding.commentEditText.text.toString()
            if (content.isNotBlank()) {
                viewModel.addComment(content, {
                    binding.commentEditText.text.clear()
                }, {
                    BaseAlert("Error", "Failed to post comment", requireContext()).show()
                })
            }
        }
    }

    private fun setupRefresh() {
        binding.commentsSwipeRefresh.setOnRefreshListener {
            viewModel.fetchComments()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.commentsSwipeRefresh.isRefreshing = isLoading
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}