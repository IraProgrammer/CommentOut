package ru.trmedia.trbtlservice.comment.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.trmedia.trbtlservice.comment.domain.Follow

@Dao
interface FollowDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(follows: List<Follow>): Completable

    @Query("SELECT * FROM Follow")
    fun getAll(): Single<List<Follow>>
}