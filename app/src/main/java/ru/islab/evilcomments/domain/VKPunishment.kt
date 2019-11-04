package ru.islab.evilcomments.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class VKPunishment(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    var text: String
)
