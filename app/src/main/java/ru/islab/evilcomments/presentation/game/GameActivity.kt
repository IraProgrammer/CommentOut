package ru.islab.evilcomments.presentation.game

import android.content.*
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.SpannableString
import android.util.Log
import android.widget.Button
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.InterstitialAd
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.dialog_new_game.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.islab.evilcomments.App
import ru.islab.evilcomments.R
import ru.islab.evilcomments.data.AppPreferences
import ru.islab.evilcomments.di.module.GameModule
import ru.islab.evilcomments.domain.Punishment
import ru.islab.evilcomments.presentation.OneModel
import javax.inject.Inject
import javax.sql.DataSource


class GameActivity : MvpAppCompatActivity(), GameView {

    @InjectPresenter
    lateinit var gamePresenter: GamePresenter

    @Inject
    lateinit var prefs: AppPreferences

    private lateinit var mInterstitialAd: InterstitialAd

    var toast: Toast? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent?.addGameComponent(GameModule())?.inject(this)
        setContentView(R.layout.activity_game)

        MobileAds.initialize(this)

        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = "ca-app-pub-3446552315762824/4298236849"

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
            if (gamePresenter.getRound() == 2) {
                mInterstitialAd.loadAd(AdRequest.Builder().build())
            }
            if (gamePresenter.getRound() == 3 && mInterstitialAd.isLoaded) {
                mInterstitialAd.show()
            }
            gamePresenter.nextStep(if (btnComment.isSelected) GamePresenter.Action.COMMENT else GamePresenter.Action.PUNISHMENT)
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

    override fun showToast() {
        toast?.cancel()
        toast = Toast.makeText(
            this,
            "Нет-нет, не так быстро!\nСначала выполни одно из заданий",
            Toast.LENGTH_SHORT
        )
        toast?.show()
    }

    override fun showGameOverDialog() {
        val l = layoutInflater.inflate(
            R.layout.dialog_in_game_end,
            null
        )

        val tv = l.findViewById<TextView>(R.id.tvDoYouWantNewGame)
        val btnExit = l.findViewById<Button>(R.id.btnExit)
        val btnNewGame = l.findViewById<Button>(R.id.btnNewGame)

        val dialog = AlertDialog.Builder(this)
            .setView(l)
            .setCancelable(false)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()

        var pointsText = "баллов"

        if (tvPoints.text.toString().toInt() == 1) pointsText = "балл"
        else if (tvPoints.text.toString().toInt() in 2..4) pointsText = "балла"

        tv.text =
            "Вы набрали ${tvPoints.text} $pointsText за игру! Хотите начать новую игру или выйти?"

        btnNewGame.setOnClickListener { v ->
            dialog.dismiss()
            gamePresenter.startNewGame()
        }
        btnExit.setOnClickListener { v -> finish() }
    }

    override fun onSetPoints(points: Int) {
        tvPoints.text = points.toString()
    }

    private fun showNewGameDialog() {
        val l = layoutInflater.inflate(
            R.layout.dialog_new_game,
            null
        )

        val tv = l.findViewById<TextView>(R.id.tvDoYouWantNewGame)
        val btnYes = l.findViewById<Button>(R.id.btnYes)
        val btnNo = l.findViewById<Button>(R.id.btnNo)

        val dialog = AlertDialog.Builder(this)
            .setView(l)
            .create()

        dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog.show()

        tv.text = "Вы действительно хотите начать новую игру?"

        btnYes.setOnClickListener { v ->
            dialog.dismiss()
            gamePresenter.startNewGame()
        }
        btnNo.setOnClickListener { v -> dialog.dismiss() }
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
        tvToDo.text = Html.fromHtml(punishment)
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
        Glide.with(this)
            .asDrawable()
            .load(url)
            .placeholder(R.drawable.ic_user)
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(ivAvatar)
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
        val clip = ClipData.newPlainText("label", comment.substring(1, comment.length - 1))
        clipboard.setPrimaryClip(clip)
    }
}
