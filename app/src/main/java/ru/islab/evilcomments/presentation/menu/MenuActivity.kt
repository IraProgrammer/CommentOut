package ru.islab.evilcomments.presentation.menu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
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
import kotlinx.android.synthetic.main.activity_insta_login.*
import kotlinx.android.synthetic.main.activity_menu.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.islab.evilcomments.App
import ru.islab.evilcomments.BuildConfig
import ru.islab.evilcomments.data.AppPreferences
import ru.islab.evilcomments.data.AppPreferences.Companion.NEED_NEW_GAME
import ru.islab.evilcomments.di.module.GameModule
import ru.islab.evilcomments.di.module.MenuModule
import ru.islab.evilcomments.di.module.SignInModule
import ru.islab.evilcomments.requests.VKFriendsRequest
import ru.islab.evilcomments.domain.VKUser
import ru.islab.evilcomments.presentation.game.GameActivity
import ru.islab.evilcomments.presentation.sign_in.SignInActivity
import java.lang.Exception
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import android.widget.Toast
import ru.islab.evilcomments.presentation.creators.CreatorsActivity


class MenuActivity : MvpAppCompatActivity(), MenuView {

    private val UPDATE_CODE = 12

    @Inject
    lateinit var prefs: AppPreferences

    @InjectPresenter
    lateinit var presenter: MenuPresenter

    private lateinit var appUpdateManager: AppUpdateManager

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ru.islab.evilcomments.R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(ru.islab.evilcomments.R.layout.activity_menu)
        App.appComponent?.addMenuComponent(MenuModule())?.inject(this)

        appUpdateManager = AppUpdateManagerFactory.create(baseContext)

        appUpdateManager.appUpdateInfo.addOnSuccessListener { appUpdateInfo ->
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

        if (BuildConfig.VERSION_CODE > prefs.getInt(AppPreferences.VERSION_CODE)) {
            presenter.putDataToDb()
        } else {
            initUI()
        }

        btnNewGame.setOnClickListener { startNewGame() }
        btnContinue.setOnClickListener { continueGame() }
        btnSendOffer.setOnClickListener { sendOffer() }
        btnCreators.setOnClickListener { creators() }
    }

    override fun initUI() {
        llMenu.visibility = View.VISIBLE
        btnContinue.isEnabled = !prefs.getBoolean(NEED_NEW_GAME)
    }

    override fun saveVersionCode() {
        prefs.putInt(AppPreferences.VERSION_CODE, BuildConfig.VERSION_CODE)
    }

    override fun startNewGame() {
        val intent = Intent(baseContext, SignInActivity::class.java)
        startActivity(intent)
    }

    override fun continueGame() {
        val intent = Intent(baseContext, GameActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun sendOffer() {
        val intent = Intent(Intent.ACTION_SENDTO) // it not ACTION_SEND
        intent.putExtra(Intent.EXTRA_SUBJECT, "Задание")
        //intent.putExtra(Intent.EXTRA_TEXT, "Body of email")
        intent.data = Uri.parse("mailto:is.lab.official@gmail.com")
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
    }

    override fun creators() {
        val intent = Intent(baseContext, CreatorsActivity::class.java)
        startActivity(intent)
    }

    private fun requestUpdate(appUpdateInfo: AppUpdateInfo?) {
        appUpdateManager.startUpdateFlowForResult(
            appUpdateInfo,
            AppUpdateType.IMMEDIATE,
            this,
            UPDATE_CODE
        )
    }

    override fun onResume() {
        super.onResume()

        appUpdateManager.getAppUpdateInfo().addOnSuccessListener { appUpdateInfo ->
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                try {
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        UPDATE_CODE
                    );
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}