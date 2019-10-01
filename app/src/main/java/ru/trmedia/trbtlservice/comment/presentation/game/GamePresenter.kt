package ru.trmedia.trbtlservice.comment.presentation.game

import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function3
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.trmedia.trbtlservice.comment.App
import ru.trmedia.trbtlservice.comment.data.database.AppDatabase
import ru.trmedia.trbtlservice.comment.data.AppPreferences
import ru.trmedia.trbtlservice.comment.data.AppPreferences.Companion.COMMENTS_SET
import ru.trmedia.trbtlservice.comment.data.AppPreferences.Companion.PUNISHMENT_SET
import ru.trmedia.trbtlservice.comment.data.AppPreferences.Companion.USERS_SET
import ru.trmedia.trbtlservice.comment.di.module.GameModule
import ru.trmedia.trbtlservice.comment.domain.EvilComment
import ru.trmedia.trbtlservice.comment.domain.Follow
import ru.trmedia.trbtlservice.comment.domain.Punishment
import ru.trmedia.trbtlservice.comment.presentation.OneModel
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashSet

@InjectViewState
class GamePresenter : MvpPresenter<GameView>() {

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var prefs: AppPreferences

    private var compositeDisposable = CompositeDisposable()

    init {
        App.appComponent?.addGameComponent(GameModule())?.inject(this)
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
                .subscribe(
                    { oneModel ->
                        viewState.onShowNextRaund(oneModel)
                    },
                    { throwable ->
                        var a = 7
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
}