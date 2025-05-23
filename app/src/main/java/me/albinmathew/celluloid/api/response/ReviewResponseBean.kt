package me.albinmathew.celluloid.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ReviewResponseBean(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("author")
    var author: String? = null,
    @SerializedName("content")
    var content: String? = null,
    @SerializedName("url")
    var url: String? = null
) : Parcelable
