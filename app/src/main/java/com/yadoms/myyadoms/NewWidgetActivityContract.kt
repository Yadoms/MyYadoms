package com.yadoms.myyadoms

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yadoms.myyadoms.preferences.Preferences

class NewWidgetActivityContract : ActivityResultContract<Unit, WidgetConfiguration?>() {

    override fun createIntent(context: Context, input: Unit?): Intent {
        return Intent(context, NewWidgetActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): WidgetConfiguration? {
        val data = intent?.getStringExtra(ID)
        if (resultCode != Activity.RESULT_OK || data == null)
            return null
        return Preferences.moshi.adapter(WidgetConfiguration::class.java).fromJson(data)
    }

    companion object {
        const val ID = "addedWidget"
        val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }
}