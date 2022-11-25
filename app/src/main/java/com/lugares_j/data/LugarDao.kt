package com.lugares_j.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase

import com.lugares_j.model.Lugar


class  LugarDao  {


// Variables uasadas para generar la estructura en la nube.
    private val coleccion1 = "lugaresApp"
    private val usuario = Firebase.auth.currentUser?.email.toString()
    private val coleccion2 = "misLugares"
 // Obtiene la conexion a la base de datos
    private  var firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    init{
        // Inicializa la configuracion de Firestore
        firestore.firestoreSettings = FirebaseFirestoreSettings.Builder().build()
    }


  // Si no puede insertar entonces no da error
     fun saveLugar(lugar: Lugar){
        // sirve para definir un documento en la nube
         val documento : DocumentReference

         if(lugar.id.isEmpty()){  // si esta vacio es un nuevo documento
            documento = firestore
                .collection(coleccion1)
                .document(usuario)
                .collection(coleccion2)
                .document()

             lugar.id = documento.id
         }else{     // si id tiene algo, entonces se modifica ese documento
             documento = firestore
                 .collection(coleccion1)
                 .document(usuario)
                 .collection(coleccion2)
                 .document(lugar.id)
         }

      // Se modifica o crea el documento en la nube..
      documento.set(lugar)
          .addOnSuccessListener {
              Log.d("saveLugar","Lugar creado/ actualizado")
          }
          .addOnCanceledListener {
              Log.e("saveLugar","Lugar NO creado/ actualizado")
          }


    }


     fun deleteLugar(lugar: Lugar){
         // se valida si el lugar tiene id para poder borrarlo
         if(lugar.id.isNotEmpty()){  // si no esta vacio se elimina
               firestore
                 .collection(coleccion1)
                 .document(usuario)
                 .collection(coleccion2)
                 .document(lugar.id)
                   .delete()
                   .addOnSuccessListener {
                       Log.d("deleteLugar","Lugar eliminado")
                   }
                   .addOnCanceledListener {
                       Log.e("saveLugar","Lugar NO eliminado")
                   }
         }

    }

    fun getLugares() : MutableLiveData<List<Lugar>> {

        val listaLugares = MutableLiveData<List<Lugar>>()

        firestore
            .collection(coleccion1)
            .document(usuario)
            .collection(coleccion2)
            .addSnapshotListener {instantanea,e ->
                if (e != null){ //se dio un error capturando la imagen de la informacion
                    return@addSnapshotListener
                }
                // Si entramos aca no hubo error

                if (instantanea != null){  // Si se pudo recuperar la informacion
                    val lista = ArrayList<Lugar>()

                    // se recorre la instantanea documento por documento, convirtiendolo en lugar y agregandolo a la lista

                    instantanea.documents.forEach{
                        val lugar = it.toObject(Lugar::class.java)
                        if (lugar != null){  // si se pudo convertir el documento en un lugar
                            lista.add(lugar)   // se agrega el lugar a la lista
                        }
                    }
                    listaLugares.value = lista
                }
            }
        return listaLugares

    }       // Devuelve un arreglo tipo lista de los registros
}

