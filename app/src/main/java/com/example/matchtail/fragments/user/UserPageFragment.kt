package com.example.matchtail.fragments.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.matchtail.R
import com.example.matchtail.databinding.FragmentUserBinding

class UserPageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_user_page, container, false)

        val user = UserPageFragmentArgs.fromBundle(requireArguments())
        val fragment = UserFragment.newInstance(user.userId)
        fragment.setOnCreate(object : OnCreateListener {
            override fun onCreate(binding: FragmentUserBinding?) {
                binding?.profileToolbar?.setNavigationIcon(R.drawable.arrow_back)
                binding?.profileToolbar?.setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
            }
        })
        getChildFragmentManager().beginTransaction()
            .replace(R.id.container_user_page, fragment)
            .commitNow()

        return view
    }
}