package com.lugares_j.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.lugares_j.model.Lugar


@Dao
interface LugarDao {

    // Las funciones de bajo nivel para hacer un CRUD
    @Insert(onConflict = OnConflictStrategy.IGNORE) // Si no puede insertar entonces no da error
    suspend fun addLugar(lugar: Lugar)

    @Update(onConflict = OnConflictStrategy.IGNORE) // Si no puede insertar entonces no da error
    suspend fun updateLugar(lugar: Lugar)

    @Delete
    suspend fun deleteLugar(lugar: Lugar)

    @Query("SELECT * FROM LUGAR")
    fun getLugares() : LiveData<List<Lugar>>          // Devuelve un arreglo tipo lista de los registros


}