package ru.islab.evilcomments.presentation.insta

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
import ru.islab.evilcomments.domain.DataHelper
import ru.islab.evilcomments.domain.InstaUser
import java.util.*
import javax.inject.Inject

@InjectViewState
class InstaLoginPresenter : MvpPresenter<InstaLoginView>() {

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var dataHelper: DataHelper

    private val compositeDisposable = CompositeDisposable()

    init {
        App.appComponent?.addInstaLoginComponent(InstaLoginModule())?.inject(this)
    }

    fun saveFollowsToDb(instaUsers: List<InstaUser>) {
        compositeDisposable.add(
            db.instaUserDao().insert(instaUsers.shuffled(Random()))
                .startWith(db.instaUserDao().delete())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.startGame()
                }, { throwable -> {} })
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