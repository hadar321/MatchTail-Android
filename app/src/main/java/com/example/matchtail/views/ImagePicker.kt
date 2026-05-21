package com.example.matchtail.views

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.util.AttributeSet
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.example.matchtail.R
import com.squareup.picasso.Picasso
import com.yalantis.ucrop.UCrop
import com.yalantis.ucrop.model.AspectRatio
import java.io.File

class ImagePicker @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : AppCompatImageView(context, attrs, defStyleAttr) {

    private val REQUEST_STORAGE_PERMISSION: Int = 1

    fun getImagePicker(uri: Uri?, uCropLauncher: ActivityResultLauncher<Intent>) {
        uri?.let {
            val destinationUri = Uri.fromFile(File(context.cacheDir, "cropped2"))
            val options = UCrop.Options()
            options.setCompressionQuality(80)
            options.setAspectRatioOptions(0, AspectRatio("1:1", 1f, 1f))

            val cropIntent = UCrop.of(uri, destinationUri)
                .withOptions(options)
                .getIntent(context)
            uCropLauncher.launch(cropIntent)
        }
    }

    fun handleUCropResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data ?: return
            val uri = UCrop.getOutput(data)
            setImageURI(uri)
        }
    }

    override fun setImageURI(uri: Uri?) {
        if (uri != null) {
            Picasso.get()
                .load(uri)
                .placeholder(R.drawable.paw)
                .into(this)
        } else {
            super.setImageURI(uri)
        }
    }

    fun requestStoragePermission(context: Context, activity: FragmentActivity) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                REQUEST_STORAGE_PERMISSION
            )
        }
    }
}