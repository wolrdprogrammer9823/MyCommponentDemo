package com.component.datastore.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.component.datastore.dao.UserDao
import com.component.datastore.entity.User

@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class RoomData : RoomDatabase() {

    abstract fun getUserDao() : UserDao

    companion object {

        @Volatile
        private var INSTANCE : RoomData? = null

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("CREATE table book(id INTEGER,name Text)")
            }
        }

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER table user ADD COLUMN strength INTEGER NOT NULL DEFAULT 0")
            }
        }

        fun getInstance(context: Context) : RoomData =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context.applicationContext, RoomData::class.java, "rootData.db")
                .addMigrations(MIGRATION_1_2)
                .addMigrations(MIGRATION_2_3)
                .build()
    }
}