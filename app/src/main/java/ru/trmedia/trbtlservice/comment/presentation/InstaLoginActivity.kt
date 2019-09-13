package ru.trmedia.trbtlservice.comment.presentation

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.webkit.ValueCallback
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_insta_login.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.trmedia.trbtlservice.comment.R
import ru.trmedia.trbtlservice.comment.UserWrap
import ru.trmedia.trbtlservice.comment.data.AppPreferences


class InstaLoginActivity : MvpAppCompatActivity(), InstaLoginView {

    @InjectPresenter
    lateinit var instaLoginPresenter: InstaLoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insta_login)

        initializeWebView()

        if (AppPreferences(baseContext).getString(AppPreferences.TOKEN) != null)
            onTokenReceived(AppPreferences(baseContext).getString(AppPreferences.TOKEN))
    }

    override fun onShowInfo(userWrap: UserWrap) {
        AppPreferences(baseContext).putString(AppPreferences.USER_NAME, userWrap.user.username)
        Toast.makeText(baseContext, userWrap.user.username, Toast.LENGTH_LONG).show()

        wvInsta.loadUrl(getString(R.string.redirect_base_url) + userWrap.user.username)
    }

    fun onTokenReceived(auth_token: String?) {
        if (auth_token == null)
            return
        AppPreferences(baseContext).putString(AppPreferences.TOKEN, auth_token)
        instaLoginPresenter.getUserInfoByAccessToken(auth_token)
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

            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//                if (url.startsWith(getString(R.string.redirect_base_url))) {
//                    wvInsta.loadUrl("https://www.instagram.com/irishkalaskova")
//                    return true
//                }
                return false
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                when {
                    url.contains(
                        AppPreferences(baseContext).getString(AppPreferences.USER_NAME) ?: "qqqqqqqqqqqqq"
                    ) -> {
                        Toast.makeText(
                            baseContext,
                            "kryak",
                            Toast.LENGTH_LONG
                        ).show()



//                        wvInsta.evaluateJavascript(
//                            //"(function(){return window.document.body.outerHTML})();",
//                            "(function() { return ('<html>'+document.getElementsByTagName('A')[2].click()+'</html>'); })();",
//                            object: ValueCallback<String>() {
//                                @Override
//                                fun onReceiveValue(html: String) {
//                                    Log.d("HTML", html);
//
//                                    //   title=\"vremya_remonta23\"
//
//                                    val a = ArrayList<String>()
//
//                                    val s = html.split("\"");
//
//                                    val k = s.size
//
//                                    for (val i = 0; i < s.length; i++) {
//                                    if (s[i].equals(" title=\\"))
//                                        a.add(s[i + 1]);
//                                    else {
//                                        int c = 8;
//                                    }
//                                }
//
//                                    int a5 = 5;
//                                }
//                            });
//                    }
//                });




                    }
                    url.contains("access_token=") -> {
                        val uri = Uri.parse(url)
                        var access_token = uri.getEncodedFragment()
                        access_token = access_token?.substring(access_token.lastIndexOf("=") + 1)
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
