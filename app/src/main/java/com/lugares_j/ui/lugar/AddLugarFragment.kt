package com.lugares_j.ui.lugar

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.lugares_j.databinding.FragmentAddLugarBinding
import com.lugares_j.databinding.FragmentLugarBinding
import com.lugares_j.viewmodel.LugarViewModel

class AddLugarFragment : Fragment() {

    private var _binding: FragmentAddLugarBinding? = null

    // This pr
    // operty is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(LugarViewModel::class.java)

        _binding = FragmentAddLugarBinding.inflate(inflater, container, false)
        //   val root: View = binding.root


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}