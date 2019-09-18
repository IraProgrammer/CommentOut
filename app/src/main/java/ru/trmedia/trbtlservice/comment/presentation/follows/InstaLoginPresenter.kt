package ru.trmedia.trbtlservice.comment.presentation.follows

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.trmedia.trbtlservice.comment.App
import ru.trmedia.trbtlservice.comment.data.database.AppDatabase
import ru.trmedia.trbtlservice.comment.data.network.InstaApi
import ru.trmedia.trbtlservice.comment.di.module.InstaLoginModule
import ru.trmedia.trbtlservice.comment.domain.Follow
import javax.inject.Inject

@InjectViewState
class InstaLoginPresenter : MvpPresenter<InstaLoginView>() {

    @Inject
    lateinit var instaApi: InstaApi

    @Inject
    lateinit var db: AppDatabase

    init {
        App.appComponent?.addInstaLoginComponent(InstaLoginModule())?.inject(this)
    }

    fun getUserInfoByAccessToken(authToken: String) {
        instaApi.getUserInfo(authToken)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ userWrap -> viewState.onShowInfo(userWrap) }, { throwable -> {} })
    }

    fun saveFollowsToDb(follows: List<Follow>) {
        db.followDao().delete()

        db.followDao().insert(follows)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ viewState.startGame() }, { throwable -> {} })
    }
}