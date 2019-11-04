package ru.islab.evilcomments.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class InstaUser(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    var username: String,

    var profilePictureUrl: String
)
