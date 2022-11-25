package com.lugares_j.repository


import androidx.lifecycle.MutableLiveData
import com.lugares_j.data.LugarDao
import com.lugares_j.model.Lugar

class LugarRepository(private val lugarDao: LugarDao) {

     fun saveLugar(lugar: Lugar){
       lugarDao.saveLugar(lugar)
    }

     fun deleteLugar(lugar: Lugar){
          // Elimina el lugar en caso de que lo encuentre
            lugarDao.deleteLugar(lugar)
    }

    val getLugares : MutableLiveData<List<Lugar>> = lugarDao.getLugares()

}