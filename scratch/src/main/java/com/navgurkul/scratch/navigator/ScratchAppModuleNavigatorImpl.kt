package com.navgurkul.scratch.navigator

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import com.google.auto.service.AutoService
import com.navgurkul.scratch.ui.ScratchActivity
import org.merakilearn.core.navigator.ScratchAppModuleNavigator


@AutoService(ScratchAppModuleNavigator::class)
class ScratchAppModuleNavigatorImpl : ScratchAppModuleNavigator {
    override fun launchScratchApp(activity: FragmentActivity, bundle: Bundle) {
        //loadKoinModules(typingModule)
        activity.startActivity(Intent(activity, ScratchActivity::class.java).putExtras(bundle),
            ActivityOptions.makeSceneTransitionAnimation(activity).toBundle())
    }
}