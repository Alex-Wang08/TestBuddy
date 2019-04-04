package com.example.testbuddy.utils

import android.view.View
import com.jakewharton.rxbinding3.view.clicks
import io.reactivex.Observable
import java.util.concurrent.TimeUnit

class ViewUtils {
}



fun View?.goneView() {
    this?.visibility = View.GONE
}

fun View.createClickListenerObservable(): Observable<Unit?> {
    return this.clicks().throttleFirst(300L, TimeUnit.MILLISECONDS)
}