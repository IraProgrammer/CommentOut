package ru.islab.evilcomments.presentation.game

import android.os.Handler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.islab.evilcomments.App
import ru.islab.evilcomments.data.database.AppDatabase
import ru.islab.evilcomments.data.AppPreferences
import ru.islab.evilcomments.data.AppPreferences.Companion.COMMENTS_SET
import ru.islab.evilcomments.data.AppPreferences.Companion.INSTA_USERS_SET
import ru.islab.evilcomments.data.AppPreferences.Companion.PUNISHMENT_SET
import ru.islab.evilcomments.data.AppPreferences.Companion.VK_GAME
import ru.islab.evilcomments.data.AppPreferences.Companion.VK_USERS_SET
import ru.islab.evilcomments.di.module.GameModule
import ru.islab.evilcomments.domain.*
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

    private var delayMillis: Long = 5000

    private var points = 0

    private var round = 1

    private var maxRoundCount = 6

    private var oneModel = OneModel("", "", "", "", "")

    private var compositeDisposable = CompositeDisposable()

    private var hasCallbacks = false

    private val handler = Handler()

    private var runnable = Runnable { }

    enum class Action { COMMENT, PUNISHMENT }

    init {
        App.appComponent?.addGameComponent(GameModule())?.inject(this)
    }

    private fun enableButtonWithDelay() {
        hasCallbacks = true
        runnable = Runnable {
            canEnable = true
            hasCallbacks = false
        }
        handler.postDelayed(runnable, delayMillis)
    }

    private fun getRandomUser() {
        val function: Single<OneModel>

        if (prefs.getBoolean(VK_GAME)) {
            function = Single.zip(
                db.vkUserDao().getAll(),
                db.vkCommentDao().getAll(),
                db.vkPunishmentDao().getAll(),
                Function3<List<VKUser>, List<VKEvilComment>, List<VKPunishment>, OneModel> { VKuser, comments, punishments ->
                    createOneModelFromVK(VKuser, comments, punishments)
                }
            )
        } else {
            function = Single.zip(
                db.instaUserDao().getAll(),
                db.commentDao().getAll(),
                db.punishmentDao().getAll(),
                Function3<List<InstaUser>, List<EvilComment>, List<Punishment>, OneModel> { instaUserDao, comments, punishments ->
                    createOneModelFromInsta(instaUserDao, comments, punishments)
                }
            )
        }

        compositeDisposable.add(
            function
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSuccess { oneModel -> this.oneModel = oneModel }
                .subscribe(
                    { oneModel ->
                        switchAction(Action.COMMENT)
                        saveGame()
                        viewState.onShowNextRound(oneModel)
                    },
                    { throwable ->
                        var c = 8
                    })
        )
    }

    private fun createOneModelFromInsta(
        instaUsers: List<InstaUser>,
        comments: List<EvilComment>,
        punishments: List<Punishment>
    ): OneModel {
        val instaUser = getRandomUniqueName(instaUsers)
        val comment = comments[getRandomUniqueInt(COMMENTS_SET, comments.size)]
        val punishment = punishments[getRandomUniqueInt(PUNISHMENT_SET, punishments.size)]

        return OneModel(
            instaUser.username,
            instaUser.profilePictureUrl,
            comment.text,
            punishment.text,
            instaUser.username
        )
    }

    private fun createOneModelFromVK(
        VKUsers: List<VKUser>,
        comments: List<VKEvilComment>,
        punishments: List<VKPunishment>
    ): OneModel {
        val vkUser = getRandomUniqueName(VKUsers)
        val comment = comments[getRandomUniqueInt(COMMENTS_SET, comments.size)]
        val punishment = punishments[getRandomUniqueInt(PUNISHMENT_SET, punishments.size)]

        return OneModel(
            vkUser.name,
            vkUser.photo,
            comment.text,
            punishment.text,
            vkUser.id.toString()
        )
    }

    private fun getRandomUniqueInt(key: String, bound: Int): Int {
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

    private fun getRandomUniqueName(users: List<VKUser>): VKUser {
        var set = prefs.getSet(VK_USERS_SET)

        if (set.size == users.size) {
            set = HashSet()
            prefs.clearSet(VK_USERS_SET)
        }

        var user = users[0]

        for (us in users) {
            if (!set.contains(us.name)) {
                user = us
                break
            }
        }

        prefs.addToSet(VK_USERS_SET, user.name)

        return user
    }

    private fun getRandomUniqueName(users: List<InstaUser>): InstaUser {
        var set = prefs.getSet(INSTA_USERS_SET)

        if (set.size == users.size) {
            set = HashSet()
            prefs.clearSet(INSTA_USERS_SET)
        }

        var user = users[0]

        for (us in users) {
            if (!set.contains(us.username)) {
                user = us
                break
            }
        }

        prefs.addToSet(INSTA_USERS_SET, user.username)

        return user
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
            if (prefs.getBoolean(VK_GAME)) {
                prefs.putBoolean(AppPreferences.NEED_VK_NEW_GAME, true)
            } else {
                prefs.putBoolean(AppPreferences.NEED_INSTA_NEW_GAME, true)
            }
            viewState.showGameOverDialog()
        } else {
            round++
            getRandomUser()
            viewState.onSetRound(round)
        }
    }

    fun startNewGame() {
        handler.removeCallbacks(runnable)
        canEnable = false
        enableButtonWithDelay()

        if (prefs.getBoolean(VK_GAME)) {
            prefs.putBoolean(AppPreferences.NEED_VK_NEW_GAME, false)
        } else {
            prefs.putBoolean(AppPreferences.NEED_INSTA_NEW_GAME, false)
        }

        round = 1
        points = 0
        viewState.onSetPoints(points)
        viewState.onSetRound(round)

        getRandomUser()
        switchAction(Action.COMMENT)
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