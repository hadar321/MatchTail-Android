package com.example.matchtail.fragments.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.matchtail.R
import com.example.matchtail.data.repositories.UserRepository
import com.example.matchtail.databinding.FragmentUserBinding

class ProfileFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        val userId =
            UserRepository.getInstance().getLoggedUserId() ?: throw Exception("User not logged in")
        val fragment = UserFragment.newInstance(userId)
        fragment.setOnCreate(object : OnCreateListener {
            override fun onCreate(binding: FragmentUserBinding?) {
                binding?.profileToolbar?.inflateMenu(R.menu.profile_menu)
                binding?.profileToolbar?.setOnMenuItemClickListener {
                    onMenuItemClick(it)
                }
            }
        })

        getChildFragmentManager().beginTransaction()
            .replace(R.id.container, fragment)
            .commitNow()

        return view
    }

    fun onMenuItemClick(item: android.view.MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_profile -> {
                findNavController().navigate(R.id.action_profileFragment_to_editUserFragment)
            }

            R.id.logout_profile -> {
                UserRepository.getInstance().logout()
            }

            else -> return false
        }
        return true
    }
}