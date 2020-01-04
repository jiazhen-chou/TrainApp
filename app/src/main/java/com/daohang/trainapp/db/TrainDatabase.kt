package com.daohang.trainapp.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.daohang.trainapp.db.dao.*
import com.daohang.trainapp.db.models.*

@Database(
    entities = [
        CoachStateModel::class,
        StudentStateModel::class,
        RecordModel::class,
        LocationModel::class,
        PreferenceModel::class,
        VehiclePreferenceModel::class,
        TrainPreferenceModel::class,
        MessageModel::class,
        RegisterModel::class,
        PictureInitModel::class,
        SavedCardInfo::class],
    version = 14
)
//TODO 版本号在打包时需要更改
abstract class TrainDatabase : RoomDatabase() {

    abstract fun locationDao(): LocationDao

    abstract fun preferenceDao(): PreferenceDao

    abstract fun recordDao(): RecordDao

    abstract fun trainPreferenceDao(): TrainPreferenceDao

    abstract fun coachStateDao(): CoachStateDao

    abstract fun studentStateDao(): StudentStateDao

    abstract fun messageDao(): MessageDao

    abstract fun registerDao(): RegisterDao

    abstract fun pictureInitDao(): PictureInitDao

    abstract fun savedCardInfoDao(): SavedCardInfoDao

    companion object {

        @Volatile
        private var INSTANCE: TrainDatabase? = null

        fun getDataBase(context: Context): TrainDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null)
                return tempInstance

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrainDatabase::class.java,
                    "train_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}