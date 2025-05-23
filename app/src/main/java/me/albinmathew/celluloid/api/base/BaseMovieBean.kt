package me.albinmathew.celluloid.api.base

import kotlinx.parcelize.Parcelize
import me.albinmathew.celluloid.api.response.MoviesResponseBean

@Parcelize
data class BaseMovieBean(
    var results: List<MoviesResponseBean>? = null
) : BaseBean()
