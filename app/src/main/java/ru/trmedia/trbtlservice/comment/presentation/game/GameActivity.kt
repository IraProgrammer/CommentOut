package ru.trmedia.trbtlservice.comment.presentation.game

import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_game.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.trmedia.trbtlservice.comment.App
import ru.trmedia.trbtlservice.comment.R
import ru.trmedia.trbtlservice.comment.data.AppPreferences
import ru.trmedia.trbtlservice.comment.di.module.GameModule
import ru.trmedia.trbtlservice.comment.presentation.OneModel
import javax.inject.Inject


class GameActivity : MvpAppCompatActivity(), GameView {

    @InjectPresenter
    lateinit var gamePresenter: GamePresenter

    @Inject
    lateinit var prefs: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent?.addGameComponent(GameModule())?.inject(this)
        setContentView(R.layout.activity_game)

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

        ivNewGame.setOnClickListener { v ->
            showNewGameDialog()
        }

        if (prefs.getBoolean(AppPreferences.NEED_NEW_GAME)) {
            gamePresenter.getRandomUser()
        } else {
            gamePresenter.restoreGame()
        }

        gamePresenter.switchAction(GamePresenter.Action.COMMENT)

        btnRefresh.setOnClickListener { v ->
            gamePresenter.nextRound(if (btnComment.isSelected) GamePresenter.Action.COMMENT else GamePresenter.Action.PUNISHMENT)
        }

        btnComment.setOnClickListener { v ->
            gamePresenter.refreshComment()
            gamePresenter.switchAction(GamePresenter.Action.COMMENT)
        }

        btnPunishment.setOnClickListener { v ->
            gamePresenter.refreshPunishment()
            gamePresenter.switchAction(GamePresenter.Action.PUNISHMENT)
        }
    }

    override fun showGameOverDialog() {
        AlertDialog.Builder(this)
            .setMessage("Вы набрали ${tvPoints.text} баллов за игру! Хотите начать новую игру или выйти?")
            .setCancelable(false)
            .setNegativeButton("Новая игра") { _, _ -> gamePresenter.startNewGame() }
            .setPositiveButton("Выход") { _, _ -> finish() }
            .create().show()
    }

    override fun onSetPoints(points: Int) {
        tvPoints.text = points.toString()
    }

    private fun showNewGameDialog() {
        AlertDialog.Builder(this)
            .setMessage("Вы действительнохотите начать новую игру?")
            .setPositiveButton("Да") { _, _ -> gamePresenter.startNewGame() }
            .setNegativeButton("Нет") { _, _ -> }
            .create().show()
    }

    override fun commentActive() {
        btnComment.isSelected = true
        btnPunishment.isSelected = false
    }

    override fun punishmentActive() {
        btnPunishment.isSelected = true
        btnComment.isSelected = false
    }

    override fun onSetComment(comment: String) {
        tvToDo.text = comment
    }

    override fun onSetPunishment(punishment: String) {
        tvToDo.text = punishment
    }

    override fun onSetRound(round: Int) {
        tvRound.text = "Раунд ${round}"
    }

    override fun onStateRestored(oneModel: OneModel) {
        tvToDo.text = oneModel.comment
        tvUsername.text = oneModel.username

        loadPicture(oneModel.profilePictureUrl)

        ivAvatar.setOnClickListener { v ->
            openUserInInsta(oneModel.username)
        }

        tvUsername.setOnClickListener { v ->
            openUserInInsta(oneModel.username)
        }
    }

    private fun loadPicture(url: String) {
        Glide.with(baseContext)
            .load(url)
            .apply(RequestOptions.circleCropTransform())
            .placeholder(R.drawable.ic_user)
            .into(ivAvatar)
    }

    private fun showRulesDialog() {
        val l = layoutInflater.inflate(
            R.layout.dialog_rules,
            null
        )

        val btn = l.findViewById<Button>(R.id.btnAgree)

        val dialog = AlertDialog.Builder(this)
            .setView(l, 0, 48, 0, 48)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()

        btn.setOnClickListener { v -> dialog.dismiss() }
    }

    override fun onPause() {
        gamePresenter.saveStateIfNeed()
        super.onPause()
    }

    private fun openUserInInsta(username: String) {
        val uri = Uri.parse("http://instagram.com/$username")
        val insta = Intent(Intent.ACTION_VIEW, uri)
        insta.setPackage("com.instagram.android")

        if (hasInsta(baseContext, insta)) {
            startActivity(insta)
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://instagram.com/$username")))
        }
    }

    private fun hasInsta(ctx: Context, intent: Intent): Boolean {
        val packageManager = ctx.packageManager
        val list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        return list.size > 0
    }

    override fun onShowNextRound(oneModel: OneModel) {
        tvUsername.text = oneModel.username
        loadPicture(oneModel.profilePictureUrl)
        tvToDo.text = oneModel.comment

        ivAvatar.setOnClickListener { v -> openUserInInsta(oneModel.username) }

        tvUsername.setOnClickListener { v -> openUserInInsta(oneModel.username) }

        copyCommentToBuffer(oneModel.comment)
    }

    private fun copyCommentToBuffer(comment: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", comment)
        clipboard.setPrimaryClip(clip)
    }
}
