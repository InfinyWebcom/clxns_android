package com.clxns.app.data.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bank_details")
data class BankDetailsEntity(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "fi_image") val fiImage: String,
)