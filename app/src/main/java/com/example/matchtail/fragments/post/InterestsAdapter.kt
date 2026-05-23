package com.example.matchtail.fragments.post

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.matchtail.R
import com.example.matchtail.data.models.User
import com.squareup.picasso.Picasso
import java.io.File

class InterestUserViewHolder(
    itemView: View,
    private val onUserClick: (String) -> Unit
) : RecyclerView.ViewHolder(itemView) {
    private val avatar: ImageView = itemView.findViewById(R.id.user_avatar)
    private val username: TextView = itemView.findViewById(R.id.user_username)

    fun bind(user: User) {
        username.text = user.username
        
        val avatarUrl = user.avatarUrl
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

        itemView.setOnClickListener { onUserClick(user.id) }
        username.setOnClickListener { onUserClick(user.id) }
    }
}

class InterestsAdapter(
    private var users: List<User>,
    private val onUserClick: (String) -> Unit
) : RecyclerView.Adapter<InterestUserViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InterestUserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.interest_user_row, parent, false)
        return InterestUserViewHolder(view, onUserClick)
    }

    override fun onBindViewHolder(holder: InterestUserViewHolder, position: Int) {
        holder.bind(users[position])
    }

    override fun getItemCount(): Int = users.size

    fun updateUsers(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }
}