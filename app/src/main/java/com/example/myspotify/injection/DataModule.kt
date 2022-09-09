package com.example.myspotify.injection

import android.content.Context
import android.content.SharedPreferences
import com.example.myspotify.BuildConfig
import com.example.myspotify.Config
import com.example.myspotify.data.local.ApplicationStorage
import com.example.myspotify.network.HerokuApiService
import com.example.myspotify.network.SpotifyApiService
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @Singleton
    @Provides
    @Named("Heroku")
    fun provideHerokuRetrofit(moshi: Moshi): Retrofit {
        val client = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            client.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(Config.HEROKU_API_BASE_URL)
            .client(client.build())
            .build()
    }

    @Singleton
    @Provides
    @Named("Spotify")
    fun provideSpotifyRetrofit(moshi: Moshi): Retrofit {
        val client = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            client.addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
        }

        return Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(Config.SPOTIFY_API_BASE_URL)
            .client(client.build())
            .build()
    }


    @Singleton
    @Provides
    fun provideMoshi(): Moshi {
        return Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()
    }

    @Provides
    @Singleton
    fun provideHerokuApiService(@Named("Heroku") retrofit: Retrofit): HerokuApiService {
        return retrofit.create(HerokuApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSpotifyApiService(@Named("Spotify") retrofit: Retrofit): SpotifyApiService {
        return retrofit.create(SpotifyApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideSharedPreferences(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences(Config.SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @Provides
    @Singleton
    fun provideApplicationStorage(sharedPreferences: SharedPreferences): ApplicationStorage {
        return ApplicationStorage(sharedPreferences)
    }
}