package com.example.matchtail.fragments.register

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.matchtail.R
import com.example.matchtail.data.repositories.UserRepository
import com.example.matchtail.databinding.FragmentRegisterBinding
import com.example.matchtail.utils.BaseAlert
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.yalantis.ucrop.UCrop

class RegisterFragment : Fragment() {
    private val viewModel: RegisterViewModel by viewModels()
    private var binding: FragmentRegisterBinding? = null

    private val imagePicker: ActivityResultLauncher<String> = getImagePicker()
    private val uCropLauncher: ActivityResultLauncher<Intent> = getUCropLauncher()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_register, container, false
        )
        bindViews()

        binding?.registerButton?.setOnClickListener {
            syncViewModel()
            showProgressBar()
            viewModel.register({ error -> onRegisterFailure(error) })
        }

        setupToolbar()
        setupRoleGroup()
        setupUploadButton()
        
        binding?.imageView?.requestStoragePermission(requireContext(), requireActivity())

        return binding?.root
    }

    private fun bindViews() {
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
    }

    private fun syncViewModel() {
        viewModel.name.value = binding?.name?.text
        viewModel.email.value = binding?.email?.text
        viewModel.phone.value = binding?.phone?.text
        viewModel.location.value = binding?.location?.text
        viewModel.description.value = binding?.description?.text
        viewModel.password.value = binding?.password?.text
        viewModel.confirmPassword.value = binding?.confirmPassword?.text
    }

    private fun setupRoleGroup() {
        binding?.roleGroup?.setOnCheckedChangeListener { group, checkedId ->
            val radioButton = group.findViewById<RadioButton>(checkedId)
            if (radioButton != null) {
                viewModel.role.value = radioButton.text.toString()
            }
        }
    }

    private fun onRegisterFailure(error: Exception?) {
        if (error != null) {
            Log.e("Register", "Error Registering", error)
            UserRepository.getInstance().logout()
            when (error) {
                is FirebaseAuthUserCollisionException -> {
                    BaseAlert(
                        "Register Error",
                        "User with this email already exists",
                        requireContext()
                    ).show()
                }
                else -> {
                    BaseAlert("Register Error", error.message ?: "An error occurred", requireContext()).show()
                }
            }
        } else {
             BaseAlert("Invalid Input", "Please fill all required fields correctly", requireContext()).show()
        }

        showRegisterButton()
    }

    private fun setupToolbar() {
        binding?.loginToolbar?.setNavigationIcon(R.drawable.arrow_back)
        binding?.loginToolbar?.setNavigationOnClickListener {
            if (!viewModel.isLoading) {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupUploadButton() {
        binding?.imageView?.setOnClickListener {
            imagePicker.launch("image/*")
        }
    }

    private fun showRegisterButton() {
        binding?.registerButton?.visibility = View.VISIBLE
        binding?.progressBar?.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding?.registerButton?.visibility = View.GONE
        binding?.progressBar?.visibility = View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun getImagePicker() =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            binding?.imageView?.getImagePicker(uri, uCropLauncher)
        }

    private fun getUCropLauncher() =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data ?: return@registerForActivityResult
                val uri = UCrop.getOutput(data)
                binding?.imageView?.setImageURI(uri)
                viewModel.avatarUri.value = uri.toString()
            }
        }
}