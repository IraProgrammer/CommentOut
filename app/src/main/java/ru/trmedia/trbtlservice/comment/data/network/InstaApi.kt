package ru.trmedia.trbtlservice.comment.data.network

import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query
import ru.trmedia.trbtlservice.comment.domain.UserWrap

interface InstaApi{
    @GET("v1/users/self")
    @Headers("content-type: application/json")
    fun getUserInfo(@Query("access_token") token: String): Observable<UserWrap>
}