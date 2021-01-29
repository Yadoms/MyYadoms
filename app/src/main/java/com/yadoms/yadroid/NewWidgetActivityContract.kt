package com.yadoms.yadroid

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.activity.result.contract.ActivityResultContract
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yadoms.yadroid.preferences.Preferences

class NewWidgetActivityContract : ActivityResultContract<Unit, Preferences.Widget?>() {

    override fun createIntent(context: Context, input: Unit?): Intent {
        return Intent(context, NewWidgetActivity::class.java)
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Preferences.Widget? {
        val data = intent?.getStringExtra(ID)
        return if (resultCode == Activity.RESULT_OK && data != null)
            moshi.adapter(Preferences.Widget::class.java).fromJson(data)
        else null
    }

    companion object {
        const val ID = "addedWidget"
        private val moshi: Moshi = Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }
}