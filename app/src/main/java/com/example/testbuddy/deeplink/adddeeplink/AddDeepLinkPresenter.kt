package com.example.testbuddy.deeplink.adddeeplink

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.example.testbuddy.deeplink.db.DeepLinkDatabase
import com.example.testbuddy.deeplink.db.DeeplinkModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class AddDeepLinkPresenter(
    private val delegate: AddDeepLinkDelegate,
    private val database: DeepLinkDatabase,
    private val lifecycleOwner: LifecycleOwner
) : LifecycleObserver {

    //region Variables
    private val viewModel: AddDeepLinkViewModel by lazy { delegate.getViewModel(AddDeepLinkViewModel::class.java) as AddDeepLinkViewModel }
    //endregion

    //region Lifecycle
    init {
        lifecycleOwner.lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun onCreate() {
        initializeUrlText()
        initializeDescriptionText()
    }

    private fun initializeUrlText() {
        if (viewModel.deepLinkUrl.isNullOrEmpty()) return
        delegate.updateUrlText(viewModel.deepLinkUrl)
    }

    private fun initializeDescriptionText() {
        if (viewModel.deepLinkDescription.isNullOrEmpty()) return
        delegate.updateDescriptionText(viewModel.deepLinkDescription)
    }

    //endregion

    //region Listener
    fun onUrlTextChanged(url: String) {
        viewModel.deepLinkUrl = url
    }

    fun onDescriptionTextChanged(description: String) {
        viewModel.deepLinkDescription = description
    }

    fun onAddDeepLinkClick() {
        if (viewModel.deepLinkUrl.isNullOrEmpty()) {
            delegate.showDeepLinkEmptyToast()
        } else {
            addDeepLinkToDb()
        }
    }


    fun onAddAndFireDeepLinkClick() {

    }
    //endregion

    //region Private Helpers
    private fun addDeepLinkToDb() {
        viewModel.deepLinkUrl?.let {
            val host = it.split(":").first()
            val deeplink = DeeplinkModel(
                id = UUID.randomUUID().toString(),
                host = host,
                url = it,
                description = viewModel.deepLinkDescription)

            if (viewModel.addDeepLinkCompletable == null) {
                viewModel.addDeepLinkCompletable = Completable.fromAction {
                    database.deepLinkDao().insertDeepLink(deeplink)
                }.subscribeOn(Schedulers.io())
            }

            viewModel.addDeepLinkCompletable
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe {
                    delegate.setResultOk()
                    delegate.finish()
                }
        }
    }


    //endregion







}