package com.example.testbuddy.deeplink

import com.example.testbuddy.deeplink.db.DeeplinkModel
import java.util.ArrayList

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

        delegate.updateDeepLinkList(viewModel.deeplinkList)
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