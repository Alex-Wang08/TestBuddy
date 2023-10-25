package com.alex.testbuddy.utils

import com.alex.testbuddy.MyApplication

fun MyApplication.getAppVersion(): String {
    return try {
        val packageInfo = applicationContext.packageManager.getPackageInfo(baseContext.packageName, 0)
        "${packageInfo.versionName} (${packageInfo.versionCode})"
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }
}