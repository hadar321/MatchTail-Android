package com.example.matchtail.fragments.post

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toUri
import androidx.core.widget.doOnTextChanged
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.matchtail.R
import com.example.matchtail.databinding.FragmentPostFormBinding
import com.example.matchtail.utils.BaseAlert
import com.yalantis.ucrop.UCrop
import java.io.File

class PostFormFragment : Fragment() {
    private val args: PostFormFragmentArgs by navArgs()
    private val viewModel: PostFormViewModel by viewModels()
    private var binding: FragmentPostFormBinding? = null

    private val imagePicker: ActivityResultLauncher<String> = getImagePicker()
    private val uCropLauncher: ActivityResultLauncher<Intent> = getUCropLauncher()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_post_form, container, false
        )
        bindViews()

        setupToolbar()
        setupLoading()
        setupImagePicker()
        setupDropdown()

        val postId = args.postId
        if (postId != null) {
            viewModel.initForm(postId)
        } else {
            viewModel.initForm()
        }

        return binding?.root
    }

    private fun setupDropdown() {
        val autoCompleteTextView = binding?.animalList

        viewModel.animalNames.observe(viewLifecycleOwner) { options ->
            val adapter =
                ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, options)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            autoCompleteTextView?.setAdapter(adapter)
        }

        viewModel.selectedAnimal.observe(viewLifecycleOwner) { selected ->
            if (autoCompleteTextView?.text.toString() != selected) {
                autoCompleteTextView?.setText(selected, false)
            }
        }

        autoCompleteTextView?.doOnTextChanged { text, _, _, _ ->
            viewModel.selectedAnimal.value = text.toString()
        }
    }

    private fun bindViews() {
        binding?.viewModel = viewModel
        binding?.lifecycleOwner = viewLifecycleOwner
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = null
    }

    private fun setupToolbar() {
        binding?.postToolbar?.setNavigationIcon(R.drawable.arrow_back)
        binding?.postToolbar?.setNavigationOnClickListener {
            if (viewModel.isLoading.value == false) {
                findNavController().popBackStack()
            }
        }
        binding?.postToolbar?.inflateMenu(R.menu.post_form)
        binding?.postToolbar?.setOnMenuItemClickListener {
            if (viewModel.isLoading.value == false) {
                viewModel.submit({
                    BaseAlert("Success", "Post saved successfully", requireContext()).show()
                    findNavController().popBackStack()
                }, { error ->
                    BaseAlert(
                        "Fail",
                        if (error == null) "Invalid form" else "Failed to save post",
                        requireContext()
                    ).show()
                })
            }
            true
        }
    }

    private fun setupLoading() {
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) binding?.progressBar?.visibility = View.VISIBLE
            else binding?.progressBar?.visibility = View.INVISIBLE
        }
    }

    private fun setupImagePicker() {
        binding?.imageView?.setOnClickListener {
            imagePicker.launch("image/*")
        }
        viewModel.imageUri.observe(viewLifecycleOwner) { uriString ->
            if (!uriString.isNullOrEmpty()) {
                val uri = if (uriString.startsWith("/") && !uriString.startsWith("//")) {
                    Uri.fromFile(File(uriString))
                } else {
                    uriString.toUri()
                }
                binding?.imageView?.setImageURI(uri)
            }
        }
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
                viewModel.imageUri.value = uri.toString()
            }
        }
}