package com.rockbass.bed

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

class MainActivity : AppCompatActivity() {

    inner class PageAdapter(context: Context) : FragmentPagerAdapter(this.supportFragmentManager){
        private val names : List<String> = listOf(context.getString(R.string.base_de_datos_rafd))

        override fun getItem(position: Int): Fragment {
            return when(position){
                1 -> RafdViewFragment()
                else -> RafdViewFragment()
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return names[position]
        }

        override fun getCount(): Int {
            return names.size
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager : ViewPager = findViewById(R.id.viewPager)
        viewPager.adapter = PageAdapter(this)

        val tabLayout : TabLayout = findViewById(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)
    }
}
