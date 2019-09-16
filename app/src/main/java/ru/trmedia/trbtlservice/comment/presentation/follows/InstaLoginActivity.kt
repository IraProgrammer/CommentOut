package ru.trmedia.trbtlservice.comment.presentation.follows

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_insta_login.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.trmedia.trbtlservice.comment.R
import ru.trmedia.trbtlservice.comment.domain.UserWrap
import ru.trmedia.trbtlservice.comment.data.network.AppPreferences
import ru.trmedia.trbtlservice.comment.domain.Follow
import ru.trmedia.trbtlservice.comment.presentation.game.GameActivity


class InstaLoginActivity : MvpAppCompatActivity(),
    InstaLoginView {

    @InjectPresenter
    lateinit var instaLoginPresenter: InstaLoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insta_login)

        initializeWebView()

        if (AppPreferences(baseContext).getString(AppPreferences.TOKEN) != null)
            onTokenReceived(
                AppPreferences(baseContext).getString(
                    AppPreferences.TOKEN
                )
            )
    }

    override fun onShowInfo(userWrap: UserWrap) {
        AppPreferences(baseContext)
            .putString(AppPreferences.USER_NAME, userWrap.user.username)
        Toast.makeText(baseContext, userWrap.user.username, Toast.LENGTH_LONG).show()

        flProgress.visibility = View.VISIBLE

        wvInsta.loadUrl(getString(R.string.redirect_base_url) + userWrap.user.username)
    }

    fun onTokenReceived(auth_token: String?) {
        if (auth_token == null)
            return
        AppPreferences(baseContext)
            .putString(AppPreferences.TOKEN, auth_token)
        instaLoginPresenter.getUserInfoByAccessToken(auth_token)
    }

    override fun startGame() {
        val intent = Intent(baseContext, GameActivity::class.java)
        startActivity(intent)
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initializeWebView() {
        wvInsta.settings.javaScriptEnabled = true
        wvInsta.settings.domStorageEnabled = true
        wvInsta.loadUrl(
            "https://api.instagram.com/" + "oauth/authorize/?client_id=" +
                    getString(R.string.client_id) +
                    "&redirect_uri=" + getString(R.string.redirect_base_url) +
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

//                        wvInsta.evaluateJavascript(
//                            "(function() { return ('<html>'+document.scrollToElement('d7ByH')+'</html>'); })();",
//                            null
//                        )

                        Handler().postDelayed({
                            wvInsta.evaluateJavascript(
                                "(function(){return window.document.body.outerHTML})();",
                                object : ValueCallback<String> {
                                    override fun onReceiveValue(html: String) {

                                        Log.d("HTML", html)

                                        val a = ArrayList<String>()

                                        val b = ArrayList<String>()

                                        val res = ArrayList<Follow>()

                                        val s = html.split("\"")

                                        for (i in s.indices) {
                                            if (s[i].equals(" title=\\")) {
                                                if (!s[i + 1].contains("Подтвержденный")) {
                                                    a.add(
                                                        s[i + 1].substring(0, s[i + 1].length - 1)
                                                    )
                                                }
                                            }
                                            if (s[i].equals(" type=\\")) {
                                                b.add(s[i + 2])
                                            }
                                        }

                                        for (i in b.indices) {
                                            if (b[i].contains("Подписки")) {
                                                res.add(Follow(0, a[i], ""))
                                            }
                                        }

                                        if (res.size > 0) instaLoginPresenter.saveFollowsToDb(res)

                                        flProgress.visibility = View.GONE

                                    }
                                })
                        }, 30000)

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
