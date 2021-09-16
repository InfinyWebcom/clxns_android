package com.clxns.app.ui.cases

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.clxns.app.databinding.FragmentCasesBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CasesFragment : Fragment() {

    private lateinit var casesViewModel: CasesViewModel
    private var _binding: FragmentCasesBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        casesViewModel =
            ViewModelProvider(this).get(CasesViewModel::class.java)

        _binding = FragmentCasesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textView: TextView = binding.textNotifications
        casesViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}