package com.student.openuwebview.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.student.openuwebview.BaseFragment
import com.student.openuwebview.R
import com.student.openuwebview.databinding.ViewPagerFragmentBinding
import com.student.openuwebview.onboarding.screens.FirstScreenPagerFragment
import com.student.openuwebview.onboarding.screens.SecondScreenPagerFragment
import com.student.openuwebview.onboarding.screens.ThirdScreenPagerFragment

class ViewPagerFragment : BaseFragment() {

    override var bottomNavigationViewVisibility = View.GONE

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.view_pager_fragment, container, false)
        val fragmentList = arrayListOf<Fragment>(
            FirstScreenPagerFragment(),
            SecondScreenPagerFragment(),
            ThirdScreenPagerFragment()
        )
        val binding = ViewPagerFragmentBinding.bind(view)


        val adapter = ViewPagerAdapter(
            fragmentList,
            requireActivity().supportFragmentManager,
            lifecycle
        )

        binding.viewPager.adapter = adapter

        return view
    }

}