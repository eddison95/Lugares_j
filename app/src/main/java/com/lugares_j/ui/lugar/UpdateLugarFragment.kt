package com.lugares_j.ui.lugar

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.lugares_j.R
import com.lugares_j.databinding.FragmentUpdateLugarBinding
import com.lugares_j.model.Lugar
import com.lugares_j.viewmodel.LugarViewModel

class UpdateLugarFragment : Fragment() {
    // se define un objeto para obtener los argumentos o datos del fragmento
    private val args by navArgs<UpdateLugarFragmentArgs>()   // Obtiene acceso a todos los argumentos


  // el objeto para interactuar con la tabla : LugarViewModel
    private lateinit var lugarViewModel : LugarViewModel
    private var _binding: FragmentUpdateLugarBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
         lugarViewModel =
            ViewModelProvider(this).get(LugarViewModel::class.java)

        _binding = FragmentUpdateLugarBinding.inflate(inflater, container, false)
        //   val root: View = binding.root

      //  Obtengo informacion de los argumentos para mostrarlos
        binding.etNombre.setText ( args.lugar.nombre)
        binding.etCorreo.setText ( args.lugar.correo)
        binding.etTelefono.setText ( args.lugar.telefono)
        binding.etWeb.setText ( args.lugar.web)
        binding.tvLongitud.text = args.lugar.longitud.toString()
        binding.tvLatitud.text = args.lugar.latitud.toString()
        binding.tvAltura.text = args.lugar.altura.toString()



        binding.btUpdate.setOnClickListener{ updateLugar()}  // Actualiza
        binding.btDelete.setOnClickListener{deleteLugar()}  // Elimina

        binding.btEmail.setOnClickListener{escribirCorreo()}
        binding.btPhone.setOnClickListener{llamarLugar()}
        binding.btWhatsapp.setOnClickListener{enviarWhatsapp()}
        binding.btWeb.setOnClickListener{verWeb()}
        binding.btLocation.setOnClickListener{verEnMapa()}

        return binding.root
    }

    private fun verEnMapa() {
        val latitud = binding.tvLatitud.text.toString().toDouble()
        val longitud = binding.tvLongitud.text.toString().toDouble()

        if (latitud.isFinite() && longitud.isFinite()) { // verifica si se tienen valores reales en las coordenadas

            val uri = "geo:$latitud,$longitud?z18"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }else{ // Si no hay informacion no se puede realizae la accion
            Toast.makeText(requireContext(),
                getString(R.string.msg_data),Toast.LENGTH_LONG).show()
        }
    }

    private fun verWeb() {
        val valor = binding.etWeb.text.toString()
        if (valor.isNotEmpty()) { // si el sitio web  no es vacio, entonces se intenta abrir navegador con la direccion

            val uri = "http://$valor"
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            startActivity(intent)
        }else{ // Si no hay informacion no se puede realizae la accion
            Toast.makeText(requireContext(),
                getString(R.string.msg_data),Toast.LENGTH_LONG).show()
        }
    }

    private fun enviarWhatsapp() {
        val valor = binding.etTelefono.text.toString()
        if (valor.isNotEmpty()) { // si el telefono no es vacio, entonces se intenta enviar mensaje por whatsapp
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = "whatsapp://send?phone=506$valor&text="+
                getString(R.string.msg_saludos)
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(uri)

            startActivity(intent)
        }else{ // Si no hay informacion no se puede realizae la accion
            Toast.makeText(requireContext(),
                getString(R.string.msg_data),Toast.LENGTH_LONG).show()
        }

    }

    private fun llamarLugar() {
        val valor = binding.etTelefono.text.toString()
        if (valor.isNotEmpty()) { // si el numero no esta vacio se intenta envia el mensaje
            //llamar los recursos que tiene el celular -- mapas, correos, mensajes, llamadas etc

            val intent = Intent(Intent.ACTION_CALL)
            intent.data = Uri.parse("tel:$valor")

            if (requireActivity()
                    .checkSelfPermission(android.Manifest.permission.CALL_PHONE) !=
                PackageManager.PERMISSION_GRANTED
            ) {
                //SI ESTAMOS ACA HAY QUE PEDIR UBICACION
                requireActivity()
                    .requestPermissions(
                        arrayOf(
                            android.Manifest.permission.CALL_PHONE), 105)
            }else{
                // si se tiene el permiso de hacer la llamada
                requireActivity().startActivity(intent)
            }
        } else { // si no hay informacion no se realiza la accion
            Toast.makeText(requireContext(), getString(R.string.msg_data), Toast.LENGTH_LONG).show()
        }
    }

    private fun escribirCorreo() {
       val valor = binding.etCorreo.text.toString()
        if (valor.isNotEmpty()) { // si el correo no es vacio, entonces se intenta enviar correo
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"    // Significa correo electronico
            intent.putExtra(Intent.EXTRA_EMAIL, arrayOf(valor))
            intent.putExtra(Intent.EXTRA_SUBJECT,
            getString(R.string.msg_saludos)+ " "+binding.etNombre.text)
            intent.putExtra(Intent.EXTRA_TEXT,
            getString(R.string.msg_mensaje_correo))
            startActivity(intent)
        }else{ // Si no hay informacion no se puede realizae la accion
            Toast.makeText(requireContext(),
            getString(R.string.msg_data),Toast.LENGTH_LONG).show()
        }

    }

    private fun deleteLugar() {
       val alerta = AlertDialog.Builder(requireContext())

        alerta.setTitle(R.string.bt_delete_lugar)
        alerta.setMessage(getString(R.string.msg_pregunta_delete)) //+" ${args.lugar.nombre}")
        alerta.setPositiveButton(getString(R.string.msg_si)){ _,_->
            lugarViewModel.deleteLugar(args.lugar)  // Efectivamente borra el lugar
            Toast.makeText(requireContext(),getString(R.string.msg_lugar_deleted),Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
        }
        alerta.setNegativeButton(getString(R.string.msg_no)){ _,_-> } // En caso de que selecciona NO
        alerta.create().show()

       /// lugarViewModel.deleteLugar(args.lugar)
       // lugarViewModel.deleteLugar(args.lugar)  // Efectivamente borra el lugar
       // findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)

    }

    private fun updateLugar() { // Funcion que regisra un lugar en base de datos
        val nombre = binding.etNombre.text.toString() // Obtiene texto de lo que el usuarios escribe
        if (nombre.isNotEmpty()) {
            val correo = binding.etCorreo.text.toString() // Obtiene texto de lo que el usuarios escribe
            val telefono = binding.etTelefono.text.toString() // Obtiene texto de lo que el usuarios escribe
            val web = binding.etWeb.text.toString() // Obtiene texto de lo que el usuarios escribe

            // id es distinto de 0 entonces actualiza en lugar de insertar
            val lugar = Lugar(args.lugar.id,nombre,correo,telefono,web,
                                args.lugar.latitud,args.lugar.longitud,
                                args.lugar.altura,args.lugar.rutaAudio,
                                args.lugar.rutaImagen)

                // se registra el nuevo lugar
            lugarViewModel.saveLugar(lugar)

            Toast.makeText(requireContext(),
                getString(R.string.msg_lugar_updated),
                Toast.LENGTH_SHORT).show()// Muestra mensaje en pantalla
            findNavController().navigate(R.id.action_updateLugarFragment_to_nav_lugar)
        }else{ // No se puede actualizar el lugar, falta informacion
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