package ru.islab.evilcomments.presentation.follows

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.ActivityManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.*
import android.webkit.*
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
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_game.*
import ru.islab.evilcomments.App
import ru.islab.evilcomments.BuildConfig
import ru.islab.evilcomments.R
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

    var isLoadingShownNow = false

    val parsingHandler = Handler()

    var parsingRunnable = Runnable {}

    var anim: Animation? = null

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

        anim = AnimationUtils.loadAnimation(this, ru.islab.evilcomments.R.anim.blink)

        instaLoginPresenter.observeNetwork(baseContext)
    }

    override fun saveVersionCode() {
        prefs.putInt(VERSION_CODE, BuildConfig.VERSION_CODE)
    }

    override fun noNetworkInStart() {
        llNoNetwork.visibility = View.VISIBLE
    }

    override fun networkFailed() {
//        parsingHandler.removeCallbacks(parsingRunnable)
//        if (llProgress.isVisible) {
//            wvInsta.stopLoading()
//            tvText.text = getString(ru.islab.evilcomments.R.string.please_check_internet)
//            progressAnimator?.pause()
//            pbHorizontal.visibility = View.GONE
//        } else {
//            llNoNetwork.visibility = View.VISIBLE
//        }
    }

    override fun networkSuccessed() {
        if (!wvInsta.url.equals("https://www.instagram.com/accounts/login/")) {
            wvInsta.reload()
        }
        if (btnStartGame.visibility == View.GONE) {
            pbHorizontal.visibility = View.VISIBLE
            llNoNetwork.visibility = View.GONE
            tvText.text = getString(ru.islab.evilcomments.R.string.please_wait)
            progressAnimator?.start()
            //wvInsta.reload()
        }
    }

    override fun initUI() {
        when {
            prefs.getBoolean(SHOW_ADULT) -> {
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
                    "Используя данное приложение, Вы подтверждаете, что несёте полную ответственность за свои действия.\nДанное приложение строго для лиц, достигших возраста 18 лет."

                btnAgree.setOnClickListener { v ->
                    prefs.putBoolean(SHOW_ADULT, false)
                    showSafetlyDialog()
                    dialog.dismiss()
                }
                btnExit.setOnClickListener { v -> finish() }
            }
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
            .setView(l)
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

        btnStartGame.visibility = View.GONE

        tvText.text = getString(R.string.please_wait)

        wvInsta.layoutParams = ConstraintLayout.LayoutParams(300, 500)

        progressAnimator = ObjectAnimator.ofInt(pbHorizontal, "progress", 0, 10000)
        progressAnimator?.addListener(onEnd = {
            if (!wvInsta.url.contains("/following/")) {
                showAuthorizationScreen(
                    "Произошла ошибка при импорте подписок. Пожалуйста, проверьте интернет-соединение попробуйте снова.",
                    "ОК"
                )
            }
        })
        progressAnimator?.duration = 38000
        progressAnimator?.interpolator = LinearInterpolator()
        progressAnimator?.start()
    }

    private fun showAuthorizationScreen(text: String, buttonText: String) {
        wvInsta.stopLoading()
        pbHorizontal.visibility = View.GONE
        tvText.text = text
        btnStartGame.visibility = View.VISIBLE
        btnStartGame.startAnimation(anim)
        btnStartGame.text = buttonText
        btnStartGame.setOnClickListener {
            finish()
        }
    }

    override fun startGame() {
        btnStartGame.text = "НАЧАТЬ ИГРУ!"
        btnStartGame.setOnClickListener { v ->
            progressAnimator?.removeAllListeners()
            val intent = Intent(baseContext, GameActivity::class.java)
            startActivity(intent)
            finish()
        }
        btnStartGame.visibility = View.VISIBLE
        btnStartGame.startAnimation(anim)
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

        //wvInsta.webChromeClient = object : WebChromeClient(){on}

        wvInsta.webViewClient = object : WebViewClient() {

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                parsingHandler.removeCallbacks(parsingRunnable)
                if (llProgress.isVisible) {
                    //wvInsta.stopLoading()
                    tvText.text = getString(ru.islab.evilcomments.R.string.please_check_internet)
                    progressAnimator?.pause()
                    pbHorizontal.visibility = View.GONE
                }
//                else {
//                    llNoNetwork.visibility = View.VISIBLE
//                }
                //super.onReceivedError(view, request, error)
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                if (url.equals("https://www.instagram.com/" + prefs.getString(AppPreferences.USER_NAME) + "/")) {
                    if (!isLoadingShownNow) {
                        onShowLoading()
                        isLoadingShownNow = true
                    }
                }
            }

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
                        wvInsta.evaluateJavascript(
                            "(function(){return window.document.body.outerHTML})();",
                            object : ValueCallback<String> {
                                override fun onReceiveValue(html: String) {
                                    Log.d("HTML", html)

                                    val a = html.split("\\\"")

                                    for (i in a.indices) {
                                        if (a[i].equals("username")) {
                                            if (!isLoadingShownNow) {
                                                onShowLoading()
                                                isLoadingShownNow = true
                                            }
                                            prefs.putString(AppPreferences.USER_NAME, a[i + 2])
                                            wvInsta.loadUrl("https://www.instagram.com/" + a[i + 2])
                                            break
                                        }
                                    }
                                }
                            })
                    }
                }
            }
        }
    }

    private fun parseFollowsWithDelay() {
        parsingRunnable = Runnable {
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
                        if (follows.size > 0) {
                            tvText.visibility = View.GONE
                            instaLoginPresenter.saveFollowsToDb(follows)
                        } else {
                            progressAnimator?.removeAllListeners()
                            tvText.text =
                                "Произошла ошибка при импорте подписок. Пожалуйста, убедитесь в наличии подписок на Вашем аккаунте и проверьте интернет-соединение."
                            btnStartGame.text = "ПОНЯТНО"
                            btnStartGame.visibility = View.VISIBLE
                            btnStartGame.startAnimation(anim)
                            btnStartGame.setOnClickListener { finish() }
                        }
                    }
                })
        }

        parsingHandler.postDelayed(parsingRunnable, 30000)

    }

    override fun onResume() {
        wvInsta.onResume()
        super.onResume()
    }

    override fun onPause() {
        wvInsta.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        wvInsta.destroy()
        super.onDestroy()
    }
}
