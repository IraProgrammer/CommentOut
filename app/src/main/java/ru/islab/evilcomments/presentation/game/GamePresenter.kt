package ru.islab.evilcomments.presentation.game

import android.os.Handler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_game.*
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.islab.evilcomments.App
import ru.islab.evilcomments.data.database.AppDatabase
import ru.islab.evilcomments.data.AppPreferences
import ru.islab.evilcomments.data.AppPreferences.Companion.COMMENTS_SET
import ru.islab.evilcomments.data.AppPreferences.Companion.PUNISHMENT_SET
import ru.islab.evilcomments.data.AppPreferences.Companion.USERS_SET
import ru.islab.evilcomments.di.module.GameModule
import ru.islab.evilcomments.domain.EvilComment
import ru.islab.evilcomments.domain.Follow
import ru.islab.evilcomments.domain.Punishment
import ru.islab.evilcomments.presentation.OneModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashSet

@InjectViewState
class GamePresenter : MvpPresenter<GameView>() {

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var prefs: AppPreferences

    private var canEnable = false

    private var isGameOver = false

    private var delayMillis: Long = 5000

    private var points = 0

    private var round = 1

    private var maxRoundCount = 6

    private var oneModel = OneModel("", "", "", "")

    private var compositeDisposable = CompositeDisposable()

    private var hasCallbacks = false

    private val handler = Handler()

    private var runnable = Runnable { }

    enum class Action { COMMENT, PUNISHMENT }

    init {
        App.appComponent?.addGameComponent(GameModule())?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        enableButtonWithDelay()
    }

    private fun enableButtonWithDelay() {
        hasCallbacks = true
        runnable = Runnable {
            canEnable = true
            hasCallbacks = false
        }
        handler.postDelayed(runnable, delayMillis)
    }

    fun getRandomUser() {
        compositeDisposable.add(
            Single.zip(
                db.followDao().getAll(),
                db.commentDao().getAll(),
                db.punishmentDao().getAll(),
                Function3<List<Follow>, List<EvilComment>, List<Punishment>, OneModel> { follows, comments, punishments ->
                    createOneModel(follows, comments, punishments)
                }
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { oneModel -> this.oneModel = oneModel }
                .subscribe(
                    { oneModel ->
                        switchAction(Action.COMMENT)
                        viewState.onShowNextRound(oneModel)
                    },
                    { throwable ->
                    })
        )
    }

    fun createOneModel(
        follows: List<Follow>,
        comments: List<EvilComment>,
        punishments: List<Punishment>
    ): OneModel {
        val follow = follows[getRandom(USERS_SET, follows.size)]
        val comment = comments[getRandom(COMMENTS_SET, comments.size)]
        val punishment = punishments[getRandom(PUNISHMENT_SET, punishments.size)]

        return OneModel(follow.username, follow.profilePictureUrl, comment.text, punishment.text)
    }

    private fun getRandom(key: String, bound: Int): Int {
        var set = prefs.getSet(key)

        if (set.size == bound) {
            set = HashSet()
            prefs.clearSet(key)
        }

        val random = Random()

        var res: Int

        do {
            res = random.nextInt(bound)
        } while (set.contains(res.toString()))

        prefs.addToSet(key, res.toString())

        return res
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }

    fun restoreGame() {
        points = prefs.getString(AppPreferences.POINTS).toInt()
        round = prefs.getString(AppPreferences.ROUND).toInt()

        oneModel.comment = prefs.getString(AppPreferences.COMMENT)
        oneModel.punishment = prefs.getString(AppPreferences.PUNISHMENT)
        oneModel.username = prefs.getString(AppPreferences.USER_IN_CIRCLE)
        oneModel.profilePictureUrl = prefs.getString(AppPreferences.PHOTO)

        viewState.onSetRound(round)
        viewState.onSetPoints(points)

        viewState.onStateRestored(oneModel)
    }

    fun refreshComment() {
        viewState.onSetComment(oneModel.comment)
    }

    fun refreshPunishment() {
        viewState.onSetPunishment(oneModel.punishment)
    }

    fun switchAction(action: Action) {
        when {
            (action == Action.COMMENT) -> viewState.commentActive()
            (action == Action.PUNISHMENT) -> viewState.punishmentActive()
        }
    }

    fun saveGame() {
        prefs.putString(AppPreferences.COMMENT, oneModel.comment)
        prefs.putString(AppPreferences.PUNISHMENT, oneModel.punishment)
        prefs.putString(AppPreferences.USER_IN_CIRCLE, oneModel.username)
        prefs.putString(AppPreferences.PHOTO, oneModel.profilePictureUrl)
        prefs.putString(AppPreferences.ROUND, round.toString())
        prefs.putString(AppPreferences.POINTS, points.toString())
        prefs.putBoolean(AppPreferences.NEED_NEW_GAME, false)
    }

    fun setNeedNewGame() {
        prefs.putBoolean(AppPreferences.NEED_NEW_GAME, true)
    }

    fun updatePoints() {
        points++
        viewState.onSetPoints(points)
    }

    fun nextRound(action: Action) {
        if (action == Action.COMMENT) {
            updatePoints()
        }

        if (round == maxRoundCount) {
            isGameOver = true
            viewState.showGameOverDialog()
        } else {
            getRandomUser()
            round++
            viewState.onSetRound(round)
        }
    }

    fun startNewGame() {
        handler.removeCallbacks(runnable)
        canEnable = false
        enableButtonWithDelay()

        prefs.putBoolean(AppPreferences.NEED_NEW_GAME, true)

        isGameOver = false
        round = 1
        points = 0
        viewState.onSetPoints(points)
        viewState.onSetRound(round)

        getRandomUser()
        switchAction(Action.COMMENT)
    }

    fun saveStateIfNeed() {
        if (!isGameOver) {
            saveGame()
        } else {
            setNeedNewGame()
        }
    }

    fun nextStep(action: Action) {
        if (canEnable) {
            canEnable = false
            nextRound(action)
        } else {
            viewState.showToast()
        }
        if (!hasCallbacks) {
            enableButtonWithDelay()
        }
    }

    fun getRound() = round
}