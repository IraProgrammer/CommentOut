package ru.trmedia.trbtlservice.comment.presentation.follows

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
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
import ru.trmedia.trbtlservice.comment.data.network.AppPreferences
import ru.trmedia.trbtlservice.comment.data.network.AppPreferences.Companion.SHOW_SAFE
import ru.trmedia.trbtlservice.comment.domain.Follow
import ru.trmedia.trbtlservice.comment.domain.UserWrap
import ru.trmedia.trbtlservice.comment.presentation.game.GameActivity
import android.widget.LinearLayout
import android.R
import androidx.constraintlayout.widget.ConstraintLayout
import ru.trmedia.trbtlservice.comment.App
import ru.trmedia.trbtlservice.comment.di.module.InstaLoginModule
import javax.inject.Inject


class InstaLoginActivity : MvpAppCompatActivity(),
    InstaLoginView {

    @InjectPresenter
    lateinit var instaLoginPresenter: InstaLoginPresenter

    @Inject
    lateinit var prefs: AppPreferences

    var progressAnimator: ObjectAnimator? = null

    var canParse = true

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ru.trmedia.trbtlservice.comment.R.style.AppTheme)
        super.onCreate(savedInstanceState)
        App.appComponent?.addInstaLoginComponent(InstaLoginModule())?.inject(this)
        setContentView(ru.trmedia.trbtlservice.comment.R.layout.activity_insta_login)

        instaLoginPresenter.checkNetwork()

        instaLoginPresenter.observeNetwork(baseContext)

        initUI()

        btnStartGame.setOnClickListener { v ->
            val intent = Intent(baseContext, GameActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun noNetworkInStart() {
        llNoNetwork.visibility = View.VISIBLE
    }

    override fun networkFailed() {
        if (llProgress.isVisible) {
            tvText.text = getString(ru.trmedia.trbtlservice.comment.R.string.please_check_internet)
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
        tvText.text = getString(ru.trmedia.trbtlservice.comment.R.string.please_wait)
        progressAnimator?.start()
        wvInsta.reload()
    }

    private fun initUI() {
        if (prefs.getBoolean(SHOW_SAFE)) {

            val l = layoutInflater.inflate(
                ru.trmedia.trbtlservice.comment.R.layout.dialog_safetly,
                null
            )

            val chb =
                l.findViewById<CheckBox>(ru.trmedia.trbtlservice.comment.R.id.chbDontShowAgain)

            AlertDialog.Builder(this)
                .setView(l)
                .setCancelable(false)
                .setPositiveButton(
                    "ponyatno"
                ) { dialog, which ->
                    if (chb.isChecked) {
                        prefs.putBoolean(SHOW_SAFE, false)
                    }
                    initializeWebView()
                }
                .create().show()
        } else {
            initializeWebView()
        }
    }

    override fun onShowInfo(userWrap: UserWrap) {
        prefs.putString(AppPreferences.USER_NAME, userWrap.user.username)

        wvInsta.loadUrl(getString(ru.trmedia.trbtlservice.comment.R.string.redirect_base_url) + userWrap.user.username)
    }

    fun onTokenReceived(auth_token: String) {
        onShowLoading()

        prefs.putString(AppPreferences.TOKEN, auth_token)
        //instaLoginPresenter.getUserInfoByAccessToken(auth_token)
    }

    private fun onShowLoading() {
        llProgress.visibility = View.VISIBLE

        wvInsta.layoutParams = ConstraintLayout.LayoutParams(500, 800)

        pbHorizontal.visibility = View.VISIBLE

        progressAnimator = ObjectAnimator.ofInt(pbHorizontal, "progress", 0, 10000)
        progressAnimator?.addListener(onEnd = {
            pbHorizontal.visibility = View.GONE
            tvText.text = "Готово!"
        })
        progressAnimator?.duration = 15000
        progressAnimator?.interpolator = LinearInterpolator()
        progressAnimator?.start()
    }

    override fun startGame() {
        pbHorizontal.visibility = View.GONE
        btnStartGame.visibility = View.VISIBLE

        val anim = AnimationUtils.loadAnimation(this, ru.trmedia.trbtlservice.comment.R.anim.blink)
        btnStartGame.startAnimation(anim)

        tvText.text = "Готово!"
    }

    fun clickFollowers() {
        wvInsta.evaluateJavascript(
            "(function() { return ('<html>'+document.getElementsByTagName('A')[2].click()+'</html>'); })();",

            null
        )
    }

    fun parseToken(url: String) {
        val uri = Uri.parse(url)
        var access_token = uri.getEncodedFragment() ?: ""
        access_token = access_token.substring(access_token.lastIndexOf("=") + 1)
        onTokenReceived(access_token)
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
                        onTokenReceived("")
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
                        getString(ru.trmedia.trbtlservice.comment.R.string.please_check_internet),
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
                                            units[j + 1].substring(0, units[j + 1].length - 2)
                                        follows.add(follow)
                                    }
                                }
                            }
                            if (canParse && follows.size > 0) {
                                instaLoginPresenter.saveFollowsToDb(follows)
                            }
                        }
                    })
            }, 10000
        )
    }

    override fun onDestroy() {
        wvInsta.destroy()
        super.onDestroy()
    }
}
