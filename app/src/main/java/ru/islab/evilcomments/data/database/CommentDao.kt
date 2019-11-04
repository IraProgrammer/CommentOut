package ru.islab.evilcomments.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.islab.evilcomments.domain.EvilComment

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(comments: List<EvilComment>): Completable

    @Query("SELECT * FROM EvilComment")
    fun getAll(): Single<List<EvilComment>>
}