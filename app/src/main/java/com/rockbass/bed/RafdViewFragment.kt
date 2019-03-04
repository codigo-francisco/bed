package com.rockbass.bed

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ss.com.bannerslider.Slider
import ss.com.bannerslider.adapters.SliderAdapter
import ss.com.bannerslider.event.OnSlideChangeListener
import ss.com.bannerslider.viewholder.ImageSlideViewHolder


class RafdViewFragment : Fragment() {

    private lateinit var rafdViewModel: RafdViewModel

    private val ROOT_FOLDER_RAFD = "rafd_images"

    inner class MainSliderAdapter : SliderAdapter(){

        private val imagenes : Array<String> = context?.assets?.list(ROOT_FOLDER_RAFD) ?: emptyArray()

        override fun getItemCount(): Int {
            return imagenes.size
        }

        override fun onBindImageSlide(position: Int, imageSlideViewHolder: ImageSlideViewHolder?) {
            imageSlideViewHolder?.bindImageSlide(imagenes[position])
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.rafd_view_fragment, container, false)

        val slider : Slider = view.findViewById(R.id.sliderView)
        slider.setAdapter(MainSliderAdapter())
        slider.onSlideChangeListener = OnSlideChangeListener { index : Int ->
            
        }

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        rafdViewModel = ViewModelProviders.of(this).get(RafdViewModel::class.java)
        // TODO: Use the ViewModel

    }

}
