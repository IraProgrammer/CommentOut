package ru.islab.evilcomments.presentation.menu

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import moxy.InjectViewState
import moxy.MvpPresenter
import ru.islab.evilcomments.App
import ru.islab.evilcomments.data.database.AppDatabase
import ru.islab.evilcomments.di.module.MenuModule
import ru.islab.evilcomments.di.module.SignInModule
import ru.islab.evilcomments.domain.DataHelper
import ru.islab.evilcomments.domain.VKUser
import javax.inject.Inject

@InjectViewState
class MenuPresenter : MvpPresenter<MenuView>() {

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var dataHelper: DataHelper

    private val compositeDisposable = CompositeDisposable()

    init {
        App.appComponent?.addMenuComponent(MenuModule())?.inject(this)
    }

    fun putDataToDb() {
        compositeDisposable.add(
            db.commentDao().insert(dataHelper.commentsList)
                .andThen(db.punishmentDao().insert(dataHelper.punishmentsList))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    viewState.saveVersionCode()
                    viewState.initUI()
                }, { throwable ->
                    var c = 8
                })
        )
    }

    override fun onDestroy() {
        compositeDisposable.clear()
        super.onDestroy()
    }
}