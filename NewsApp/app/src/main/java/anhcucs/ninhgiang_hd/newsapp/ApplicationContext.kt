package anhcucs.ninhgiang_hd.newsapp

import android.app.Application

class ApplicationContext : Application() {
    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }

    companion object {
        var INSTANCE: ApplicationContext? = null
        fun getInstance(): ApplicationContext = INSTANCE!!
    }

}