package com.lugares_j.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.lugares_j.databinding.LugarFilaBinding
import com.lugares_j.model.Lugar
import com.lugares_j.ui.lugar.LugarFragmentDirections

class LugarAdapter : RecyclerView.Adapter<LugarAdapter.LugarViewHolder>(){

    // Se encarga de dibujar la informacion en las cajitas
    inner class LugarViewHolder(private val itemBinding: LugarFilaBinding)
        : RecyclerView.ViewHolder(itemBinding.root){
        fun dibuja(lugar: Lugar){
            itemBinding.tvNombre.text = lugar.nombre
            itemBinding.tvCorreo.text = lugar.correo
            itemBinding.tvTelefono.text = lugar.telefono

            Glide.with(itemBinding.root.context)
                .load(lugar.rutaImagen)
                .circleCrop()
                .into(itemBinding.imagen)

            // Me sirva para pasar la informacion de la caja de texto hacia UPDATELUGAR
            itemBinding.vistaFila.setOnClickListener{
                val action = LugarFragmentDirections.actionNavLugarToUpdateLugarFragment(lugar)
                itemView.findNavController().navigate(action)
            }
        }
    }

    // Lista donde se encuentran los objetos Lugar a dibujarse
    private var listaLugares = emptyList<Lugar>()

    // Esta funcion crea "cajitas para cada lugar en memoria"
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LugarViewHolder {
        // Crea vista de una cajita
        val itemBinding = LugarFilaBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false)
        return LugarViewHolder(itemBinding)
    }

    // Esta funcion toma un lugar y lo envia a dibujar
    override fun onBindViewHolder(holder: LugarViewHolder, position: Int) {
       val lugar = listaLugares[position]
        holder.dibuja(lugar)

    }

    // Esta funcion devuelve la cantidad de elementos a dibujar (cajitas)
    override fun getItemCount(): Int {
        return listaLugares.size
    }

    //
    fun setListaLugares(lugares: List<Lugar>){
        this.listaLugares = lugares
        notifyDataSetChanged()
    }

}