package ru.trmedia.trbtlservice.comment.presentation.game

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_game.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.trmedia.trbtlservice.comment.R
import ru.trmedia.trbtlservice.comment.domain.Follow


class GameActivity : MvpAppCompatActivity(), GameView {

    @InjectPresenter
    lateinit var gamePresenter: GamePresenter

    var count = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        MobileAds.initialize(this)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        ivHelp.setOnClickListener { v ->
            AlertDialog.Builder(this)
                .setView(R.layout.dialog_rules)
                .setTitle("Правила игры")
                .setPositiveButton(
                    "ponyatno"
                ) { _, _ ->
                }
                .create().show()
        }

        btnRefresh.setOnClickListener { v ->

            if (cvComment.cardBackgroundColor.equals(
                    ColorStateList.valueOf(
                        resources.getColor(R.color.colorAccent)
                    )
                )
            ) count++

            tvPoints.text = count.toString()

            gamePresenter.getRandomUser(baseContext)
            cvPunishment.setCardBackgroundColor(
                ColorStateList.valueOf(
                    resources.getColor(R.color.white)
                )
            )
            cvComment.setCardBackgroundColor(
                ColorStateList.valueOf(
                    resources.getColor(R.color.white)
                )
            )
        }

        cvComment.setOnClickListener { v ->
            cvPunishment.setCardBackgroundColor(
                ColorStateList.valueOf(
                    resources.getColor(R.color.white)
                )
            )
            cvComment.setCardBackgroundColor(
                ColorStateList.valueOf(
                    resources.getColor(R.color.colorAccent)
                )
            )
        }

        cvPunishment.setOnClickListener { v ->
            cvComment.setCardBackgroundColor(
                ColorStateList.valueOf(
                    resources.getColor(R.color.white)
                )
            )
            cvPunishment.setCardBackgroundColor(
                ColorStateList.valueOf(
                    resources.getColor(R.color.colorAccent)
                )
            )
        }
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
        val list =
            packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
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

        tvComment.text = "comment"

        Glide.with(baseContext)
            .load(follow.profilePictureUrl)
            .placeholder(R.drawable.ic_user)
            .into(ivAvatar)

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", tvComment.text.toString())
        clipboard.setPrimaryClip(clip)

        Toast.makeText(baseContext, "Copy ok", Toast.LENGTH_SHORT).show()

        tvPunishment.text = "pun"
    }
}
