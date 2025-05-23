package me.albinmathew.celluloid.api.base

import kotlinx.parcelize.Parcelize
import me.albinmathew.celluloid.api.response.ReviewResponseBean

@Parcelize
data class BaseReviewBean(
    var results: List<ReviewResponseBean>? = null
) : BaseBean()
