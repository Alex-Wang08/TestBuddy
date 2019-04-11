package com.example.testbuddy.deeplink

import androidx.lifecycle.ViewModel
import com.example.testbuddy.deeplink.db.DeeplinkModel

interface DeeplinkListDelegate {
    fun <T : ViewModel> getViewModel(clazz: Class<T>): ViewModel
    fun updateDeepLinkList(deepLinkList: List<DeeplinkModel>?)
    fun openAddDeepLinkActivity(requestCode: Int)
    fun restoreItem(position: Int, swipedItem: DeeplinkModel)
    fun showDeepLinkDeleteToast()
    fun dismissSnackbar()
    fun showSnackbar()

    fun removeSnackbarCallback()
    fun addSnackbarCallback()

    fun updateSearchText(searchText: String?)
}