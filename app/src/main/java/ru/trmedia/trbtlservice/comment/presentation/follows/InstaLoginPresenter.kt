package ru.trmedia.trbtlservice.comment.presentation.follows

import android.content.Context
import android.view.View
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_insta_login.*
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.trmedia.trbtlservice.comment.App
import ru.trmedia.trbtlservice.comment.data.database.AppDatabase
import ru.trmedia.trbtlservice.comment.data.network.AppPreferences
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

    val compositeDisposable = CompositeDisposable()

    init {
        App.appComponent?.addInstaLoginComponent(InstaLoginModule())?.inject(this)
    }

    fun saveFollowsToDb(follows: List<Follow>) {
        db.followDao().delete()

        compositeDisposable.add(db.followDao().insert(follows)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ viewState.startGame() }, { throwable -> {} })
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