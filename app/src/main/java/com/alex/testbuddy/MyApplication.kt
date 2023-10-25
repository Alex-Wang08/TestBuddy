package com.alex.testbuddy

import android.app.Application

class MyApplication : Application() {
    companion object {
        fun getInstance(): MyApplication {
            return MyApplication()
        }
    }
}
