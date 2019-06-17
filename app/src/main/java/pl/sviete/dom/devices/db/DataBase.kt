package pl.sviete.dom.devices.db

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.sviete.dom.devices.models.AisDeviceType

@Database(entities = [AisDeviceEntity::class, AreaEntity::class], version = 1)
abstract class DataBase : RoomDatabase() {

    abstract fun aisDeviceDao(): AisDeviceDao

    abstract fun areaDao(): AreaDao

    companion object {
        private var INSTANCE: DataBase? = null

        fun getInstance(context: Context, scope: CoroutineScope): DataBase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataBase::class.java,
                    "aisdevices.db"
                )
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }
}