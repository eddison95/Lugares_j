package com.lugares_j.ui.lugar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.lugares_j.R
import com.lugares_j.databinding.FragmentAddLugarBinding
import com.lugares_j.model.Lugar
import com.lugares_j.viewmodel.LugarViewModel

class AddLugarFragment : Fragment() {
  // el objeto para interactuar con la tabla : LugarViewModel
    private lateinit var lugarViewModel : LugarViewModel
    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         lugarViewModel =
            ViewModelProvider(this).get(LugarViewModel::class.java)

        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)
        //   val root: View = binding.root

        binding.btAdd.setOnClickListener{ addLugar()}
        return binding.root
    }
    private fun addLugar() { // Funcion que regisra un lugar en base de datos
        val nombre = binding.etNombre.text.toString() // Obtiene texto de lo que el usuarios escribe
        if (nombre.isNotEmpty()) {
            val correo = binding.etCorreo.text.toString() // Obtiene texto de lo que el usuarios escribe
            val telefono = binding.etTelefono.text.toString() // Obtiene texto de lo que el usuarios escribe
            val web = binding.etWeb.text.toString() // Obtiene texto de lo que el usuarios escribe

            val lugar = Lugar(0,nombre,correo,telefono,web,0.0,0.0,0.0,"","")

                // se registra el nuevo lugar
            lugarViewModel.saveLugar(lugar)

            Toast.makeText(requireContext(),
                getString(R.string.msg_lugar_added),
                Toast.LENGTH_SHORT).show()// Muestra mensaje en pantalla
            findNavController().navigate(R.id.action_addLugarFragment_to_nav_lugar)
        }else{ // No se puede registrar el lugar, falta informacion
            Toast.makeText(requireContext(),
                getString(R.string.msg_data),
                Toast.LENGTH_LONG).show()// Muestra mensaje en pantalla

        }


    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}