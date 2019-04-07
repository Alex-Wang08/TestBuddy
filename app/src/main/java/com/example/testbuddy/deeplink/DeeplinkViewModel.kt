package com.example.testbuddy.deeplink

import androidx.lifecycle.ViewModel
import com.example.testbuddy.deeplink.db.DeeplinkModel
import io.reactivex.Single

class DeeplinkViewModel : ViewModel() {
    var deeplinkList: List<DeeplinkModel>? = null
    var getDeepLinkListSingle: Single<List<DeeplinkModel>>? = null
    var swipedItemPosition: Int = 0
    var swipedItem: DeeplinkModel? = null
}