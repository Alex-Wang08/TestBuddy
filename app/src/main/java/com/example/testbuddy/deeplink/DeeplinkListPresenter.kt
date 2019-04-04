package com.example.testbuddy.deeplink

import com.example.testbuddy.deeplink.db.DeeplinkModel

class DeeplinkListPresenter constructor(
    private val delegate: DeeplinkListDelegate


) {

    //region Variables
    private val viewModel: DeeplinkViewModel by lazy { delegate.getViewModel(DeeplinkViewModel::class.java) as DeeplinkViewModel }
    //endregion

    //region Lifecycle
    fun onAttach() {
        if (viewModel.deeplinkList.isNullOrEmpty()) {
            initializeData()
        }

        if (viewModel.deeplinkList.size == 0) {
            delegate.openAddDeepLinkActivity()
        } else {
            delegate.updateDeepLinkList(viewModel.deeplinkList)
        }
    }
    //endregion

    //region Private Helpers
    private fun initializeData() {
        val settingsDeepLink = DeeplinkModel(
            "gasbuddy://settings",
            "open settings screen")

        viewModel.deeplinkList.add(settingsDeepLink)
    }
    //endregion
}