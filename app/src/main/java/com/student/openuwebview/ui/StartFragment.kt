package com.student.openuwebview.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.student.openuwebview.BaseFragment
import com.student.openuwebview.R
import com.student.openuwebview.databinding.FragmentStartBinding

class StartFragment : BaseFragment() {


    private lateinit var binding: FragmentStartBinding

    override var bottomNavigationViewVisibility = View.VISIBLE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_start, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStartBinding.bind(view)

        binding.OpenULogo.setOnClickListener {
            val action = StartFragmentDirections.actionStartFragmentToOpenUFragment()
            view.findNavController().navigate(action)
        }

        binding.btnLogin.setOnClickListener {
            val action = StartFragmentDirections.actionStartFragmentToLoginFragment()
            view.findNavController().navigate(action)
        }
        binding.tvRegister.setOnClickListener {
            val action = StartFragmentDirections.actionStartFragmentToRegisterFragment()
            view.findNavController().navigate(action)
        }
    }

}