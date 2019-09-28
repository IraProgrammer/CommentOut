package ru.trmedia.trbtlservice.comment.presentation.game

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.ContextCompat.startActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.activity_game.adView
import kotlinx.android.synthetic.main.activity_game.btnRefresh
import kotlinx.android.synthetic.main.activity_game.ivAvatar
import kotlinx.android.synthetic.main.activity_game.ivHelp
import kotlinx.android.synthetic.main.activity_game.tvPoints
import kotlinx.android.synthetic.main.activity_game.tvUsername
import kotlinx.android.synthetic.main.dialog_rules.*
import kotlinx.android.synthetic.main.test.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.trmedia.trbtlservice.comment.App
import ru.trmedia.trbtlservice.comment.R
import ru.trmedia.trbtlservice.comment.data.network.AppPreferences
import ru.trmedia.trbtlservice.comment.di.module.GameModule
import ru.trmedia.trbtlservice.comment.di.module.InstaLoginModule
import ru.trmedia.trbtlservice.comment.domain.Follow
import javax.inject.Inject


class GameActivity : MvpAppCompatActivity(), GameView {

    @InjectPresenter
    lateinit var gamePresenter: GamePresenter

    @Inject
    lateinit var prefs: AppPreferences

    var count = 0
    var raund = 1
    var isComment = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent?.addGameComponent(GameModule())?.inject(this)
        setContentView(R.layout.test)

        MobileAds.initialize(this)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        if (prefs.getBoolean(AppPreferences.SHOW_RULES)) {
            showRulesDialog()
            prefs.putBoolean(AppPreferences.SHOW_RULES, false)
        }

        ivHelp.setOnClickListener { v ->
            showRulesDialog()
        }

        btnRefresh.setOnClickListener { v ->

            if (raund == 6) {
            } else {

                raund++
                tvRaund.text = "Раунд $raund"
            }

            if (isComment) {
                count++

                tvPoints.text = count.toString()

                gamePresenter.getRandomUser(baseContext)
            }
        }

        btnComment.setOnClickListener { v ->
            btnComment.isSelected = true
            btnPunishment.isSelected = false
        }

        btnPunishment.setOnClickListener { v ->
            btnPunishment.isSelected = true
            btnComment.isSelected = false
        }
    }

    private fun showRulesDialog() {
        val l = layoutInflater.inflate(
            R.layout.dialog_rules,
            null
        )

        val btn = l.findViewById<Button>(R.id.btnAgree)

        val dialog = AlertDialog.Builder(this)
            .setView(l)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()

        btn.setOnClickListener { v -> dialog.dismiss() }
    }

    fun openUserInInsta(username: String) {
        val uri = Uri.parse("http://instagram.com/$username")
        val insta = Intent(Intent.ACTION_VIEW, uri)
        insta.setPackage("com.instagram.android")

        if (isIntentAvailable(baseContext, insta)) {
            startActivity(insta)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/$username")))
        }
    }

    fun isIntentAvailable(ctx: Context, intent: Intent): Boolean {
        val packageManager = ctx.packageManager
        val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.size > 0
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setMessage("Сохранить игру?")
            .setTitle("Ня! :3")
            .setPositiveButton(
                "da"
            ) { dialog, which ->
                Toast.makeText(baseContext, "ok", Toast.LENGTH_SHORT).show()
                super.onBackPressed()
                dialog.dismiss()
            }
            .setNegativeButton(
                "net"
            ) { dialog, which ->
                Toast.makeText(baseContext, "net", Toast.LENGTH_SHORT).show()
                super.onBackPressed()
                dialog.dismiss()
            }
            .create().show()
    }

    override fun onShowRandomUser(follow: Follow) {

        tvUsername.setText(follow.username)

        tvUsername.setOnClickListener { v -> openUserInInsta(follow.username) }

        tvToDo.text = "comment"

        Glide.with(baseContext)
            .load(follow.profilePictureUrl)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_user)
            .into(ivAvatar)

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", tvToDo.text.toString())
        clipboard.setPrimaryClip(clip)

        Toast.makeText(baseContext, "Copy ok", Toast.LENGTH_SHORT).show()

        //tvPunishment.text = "pun"
    }
}
