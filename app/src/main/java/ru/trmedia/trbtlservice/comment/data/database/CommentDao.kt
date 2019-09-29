package ru.trmedia.trbtlservice.comment.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import org.w3c.dom.Comment
import ru.trmedia.trbtlservice.comment.domain.EvilComment
import ru.trmedia.trbtlservice.comment.domain.Follow

@Dao
interface CommentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(comments: List<EvilComment>)

    @Query("SELECT * FROM EvilComment")
    fun getAll(): Single<List<EvilComment>>

    @Query("SELECT * FROM EvilComment")
    fun getAllS(): List<EvilComment>
}