package ru.trmedia.trbtlservice.comment.presentation

import androidx.room.Entity
import androidx.room.PrimaryKey

data class OneModel(

    var username: String,

    var profilePictureUrl: String,

    var comment: String,

    var punishment: String
)
