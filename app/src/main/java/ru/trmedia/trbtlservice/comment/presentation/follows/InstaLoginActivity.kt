package ru.trmedia.trbtlservice.comment.presentation.follows

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.LinearInterpolator
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.animation.addListener
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_insta_login.*
import kotlinx.android.synthetic.main.activity_insta_login.tvText
import kotlinx.android.synthetic.main.dialog_safetly.*
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insta_login)

        val l = layoutInflater.inflate(R.layout.dialog_safetly, null)

        val chb = l.findViewById<CheckBox>(R.id.chbDontShowAgain)

        if (AppPreferences(baseContext).getBoolean(SHOW_SAFE)) {

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
                    if (AppPreferences(baseContext).getString(AppPreferences.TOKEN) != null)
                        onTokenReceived(
                            AppPreferences(baseContext).getString(
                                AppPreferences.TOKEN
                            )
                        )
                }
                .create().show()
        }

        btnStartGame.setOnClickListener { v ->
            val intent = Intent(baseContext, GameActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onShowInfo(userWrap: UserWrap) {
        AppPreferences(baseContext)
            .putString(AppPreferences.USER_NAME, userWrap.user.username)
        Toast.makeText(baseContext, userWrap.user.username, Toast.LENGTH_LONG).show()

        llProgress.visibility = View.VISIBLE

        val progressAnimator = ObjectAnimator.ofInt(pbHorizontal, "progress", 0, 10000)
        progressAnimator.addListener(onEnd = {
            pbHorizontal.visibility = View.GONE
            tvText.text = "Готово!"
        })
        progressAnimator.duration = 12000
        progressAnimator.interpolator = LinearInterpolator()
        progressAnimator.start()

        wvInsta.loadUrl(getString(ru.trmedia.trbtlservice.comment.R.string.redirect_base_url) + userWrap.user.username)
    }

    fun onTokenReceived(auth_token: String?) {
        if (auth_token == null)
            return
        AppPreferences(baseContext)
            .putString(AppPreferences.TOKEN, auth_token)
        instaLoginPresenter.getUserInfoByAccessToken(auth_token)
    }

    override fun startGame() {
        btnStartGame.visibility = View.VISIBLE
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
                        "https://www.instagram.com/" + AppPreferences(
                            baseContext
                        ).getString(
                            AppPreferences.USER_NAME
                        ) + "/followers/"
                    ) -> {

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

                                        if (res.size > 0) instaLoginPresenter.saveFollowsToDb(res)

                                    }
                                })
                        }, 10000)

                    }
                    url.equals(
                        "https://www.instagram.com/" + AppPreferences(
                            baseContext
                        ).getString(
                            AppPreferences.USER_NAME
                        ) + "/"
                    ) -> {
                        Toast.makeText(
                            baseContext,
                            "kryak",
                            Toast.LENGTH_LONG
                        ).show()


                        wvInsta.evaluateJavascript(
                            "(function() { return ('<html>'+document.getElementsByTagName('A')[1].click()+'</html>'); })();",
                            null
                        )


                    }
                    url.contains("access_token=") -> {
                        val uri = Uri.parse(url)
                        var access_token = uri.getEncodedFragment()
                        access_token =
                            access_token?.substring(access_token.lastIndexOf("=") + 1)
                        onTokenReceived(access_token)

                        //dismiss();
                    }
                    url.contains("?error") -> Toast.makeText(
                        baseContext,
                        "Error :( Try Again",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
