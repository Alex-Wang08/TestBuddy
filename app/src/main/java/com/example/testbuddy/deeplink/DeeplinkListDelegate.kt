package com.example.testbuddy.deeplink

import androidx.lifecycle.ViewModel
import com.example.testbuddy.deeplink.db.DeeplinkModel
import java.util.ArrayList

interface DeeplinkListDelegate {

    fun placeHolder()
    fun <T : ViewModel> getViewModel(clazz: Class<T>): ViewModel
    fun updateDeepLinkList(deepLinkList: ArrayList<DeeplinkModel>)
}