package com.example.matchtail.views

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.core.content.withStyledAttributes
import com.example.matchtail.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class EditTextInput @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {

    private var textInputLayout: TextInputLayout
    private var textInputEditText: TextInputEditText

    var text: String
        get() = textInputEditText.text.toString()
        set(value) {
            textInputEditText.setText(value)
        }

    var helperText: String
        get() = textInputLayout.helperText.toString()
        set(value) {
            textInputLayout.helperText = value
        }

    var helperTextEnabled: Boolean
        get() = textInputLayout.isHelperTextEnabled
        set(value) {
            textInputLayout.isHelperTextEnabled = value
        }

    private var hint: String
        get() = textInputLayout.hint.toString()
        set(value) {
            textInputLayout.hint = value
        }

    var helperTextColor: Int
        get() = textInputLayout.helperTextCurrentTextColor
        set(value) {
            textInputLayout.setHelperTextColor(ColorStateList.valueOf(value))
        }
    var inputType: Int
        get() = textInputEditText.inputType
        set(value) {
            textInputEditText.inputType = value
        }
    var lines: Int
        get() = textInputEditText.minLines
        set(value) {
            textInputEditText.minLines = value
        }

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater.inflate(R.layout.custom_text_input, this, true)

        textInputLayout = view.findViewById(R.id.text_input_layout)
        textInputEditText = view.findViewById(R.id.text_input_edit_text)

        context.withStyledAttributes(attrs, R.styleable.EditTextInput) {
            this@EditTextInput.hint = getString(R.styleable.EditTextInput_android_hint) ?: ""
            this@EditTextInput.helperText =
                getString(R.styleable.EditTextInput_helperText) ?: ""
            this@EditTextInput.text = getString(R.styleable.EditTextInput_android_text) ?: ""
            this@EditTextInput.helperTextColor =
                getColor(R.styleable.EditTextInput_helperTextColor, Color.RED)
            this@EditTextInput.inputType =
                getInt(R.styleable.EditTextInput_android_inputType, 1)
            this@EditTextInput.lines = getInt(R.styleable.EditTextInput_android_lines, 1)
        }
    }
}