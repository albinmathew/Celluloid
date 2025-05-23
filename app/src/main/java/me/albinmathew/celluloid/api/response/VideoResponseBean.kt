package me.albinmathew.celluloid.api.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class VideoResponseBean(
    @SerializedName("id")
    var id: String? = null,
    @SerializedName("name")
    var name: String? = null,
    @SerializedName("site")
    var site: String? = null,
    @SerializedName("key")
    var videoId: String? = null, // "key" in JSON
    @SerializedName("size")
    var size: Int = 0,
    @SerializedName("type")
    var type: String? = null
) : Parcelable
