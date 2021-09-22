package com.clxns.app.data.database

import androidx.annotation.NonNull
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disposition_table")
data class DispositionEntity(
    @PrimaryKey(autoGenerate = false)
    @NonNull
    val id: Int? = null,
    val name: String? = null
)