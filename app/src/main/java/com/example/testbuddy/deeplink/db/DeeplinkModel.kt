package com.example.testbuddy.deeplink.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "deep_link")
class DeeplinkModel constructor(
    @PrimaryKey var id: String = "",
    var url: String? = null,
    var description: String? = null)