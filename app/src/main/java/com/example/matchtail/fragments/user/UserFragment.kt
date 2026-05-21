package com.example.matchtail.fragments.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.matchtail.R
import com.example.matchtail.databinding.FragmentUserBinding
import com.squareup.picasso.Picasso
import java.io.File

private const val USER_ID = "user_ID"

interface OnCreateListener {
    fun onCreate(binding: FragmentUserBinding?)
}

class UserFragment : Fragment() {
    private var viewModel: UserViewModel? = null
    private var binding: FragmentUserBinding? = null

    private var onCreateListener: OnCreateListener? = null

    private var userId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            userId = it.getString(USER_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_user, container, false
        )
        viewModel = userId?.let { UserViewModel(it) }
        bindViews()

        setupPostList()
        setupUser()

        binding?.swipeRefreshLayout?.setOnRefreshListener {
            viewModel?.fetchPosts()
        }

        viewModel?.isLoadingPosts?.observe(viewLifecycleOwner) {
            binding?.swipeRefreshLayout?.isRefreshing =
                it || viewModel?.isLoadingUser?.value == true
        }

        this.onCreateListener?.onCreate(binding)

        return binding?.root
    }

    private fun bindViews() {
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setupPostList() {// TODO: fetch post
    }

    private fun setupUser() {
        viewModel?.username?.observe(viewLifecycleOwner) { username ->
            binding?.profileUsername?.text = username
        }

        viewModel?.avatarUrl?.observe(viewLifecycleOwner) { avatarUrl ->
            val avatar = binding?.profileAvatar
            if (avatar != null && !avatarUrl.isNullOrEmpty()) {
                val file = File(avatarUrl)
                if (file.exists()) {
                    Picasso.get()
                        .load(file)
                        .placeholder(R.drawable.avatar_image)
                        .into(avatar)
                } else {
                    Picasso.get()
                        .load(avatarUrl)
                        .placeholder(R.drawable.avatar_image)
                        .into(avatar)
                }
            }
        }

        viewModel?.isLoadingUser?.observe(viewLifecycleOwner) {
            binding?.swipeRefreshLayout?.isRefreshing = it
        }
    }

    companion object {
        fun newInstance(userId: String) =
            UserFragment().apply {
                arguments = Bundle().apply {
                    putString(USER_ID, userId)
                }
            }
    }

    fun setOnCreate(listener: OnCreateListener): UserFragment {
        this.onCreateListener = listener
        return this
    }
}