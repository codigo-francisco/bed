package com.rockbass.bed2

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import org.opencv.android.OpenCVLoader

class MainActivity : AppCompatActivity() {

    inner class PageAdapter(context: Context) : FragmentPagerAdapter(this.supportFragmentManager){
        private val names : List<String> = listOf(*context.resources.getStringArray(R.array.menu))

        override fun getItem(position: Int): Fragment {
            return when(position){
                0 -> FotografiaFragment()
                1 -> RafdViewFragment()
                else -> FotografiaFragment()
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
        verificarOpenCV()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewPager : ViewPager = findViewById(R.id.viewPager)
        viewPager.adapter = PageAdapter(this)

        val tabLayout : TabLayout = findViewById(R.id.tabLayout)
        tabLayout.setupWithViewPager(viewPager)
    }

    private fun verificarOpenCV(){
        if (!OpenCVLoader.initDebug()){
            AlertDialog.Builder(this)
                .setTitle(R.string.titulo_error_opencv)
                .setMessage(R.string.mensaje_error_opencv)
                .setOnDismissListener { android.os.Process.killProcess(android.os.Process.myPid()) }
                .show()
        }
    }
}
