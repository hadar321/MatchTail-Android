package com.example.matchtail.adapters

import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matchtail.NavGraphDirections
import com.example.matchtail.R
import com.example.matchtail.data.local.AppLocalDB
import com.example.matchtail.data.models.Comment
import com.example.matchtail.data.models.InflatedPost
import com.example.matchtail.data.repositories.CommentRepository
import com.example.matchtail.data.repositories.InflatedCommentRepository
import com.example.matchtail.data.repositories.PostRepository
import com.example.matchtail.data.repositories.UserRepository
import com.example.matchtail.fragments.post.InterestsAdapter
import com.example.matchtail.fragments.post.comments.CommentsAdapter
import com.example.matchtail.utils.BaseAlert
import com.example.matchtail.utils.ImageLoaderViewModel
import com.google.firebase.Timestamp
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PostViewHolder(
    itemView: View,
    animalListener: OnPostItemClickListener?,
    userListener: OnPostItemClickListener?,
    editPostListener: OnPostItemClickListener?,
    private val fragmentManager: FragmentManager?,
    private val imageLoaderViewModel: ImageLoaderViewModel,
    private val postType: PostType
) : RecyclerView.ViewHolder(itemView) {
    private var layout: ConstraintLayout = itemView.findViewById(R.id.post_row_main)
    private var menu: ImageView = itemView.findViewById(R.id.post_row_menu)
    private var username: TextView = itemView.findViewById(R.id.post_row_username)
    private var animal: TextView = itemView.findViewById(R.id.post_row_animal)
    private var content: TextView = itemView.findViewById(R.id.post_row_content)
    private var animalImage: ImageView = itemView.findViewById(R.id.post_row_animal_image)
    private var avatar: ImageView = itemView.findViewById(R.id.post_row_avatar)
    private var interestButton: Button = itemView.findViewById(R.id.post_row_interest_button)
    private var interestsCount: TextView = itemView.findViewById(R.id.post_row_interests_count)
    private var commentButton: Button = itemView.findViewById(R.id.post_row_comment_button)
    private var commentsCount: TextView = itemView.findViewById(R.id.post_row_comments_count)
    private var date: TextView = itemView.findViewById(R.id.date)
    private var relevant: TextView = itemView.findViewById(R.id.post_row_relevant)
    private var progressBarAvatar: View = itemView.findViewById(R.id.progress_bar_avatar)
    private var progressBarRestaurant: View = itemView.findViewById(R.id.progress_bar_animal)

    // Comments internal components
    private var commentsContainer: LinearLayout = itemView.findViewById(R.id.post_row_comments_container)
    private var commentsRecyclerView: RecyclerView = itemView.findViewById(R.id.post_row_comments_recycler)
    private var commentInput: EditText = itemView.findViewById(R.id.post_row_comment_input)
    private var commentPublishButton: Button = itemView.findViewById(R.id.post_row_comment_publish_button)
    private var commentsAdapter: CommentsAdapter? = null

    private var post: InflatedPost? = null

    init {
        username.setOnClickListener {
            val post = post
            if (post != null) {
                val action = NavGraphDirections.actionGlobalUserPageFragment(post.userId)
                itemView.findNavController().navigate(action)
            }
        }
        avatar.setOnClickListener {
            val post = post
            if (post != null) {
                val action = NavGraphDirections.actionGlobalUserPageFragment(post.userId)
                itemView.findNavController().navigate(action)
            }
        }
        animal.setOnClickListener {
            val post = post
            if (post != null) animalListener?.onClickListener(post)
        }

        interestButton.setOnClickListener {
            val post = post ?: return@setOnClickListener
            showInterestsDialog(post)
        }

        interestsCount.setOnClickListener {
            val post = post ?: return@setOnClickListener
            showInterestsDialog(post)
        }

        commentButton.setOnClickListener {
            if (commentsContainer.visibility == View.VISIBLE) {
                commentsContainer.visibility = View.GONE
            } else {
                commentsContainer.visibility = View.VISIBLE
                setupCommentsList()
            }
        }

        commentPublishButton.setOnClickListener {
            val content = commentInput.text.toString()
            val post = post ?: return@setOnClickListener
            val userId = UserRepository.getInstance().getLoggedUserId() ?: return@setOnClickListener

            if (content.isNotBlank()) {
                val comment = Comment(
                    userId = userId,
                    postId = post.id,
                    content = content
                )
                itemView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch(Dispatchers.IO) {
                    try {
                        CommentRepository.getInstance().save(comment)
                        withContext(Dispatchers.Main) {
                            commentInput.text.clear()
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            BaseAlert("Error", "Failed to post comment", itemView.context).show()
                        }
                    }
                }
            }
        }

        menu.setOnClickListener {
            PopupMenu(menu.context, menu).apply {
                setOnMenuItemClickListener(object : MenuItem.OnMenuItemClickListener,
                    PopupMenu.OnMenuItemClickListener {
                    override fun onMenuItemClick(item: MenuItem): Boolean {
                        val post = post ?: return false
                        when (item.itemId) {
                            R.id.edit_post -> {
                                editPostListener?.onClickListener(post)
                            }

                            R.id.set_relevance -> {
                                itemView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch(Dispatchers.IO) {
                                    try {
                                        PostRepository.getInstance().updateRelevance(post.id, !post.isRelevant)
                                    } catch (e: Exception) {
                                        withContext(Dispatchers.Main) {
                                            BaseAlert("Error", "Failed to update relevance", itemView.context).show()
                                        }
                                    }
                                }
                            }

                            R.id.delete_post -> {
                                layout.alpha = 0.4f
                                PostRepository.getInstance().delete(post.id) {
                                    layout.alpha = 1F
                                    BaseAlert(
                                        "Fail",
                                        "Failed to delete post",
                                        itemView.context
                                    ).show()
                                }
                            }

                            else -> return false
                        }
                        return true
                    }
                })
                inflate(R.menu.post_menu)
                val relevanceItem = menu.findItem(R.id.set_relevance)
                relevanceItem?.title = if (post?.isRelevant == true) "Mark as Not Relevant" else "Mark as Relevant"
                show()
            }
        }
    }

    private fun toggleInterest(post: InflatedPost) {
        val userId = UserRepository.getInstance().getLoggedUserId() ?: return
        itemView.findViewTreeLifecycleOwner()?.lifecycleScope?.launch(Dispatchers.IO) {
            try {
                PostRepository.getInstance().toggleInterest(post.id, userId)
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    BaseAlert("Error", "Failed to update interest", itemView.context).show()
                }
            }
        }
    }

    private fun showInterestsDialog(post: InflatedPost) {
        val context = itemView.context
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_interests, null)
        val recycler = dialogView.findViewById<RecyclerView>(R.id.interests_recycler_view)
        val toggleBtn = dialogView.findViewById<Button>(R.id.btn_toggle_interest)
        
        val dialog = AlertDialog.Builder(context)
            .setView(dialogView)
            .create()

        val adapter = InterestsAdapter(emptyList()) { userId ->
            val action = NavGraphDirections.actionGlobalUserPageFragment(userId)
            itemView.findNavController().navigate(action)
            dialog.dismiss()
        }
        recycler.layoutManager = LinearLayoutManager(context)
        recycler.adapter = adapter

        val userId = UserRepository.getInstance().getLoggedUserId()
        val isInterested = userId != null && post.interests.contains(userId)
        toggleBtn.text = if (isInterested) "Remove Interest" else "I'm Interested"
        toggleBtn.setOnClickListener {
            toggleInterest(post)
            dialog.dismiss()
        }

        val lifecycleOwner = itemView.findViewTreeLifecycleOwner()
        if (lifecycleOwner != null && post.interests.isNotEmpty()) {
            AppLocalDB.getInstance().userDao().getByIds(post.interests).observe(lifecycleOwner) { users ->
                adapter.updateUsers(users)
            }
        }

        dialog.show()
    }

    private fun setupCommentsList() {
        val post = post ?: return
        if (commentsAdapter == null) {
            commentsAdapter = CommentsAdapter(emptyList(), imageLoaderViewModel)
            commentsRecyclerView.layoutManager = LinearLayoutManager(itemView.context)
            commentsRecyclerView.adapter = commentsAdapter
        }

        val lifecycleOwner = itemView.findViewTreeLifecycleOwner()
        if (lifecycleOwner != null) {
            InflatedCommentRepository.getInstance().getByPostId(post.id).observe(lifecycleOwner) { comments ->
                commentsAdapter?.updateComments(comments)
            }
        }
    }

    fun bind(post: InflatedPost?, position: Int) {
        layout.alpha = 1F
        this.post = post

        if (post == null) return

        username.text = post.userName
        animal.text = post.animalId
        content.text = post.content
        interestsCount.text = post.interests.size.toString()
        commentsCount.text = post.commentCount.toString()

        val userId = UserRepository.getInstance().getLoggedUserId()
        if (userId != null && post.interests.contains(userId)) {
            interestButton.setTextColor(itemView.context.getColor(R.color.matchtail_green))
            interestButton.text = "Interested"
        } else {
            interestButton.setTextColor(itemView.context.getColor(R.color.matchtail_dark_green))
            interestButton.text = "Interest"
        }

        val lastUpdated = post.lastUpdated
        if (lastUpdated != null) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            date.text = dateFormat.format(Timestamp(Date(lastUpdated)).toDate())
        }

        if (post.isRelevant) {
            relevant.visibility = View.VISIBLE
            relevant.text = "STILL RELEVANT"
            relevant.setTextColor(itemView.context.getColor(R.color.matchtail_green))
        } else {
            relevant.visibility = View.VISIBLE
            relevant.text = "NOT RELEVANT"
            relevant.setTextColor(itemView.context.getColor(R.color.gray))
        }

        progressBarRestaurant.visibility = View.VISIBLE
        imageLoaderViewModel.getImageUrl(post.id, PostRepository.getInstance()) { path ->
            if (post.id == this.post?.id) {
                if (path.isNotEmpty()) {
                    Picasso.get()
                        .load(File(path))
                        .placeholder(R.drawable.paw) 
                        .into(animalImage, object : com.squareup.picasso.Callback {
                            override fun onSuccess() {
                                progressBarRestaurant.visibility = View.GONE
                            }
                            override fun onError(e: Exception?) {
                                progressBarRestaurant.visibility = View.GONE
                            }
                        })
                } else {
                    animalImage.setImageResource(R.drawable.paw)
                    progressBarRestaurant.visibility = View.GONE
                }
            }
        }

        if (postType != PostType.PROFILE) {
            progressBarAvatar.visibility = View.VISIBLE
            imageLoaderViewModel.getImageUrl(post.userId, UserRepository.getInstance()) { path ->
                if (post.id == this.post?.id) {
                    if (path.isNotEmpty()) {
                        Picasso.get()
                            .load(File(path))
                            .placeholder(R.drawable.avatar_image)
                            .into(avatar, object : com.squareup.picasso.Callback {
                                override fun onSuccess() {
                                    progressBarAvatar.visibility = View.GONE
                                }
                                override fun onError(e: Exception?) {
                                    progressBarAvatar.visibility = View.GONE
                                }
                            })
                    } else {
                        avatar.setImageResource(R.drawable.avatar_image)
                        progressBarAvatar.visibility = View.GONE
                    }
                }
            }
        }

        val isOwner = post.userId == UserRepository.getInstance().getLoggedUserId()
        if (isOwner && postType == PostType.PROFILE) {
            menu.visibility = View.VISIBLE
        } else {
            menu.visibility = View.GONE
        }

        // Reset comments container when binding to a new post
        commentsContainer.visibility = View.GONE

        when (postType) {
            PostType.PROFILE -> {
                username.visibility = View.GONE
                avatar.visibility = View.GONE
                progressBarAvatar.visibility = View.GONE

                val constraintSet = ConstraintSet()
                constraintSet.clone(layout)
                constraintSet.connect(
                    R.id.post_row_animal,
                    ConstraintSet.START,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.START
                )
                constraintSet.applyTo(layout)
            }

            PostType.ANIMAL -> {
                animal.visibility = View.INVISIBLE

                val constraintSet = ConstraintSet()
                constraintSet.clone(layout)
                constraintSet.connect(
                    R.id.post_row_animal,
                    ConstraintSet.TOP,
                    R.id.post_row_avatar,
                    ConstraintSet.TOP
                )
                constraintSet.connect(
                    R.id.post_row_animal,
                    ConstraintSet.BOTTOM,
                    R.id.post_row_avatar,
                    ConstraintSet.BOTTOM
                )
                constraintSet.connect(
                    R.id.post_row_content,
                    ConstraintSet.TOP,
                    R.id.post_row_avatar,
                    ConstraintSet.BOTTOM
                )
                constraintSet.applyTo(layout)
            }

            else -> {}
        }
    }
}