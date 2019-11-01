package ru.islab.evilcomments.presentation.sign_in

import android.content.Context
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.islab.evilcomments.App
import ru.islab.evilcomments.BuildConfig
import ru.islab.evilcomments.data.AppPreferences
import ru.islab.evilcomments.data.database.AppDatabase
import ru.islab.evilcomments.di.module.InstaLoginModule
import ru.islab.evilcomments.di.module.SignInModule
import ru.islab.evilcomments.domain.DataHelper
import ru.islab.evilcomments.domain.Follow
import ru.islab.evilcomments.domain.VKUser
import ru.islab.evilcomments.presentation.follows.InstaLoginView
import javax.inject.Inject

@InjectViewState
class SignInPresenter : MvpPresenter<SignInView>() {

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var dataHelper: DataHelper

    private val compositeDisposable = CompositeDisposable()

    init {
        App.appComponent?.addSignInComponent(SignInModule())?.inject(this)
    }

    fun saveFollowsToDb(follows: List<VKUser>) {
        compositeDisposable.add(
            db.vkUserDao().insert(follows)
                .startWith(db.vkUserDao().delete())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.startGame()
                }, { throwable -> {
                    var c = 100 } })
        )
    }

    fun putDataToDb() {
        compositeDisposable.add(
            db.commentDao().insert(dataHelper.commentsList)
                .andThen(db.punishmentDao().insert(dataHelper.punishmentsList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.saveVersionCode()
                    viewState.authorize()
                }, { throwable ->
                    var c = 8
                })
        )
    }

//    fun putDataToDb() {
//        compositeDisposable.add(
//            db.commentDao().insert(dataHelper.commentsList)
//                .andThen(db.punishmentDao().insert(dataHelper.punishmentsList))
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    viewState.saveVersionCode()
//                    viewState.initUI()
//                }, { throwable -> })
//        )
//    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}