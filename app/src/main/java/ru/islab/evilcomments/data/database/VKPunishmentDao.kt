package ru.islab.evilcomments.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.islab.evilcomments.domain.Punishment
import ru.islab.evilcomments.domain.VKPunishment

@Dao
interface VKPunishmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(punishments: List<VKPunishment>): Completable

    @Query("SELECT * FROM VKPunishment")
    fun getAll(): Single<List<VKPunishment>>
}