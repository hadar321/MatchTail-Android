package com.example.matchtail.fragments.post.comments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.matchtail.R
import com.example.matchtail.data.models.InflatedComment
import com.example.matchtail.data.repositories.UserRepository
import com.example.matchtail.utils.ImageLoaderViewModel
import com.squareup.picasso.Picasso
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CommentViewHolder(
    itemView: View,
    private val viewModel: ImageLoaderViewModel
) : RecyclerView.ViewHolder(itemView) {
    private val avatar: ImageView = itemView.findViewById(R.id.comment_avatar)
    private val username: TextView = itemView.findViewById(R.id.comment_username)
    private val content: TextView = itemView.findViewById(R.id.comment_content)
    private val date: TextView = itemView.findViewById(R.id.comment_date)

    fun bind(comment: InflatedComment) {
        username.text = comment.userName
        content.text = comment.content
        
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        date.text = comment.lastUpdated?.let { dateFormat.format(Date(it)) } ?: ""

        viewModel.getImageUrl(comment.userId, UserRepository.getInstance()) { path ->
            if (path.isNotEmpty()) {
                Picasso.get()
                    .load(File(path))
                    .placeholder(R.drawable.avatar_image)
                    .into(avatar)
            } else {
                avatar.setImageResource(R.drawable.avatar_image)
            }
        }
    }
}

class CommentsAdapter(
    private var comments: List<InflatedComment>,
    private val viewModel: ImageLoaderViewModel
) : RecyclerView.Adapter<CommentViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_row, parent, false)
        return CommentViewHolder(view, viewModel)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int = comments.size

    fun updateComments(newComments: List<InflatedComment>) {
        comments = newComments
        notifyDataSetChanged()
    }
}