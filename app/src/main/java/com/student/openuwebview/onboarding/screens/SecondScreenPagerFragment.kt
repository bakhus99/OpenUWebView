package com.student.openuwebview.onboarding.screens

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.student.openuwebview.R
import com.student.openuwebview.databinding.FragmentSecondScreenPagerBinding


class SecondScreenPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_second_screen_pager, container, false)
        val binding = FragmentSecondScreenPagerBinding.bind(view)
        (activity as AppCompatActivity).supportActionBar?.hide()
        val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager)
        binding.btnSecondScreenNex.setOnClickListener {
            viewPager?.currentItem = 2
        }

        return view
    }


}