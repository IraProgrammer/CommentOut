package ru.trmedia.trbtlservice.comment.presentation.game

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_game.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.trmedia.trbtlservice.comment.R
import ru.trmedia.trbtlservice.comment.domain.Follow


class GameActivity : MvpAppCompatActivity(), GameView {

    @InjectPresenter
    lateinit var gamePresenter: GamePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        btnRefresh.setOnClickListener{v -> gamePresenter.getRandomUser()}
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

    override fun onShowRandomUser(follow: Follow) {
        tvUsername.setText(follow.username)

        tvUsername.setOnClickListener { v -> openUserInInsta(follow.username) }

        tvComment.text = "comment"

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", tvComment.text.toString())
        clipboard.setPrimaryClip(clip)

        Toast.makeText(baseContext, "Copy ok", Toast.LENGTH_SHORT).show()

        tvPunishment.text = "pun"
    }
}
