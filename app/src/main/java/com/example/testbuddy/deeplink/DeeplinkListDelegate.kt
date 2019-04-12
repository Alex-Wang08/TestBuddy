package com.example.testbuddy.deeplink

import androidx.lifecycle.ViewModel
import com.example.testbuddy.deeplink.db.DeeplinkModel
import java.util.logging.Filter

interface DeeplinkListDelegate {
    fun <T : ViewModel> getViewModel(clazz: Class<T>): ViewModel
    fun updateDeepLinkList(deepLinkList: List<DeeplinkModel>?, searchText: String? = null)
    fun openAddDeepLinkActivity(requestCode: Int)
    fun showDeepLinkDeleteToast()
    fun dismissSnackbar()
    fun showSnackbar()
    fun removeSnackbarCallback()
    fun addSnackbarCallback()

    fun updateSearchText(searchText: String?)
}