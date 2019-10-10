package ru.islab.evilcomments.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.islab.evilcomments.domain.Punishment

@Dao
interface PunishmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(punishments: List<Punishment>): Completable

    @Query("SELECT * FROM Punishment")
    fun getAll(): Single<List<Punishment>>
}