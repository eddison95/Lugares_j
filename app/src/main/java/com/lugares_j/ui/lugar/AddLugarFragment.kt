package com.lugares_j.ui.lugar

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.lugares.utiles.AudioUtiles
import com.lugares.utiles.ImagenUtiles
import com.lugares_j.R
import com.lugares_j.databinding.FragmentAddLugarBinding
import com.lugares_j.model.Lugar
import com.lugares_j.viewmodel.LugarViewModel


class AddLugarFragment : Fragment() {
  // el objeto para interactuar con la tabla : LugarViewModel
    private lateinit var lugarViewModel : LugarViewModel
    private var _binding: FragmentAddLugarBinding? = null
    private val binding get() = _binding!!

    private lateinit var  audioUtiles: AudioUtiles
    private lateinit var imagenUtiles: ImagenUtiles
    private lateinit var tomarFotoActivity: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         lugarViewModel =
            ViewModelProvider(this).get(LugarViewModel::class.java)

        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)
        //   val root: View = binding.root

        binding.btAdd.setOnClickListener{
            binding.progressBar.visibility = ProgressBar.VISIBLE
            binding.msgMensaje.text = getString(R.string.msg_subiendo_audio)
            binding.msgMensaje.visibility = TextView.VISIBLE
            subeNota()
        }

        activaGPS()

        audioUtiles = AudioUtiles(  // logica para tomar foto
            requireActivity(),
            requireContext(),
            binding.btAccion,
            binding.btPlay,
            binding.btDelete,
            getString(R.string.msg_graba_audio),
            getString(R.string.msg_detener_audio))

        tomarFotoActivity = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == Activity.RESULT_OK){
                imagenUtiles.actualizaFoto()
            }
        }

        imagenUtiles = ImagenUtiles(
            requireContext(),
            binding.btPhoto,
            binding.btRotaL,
            binding.btRotaR,
            binding.imagen,
            tomarFotoActivity)

        return binding.root
    }

    private fun subeNota() { // Esta funcion sube la nota de audio al storage y pasa la ruta publica del archivo a la siguiente funcion
        val archivoLocal = audioUtiles.audioFile
        if (archivoLocal.exists() && archivoLocal.isFile && archivoLocal.canRead()){
            val rutaLocal = Uri.fromFile(archivoLocal) // Se obtiene la ruta del archivo local del audio

            val rutaNube = "lugaresApp/${Firebase.auth.currentUser?.email}/audios/${archivoLocal.name}" // genera ruta en la nube donde se almacena audio por usuario

            val referencia: StorageReference = Firebase.storage.reference.child(rutaNube)

            referencia.putFile(rutaLocal).addOnSuccessListener {             // Sube archivo a la nube
                referencia.downloadUrl
                    .addOnSuccessListener {
                        val rutaAudio = it.toString()    // Se obtiene la ruta publica del archivo
                    subeImagen(rutaAudio)
                    }
            }
                .addOnFailureListener {
                    subeImagen("")    // en caso de error entonces sube una ruta vacia
                }

        }else{  // no existe foto o error en archivo o no se puede leer
            subeImagen("")
        }
    }

    private fun subeImagen(rutaAudio: String) {
        binding.msgMensaje.text = getString(R.string.msg_subiendo_imagen) // muestra mensaje con progreso

        val archivoLocal = imagenUtiles.imagenFile
        if (archivoLocal.exists() && archivoLocal.isFile && archivoLocal.canRead()){
            val rutaLocal = Uri.fromFile(archivoLocal) // Se obtiene la ruta del archivo local de la imagen o foto

            val rutaNube = "lugaresApp/${Firebase.auth.currentUser?.email}/imagenes/${archivoLocal.name}" // genera ruta en la nube donde se almacena audio por usuario

            val referencia: StorageReference = Firebase.storage.reference.child(rutaNube)

            referencia.putFile(rutaLocal).addOnSuccessListener {             // Sube archivo a la nube
                referencia.downloadUrl
                    .addOnSuccessListener {
                        val rutaimagen = it.toString()    // Se obtiene la ruta publica del archivo
                        addLugar(rutaAudio,rutaimagen)
                    }
            }
                .addOnFailureListener {
                    addLugar(rutaAudio,"")// caso de error entonces sube una ruta vacia
                }

        }else{  // no existe foto o error en archivo o no se puede leer
            addLugar(rutaAudio,"")
        }

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


    private fun addLugar(rutaAudio: String, rutaimagen: String) { // Funcion que regisra un lugar en base de datos
        binding.msgMensaje.text = getString(R.string.msg_subiendo_lugar)
        val nombre = binding.etNombre.text.toString() // Obtiene texto de lo que el usuarios escribe
        if (nombre.isNotEmpty()) {
            val correo = binding.etCorreo.text.toString() // Obtiene texto de lo que el usuarios escribe
            val telefono = binding.etTelefono.text.toString() // Obtiene texto de lo que el usuarios escribe
            val web = binding.etWeb.text.toString() // Obtiene texto de lo que el usuarios escribe
            val latitud = binding.tvLatitud.text.toString().toDouble()
            val longitud = binding.tvLongitud.text.toString().toDouble()
            val altura = binding.tvAltura.text.toString().toDouble()
            val lugar = Lugar("",nombre,correo,telefono,web,latitud,longitud,altura,rutaAudio,rutaimagen)

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