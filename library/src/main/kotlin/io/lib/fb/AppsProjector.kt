package io.lib.fb

import android.app.Activity
import android.content.Context
import io.lib.fb.Constants.appsDevKey
import io.lib.fb.heads.AppsflyerMngr
import io.lib.fb.heads.FirebaseMngr
import io.lib.fb.heads.PushMngr
import io.lib.fb.chest.Rpstr
import io.lib.fb.chest.persistroom.LinkDatabase
import io.lib.fb.chest.prfrncs.ChestTools

object AppsProjector {


    lateinit var preferences: ChestTools.Preferences
    var rpstr: Rpstr? = null



    fun createRemoteConfigInstance(activity: Activity): FirebaseMngr {
        preferences = ChestTools.Preferences(activity, Constants.NAME,
            Constants.MAINKEY,
            Constants.CHYPRBOOL
        )
        return FirebaseMngr(activity)
    }

    fun createAppsInstance(context: Context, devKey: String): AppsflyerMngr {
        appsDevKey = devKey
       return AppsflyerMngr(context, devKey)
    }

    fun createOneSignalInstance(context: Context, oneSignalId: String): PushMngr {
      /*  val userDao = LinkDatabase.getDatabase(context).linkDao()
        repository = Repository(userDao)*/
        return PushMngr(context, oneSignalId)
    }

    fun createRepoInstance(context: Context): Rpstr {
        if (rpstr == null){
            return Rpstr(LinkDatabase.getDatabase(context).linkDao())
        } else {
            return rpstr as Rpstr
        }
    }

    //class AppsProjector
}