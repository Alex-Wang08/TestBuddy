package com.example.testbuddy.deeplink

import androidx.lifecycle.ViewModel
import com.example.testbuddy.deeplink.db.DeeplinkModel
import java.util.ArrayList

class DeeplinkViewModel : ViewModel() {
    var deeplinkList: ArrayList<DeeplinkModel> = ArrayList()
}