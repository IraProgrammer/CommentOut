package ru.trmedia.trbtlservice.comment.presentation.follows

import android.animation.AnimatorInflater
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
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.addListener
import androidx.core.view.isVisible
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_insta_login.*
import kotlinx.android.synthetic.main.activity_insta_login.tvText
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.trmedia.trbtlservice.comment.R
import ru.trmedia.trbtlservice.comment.data.network.AppPreferences
import ru.trmedia.trbtlservice.comment.data.network.AppPreferences.Companion.SHOW_SAFE
import ru.trmedia.trbtlservice.comment.domain.Follow
import ru.trmedia.trbtlservice.comment.domain.UserWrap
import ru.trmedia.trbtlservice.comment.presentation.game.GameActivity


class InstaLoginActivity : MvpAppCompatActivity(),
    InstaLoginView {

    @InjectPresenter
    lateinit var instaLoginPresenter: InstaLoginPresenter

    var progressAnimator: ObjectAnimator? = null

    var canParse = true

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insta_login)

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
            tvText.text = getString(R.string.please_check_internet)
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
        tvText.text = getString(R.string.please_wait)
        progressAnimator?.start()
        wvInsta.reload()
    }

    private fun initUI() {
        if (AppPreferences(baseContext).getBoolean(SHOW_SAFE)) {

            val l = layoutInflater.inflate(R.layout.dialog_safetly, null)

            val chb = l.findViewById<CheckBox>(R.id.chbDontShowAgain)

            AlertDialog.Builder(this)
                .setView(l)
                .setCancelable(false)
                .setPositiveButton(
                    "ponyatno"
                ) { dialog, which ->
                    if (chb.isChecked) {
                        AppPreferences(baseContext).putBoolean(SHOW_SAFE, false)
                    }
                    initializeWebView()
                }
                .create().show()
        } else {
            initializeWebView()
        }
    }

    override fun onShowInfo(userWrap: UserWrap) {
        AppPreferences(baseContext).putString(AppPreferences.USER_NAME, userWrap.user.username)

        wvInsta.loadUrl(getString(ru.trmedia.trbtlservice.comment.R.string.redirect_base_url) + userWrap.user.username)
    }

    fun onTokenReceived(auth_token: String) {
        onShowLoading()

        AppPreferences(baseContext)
            .putString(AppPreferences.TOKEN, auth_token)
        instaLoginPresenter.getUserInfoByAccessToken(auth_token)
    }

    private fun onShowLoading() {
        llProgress.visibility = View.VISIBLE
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

        val anim = AnimationUtils.loadAnimation(this, R.anim.blink)
        btnStartGame.startAnimation(anim)

        tvText.text = "Готово!"
    }

    fun clickFollowers() {
        wvInsta.evaluateJavascript(
            "(function() { return ('<html>'+document.getElementsByTagName('A')[1].click()+'</html>'); })();",
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
            "https://api.instagram.com/" + "oauth/authorize/?client_id=" +
                    getString(ru.trmedia.trbtlservice.comment.R.string.client_id) +
                    "&redirect_uri=" + getString(ru.trmedia.trbtlservice.comment.R.string.redirect_base_url) +
                    "&response_type=token&display=touch&scope=basic"
        )

        wvInsta.webViewClient = object : WebViewClient() {

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                when {
                    url.equals(
                        "https://www.instagram.com/" + AppPreferences(baseContext)
                            .getString(AppPreferences.USER_NAME) + "/followers/"
                    ) -> {
                        parseFollowsWithDelay()
                    }
                    url.equals(
                        "https://www.instagram.com/" + AppPreferences(baseContext)
                            .getString(AppPreferences.USER_NAME) + "/"
                    ) -> {
                        clickFollowers()
                    }
                    url.contains("access_token=") -> {
                        parseToken(url)
                    }
                    url.contains("?error") -> Toast.makeText(
                        baseContext,
                        getString(R.string.please_check_internet),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun parseFollowsWithDelay() {
        Handler().postDelayed({
            wvInsta.evaluateJavascript(
                "(function(){return window.document.body.outerHTML})();",
                object : ValueCallback<String> {
                    override fun onReceiveValue(html: String) {

                        Log.d("HTML", html)

                        val a = ArrayList<String>()

                        val b = ArrayList<String>()

                        val p = ArrayList<String>()

                        val res = ArrayList<Follow>()

                        val s = html.split("\"")

                        for (i in s.indices) {
                            if (s[i].equals(" title=\\")) {
                                if (!s[i + 1].contains("Подтвержденный") && !s[i + 1].contains(
                                        "Verified"
                                    )
                                ) {
                                    a.add(
                                        s[i + 1].substring(0, s[i + 1].length - 1)
                                    )
                                }
                            }
                            if (s[i].equals(" type=\\")) {
                                b.add(s[i + 2])
                            }
                            if (s[i].equals(" src=\\")) {
                                p.add(s[i + 1].substring(0, s[i + 1].length - 1))
                            }
                        }

                        for (i in b.indices) {
                            if (b[i].contains("Подписки") || b[i].contains("Following")) {
                                res.add(Follow(0, a[i], p[i]))
                            }
                        }

                        if (canParse && res.size > 0) instaLoginPresenter.saveFollowsToDb(res)

                    }
                })
        }, 10000)
    }
}
