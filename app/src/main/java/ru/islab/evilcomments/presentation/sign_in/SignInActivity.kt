package ru.islab.evilcomments.presentation.sign_in

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.vk.api.sdk.*
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKApiCodes
import com.vk.api.sdk.exceptions.VKApiExecutionException
import com.vk.api.sdk.utils.VKUtils
import com.vk.api.sdk.utils.log.DefaultApiLogger
import com.vk.api.sdk.utils.log.Logger
import kotlinx.android.synthetic.main.activity_choise.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.islab.evilcomments.App
import ru.islab.evilcomments.BuildConfig
import ru.islab.evilcomments.data.AppPreferences
import ru.islab.evilcomments.di.module.GameModule
import ru.islab.evilcomments.di.module.SignInModule
import ru.islab.evilcomments.requests.VKFriendsRequest
import ru.islab.evilcomments.domain.VKUser
import ru.islab.evilcomments.presentation.game.GameActivity
import java.lang.Exception
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class SignInActivity : MvpAppCompatActivity(), SignInView {

    @Inject
    lateinit var prefs: AppPreferences

    @InjectPresenter
    lateinit var presenter: SignInPresenter

    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ru.islab.evilcomments.R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(ru.islab.evilcomments.R.layout.activity_choise)
        App.appComponent?.addSignInComponent(SignInModule())?.inject(this)

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

    override fun authorize() {
        if (BuildConfig.VERSION_CODE > prefs.getInt(AppPreferences.VERSION_CODE)) {
            presenter.putDataToDb()
        } else {
            if (VK.isLoggedIn()) {
                requestFriends()
            } else {
                VK.login(this, arrayListOf(VKScope.FRIENDS, VKScope.PHOTOS))
            }
        }
    }

    override fun saveVersionCode() {
        prefs.putInt(AppPreferences.VERSION_CODE, BuildConfig.VERSION_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val callback = object : VKAuthCallback {
            override fun onLogin(token: VKAccessToken) {
                requestFriends()
            }

            override fun onLoginFailed(errorCode: Int) {
            }
        }
        if (!VK.onActivityResult(requestCode, resultCode, data, callback)) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun requestFriends() {
        VK.execute(VKFriendsRequest(), object : VKApiCallback<List<VKUser>> {
            override fun success(result: List<VKUser>) {
                if (!isFinishing && !result.isEmpty()) {
                    presenter.saveFollowsToDb(result)
                }
            }

            override fun fail(error: VKApiExecutionException) {
                Log.e("", error.toString())
            }
        })
    }

    private fun requestUpdate(appUpdateInfo: AppUpdateInfo?) {
    }

    override fun startGame() {
        val intent = Intent(baseContext, GameActivity::class.java)
        intent.putExtra("type", true)
        startActivity(intent)
        finish()
    }
}