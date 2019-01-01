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

@Database(entities = [(AisDeviceEntity::class)], version = 1)
abstract class DataBase : RoomDatabase() {

    abstract fun dao(): AisDeviceDao

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
                    .addCallback(DataBaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }

    class DataBaseCallback (
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateDatabase(database.dao())
                }
            }
        }

        private fun populateDatabase(dao: AisDeviceDao) {
            //insert TEST data
            val count = dao.getCount()
            if (count == 0) {
                var device = AisDeviceEntity(null, "Test Device 1", "00:00:00:00:00:00", "123.1.2.3", AisDeviceType.Bulb.value)
                dao.insert(device)
                device = AisDeviceEntity(null, "Test Device 2", "11:00:00:00:00:00", "123.1.2.4", AisDeviceType.Socket.value)
                dao.insert(device)
                device = AisDeviceEntity(null, "Test3", "22:00:00:00:00:00", "123.1.2.5", null)
                dao.insert(device)
            }
        }
    }
}