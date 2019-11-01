package ru.islab.evilcomments.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.Completable
import io.reactivex.Single
import ru.islab.evilcomments.domain.Follow
import ru.islab.evilcomments.domain.VKUser

@Dao
interface VKUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(follows: List<VKUser>): Completable

    @Query("DELETE FROM VKUser")
    fun delete(): Completable

    @Query("SELECT * FROM VKUser")
    fun getAll(): Single<List<VKUser>>
}