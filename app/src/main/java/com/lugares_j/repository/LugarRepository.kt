package com.lugares_j.repository

import androidx.lifecycle.LiveData
import com.lugares_j.data.LugarDao
import com.lugares_j.model.Lugar

class LugarRepository(private val lugarDao: LugarDao) {

    suspend fun saveLugar(lugar: Lugar){
        if (lugar.id == 0){  // Es un lugar nuevo

            lugarDao.addLugar(lugar)
        }else{      // Es un lugar ya registrado
            lugarDao.updateLugar(lugar)
        }
    }

    suspend fun deleteLugar(lugar: Lugar){
        if (lugar.id == 0) {  // Elimina el lugar en caso de que lo encuentre
            lugarDao.deleteLugar(lugar)
        }
    }

    val getLugares : LiveData<List<Lugar>> = lugarDao.getLugares()

}