package ru.trmedia.trbtlservice.comment.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Follow(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    var username: String,

    var profilePictureUrl: String
)
