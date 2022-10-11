package com.lugares_j.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lugares_j.model.Lugar

@Database(entities = [Lugar::class], version = 1, exportSchema = false)
abstract class LugarDatabase: RoomDatabase() {

    abstract fun lugarDao() : LugarDao

    companion object {
        @Volatile
        private var INSTANCE: LugarDatabase? = null;

        // funcion para obtener conexion a base de datos
        fun getDatabase(context: Context) : LugarDatabase{
            val local = INSTANCE
            if (local != null){
                return local
            }
            synchronized(this){  // En caso de que no encuentre la DB entonces la crea y la retorna
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    LugarDatabase::class.java,
                    "lugar_database").build()
                INSTANCE = instance
                return instance
            }
        }

    }
}