package ru.trmedia.trbtlservice.comment.domain

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class UserWrap(
    @Expose
    @SerializedName("data")
    var user: User
)

data class User(
    @Expose
    var id: String,
    @Expose
    var username: String,
    @SerializedName("profile_picture")
    @Expose
    var profilePictureUrl: String
)
