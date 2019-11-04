package ru.islab.evilcomments.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.islab.evilcomments.domain.InstaUser

@Dao
interface InstaUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(instaUsers: List<InstaUser>): Completable

    @Query("DELETE FROM InstaUser")
    fun delete(): Completable

    @Query("SELECT * FROM InstaUser")
    fun getAll(): Single<List<InstaUser>>
}