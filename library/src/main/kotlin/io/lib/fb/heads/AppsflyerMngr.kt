package io.lib.fb.heads

import android.content.Context
import android.util.Log
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import io.lib.fb.AppsProjector
import io.lib.fb.AppsProjector.preferences
import io.lib.fb.Constants
import io.lib.fb.Constants.LOG
import io.lib.fb.Constants.ONCONVERSION
import io.lib.fb.Constants.TAG
import io.lib.fb.Constants.TRUE
import io.lib.fb.interfaces.AppsflrMngrCallback
import io.lib.fb.interfaces.RemoteMngrCallback
import io.lib.fb.chest.persistroom.model.Link
import io.lib.fb.Tools

class AppsflyerMngr(private val context: Context, private val appsDevKey: String):
    RemoteMngrCallback {

    private lateinit var appsflrMngrCallback: AppsflrMngrCallback

    fun start(offerUrl: String) {

        appsflrMngrCallback = context as AppsflrMngrCallback

        //  if (LOG) Log.d(TAG, "got apps Data - method invoked")
        val conversionDataListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                data?.let { cvData ->
                    cvData.map {
                        if (LOG) Log.d(TAG, "got apps Data - succes conversion")
                        when (preferences.getOnConversionDataSuccess(ONCONVERSION)) {
                            "null" -> {
                                preferences.setOnConversionDataSuccess(
                                    ONCONVERSION,
                                    TRUE
                                )
                                if (LOG) Log.d(TAG, "got apps Data - $data")
                                if (data["campaign"].toString().contains("sub")) {

                                    /*
                                    if (LOG) Log.d(TAG, "added link to storage - " + MainClass.utils.getFinalUrl(
                                            part1 + part2 + part3, data["campaign"].toString(), context))
                                     */

                                    val url = Tools.getFinalUrl(
                                        offerUrl,
                                        data["campaign"].toString(),
                                        context, data["af_c_id"].toString(),
                                        data["media_source"].toString(),
                                    )


                                    if (LOG) Log.d(TAG, "$url -- final url")
                                    AppsProjector.createRepoInstance(context).insert(Link(1, url))
                                    //   if (LOG) Log.d(TAG, "added to viewmodel number 1")
                                    appsflrMngrCallback.onConversionDataSuccess(data, url)

                                } else {
                                    preferences.setOnConversionDataSuccess(
                                        ONCONVERSION,
                                        Constants.TRUE
                                    )
                                    val url = offerUrl + "?app_id=" + Tools.getAppBundle(context) +
                                            "&af_status=" + "Organic" +
                                            "&afToken=" + appsDevKey +
                                            "&afid=" + AppsFlyerLib.getInstance().getAppsFlyerUID(context)
                                    //  if (LOG) Log.d(TAG, "url - $url")
                                    AppsProjector.createRepoInstance(context).insert(Link(1, url))
                                    //  if (LOG) Log.d(TAG, "added to viewmodel number 2")
                                    appsflrMngrCallback.onConversionDataSuccess(data, url)
                                }
                            }
                            "true" -> {

                            }
                            "false" -> {

                            }
                            else -> {

                            }
                        }


                    }
                }
            }

            override fun onConversionDataFail(error: String?) {
                if (LOG) Log.d(TAG, "onConversionDataFail")
                appsflrMngrCallback.onConversionDataFail(error)
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                data?.map {
                    if (LOG) Log.d(TAG, "onAppOpenAttribution")
                }
            }

            override fun onAttributionFailure(error: String?) {
                if (LOG) Log.d(TAG, "onAttributionFailure")
            }
        }
        //инициализируем SDK AppsFlyer'a
        AppsFlyerLib.getInstance().init(appsDevKey, conversionDataListener, context)
        AppsFlyerLib.getInstance().start(context)
    }

    override fun onFalseCode(int: Int) {

    }

    override fun onSuccessCode(offerUrl: String) {
        Log.d(TAG, "onSuccessCode AppsFlyer Class")
        start(offerUrl)
    }

    override fun onStatusTrue() {

    }

    override fun onStatusFalse() {

    }

    override fun nonFirstLaunch(url: String) {

    }

    override fun onDeepLinkSuccess(
        fbappid: String,
        fbappsecret: String,
        offerUrl: String,
        naming: String
    ) {

    }

}