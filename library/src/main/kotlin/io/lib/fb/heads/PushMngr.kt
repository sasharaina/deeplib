package io.lib.fb.heads

import android.content.Context
import com.onesignal.OneSignal

class PushMngr(private val context: Context, private val oneSignalID: String) {

    fun initialize(){
        OneSignal.initWithContext(context)
        OneSignal.setAppId(oneSignalID)
    }

}