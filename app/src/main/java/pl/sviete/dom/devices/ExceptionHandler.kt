package pl.sviete.dom.devices

import android.util.Log

class ExceptionHandler: java.lang.Thread.UncaughtExceptionHandler {
    val TAG = "ExceptionHandler"

    override fun uncaughtException(t: Thread?, e: Throwable?) {
        Log.e(TAG, t?.name, e)
    }
}