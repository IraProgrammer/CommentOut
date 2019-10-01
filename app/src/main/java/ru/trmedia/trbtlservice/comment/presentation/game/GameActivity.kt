package ru.trmedia.trbtlservice.comment.presentation.game

import android.content.*
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import kotlinx.android.synthetic.main.activity_game.*
import moxy.MvpAppCompatActivity
import moxy.presenter.InjectPresenter
import ru.trmedia.trbtlservice.comment.App
import ru.trmedia.trbtlservice.comment.data.AppPreferences
import ru.trmedia.trbtlservice.comment.di.module.GameModule
import ru.trmedia.trbtlservice.comment.presentation.OneModel
import javax.inject.Inject
import android.widget.SeekBar
import android.R.attr.bottom
import android.R.attr.top
import android.R.attr.left
import android.R.attr.right
import android.opengl.ETC1.getHeight
import androidx.constraintlayout.solver.widgets.WidgetContainer.getBounds
import android.view.View.OnTouchListener
import android.R.attr.bottom
import android.R.attr.top
import android.R.attr.left
import android.R.attr.right
import android.opengl.ETC1.getHeight
import androidx.constraintlayout.solver.widgets.WidgetContainer.getBounds


class GameActivity : MvpAppCompatActivity(), GameView {

    @InjectPresenter
    lateinit var gamePresenter: GamePresenter

    @Inject
    lateinit var prefs: AppPreferences

    private var maxRoundCount = 6

    private var count = 0
    private var round = 1

    private var oneModel = OneModel("", "", "", "")

    private var isGameOver = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        App.appComponent?.addGameComponent(GameModule())?.inject(this)
        setContentView(ru.trmedia.trbtlservice.comment.R.layout.activity_game)

        MobileAds.initialize(this)

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)

        if (prefs.getBoolean(AppPreferences.SHOW_RULES)) {
            showRulesDialog()
            prefs.putBoolean(AppPreferences.SHOW_RULES, false)
        }

        if (prefs.getBoolean(AppPreferences.NEED_NEW_GAME)) {

            gamePresenter.getRandomUser()
        } else {
            tvToDo.text = prefs.getString(AppPreferences.COMMENT)
            tvUsername.text = prefs.getString(AppPreferences.USER_IN_CIRCLE)

            Glide.with(baseContext)
                .load(prefs.getString(AppPreferences.PHOTO))
                .apply(RequestOptions.circleCropTransform())
                .placeholder(ru.trmedia.trbtlservice.comment.R.drawable.ic_user)
                .into(ivAvatar)

            oneModel.punishment = prefs.getString(AppPreferences.PUNISHMENT) ?: ""

            tvRaund.text = prefs.getString(AppPreferences.RAUND)

            tvPoints.text = prefs.getString(AppPreferences.POINTS)

            ivAvatar.setOnClickListener { v ->
                openUserInInsta(
                    prefs.getString(AppPreferences.USER_IN_CIRCLE) ?: ""
                )
            }

            tvUsername.setOnClickListener { v ->
                openUserInInsta(
                    prefs.getString(AppPreferences.USER_IN_CIRCLE) ?: ""
                )
            }
        }

        btnComment.isSelected = true
        btnPunishment.isSelected = false

        ivHelp.setOnClickListener { v ->
            showRulesDialog()
        }

        ivNewGame.setOnClickListener { v ->
            AlertDialog.Builder(this)
                .setMessage("Вы действительнохотите начать новую игру?")
                .setPositiveButton("Да") { _, _ -> startNewGame() }
                .setNegativeButton("Нет") { _, _ -> }
                .create().show()
        }

//        seekBar2.setOnTouchListener(OnTouchListener { v, event ->
//            if ((event.action == MotionEvent.ACTION_MOVE || event.action == MotionEvent.ACTION_HOVER_MOVE) && event.action != MotionEvent.ACTION_BUTTON_PRESS) {
////                val seekBarThumbRect = seekBar2.thumb.bounds
////                val seekBarHeight = seekBar2.width
////                if (seekBarThumbRect.bottom - (seekBarThumbRect.top - seekBarThumbRect.bottom) / 2 < Math.abs(
////                        seekBarHeight - event.x
////                    ) &&
////                    seekBarThumbRect.top + (seekBarThumbRect.top - seekBarThumbRect.bottom) / 2 > Math.abs(
////                        seekBarHeight - event.x
////                    ) &&
////                    seekBarThumbRect.right < event.y &&
////                    seekBarThumbRect.left > event.y
////                )
//                return@OnTouchListener false
//            }
//            true
//        })

        seekBar2.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {

            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                var a = 5
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                if (seekBar.progress != 0) {
                    var k = 6
                } else {
                    var r = seekBar.progress
                    var t = 8
                }
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                if (seekBar.progress == 100) {
                    if (btnComment.isSelected) {
                        count++

                        tvPoints.text = count.toString()
                    }

                    if (round == maxRoundCount) {
                        isGameOver = true
                        AlertDialog.Builder(seekBar2.context)
                            .setMessage("Вы набрали $count баллов за игру! Хотите начать новую игру или выйти?")
                            .setCancelable(false)
                            .setNegativeButton("Новая игра") { _, _ -> startNewGame() }
                            .setPositiveButton("Выход") { _, _ -> finish() }
                            .create().show()
                    } else {
                        gamePresenter.getRandomUser()
                        btnComment.isSelected = true
                        btnPunishment.isSelected = false
                        round++
                        tvRaund.text = "Раунд $round"
                    }
                }

                seekBar2.progress = 0
            }
        })

//        btnRefresh.setOnClickListener { v ->
//
//            if (btnComment.isSelected) {
//                count++
//
//                tvPoints.text = count.toString()
//            }
//
//            if (round == maxRoundCount) {
//                isGameOver = true
//                AlertDialog.Builder(this)
//                    .setMessage("Вы набрали $count баллов за игру! Хотите начать новую игру или выйти?")
//                    .setCancelable(false)
//                    .setNegativeButton("Новая игра") { _, _ -> startNewGame() }
//                    .setPositiveButton("Выход") { _, _ -> finish() }
//                    .create().show()
//            } else {
//                gamePresenter.getRandomUser()
//                btnComment.isSelected = true
//                btnPunishment.isSelected = false
//                round++
////                val toast = Toast.makeText(this, "Раунд $round", Toast.LENGTH_SHORT)
////                toast.setGravity(Gravity.TOP, 0, 0)
////                toast.show()
//                tvRaund.text = "Раунд $round"
//            }
//        }

        btnComment.setOnClickListener { v ->
            tvToDo.text = oneModel.comment
            btnComment.isSelected = true
            btnPunishment.isSelected = false
        }

        btnPunishment.setOnClickListener { v ->
            tvToDo.text = oneModel.punishment
            btnPunishment.isSelected = true
            btnComment.isSelected = false
        }
    }

    private fun startNewGame() {
        prefs.putBoolean(AppPreferences.NEED_NEW_GAME, true)
        //this.recreate()

        isGameOver = false
        round = 1
        count = 0
        tvPoints.text = "0"
        gamePresenter.getRandomUser()
        tvRaund.text = "Раунд 1"

        btnComment.isSelected = true
        btnPunishment.isSelected = false
    }

    private fun showRulesDialog() {
        val l = layoutInflater.inflate(
            ru.trmedia.trbtlservice.comment.R.layout.dialog_rules,
            null
        )

        val btn = l.findViewById<Button>(ru.trmedia.trbtlservice.comment.R.id.btnAgree)

        val dialog = AlertDialog.Builder(this)
            .setView(l, 0, 48, 0, 48)
            .create()

        dialog.window?.setBackgroundDrawableResource(ru.trmedia.trbtlservice.comment.R.color.transparent)
        dialog.show()

        btn.setOnClickListener { v -> dialog.dismiss() }
    }

    override fun onPause() {
        if (!isGameOver) {
            prefs.putString(AppPreferences.COMMENT, oneModel.comment)
            prefs.putString(AppPreferences.PUNISHMENT, oneModel.punishment)
            prefs.putString(AppPreferences.USER_IN_CIRCLE, tvUsername.text.toString())
            prefs.putString(AppPreferences.PHOTO, oneModel.profilePictureUrl)
            prefs.putString(AppPreferences.RAUND, tvRaund.text.toString())
            prefs.putString(AppPreferences.POINTS, tvPoints.text.toString())
            prefs.putBoolean(AppPreferences.NEED_NEW_GAME, false)
        } else {
            prefs.putBoolean(AppPreferences.NEED_NEW_GAME, true)
        }
        super.onPause()
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

    override fun onShowNextRaund(oneModel: OneModel) {
        this.oneModel = oneModel

        tvUsername.setText(oneModel.username)

        ivAvatar.setOnClickListener { v -> openUserInInsta(oneModel.username) }

        tvUsername.setOnClickListener { v -> openUserInInsta(oneModel.username) }

        tvToDo.text = "comment"

        Glide.with(baseContext)
            .load(oneModel.profilePictureUrl)
            .apply(RequestOptions.circleCropTransform())
            .transition(DrawableTransitionOptions.withCrossFade())
            .into(ivAvatar)

        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", tvToDo.text.toString())
        clipboard.setPrimaryClip(clip)

        tvToDo.text = oneModel.comment
    }
}
