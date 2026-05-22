package com.example.matchtail.fragments.user.edit

import android.accounts.AuthenticatorException
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
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.matchtail.R
import com.example.matchtail.databinding.FragmentEditUserBinding
import com.example.matchtail.utils.BaseAlert
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.yalantis.ucrop.UCrop

class EditUserFragment : Fragment() {
    private val viewModel: EditUserViewModel by viewModels()
    private var binding: FragmentEditUserBinding? = null

    private val imagePicker: ActivityResultLauncher<String> = getImagePicker()
    private val uCropLauncher: ActivityResultLauncher<Intent> = getUCropLauncher()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_edit_user, container, false
        )
        bindViews()

        binding?.submitButton?.setOnClickListener {
            syncViewModel()
            viewModel.submit({ onUpdateSuccess() }) { error -> onUpdateFailure(error) }
        }

        setupToolbar()
        setupLoading()
        setupUploadButton()
        setupRoleGroup()
        
        binding?.imageView?.requestStoragePermission(requireContext(), requireActivity())

        return binding?.root
    }

    private fun bindViews() {
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
    }

    private fun syncViewModel() {
        viewModel.name.value = binding?.name?.text
        viewModel.phone.value = binding?.phone?.text
        viewModel.location.value = binding?.location?.text
        viewModel.description.value = binding?.description?.text
        viewModel.oldPassword.value = binding?.oldPassword?.text
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

        viewModel.role.observe(viewLifecycleOwner) { role ->
            if (role == getString(R.string.adopt_pet)) {
                binding?.roleAdopter?.isChecked = true
            } else if (role == getString(R.string.send_pet)) {
                binding?.roleGiver?.isChecked = true
            }
        }
    }

    private fun onUpdateSuccess() {
        BaseAlert("Success", getString(R.string.success_update), requireContext()).show()
        findNavController().popBackStack()
    }

    private fun onUpdateFailure(error: Exception?) {
        if (error != null) {
            Log.e("Edit", "Error Editing", error)
            when (error) {
                is AuthenticatorException, is FirebaseAuthInvalidUserException, is FirebaseAuthInvalidCredentialsException -> {
                    BaseAlert(
                        "Edit Error",
                        "Current password is not correct",
                        requireContext()
                    ).show()
                }
                else -> {
                    BaseAlert("Edit Error", error.message ?: "An error occurred", requireContext()).show()
                }
            }
        } else {
            BaseAlert(getString(R.string.invalid_input_title), getString(R.string.fill_required_fields), requireContext()).show()
        }

        showSubmitButton()
    }

    private fun setupToolbar() {
        binding?.editUserToolbar?.setNavigationIcon(R.drawable.arrow_back)
        binding?.editUserToolbar?.setNavigationOnClickListener {
            if (viewModel.isLoading.value == false) {
                findNavController().popBackStack()
            }
        }
    }

    private fun setupUploadButton() {
        binding?.imageView?.setOnClickListener {
            imagePicker.launch("image/*")
        }
        viewModel.avatarUri.observe(viewLifecycleOwner) { uriString ->
            if (!uriString.isNullOrEmpty()) {
                binding?.imageView?.setImageURI(uriString.toUri())
            }
        }
    }

    private fun showSubmitButton() {
        binding?.submitButton?.visibility = View.VISIBLE
        binding?.progressBar?.visibility = View.INVISIBLE
    }

    private fun showProgressBar() {
        binding?.submitButton?.visibility = View.GONE
        binding?.progressBar?.visibility = View.VISIBLE
    }

    private fun setupLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) showProgressBar()
            else showSubmitButton()
        }
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