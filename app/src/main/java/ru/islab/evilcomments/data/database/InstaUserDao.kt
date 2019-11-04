package ru.islab.evilcomments.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.islab.evilcomments.domain.Follow

@Dao
interface InstaUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(instaUsers: List<Follow>): Completable

    @Query("DELETE FROM Follow")
    fun delete(): Completable

    @Query("SELECT * FROM Follow")
    fun getAll(): Single<List<Follow>>
}