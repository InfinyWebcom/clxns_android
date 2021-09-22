package com.clxns.app.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sub_disposition_table")
data class SubDispositionEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int? = null,
    val name: String? = null,
    val dispositionId: Int? = null
)