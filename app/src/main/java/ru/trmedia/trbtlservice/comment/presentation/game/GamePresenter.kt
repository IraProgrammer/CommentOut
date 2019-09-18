package ru.trmedia.trbtlservice.comment.presentation.game

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.trmedia.trbtlservice.comment.App
import ru.trmedia.trbtlservice.comment.data.database.AppDatabase
import ru.trmedia.trbtlservice.comment.data.network.AppPreferences
import ru.trmedia.trbtlservice.comment.data.network.AppPreferences.Companion.USERS_SET
import ru.trmedia.trbtlservice.comment.di.module.GameModule
import java.util.*
import javax.inject.Inject
import kotlin.collections.HashSet

@InjectViewState
class GamePresenter : MvpPresenter<GameView>() {

    @Inject
    lateinit var db: AppDatabase

    init {
        App.appComponent?.addGameComponent(GameModule())?.inject(this)
    }

//    override fun onFirstViewAttach() {
//        super.onFirstViewAttach()
//        getRandomUser()
//    }

    fun getRandomUser(context: Context) {
        db.followDao().getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { list ->
                    viewState.onShowRandomUser(
                        list[getRandom(
                            context,
                            USERS_SET,
                            list.size - 1
                        )]
                    )
                },
                { throwable -> {} })
    }

    fun getRandom(context: Context, key: String, bound: Int): Int {
        var set = AppPreferences(context).getSet(key)

        if (set.size == bound + 1) {
            set = HashSet()
            AppPreferences(context).clearSet(key)
        }

        val random = Random()

        var res: Int

        do {
            res = random.nextInt(bound)
        } while (set.contains(res.toString()))

        AppPreferences(context).addToSet(USERS_SET, res.toString())

        return res
    }
}