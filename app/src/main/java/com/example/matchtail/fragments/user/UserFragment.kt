package com.example.matchtail.fragments.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.matchtail.R
import com.example.matchtail.adapters.OnPostItemClickListener
import com.example.matchtail.adapters.PaddedItemDecoration
import com.example.matchtail.adapters.PostType
import com.example.matchtail.adapters.PostsRecyclerAdapter
import com.example.matchtail.data.models.InflatedPost
import com.example.matchtail.data.repositories.UserRepository
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

    private fun setupPostList() {
        binding?.postsRecyclerView?.setHasFixedSize(true)
        binding?.postsRecyclerView?.layoutManager = LinearLayoutManager(context)
        binding?.postsRecyclerView?.addItemDecoration(PaddedItemDecoration())
        
        val vm = viewModel ?: return
        val adapter = PostsRecyclerAdapter(emptyList(), vm)
        
        // Only enable edit/delete menu if this is the logged-in user's profile
        if (userId == UserRepository.getInstance().getLoggedUserId()) {
            adapter.postType = PostType.PROFILE
            adapter.editPostListener = object : OnPostItemClickListener {
                override fun onClickListener(post: InflatedPost) {
                    val action = ProfileFragmentDirections.actionProfileFragmentToPostFormFragment(post.id)
                    findNavController().navigate(action)
                }
            }
        } else {
            adapter.postType = PostType.REGULAR
        }

        binding?.postsRecyclerView?.adapter = adapter

        vm.posts.observe(viewLifecycleOwner) { posts ->
            adapter.updatePosts(posts)
        }
    }

    private fun setupUser() {
        viewModel?.avatarUrl?.observe(viewLifecycleOwner) { avatarUrl ->
            val avatar = binding?.profileAvatar
            if (avatar != null) {
                if (!avatarUrl.isNullOrEmpty()) {
                    val file = File(avatarUrl)
                    val picasso = Picasso.get()
                    val request = if (file.exists()) picasso.load(file) else picasso.load(avatarUrl)
                    
                    request.placeholder(R.drawable.avatar_image)
                        .error(R.drawable.avatar_image)
                        .into(avatar)
                } else {
                    avatar.setImageResource(R.drawable.avatar_image)
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