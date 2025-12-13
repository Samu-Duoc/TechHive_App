package com.example.techhive_app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.techhive_app.data.local.product.ProductDao
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.data.local.user.UserDao
import com.example.techhive_app.data.local.user.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class, ProductEntity::class],
    version = 6, //sube versión
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "techhive.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : RoomDatabase.Callback() {

                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)

                            // NO LLAMES getInstance() aquí adentro.
                            // Usamos INSTANCE cuando ya exista (Room la asigna al build abajo).
                            CoroutineScope(Dispatchers.IO).launch {
                                val database = INSTANCE ?: return@launch
                                val userDao = database.userDao()

                                // Seed de usuarios (opcional)
                                if (userDao.count() == 0) {
                                    val userSeed = listOf(
                                        UserEntity(
                                            name = "Admin",
                                            email = "admin@techive.cl",
                                            phone = "+56911111111",
                                            password = "Admin123!"
                                        ),
                                        UserEntity(
                                            name = "Samuel Fuenzalida",
                                            email = "Samu@duoc.cl",
                                            phone = "+56922222222",
                                            password = "123456"
                                        )
                                    )
                                    userSeed.forEach { userDao.insert(it) }
                                }

                                // Seed de productos eliminado:
                                // Ahora los productos vienen del MS y/o se sincronizan.
                            }
                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
