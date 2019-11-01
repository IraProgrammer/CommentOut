package ru.islab.evilcomments.domain

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.json.JSONObject

@Entity
data class VKUser(
    @PrimaryKey
    val id: Int = 0,
    val firstName: String = "",
    val lastName: String = "",
    val photo: String = "",
    val canPost: Boolean = false
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readByte() != 0.toByte()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(firstName)
        parcel.writeString(lastName)
        parcel.writeString(photo)
        parcel.writeByte(if (canPost) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VKUser> {
        override fun createFromParcel(parcel: Parcel): VKUser {
            return VKUser(parcel)
        }

        override fun newArray(size: Int): Array<VKUser?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject) = VKUser(
            id = json.optInt("id", 0),
            firstName = json.optString("first_name", ""),
            lastName = json.optString("last_name", ""),
            photo = json.optString("photo_200_orig", ""),
            canPost = json.optBoolean("can_post", true)
        )
    }
}