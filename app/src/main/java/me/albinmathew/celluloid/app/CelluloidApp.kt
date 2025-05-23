package me.albinmathew.celluloid.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class CelluloidApp : Application() {
    // TODO: Move genre loading and RestClient initialization to a Hilt module or repository
}
