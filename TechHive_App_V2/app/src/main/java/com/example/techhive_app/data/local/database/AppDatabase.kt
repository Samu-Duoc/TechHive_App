package com.example.techhive_app.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.techhive_app.R

import com.example.techhive_app.data.local.product.ProductDao
import com.example.techhive_app.data.local.product.ProductEntity
import com.example.techhive_app.data.local.user.UserDao
import com.example.techhive_app.data.local.user.UserEntity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [UserEntity::class, ProductEntity::class],
    version = 5,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun productDao(): ProductDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        private const val DB_NAME = "techhive.db"

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DB_NAME
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            CoroutineScope(Dispatchers.IO).launch {
                                val userdao = getInstance(context).userDao()
                                val productdao = getInstance(context).productDao()

                                val userSeed = listOf(
                                    UserEntity(name = "Admin", email = "admin@duoc.cl", phone = "+56911111111", password = "Admin123!"),
                                    UserEntity(name = "Samuel Fuenzalida", email = "Samu@duoc.cl", phone = "+56922222222", password = "123456")
                                )
                                if (userdao.count() == 0) {
                                    userSeed.forEach { userdao.insert(it) }
                                }

                                // Precarga de productos
                                val productSeed = listOf(
                                    ProductEntity(name = "Asus vivoBook 15", description = "Notebook liviano con pantalla Full HD, ideal para estudio y trabajo diario.", price = 499990.0, imageUrl = R.drawable.asus_vivobook15, stock = 10, sku = "PC-ASU-001", category = "Computadores"),
                                    ProductEntity(name = "MacBook Air M1", description = "Ultraportátil de Apple con chip M1 y batería de larga duración.", price = 899990.0, imageUrl = R.drawable.macbook_air_m1, stock = 8, sku = "PC-APL-002", category = "Computadores"),
                                    ProductEntity(name = "PC Gamer Ryzen 5", description = "Computador de escritorio con gran potencia para juegos y multitarea.", price = 649990.0, imageUrl = R.drawable.pc_ryzen5, stock = 5, sku = "PC-GAM-003", category = "Computadores"),

                                    ProductEntity(name = "Samsung Galaxy S21", description = "Smartphone Android con cámara avanzada y rendimiento rápido.", price = 429990.0, imageUrl = R.drawable.samsung_galaxy_s21_negro, stock = 12, sku = "SMT-SAM-004", category = "Smartphones"),
                                    ProductEntity(name = "Samsung Galaxy S21", description = "Smartphone Android con cámara avanzada y rendimiento rápido.", price = 429990.0, imageUrl = R.drawable.samsung_galaxy_s21_morado, stock = 12, sku = "SMT-SAM-004", category = "Smartphones"),

                                    ProductEntity(name = "iPhone 12 Reacondicionado", description = "Teléfono Apple reacondicionado con diseño premium y garantía.", price = 389990.0, imageUrl = R.drawable.iphone_12_morado, stock = 7, sku = "SMT-APL-005", category = "Smartphones"),
                                    ProductEntity(name = "iPhone 12 Reacondicionado", description = "Teléfono Apple reacondicionado con diseño premium y garantía.", price = 389990.0, imageUrl = R.drawable.iphone_12_blanco, stock = 7, sku = "SMT-APL-005", category = "Smartphones"),
                                    ProductEntity(name = "iPhone 12 Reacondicionado", description = "Teléfono Apple reacondicionado con diseño premium y garantía.", price = 389990.0, imageUrl = R.drawable.iphone_12_azul, stock = 7, sku = "SMT-APL-005", category = "Smartphones"),

                                    ProductEntity(name = "Cargador Rápido 65W", description = "Adaptador de carga rápida compatible con notebooks y smartphones.", price = 24990.0, imageUrl = R.drawable.cargador_65w, stock = 20, sku = "ACC-CAR-006", category = "Accesorios"),
                                    ProductEntity(name = "Cable LAN Cat6", description = "Cable de red de alta velocidad para conexión estable a internet.", price = 6000.0, imageUrl = R.drawable.cable_lan_cat6, stock = 30, sku = "ACC-LAN-007", category = "Accesorios"),
                                    ProductEntity(name = "Pendrive Kingston 64GB", description = "Memoria portátil confiable para almacenar archivos.", price = 15000.0, imageUrl = R.drawable.pendrive_kingston, stock = 25, sku = "ACC-USB-008", category = "Accesorios"),
                                    ProductEntity(name = "PlayStation 4 Slim Reacondicionada", description = "Consola reacondicionada con garantía y amplio catálogo de juegos.", price = 229990.0, imageUrl = R.drawable.ps4_slim, stock = 4, sku = "CON-PS4-009", category = "Reacondicionados"),
                                    ProductEntity(name = "Dell Inspiron 3520 Reacondicionada", description = "Notebook reacondicionado con rendimiento confiable para trabajo y estudio.", price = 229990.0, imageUrl = R.drawable.dell_inspiron, stock = 6, sku = "PC-DEL-010", category = "Reacondicionados"),
                                    ProductEntity(name = "NVIDIA RTX 4060 MSI", description = "Tarjeta gráfica potente para gaming y creación de contenido.", price = 379990.0, imageUrl = R.drawable.rtx_4060, stock = 5, sku = "CMP-RTX-011", category = "Componentes"),
                                    ProductEntity(name = "ASUS Prime B550M", description = "Placa madre de alta calidad para procesadores AMD Ryzen.", price = 149990.0, imageUrl = R.drawable.asus_b550m, stock = 9, sku = "CMP-MOB-012", category = "Componentes"),
                                    ProductEntity(name = "Steam Deck 512GB", description = "Consola portátil para jugar toda tu biblioteca de Steam en cualquier lugar.", price = 699990.0, imageUrl = R.drawable.steam_deck, stock = 3, sku = "CON-STM-013", category = "Consolas")
                                    ,
                                    ProductEntity(name = "Sony WH-1000XM4", description = "Audífonos inalámbricos con cancelación de ruido de última generación.", price = 249990.0, imageUrl = R.drawable.sony_wh1000xm4_negro, stock = 10, sku = "AUD-SON-014", category = "Audio"),
                                    ProductEntity(name = "Sony WH-1000XM4", description = "Audífonos inalámbricos con cancelación de ruido de última generación.", price = 249990.0, imageUrl = R.drawable.sony_wh1000xm4_crema, stock = 10, sku = "AUD-SON-014", category = "Audio"),
                                    ProductEntity(name = "Sony WH-1000XM4", description = "Audífonos inalámbricos con cancelación de ruido de última generación.", price = 249990.0, imageUrl = R.drawable.sony_wh1000xm4_azul, stock = 10, sku = "AUD-SON-014", category = "Audio"),
                                    ProductEntity(name = "Sound BlasterX G6", description = "Amplificador de audio DAC/AMP para experiencia de sonido premium.", price = 99990.0, imageUrl = R.drawable.sound_blasterx_g6, stock = 10, sku = "AUD-SBL-015", category = "Audio"),
                                    ProductEntity(name = "Teclado Mecánico Redragon K630", description = "Teclado retroiluminado mecánico, perfecto para gaming.", price = 179990.0, imageUrl = R.drawable.teclado_redragon_k630, stock = 10, sku = "PER-RED-016", category = "Periféricos"),

                                    ProductEntity(name = "Silla Gamer Cougar Armor", description = "Silla ergonómica con soporte ajustable para largas sesiones.", price = 199990.0, imageUrl = R.drawable.silla_cougar_armor_negra, stock = 5, sku = "PER-COU-017", category = "Periféricos"),
                                    ProductEntity(name = "Silla Gamer Cougar Armor", description = "Silla ergonómica con soporte ajustable para largas sesiones.", price = 199990.0, imageUrl = R.drawable.silla_cougar_armor_naranja, stock = 5, sku = "PER-COU-017", category = "Periféricos"),
                                    ProductEntity(name = "Silla Gamer Cougar Armor", description = "Silla ergonómica con soporte ajustable para largas sesiones.", price = 199990.0, imageUrl = R.drawable.silla_cougar_armor_rosada, stock = 5, sku = "PER-COU-017", category = "Periféricos")
                                )
                                if (productdao.count() == 0) {
                                    productSeed.forEach { productdao.insert(it) }
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}
