package ru.islab.evilcomments.presentation.creators

import androidx.room.Entity
import androidx.room.PrimaryKey

data class Creator(
    var link: String = "",

    var username: String
)
