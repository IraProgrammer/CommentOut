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
import ru.trmedia.trbtlservice.comment.domain.Punishment

@Dao
interface PunishmentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(punishments: List<Punishment>)

    @Query("SELECT * FROM Punishment")
    fun getAll(): Single<List<Punishment>>
}