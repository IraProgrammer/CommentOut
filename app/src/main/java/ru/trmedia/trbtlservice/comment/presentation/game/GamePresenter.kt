package ru.trmedia.trbtlservice.comment.presentation.game

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.trmedia.trbtlservice.comment.App
import ru.trmedia.trbtlservice.comment.data.database.AppDatabase
import ru.trmedia.trbtlservice.comment.di.module.GameModule
import javax.inject.Inject

@InjectViewState
class GamePresenter : MvpPresenter<GameView>() {

    @Inject
    lateinit var db: AppDatabase

    init {
        App.appComponent?.addGameComponent(GameModule())?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        getRandomUser()
    }

    fun getRandomUser() {
        db.followDao().getAll()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list -> viewState.onShowRandomUser(list[5]) }, { throwable -> {} })
    }
}