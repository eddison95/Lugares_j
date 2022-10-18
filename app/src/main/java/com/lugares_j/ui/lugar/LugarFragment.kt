package com.lugares_j.ui.lugar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.lugares_j.R
import com.lugares_j.adapter.LugarAdapter
import com.lugares_j.databinding.FragmentLugarBinding
import com.lugares_j.viewmodel.LugarViewModel

class LugarFragment : Fragment() {

    private var _binding: FragmentLugarBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val lugarViewModel =
            ViewModelProvider(this).get(LugarViewModel::class.java)

        _binding = FragmentLugarBinding.inflate(inflater, container, false)
     //   val root: View = binding.root
        binding.addLugarFabButton.setOnClickListener {
               findNavController().navigate(R.id.action_nav_lugar_to_addLugarFragment) // Boton de agregar redirije a pantalla para insertar
        }

        // Se genera el recicler view para ver la informacion
        val lugarAdapter = LugarAdapter()
        val reciclador = binding.reciclador  // reciclador es el elemento de la vista fragment_lugar
        reciclador.adapter = lugarAdapter
        reciclador.layoutManager = LinearLayoutManager(requireContext())  // muestra informacion para abajo

        lugarViewModel.getLugares.observe(viewLifecycleOwner){
            lugares ->lugarAdapter.setListaLugares(lugares)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}