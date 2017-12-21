package pl.poznan.put.rainbowtranslator

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import io.fabric.sdk.android.Fabric


class RainbowTranslatorApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Fabric.with(this, Crashlytics.Builder().core(CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build())
    }
}