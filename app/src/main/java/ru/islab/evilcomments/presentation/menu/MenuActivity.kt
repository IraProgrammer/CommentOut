package ru.islab.evilcomments.presentation.menu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.android.synthetic.main.activity_menu.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.islab.evilcomments.App
import ru.islab.evilcomments.BuildConfig
import ru.islab.evilcomments.data.AppPreferences
import ru.islab.evilcomments.data.AppPreferences.Companion.NEED_INSTA_NEW_GAME
import ru.islab.evilcomments.di.module.MenuModule
import ru.islab.evilcomments.presentation.game.GameActivity
import ru.islab.evilcomments.presentation.sign_in.SignInActivity
import java.lang.Exception
import javax.inject.Inject
import androidx.appcompat.app.AlertDialog
import ru.islab.evilcomments.R
import ru.islab.evilcomments.data.AppPreferences.Companion.NEED_VK_NEW_GAME
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

        when {
            prefs.getBoolean(AppPreferences.SHOW_ADULT) -> {
                val l = layoutInflater.inflate(
                    R.layout.dialog_adult,
                    null
                )

                val tv = l.findViewById<TextView>(R.id.tvAdultText)
                val btnExit = l.findViewById<Button>(R.id.btnExit)
                val btnAgree = l.findViewById<Button>(R.id.btnAgree)

                val dialog = AlertDialog.Builder(this)
                    .setView(l)
                    .setCancelable(false)
                    .create()

                dialog.window?.setBackgroundDrawableResource(R.color.transparent)
                dialog.show()

                tv.text =
                    "Используя данное приложение, Вы действуете от своего имени и несёте полную ответственность за возможные последствия." +
                            "\nЗадания в игре не являются призывом к действию и созданы исключительно в развлекательных целях, " +
                            "могут содержать ненормативную лексику и грубый юмор.\n" +
                            "Если Вы считаете задание оскорбительным, откажитесь от его выполнения."

                btnAgree.setOnClickListener { v ->
                    prefs.putBoolean(AppPreferences.SHOW_ADULT, false)
                    if (BuildConfig.VERSION_CODE > prefs.getInt(AppPreferences.VERSION_CODE)) {
                        presenter.putDataToDb()
                    } else {
                        initUI()
                    }
                    dialog.dismiss()
                }
                btnExit.setOnClickListener { v -> finish() }
            }
            BuildConfig.VERSION_CODE > prefs.getInt(AppPreferences.VERSION_CODE) -> {
                presenter.putDataToDb()
            }
            else -> {
                initUI()
            }
        }

        btnNewGame.setOnClickListener { startNewGame() }
        btnContinue.setOnClickListener { continueGame() }
        btnSendOffer.setOnClickListener { sendOffer() }
        btnCreators.setOnClickListener { creators() }
    }

    override fun initUI() {
        llMenu.visibility = View.VISIBLE
        btnContinue.isEnabled =
            !(prefs.getBoolean(NEED_INSTA_NEW_GAME) && prefs.getBoolean(NEED_VK_NEW_GAME))
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
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.putExtra(Intent.EXTRA_SUBJECT, "EC: Задания")
        intent.putExtra(
            Intent.EXTRA_TEXT,
            "Форма отправки:\n1. Страна, город* :\n2. Никнейм (до 20 символов)* :\n3. Ссылка на соц.сеть:\n4.Задания* :\nПоля с обозначением * обязательны для заполнения."
        )
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