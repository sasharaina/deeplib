package io.lib.fb.interfaces

interface AppsflrMngrCallback {

    fun onConversionDataSuccess(data: MutableMap<String, Any>?, url: String)

    fun onConversionDataFail(error: String?)

}