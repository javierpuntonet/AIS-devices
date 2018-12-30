package pl.sviete.dom.devices.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.content.Context

@Database(entities = arrayOf(AisDeviceEntity::class), version = 1)
abstract class DataBase : RoomDatabase() {

    abstract fun dao(): AisDeviceDao

    companion object {
        private var INSTANCE: DataBase? = null

        fun getInstance(context: Context): DataBase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DataBase::class.java,
                    "aisdevices.db"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}