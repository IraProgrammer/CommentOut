package ru.islab.evilcomments.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.islab.evilcomments.domain.EvilComment
import ru.islab.evilcomments.domain.VKEvilComment

@Dao
interface VKCommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(comments: List<VKEvilComment>): Completable

    @Query("SELECT * FROM VKEvilComment")
    fun getAll(): Single<List<VKEvilComment>>
}