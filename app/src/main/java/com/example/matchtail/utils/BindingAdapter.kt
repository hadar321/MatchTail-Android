package com.example.matchtail.utils

import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.databinding.BindingAdapter
import androidx.databinding.InverseBindingAdapter
import androidx.databinding.InverseBindingListener
import androidx.lifecycle.LiveData
import com.example.matchtail.R
import com.example.matchtail.views.EditTextInput
import com.google.android.material.textfield.TextInputEditText

@BindingAdapter("android:text")
fun setText(editTextInput: EditTextInput, text: String) {
    if (editTextInput.text != text) {
        editTextInput.text = text
    }
}

@BindingAdapter("android:text")
fun setText(editTextInput: EditTextInput, text: LiveData<String>) {
    if (editTextInput.text != text.value) {
        editTextInput.text = text.value ?: ""
    }
}

@InverseBindingAdapter(attribute = "android:text")
fun getText(editTextInput: EditTextInput): String {
    return editTextInput.text
}

@BindingAdapter("android:textAttrChanged")
fun setTextWatcher(editTextInput: EditTextInput, textAttrChanged: InverseBindingListener) {
    editTextInput.findViewById<TextInputEditText>(R.id.text_input_edit_text)
        .doOnTextChanged { _, _, _, _ ->
            textAttrChanged.onChange()
        }
}

@BindingAdapter("helperTextEnabled")
fun setHelperTextEnabled(editTextInput: EditTextInput, enabled: Boolean) {
    editTextInput.helperTextEnabled = enabled
}

@InverseBindingAdapter(attribute = "helperTextEnabled")
fun getHelperTextEnabled(editTextInput: EditTextInput): Boolean {
    return editTextInput.helperTextEnabled
}

@BindingAdapter("helperTextEnabledAttrChanged")
fun setHelperTextEnabledListener(editTextInput: EditTextInput, attrChange: InverseBindingListener) {
    editTextInput.findViewById<TextInputEditText>(R.id.text_input_edit_text)
        .doOnTextChanged { _, _, _, _ ->
            attrChange.onChange()
        }
}

@BindingAdapter("helperText")
fun setHelperText(editTextInput: EditTextInput, helperText: String?) {
    editTextInput.helperText = helperText ?: ""
}

@InverseBindingAdapter(attribute = "helperText")
fun getHelperText(editTextInput: EditTextInput): String? {
    return editTextInput.helperText
}

@BindingAdapter("helperTextAttrChanged")
fun setHelperTextWatcher(
    editTextInput: EditTextInput,
    helperTextAttrChanged: InverseBindingListener
) {
    editTextInput.findViewById<TextInputEditText>(R.id.text_input_edit_text)
        .doOnTextChanged { _, _, _, _ ->
            helperTextAttrChanged.onChange()
        }
}

@BindingAdapter("android:text")
fun setText(editText: EditText, text: String) {
    if (editText.text.toString() != text) {
        editText.setText(text)
    }
}

@BindingAdapter("android:text")
fun setText(editText: EditText, text: LiveData<String>) {
    if (editText.text.toString() != text.value) {
        editText.setText(text.value ?: "")
    }
}

@InverseBindingAdapter(attribute = "android:text")
fun getText(editText: EditText): String {
    return editText.text.toString()
}