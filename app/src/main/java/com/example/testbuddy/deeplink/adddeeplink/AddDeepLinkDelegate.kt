package com.example.testbuddy.deeplink.adddeeplink

import androidx.lifecycle.ViewModel

interface AddDeepLinkDelegate {
    fun <T : ViewModel> getViewModel(clazz: Class<T>): ViewModel
    fun finish()
    fun showDeepLinkEmptyToast()
    fun updateUrlText(url: String?)
    fun updateDescriptionText(description: String?)
    fun setResultOk()
}