package com.example.testbuddy.deeplink.deeplinkrow

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.example.testbuddy.R
import com.example.testbuddy.deeplink.db.DeeplinkModel
import com.example.testbuddy.utils.createClickListenerObservable
import com.example.testbuddy.utils.goneView
import com.example.testbuddy.utils.visibleView
import io.reactivex.disposables.Disposable
import android.graphics.Color
import android.graphics.Typeface
import android.text.style.TextAppearanceSpan
import android.widget.TextView
import kotlinx.android.synthetic.main.component_deep_link_row.view.*


class DeepLinkRow @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle), LifecycleObserver {

    //region Init
    init {
        LayoutInflater.from(context).inflate(R.layout.component_deep_link_row, this, true)
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
    //endregion

    //region Update
    fun updateDeepLinkInfo(deeplinkModel: DeeplinkModel?, filter: String? = null) {
        updateTextWithHighlight(deepLinkUrl, deeplinkModel?.url, filter)
        updateTextWithHighlight(deepLinkDescription, deeplinkModel?.description, filter)
    }

    private fun updateTextWithHighlight(textView: TextView, text: String?, filter: String?) {
        if (filter.isNullOrBlank()) {
            textView.text = text
        } else {
            val startPosition = text?.toLowerCase()?.indexOf(filter.toLowerCase()) ?: -1
            val endPosition = startPosition + filter.length
            if (startPosition != -1) {
                val spannable = SpannableString(text)
                val blueColor = ColorStateList(arrayOf(intArrayOf()), intArrayOf(Color.BLUE))
                val highlightSpan = TextAppearanceSpan(null, Typeface.BOLD, -1, blueColor, null)
                spannable.setSpan(highlightSpan, startPosition, endPosition, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                textView.text = spannable
            } else {
                textView.text = text
            }
        }
    }

    fun hideDivider() {
        deepLinkDivider.goneView()
    }

    fun showDivider() {
        deepLinkDivider.visibleView()
    }
    //endregion
}