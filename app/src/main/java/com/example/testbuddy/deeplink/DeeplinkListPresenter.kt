package com.example.testbuddy.deeplink

import android.app.Activity.RESULT_OK
import android.content.Intent
import com.example.testbuddy.deeplink.db.DeepLinkDatabase
import com.example.testbuddy.deeplink.db.DeeplinkModel
import com.example.testbuddy.utils.RequestCode
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.util.*

class DeeplinkListPresenter constructor(
    private val delegate: DeeplinkListDelegate,
    private val deepLinkDatabase: DeepLinkDatabase
) {

    //region Variables
    private val viewModel: DeeplinkViewModel by lazy { delegate.getViewModel(DeeplinkViewModel::class.java) as DeeplinkViewModel }
    private var disposable: Disposable? = null
    //endregion

    //region Lifecycle
    fun onAttach() {
        updateDeepLinkList()
    }

    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RequestCode.ADD_DEEP_LINK && resultCode == RESULT_OK) {
            refreshDeepLinkList()
        }
    }
    //endregion

    //region Clicks
    fun onAddDeepLinkClick() {
        delegate.openAddDeepLinkActivity(RequestCode.ADD_DEEP_LINK)
    }
    //endregion

    //region Private Helpers
    private fun getDeepLinksFromDb(): Single<List<DeeplinkModel>> =
            deepLinkDatabase.deepLinkDao()
                .getDeepLinkList()
                .filter { it.isNotEmpty() }
                .toSingle()
                .subscribeOn(Schedulers.io())

    private fun updateDeepLinkList() {
        if (viewModel.deeplinkList.isNullOrEmpty()) {
            if (viewModel.getDeepLinkListSingle == null) {
                viewModel.getDeepLinkListSingle = getDeepLinksFromDb()
            }

            disposable = viewModel.getDeepLinkListSingle
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe(
                    {
                        viewModel.deeplinkList = it
                        if (it.isEmpty()) {
                            delegate.openAddDeepLinkActivity(RequestCode.ADD_DEEP_LINK)
                        } else {
                            delegate.updateDeepLinkList(it)
                        }

                        disposable?.dispose()
                    },
                    {
                        disposable?.dispose()
                    }
                )
        } else {
            delegate.updateDeepLinkList(viewModel.deeplinkList)
        }
    }

    private fun refreshDeepLinkList() {
        if (viewModel.getDeepLinkListSingle == null) {
            viewModel.getDeepLinkListSingle = getDeepLinksFromDb()
        }

        disposable = viewModel.getDeepLinkListSingle
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe(
                {
                    viewModel.deeplinkList = it
                    delegate.updateDeepLinkList(it)
                    disposable?.dispose()
                },
                {
                    disposable?.dispose()
                }
            )
    }
    //endregion
}