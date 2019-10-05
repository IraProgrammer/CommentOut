package ru.islab.evilcomments.presentation.follows

import android.content.Context
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.islab.evilcomments.App
import ru.islab.evilcomments.data.database.AppDatabase
import ru.islab.evilcomments.di.module.InstaLoginModule
import ru.islab.evilcomments.domain.Follow
import javax.inject.Inject

@InjectViewState
class InstaLoginPresenter : MvpPresenter<InstaLoginView>() {

    @Inject
    lateinit var db: AppDatabase

    val compositeDisposable = CompositeDisposable()

    init {
        App.appComponent?.addInstaLoginComponent(InstaLoginModule())?.inject(this)
    }

    fun saveFollowsToDb(follows: List<Follow>) {
        compositeDisposable.add(
            db.followDao().insert(follows)
                .startWith(db.followDao().delete())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.startGame()
                }, { throwable -> {} })
        )
    }

    fun checkNetwork() {
        compositeDisposable.add(
            ReactiveNetwork
                .checkInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { conn ->
                    if (!conn) {
                        viewState.noNetworkInStart()
                    }
                }
        )
    }

    fun observeNetwork(context: Context) {

        compositeDisposable.add(
            ReactiveNetwork
                .observeNetworkConnectivity(context)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { connectivity ->
                    if (connectivity.isAvailable) {
                        viewState.networkSuccessed()
                    } else {
                        viewState.networkFailed()
                    }
                })
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}