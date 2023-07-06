package com.tws.taskroom.di

import android.app.Application
import androidx.room.Room
import com.tws.taskroom.feature_room.data.data_source.MyRoomDatabase
import com.tws.taskroom.feature_room.data.repository.MyRoomRepoImpl
import com.tws.taskroom.feature_room.domain.repository.MyRoomRepo
import com.tws.taskroom.feature_room.domain.use_case.GetAllRoomUseCase
import com.tws.taskroom.feature_room.domain.use_case.GetMyRoomByNameUseCase
import com.tws.taskroom.feature_room.domain.use_case.InsertMyRoom
import com.tws.taskroom.feature_room.domain.use_case.MyRoomUseCase
import com.tws.taskroom.feature_room.domain.use_case.UpdateMyRoom
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideMyDatabase(context: Application): MyRoomDatabase {
        return Room.databaseBuilder(
            context,
            MyRoomDatabase::class.java,
            MyRoomDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideMyRoomRepo(
        db: MyRoomDatabase,
    ): MyRoomRepo {
        return MyRoomRepoImpl(db.roomDao)
    }

    @Provides
    @Singleton
    fun provideMyRoomUseCase(
        repo: MyRoomRepo
    ): MyRoomUseCase {
        return MyRoomUseCase(
            getAllRoom = GetAllRoomUseCase(repo),
            insertMyRoom = InsertMyRoom(repo),
            updateMyRoom = UpdateMyRoom(repo),
            getMyRoomByNameUseCase = GetMyRoomByNameUseCase(repo)
        )
    }

}