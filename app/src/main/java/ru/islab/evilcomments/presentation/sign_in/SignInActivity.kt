package ru.islab.evilcomments.presentation.sign_in

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.vk.api.sdk.VK
import com.vk.api.sdk.VKApiCallback
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKApiExecutionException
import com.vk.api.sdk.requests.VKRequest
import com.vk.api.sdk.utils.VKUtils
import kotlinx.android.synthetic.main.activity_choise.*
import kotlinx.android.synthetic.main.activity_insta_login.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.islab.evilcomments.App
import ru.islab.evilcomments.data.AppPreferences
import ru.islab.evilcomments.di.module.InstaLoginModule
import ru.islab.evilcomments.presentation.VKFriendsRequest
import ru.islab.evilcomments.presentation.VKUser
import ru.islab.evilcomments.presentation.follows.InstaLoginPresenter
import java.lang.Exception
import javax.inject.Inject

class SignInActivity : MvpAppCompatActivity() {

    @Inject
    lateinit var prefs: AppPreferences

    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ru.islab.evilcomments.R.style.AppTheme)
        super.onCreate(savedInstanceState)
        //App.appComponent?.addInstaLoginComponent(InstaLoginModule())?.inject(this)
        setContentView(ru.islab.evilcomments.R.layout.activity_choise)

        appUpdateManager = AppUpdateManagerFactory.create(baseContext)

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE &&
                appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)
            ) {
                try {
                    requestUpdate(appUpdateInfo);
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        VK.initialize(baseContext)

        btnVk.setOnClickListener { authorize() }
    }

    private fun authorize() {
        VK.login(this, arrayListOf(VKScope.FRIENDS))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object: VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                requestFriends()
                var с = 8
            }

            override fun onLoginFailed(errorCode: Int) {
                var g = 800
            }
        }
        if (!VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun requestFriends() {
        VK.execute(VKFriendsRequest(), object: VKApiCallback<List<VKUser>> {
            override fun success(result: List<VKUser>) {
                if (!isFinishing && !result.isEmpty()) {
                    //showFriends(result)
                    var s = 9
                }
            }
            override fun fail(error: VKApiExecutionException) {
                Log.e("", error.toString())
            }
        })
    }

    private fun requestUpdate(appUpdateInfo: AppUpdateInfo?) {
    }
}