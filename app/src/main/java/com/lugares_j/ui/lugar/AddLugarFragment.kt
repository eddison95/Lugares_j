package com.lugares_j.ui.lugar

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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

        activaGPS()
        return binding.root
    }
    private fun activaGPS() {
        if(requireActivity()
                .checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)!=
                    PackageManager.PERMISSION_GRANTED
            &&
            requireActivity()
                .checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)!=
            PackageManager.PERMISSION_GRANTED) {
            // Si estamos aqui entonces hay que perdir permiso para acceder al GPS
            requireActivity()
                .requestPermissions(
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION),105)

        }else{
            // si se tienen los permisos entonces se busca la ubicacion
            val ubicacion: FusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext())
             ubicacion.lastLocation.addOnSuccessListener {
                 location: Location?->
                 if(location != null){
                     binding.tvLatitud.text = "${location.latitude}"
                     binding.tvLongitud.text = "${location.longitude}"
                     binding.tvAltura.text = "${location.altitude}"
                 }else{
                     binding.tvLatitud.text = "0.0"
                     binding.tvLongitud.text = "0.0"
                     binding.tvAltura.text = "0.0"
                 }
             }

        }
    }



    private fun addLugar() { // Funcion que regisra un lugar en base de datos
        val nombre = binding.etNombre.text.toString() // Obtiene texto de lo que el usuarios escribe
        if (nombre.isNotEmpty()) {
            val correo = binding.etCorreo.text.toString() // Obtiene texto de lo que el usuarios escribe
            val telefono = binding.etTelefono.text.toString() // Obtiene texto de lo que el usuarios escribe
            val web = binding.etWeb.text.toString() // Obtiene texto de lo que el usuarios escribe
            val latitud = binding.tvLatitud.text.toString().toDouble()
            val longitud = binding.tvLongitud.text.toString().toDouble()
            val altura = binding.tvAltura.text.toString().toDouble()
            val lugar = Lugar("",nombre,correo,telefono,web,latitud,longitud,altura,"","")

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