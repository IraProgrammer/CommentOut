package ru.islab.evilcomments.presentation.sign_in

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.islab.evilcomments.App
import ru.islab.evilcomments.data.database.AppDatabase
import ru.islab.evilcomments.di.module.SignInModule
import ru.islab.evilcomments.domain.DataHelper
import ru.islab.evilcomments.domain.VKUser
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
            db.vkUserDao().insert(follows.shuffled())
                .startWith(db.vkUserDao().delete())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.startGame()
                }, { throwable ->
                    run {
                        var c = 100
                    }
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}