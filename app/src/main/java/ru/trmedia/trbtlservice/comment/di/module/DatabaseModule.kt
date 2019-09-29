package ru.trmedia.trbtlservice.comment.di.module

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import ru.trmedia.trbtlservice.comment.data.database.AppDatabase
import ru.trmedia.trbtlservice.comment.data.database.AppDatabase.Companion.DATABASE_NAME
import ru.trmedia.trbtlservice.comment.di.scope.PerApplication
import javax.inject.Named
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.room.RoomDatabase
import ru.trmedia.trbtlservice.comment.domain.EvilComment
import ru.trmedia.trbtlservice.comment.domain.Punishment
import java.util.concurrent.Executors


@Module
class DatabaseModule {

    lateinit var trainDB: AppDatabase

    @Provides
    @PerApplication
    fun provideDatabase(@Named("AppContext") context: Context): AppDatabase {

        trainDB = Room.databaseBuilder(
            context,
            AppDatabase::class.java, DATABASE_NAME
        )
            .allowMainThreadQueries()
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(db: SupportSQLiteDatabase) {
                    Executors.newSingleThreadScheduledExecutor()
                        .execute {
                            super.onCreate(db)
                            trainDB.commentDao().insert(getComments())
                            trainDB.punishmentDao().insert(getPunishments())
                        }
                }
            })
            .build()

        return trainDB
    }

    private fun getComments(): List<EvilComment> {
        val comments = ArrayList<EvilComment>()

        comments.add(EvilComment(0, "comment1"))
        comments.add(EvilComment(0, "comment2"))
        comments.add(EvilComment(0, "comment3"))
        comments.add(EvilComment(0, "comment4"))
        comments.add(EvilComment(0, "comment5"))

        return comments
    }

    private fun getPunishments(): List<Punishment> {
        val punishments = ArrayList<Punishment>()

        punishments.add(Punishment(0, "punishment1"))
        punishments.add(Punishment(0, "punishment2"))
        punishments.add(Punishment(0, "punishment3"))
        punishments.add(Punishment(0, "punishment4"))
        punishments.add(Punishment(0, "punishment5"))

        return punishments
    }
}