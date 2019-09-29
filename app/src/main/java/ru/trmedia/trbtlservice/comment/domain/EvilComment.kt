package ru.trmedia.trbtlservice.comment.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class EvilComment(
    @PrimaryKey(autoGenerate = true)
    var id: Int,

    var text: String
)
