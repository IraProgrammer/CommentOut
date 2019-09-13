package ru.trmedia.trbtlservice.comment.di.module

import androidx.annotation.NonNull
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import ru.trmedia.trbtlservice.comment.data.ApiKeyInterceptor
import ru.trmedia.trbtlservice.comment.data.InstaApi
import ru.trmedia.trbtlservice.comment.di.scope.PerApplication

@Module
class NetworkModule {
    private val BASE_URL = "https://api.instagram.com/"

    @NonNull
    @Provides
    @PerApplication
    fun provideRetrofit(@NonNull okHttpClient: OkHttpClient, @NonNull gson: Gson): InstaApi {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(okHttpClient)
            .build().create(InstaApi::class.java)
    }

    @NonNull
    @Provides
    @PerApplication
    fun provideGson(): Gson {
        return GsonBuilder()
            .create()
    }

    @Provides
    @PerApplication
    fun provideApiKeyInterceptor(): ApiKeyInterceptor {
        return ApiKeyInterceptor()
    }

    @Provides
    @PerApplication
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @NonNull
    @Provides
    @PerApplication
    fun provideOkHttpClient(
        apiKeyInterceptor: ApiKeyInterceptor,
        httpLoggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(apiKeyInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }
}