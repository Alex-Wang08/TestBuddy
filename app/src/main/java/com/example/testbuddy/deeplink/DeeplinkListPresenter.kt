package com.example.testbuddy.deeplink

import com.example.testbuddy.deeplink.db.DeepLinkDatabase
import com.example.testbuddy.deeplink.db.DeeplinkModel
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

        viewModel.deeplinkList?.let {
            if (it.isNotEmpty()) {
                delegate.updateDeepLinkList(it)
                return
            }
        }

        delegate.openAddDeepLinkActivity()
    }
    //endregion

    //region Private Helpers
    private fun initializeDeepLinkList() {
        val deepLinkList = ArrayList<DeeplinkModel>()
        val url = "gasbuddy://settings"
        val host = url.split(":").first()

        val settingsDeepLink = DeeplinkModel(
            id = UUID.randomUUID().toString(),
            host = host,
            url = url,
            description = "open settings screen")
        deepLinkList.add(settingsDeepLink)
        viewModel.deeplinkList = deepLinkList
    }

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
                        disposable?.dispose()
                    },
                    {
                        disposable?.dispose()
                    }
                )
        }

        if (viewModel.deeplinkList.isNullOrEmpty()) {
            initializeDeepLinkList()
        }
    }
    //endregion
}