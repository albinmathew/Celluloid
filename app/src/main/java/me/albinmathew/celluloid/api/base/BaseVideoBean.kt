package me.albinmathew.celluloid.api.base

import kotlinx.parcelize.Parcelize
import me.albinmathew.celluloid.api.response.VideoResponseBean

@Parcelize
data class BaseVideoBean(
    var results: List<VideoResponseBean>? = null
) : BaseBean()
