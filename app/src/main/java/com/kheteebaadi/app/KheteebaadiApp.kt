package com.kheteebaadi.app

import android.app.Application
import com.kheteebaadi.app.data.Api
import com.kheteebaadi.app.data.Locations

class KheteebaadiApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Api.init(this)
        Locations.load(this)
    }
}
