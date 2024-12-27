package com.yadoms.myyadoms

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContract
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yadoms.myyadoms.preferences.Preferences
import com.yadoms.myyadoms.widgets.WidgetTypes

class NewWidgetActivityContract(
    private val windowTitle: String,
    private val filteredWidgetTypes: Array<WidgetTypes.WidgetType> = WidgetTypes.WidgetType.entries.toTypedArray(),
    private val askForName:Boolean = true
) :
    ActivityResultContract<Unit, Preferences.WidgetData?>() {

    override fun createIntent(context: Context, input: Unit): Intent {
        return Intent(context, NewWidgetActivity::class.java).apply {
            putExtras(Bundle().apply {
                putString("windowTitle", windowTitle)
                putParcelableArrayList("filteredWidgetTypes", ArrayList(filteredWidgetTypes.toList()))
                putBoolean("askForName", askForName)
            })
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Preferences.WidgetData? {
        val data = intent?.getStringExtra(ID)
        return if (resultCode == Activity.RESULT_OK && data != null)
            moshi.adapter(Preferences.WidgetData::class.java).fromJson(data)
        else null
    }

    companion object {
        const val ID = "addedWidget"
        private val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }
}