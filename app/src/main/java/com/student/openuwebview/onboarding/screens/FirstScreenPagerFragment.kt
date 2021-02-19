package com.student.openuwebview.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.student.openuwebview.R



class FirstScreenPagerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_first_screen_pager, container, false)
        val binding = com.student.openuwebview.databinding.FragmentFirstScreenPagerBinding.bind(view)
        // mb viewpager 1
        (activity as AppCompatActivity).supportActionBar?.hide()

        val viewPager = activity?.findViewById<ViewPager2>(R.id.view_pager)
        binding.btnFirstScreenNex.setOnClickListener {
            viewPager?.currentItem = 1
        }
        return view
    }


}