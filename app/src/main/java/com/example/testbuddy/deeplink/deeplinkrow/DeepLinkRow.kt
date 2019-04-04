package com.example.testbuddy.deeplink.deeplinkrow

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
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
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.component_deep_link_row.view.*

class DeepLinkRow @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attributeSet, defStyle), LifecycleObserver {

    //region Variables
    private var disposable: Disposable? = null
    private val lifecycleOwner: LifecycleOwner = context as LifecycleOwner
    private var deeplinkModel: DeeplinkModel? = null
    //endregion

    //region Init
    init {
        LayoutInflater.from(context).inflate(R.layout.component_deep_link_row, this, true)
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        lifecycleOwner.lifecycle.addObserver(this)
        disposable = this.createClickListenerObservable().subscribe { openDeepLinkActivity() }
    }
    //endregion

    //region Lifecycle
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        disposable?.dispose()
    }
    //endregion

    //region Update
    fun updateDeepLinkInfo(deeplinkModel: DeeplinkModel?) {
        this.deeplinkModel = deeplinkModel
        deepLinkUrl.text = deeplinkModel?.url
        deepLinkDescription.text = deeplinkModel?.description
    }

    fun hideDivider() {
        deepLinkDivider.goneView()
    }
    //endregion

    //region Private Helpers
    private fun openDeepLinkActivity() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(deeplinkModel?.url)
        }

        (context as Activity).startActivity(intent)
    }
    //endregion
}