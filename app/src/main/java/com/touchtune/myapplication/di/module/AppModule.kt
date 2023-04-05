package com.touchtune.myapplication.di.module

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.touchtune.myapplication.MainApplication
import com.touchtune.myapplication.api.ConnectivityInterceptor
import com.touchtune.myapplication.api.ItunesApiService
import com.touchtune.myapplication.data.*
import com.touchtune.myapplication.utilities.DATABASE_NAME
import com.touchtune.myapplication.utilities.NetworkHelper
import com.touchtune.myapplication.utilities.RECENT_SEARCH_FILE_NAME
import com.touchtune.myapplication.workers.RecentSearchDatabaseWorker
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

private const val BASE_URL = "https://itunes.apple.com/"

val appModule = module {
    single { provideOkHttpClient() }
    single { provideRetrofit(get(), BASE_URL) }
    single { provideiTunesApiService(get()) }
    single { provideNetworkHelper(androidContext()) }

    single {
        Room.databaseBuilder(
            MainApplication.appContext,
            AppDatabase::class.java,
            DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        val request = OneTimeWorkRequestBuilder<RecentSearchDatabaseWorker>()
                            .setInputData(workDataOf(RecentSearchDatabaseWorker.KEY_FILENAME to RECENT_SEARCH_FILE_NAME))
                            .build()
                        WorkManager.getInstance(MainApplication.appContext).enqueue(request)
                    }
                }
            )
            .build()
    }
    single<RecentSearchDao> {
        val database = get<AppDatabase>()
        database.RecentSearchDao()
    }

    single<AlbumDataSource> {
        return@single AlbumRemoteDataSource(get())
    }
    single<RecentSearchDataSource> {
        return@single RecentSearchLocalDataSource(get())
    }
    single<Repository> {
        return@single DefaultRepository(get(), get())
    }
}

private fun provideNetworkHelper(context: Context) = NetworkHelper(context)

private fun provideOkHttpClient(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor()
    loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
    return OkHttpClient.Builder()
        .addInterceptor(ConnectivityInterceptor(MainApplication.appContext))
        .build()
}

private fun provideRetrofit(
    okHttpClient: OkHttpClient,
    BASE_URL: String
): Retrofit {
    val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()
    return Retrofit.Builder()
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .build()
}


private fun provideiTunesApiService(retrofit: Retrofit): ItunesApiService =
    retrofit.create(ItunesApiService::class.java)
