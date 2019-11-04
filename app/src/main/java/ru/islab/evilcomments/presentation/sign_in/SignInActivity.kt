package ru.islab.evilcomments.presentation.sign_in

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.vk.api.sdk.*
import com.vk.api.sdk.auth.VKAccessToken
import com.vk.api.sdk.auth.VKAuthCallback
import com.vk.api.sdk.auth.VKScope
import com.vk.api.sdk.exceptions.VKApiExecutionException
import kotlinx.android.synthetic.main.activity_choise.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.islab.evilcomments.App
import ru.islab.evilcomments.data.AppPreferences
import ru.islab.evilcomments.data.AppPreferences.Companion.VK_GAME
import ru.islab.evilcomments.di.module.SignInModule
import ru.islab.evilcomments.requests.VKFriendsRequest
import ru.islab.evilcomments.domain.VKUser
import ru.islab.evilcomments.presentation.game.GameActivity
import ru.islab.evilcomments.presentation.insta.InstaLoginActivity
import javax.inject.Inject

class SignInActivity : MvpAppCompatActivity(), SignInView {

    @Inject
    lateinit var prefs: AppPreferences

    @InjectPresenter
    lateinit var presenter: SignInPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(ru.islab.evilcomments.R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(ru.islab.evilcomments.R.layout.activity_choise)
        App.appComponent?.addSignInComponent(SignInModule())?.inject(this)

        VK.initialize(baseContext)

        btnVk.setOnClickListener { authorizeVK() }

        btnInsta.setOnClickListener { authorizeInsta() }
    }

    override fun authorizeInsta() {
        prefs.putBoolean(VK_GAME, false)
        val intent = Intent(baseContext, InstaLoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun authorizeVK() {
        if (VK.isLoggedIn()) {
            requestFriends()
        } else {
            VK.login(this, arrayListOf(VKScope.FRIENDS))
        }
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
        llProgress.visibility = View.VISIBLE
        VK.execute(VKFriendsRequest(), object : VKApiCallback<List<VKUser>> {
            override fun success(result: List<VKUser>) {
                if (!isFinishing && !result.isEmpty()) {
                    presenter.saveFollowsToDb(result)
                }
            }

            override fun fail(error: VKApiExecutionException) {
                llProgress.visibility = View.GONE
                Toast.makeText(baseContext, error.errorMsg, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun startGame() {
        prefs.putBoolean(AppPreferences.NEED_INSTA_NEW_GAME, true)
        prefs.putBoolean(AppPreferences.NEED_VK_NEW_GAME, true)
        prefs.putBoolean(VK_GAME, true)
        val intent = Intent(baseContext, GameActivity::class.java)
        startActivity(intent)
        finishAffinity()
    }
}