package ru.topchu.winfoxtestapp.di

import android.app.Application
import android.content.Context
import androidx.navigation.NavOptions
import androidx.room.Room
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.topchu.winfoxtestapp.R
import ru.topchu.winfoxtestapp.data.local.AppDatabase
import ru.topchu.winfoxtestapp.data.local.Converters
import ru.topchu.winfoxtestapp.data.remote.WinfoxApi
import ru.topchu.winfoxtestapp.data.repository.WinfoxRepoImpl
import ru.topchu.winfoxtestapp.domain.repository.WinfoxRepository
import ru.topchu.winfoxtestapp.utils.GsonParser
import ru.topchu.winfoxtestapp.utils.SharedPref
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @ApplicationScope
    @Provides
    @Singleton
    fun provideApplicationScope() = CoroutineScope(SupervisorJob())

    @Provides
    @Singleton
    fun provideApi(): WinfoxApi {
        return Retrofit.Builder()
            .baseUrl(WinfoxApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WinfoxApi::class.java)
    }

    @Provides
    @Singleton
    fun provideDatabase(app: Application): AppDatabase = Room.databaseBuilder(
        app, AppDatabase::class.java, "app_db"
    )
        .addTypeConverter(Converters(GsonParser(Gson())))
        .build()

    @Provides
    @Singleton
    fun provideWinfoxRepository(
        api: WinfoxApi
    ): WinfoxRepository {
        return WinfoxRepoImpl(api)
    }

    @Provides
    @Singleton
    fun provideSharedPref(@ApplicationContext context: Context)
            = SharedPref(context)

    @Singleton
    @Provides
    fun provideGlideInstance(
        @ApplicationContext context: Context
    ) = Glide.with(context).setDefaultRequestOptions(
        RequestOptions()
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_background)
            .diskCacheStrategy(DiskCacheStrategy.DATA)
    )

    @Singleton
    @Provides
    fun provideNavOptions() = NavOptions.Builder().apply {
        setEnterAnim(R.anim.nav_default_enter_anim)
        setExitAnim(R.anim.nav_default_exit_anim)
        setPopEnterAnim(R.anim.nav_default_pop_enter_anim)
        setPopExitAnim(R.anim.nav_default_pop_exit_anim)
    }.build()
}

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class ApplicationScope