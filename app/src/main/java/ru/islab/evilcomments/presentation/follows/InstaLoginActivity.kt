package ru.islab.evilcomments.presentation.follows

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.*
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import kotlinx.android.synthetic.main.activity_insta_login.*
import kotlinx.android.synthetic.main.activity_insta_login.tvText
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.islab.evilcomments.data.AppPreferences
import ru.islab.evilcomments.data.AppPreferences.Companion.SHOW_SAFE
import ru.islab.evilcomments.domain.Follow
import ru.islab.evilcomments.presentation.game.GameActivity
import android.widget.Button
import androidx.constraintlayout.widget.ConstraintLayout
import ru.islab.evilcomments.App
import ru.islab.evilcomments.BuildConfig
import ru.islab.evilcomments.data.AppPreferences.Companion.SHOW_ADULT
import ru.islab.evilcomments.data.AppPreferences.Companion.VERSION_CODE
import ru.islab.evilcomments.di.module.InstaLoginModule
import javax.inject.Inject


class InstaLoginActivity : MvpAppCompatActivity(),
    InstaLoginView {

    @InjectPresenter
    lateinit var instaLoginPresenter: InstaLoginPresenter

    @Inject
    lateinit var prefs: AppPreferences

    var progressAnimator: ObjectAnimator? = null

    var canParse = true

    var isLoadingShownNow = false

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ru.islab.evilcomments.R.style.AppTheme)
        super.onCreate(savedInstanceState)
        App.appComponent?.addInstaLoginComponent(InstaLoginModule())?.inject(this)
        setContentView(ru.islab.evilcomments.R.layout.activity_insta_login)

        if (BuildConfig.VERSION_CODE > prefs.getInt(VERSION_CODE)) {
            instaLoginPresenter.putDataToDb()
        } else {

            if (!prefs.getBoolean(AppPreferences.NEED_NEW_GAME)) {
                val intent = Intent(baseContext, GameActivity::class.java)
                startActivity(intent)
                finish()
            }

            initUI()
        }

        instaLoginPresenter.checkNetwork()

        instaLoginPresenter.observeNetwork(baseContext)

        btnStartGame.setOnClickListener { v ->
            val intent = Intent(baseContext, GameActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun saveVersionCode() {
        prefs.putInt(VERSION_CODE, BuildConfig.VERSION_CODE)
    }

    override fun noNetworkInStart() {
        llNoNetwork.visibility = View.VISIBLE
    }

    override fun networkFailed() {
        if (llProgress.isVisible) {
            tvText.text = getString(ru.islab.evilcomments.R.string.please_check_internet)
            progressAnimator?.pause()
            canParse = false
            pbHorizontal.visibility = View.GONE
            wvInsta.stopLoading()
        } else {
            llNoNetwork.visibility = View.VISIBLE
        }
    }

    override fun networkSuccessed() {
        pbHorizontal.visibility = View.VISIBLE
        canParse = true
        llNoNetwork.visibility = View.GONE
        tvText.text = getString(ru.islab.evilcomments.R.string.please_wait)
        progressAnimator?.start()
        wvInsta.reload()
    }

    override fun initUI() {
        when {
            prefs.getBoolean(SHOW_ADULT) -> AlertDialog.Builder(this)
                .setTitle("Внимание!")
                .setMessage("Используя данное приложение, Вы подтверждаете, что несёте полную ответственность за свои действия.\nДанное приложение строго для лиц, достигших возраста 18 лет.")
                .setNegativeButton("Мне есть 18 лет") { _, _ ->
                    prefs.putBoolean(SHOW_ADULT, false)
                    showSafetlyDialog()
                }
                .setPositiveButton("Выход") { _, _ -> finish() }
                .create()
                .show()
            prefs.getBoolean(SHOW_SAFE) -> showSafetlyDialog()
            else -> initializeWebView()
        }
    }

    private fun showSafetlyDialog() {
        val l = layoutInflater.inflate(
            ru.islab.evilcomments.R.layout.dialog_safetly,
            null
        )

        val chb =
            l.findViewById<CheckBox>(ru.islab.evilcomments.R.id.chbDontShowAgain)
        val btn = l.findViewById<Button>(ru.islab.evilcomments.R.id.btnAgree)

        val dialog = AlertDialog.Builder(this)
            .setView(l, 0, 48, 0, 48)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(ru.islab.evilcomments.R.color.transparent)
        dialog.show()

        btn.setOnClickListener { v ->
            run {
                if (chb.isChecked) {
                    prefs.putBoolean(SHOW_SAFE, false)
                }
                dialog.dismiss()
                initializeWebView()
            }
        }
    }

    private fun onShowLoading() {
        llProgress.visibility = View.VISIBLE
        pbHorizontal.visibility = View.VISIBLE

        wvInsta.layoutParams = ConstraintLayout.LayoutParams(300, 500)

        progressAnimator = ObjectAnimator.ofInt(pbHorizontal, "progress", 0, 10000)
//        progressAnimator?.addListener(onEnd = {
//            pbHorizontal.visibility = View.GONE
//            tvText.text = "Готово!"
//        })
        progressAnimator?.duration = 38000
        progressAnimator?.interpolator = LinearInterpolator()
        progressAnimator?.start()
    }

    override fun startGame() {
        btnStartGame.visibility = View.VISIBLE

        val anim = AnimationUtils.loadAnimation(this, ru.islab.evilcomments.R.anim.blink)
        btnStartGame.startAnimation(anim)

        //tvText.text = "Готово!"
    }

    private fun clickFollowers() {
        wvInsta.evaluateJavascript(
            "(function() { return ('<html>'+document.getElementsByTagName('A')[2].click()+'</html>'); })();",

            null
        )
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initializeWebView() {

        wvInsta.settings.javaScriptEnabled = true
        wvInsta.settings.domStorageEnabled = true
        wvInsta.loadUrl(
            "https://www.instagram.com/accounts/login"
        )

        wvInsta.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                when {
                    url.contains(
                        "/following/"
                    ) -> {
                        parseFollowsWithDelay()
                    }
                    url.equals("https://www.instagram.com/" + prefs.getString(AppPreferences.USER_NAME) + "/") -> {
                        clickFollowers()
                    }
                    url.equals("https://www.instagram.com/") -> {
                        if (!isLoadingShownNow) {
                            onShowLoading()
                            isLoadingShownNow = true
                        }
                        wvInsta.evaluateJavascript(
                            "(function(){return window.document.body.outerHTML})();",
                            object : ValueCallback<String> {
                                override fun onReceiveValue(html: String) {
                                    Log.d("HTML", html)

                                    val a = html.split("\\\"")

                                    for (i in a.indices) {
                                        if (a[i].equals("username")) {
                                            prefs.putString(AppPreferences.USER_NAME, a[i + 2])
                                            wvInsta.loadUrl("https://www.instagram.com/" + a[i + 2])
                                            break
                                        }
                                    }
                                }
                            })
                    }
                    url.contains("?error") -> Toast.makeText(
                        baseContext,
                        getString(ru.islab.evilcomments.R.string.please_check_internet),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun parseFollowsWithDelay() {
        Handler().postDelayed(
            {
                wvInsta.evaluateJavascript(
                    "(function(){return window.document.body.outerHTML})();",
                    object : ValueCallback<String> {
                        override fun onReceiveValue(html: String) {

                            Log.d("HTML", html)

                            val list = html.split("img alt=\\\"")

                            val peoples = list.subList(1, list.size)

                            val follows = ArrayList<Follow>()

                            for (i in peoples.indices) {

                                val units = peoples[i].split("\"")
                                val follow = Follow(0, "", "")

                                for (j in units.indices) {
                                    if (units[j].equals(" src=\\")) {
                                        follow.profilePictureUrl =
                                            units[j + 1].substring(0, units[j + 1].length - 1)
                                        continue
                                    } else if (units[j].equals(" title=\\") && units[j + 2].equals(
                                            " href=\\"
                                        )
                                    ) {
                                        follow.username =
                                            units[j + 1].substring(0, units[j + 1].length - 1)
                                        follows.add(follow)
                                    }
                                }
                            }
                            pbHorizontal.visibility = View.GONE
                            if (canParse && follows.size > 0) {
                                tvText.visibility = View.GONE
                                instaLoginPresenter.saveFollowsToDb(follows)
                            } else {
                                tvText.text =
                                    "К сожалению, Вы ни на кого не подписаны. Для продолжения игры необходимо наличие подписок на Вашем аккаунте."
                            }
                        }
                    })
            }, 30000
        )
    }

    override fun onDestroy() {
        wvInsta.destroy()
        super.onDestroy()
    }
}
