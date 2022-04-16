package com.elapp.storyapp.presentation.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import com.elapp.storyapp.R.string
import com.elapp.storyapp.utils.ext.isEmailValid

class StoryEmailEditText : AppCompatEditText {

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                // do nothing
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val email = s.toString()
                when {
                    email.isBlank() -> error = context.getString(string.error_empty_email)
                    !email.isEmailValid() -> error = context.getString(string.error_invalid_email)
                }
            }

            override fun afterTextChanged(p0: Editable?) {
                // do nothing
            }
        })
    }
}