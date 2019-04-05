package com.example.testbuddy.deeplink.adddeeplink

import androidx.lifecycle.LifecycleObserver
import com.example.testbuddy.deeplink.db.DeepLinkDatabase
import com.example.testbuddy.deeplink.db.DeeplinkModel
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class AddDeepLinkPresenter(
    private val delegate: AddDeepLinkDelegate,
    private val database: DeepLinkDatabase
) : LifecycleObserver {

    //region Variables
    private val viewModel: AddDeepLinkViewModel by lazy { delegate.getViewModel(AddDeepLinkViewModel::class.java) as AddDeepLinkViewModel }
    //endregion

    //region Lifecycle
    init {

    }


    //endregion

    //region Clicks
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
                    delegate.finish()
                }
        }
    }


    //endregion







}