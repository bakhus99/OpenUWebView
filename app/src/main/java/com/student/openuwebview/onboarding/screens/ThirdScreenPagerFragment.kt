package com.student.openuwebview.onboarding.screens

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.student.openuwebview.R
import com.student.openuwebview.databinding.FragmentThirdScreenPagerBinding


class ThirdScreenPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_third_screen_pager, container, false)
        val binding = FragmentThirdScreenPagerBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.hide()

        binding.finish.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_startFragment)
            onBoardingFinished()
        }


        return view
    }


    private fun onBoardingFinished(){

        val sharePref = requireActivity().getSharedPreferences("onBoarding", Context.MODE_PRIVATE)
        val editor = sharePref.edit()
        editor.putBoolean("Finished",true)
        editor.apply()

    }


}