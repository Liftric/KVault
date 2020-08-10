package com.liftric

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.app.Service
import android.app.backup.BackupAgent
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.content.ContextWrapper
import android.database.Cursor
import android.net.Uri

private var appContext: Context? = null

val application: Context
    get() = appContext ?: initAndGetAppCtxWithReflection()

fun Context.canLeakMemory(): Boolean = when (this) {
    is Application -> false
    is Activity, is Service, is BackupAgent -> true
    is ContextWrapper -> if (baseContext === this) true else baseContext.canLeakMemory()
    else -> applicationContext === null
}

@SuppressLint("PrivateApi", "DiscouragedPrivateApi")
private fun initAndGetAppCtxWithReflection(): Context {
    // Fallback, should only run once per non default process.
    val activityThread = Class.forName("android.app.ActivityThread")
    val ctx = activityThread.getDeclaredMethod("currentApplication").invoke(null) as Context
    appContext = ctx
    return ctx
}

class AppContext : ContentProvider() {
    override fun onCreate() : Boolean {
        val context = context
        require(!context!!.canLeakMemory()) { "The passed Context($this) would leak memory!" }
        appContext = context
        return true
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri {
        throw Exception("unimplemented")
    }

    override fun query(uri: Uri, projection: Array<out String>?, selection: String?, selectionArgs: Array<out String>?, sortOrder: String?) : Cursor {
        throw Exception("unimplemented")
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<out String>?) : Int {
        throw Exception("unimplemented")
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?) : Int {
        throw Exception("unimplemented")
    }

    override fun getType(uri: Uri) : String {
        throw Exception("unimplemented")
    }
}