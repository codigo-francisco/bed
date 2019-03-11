package com.rockbass.bed2

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import ss.com.bannerslider.Slider
import ss.com.bannerslider.adapters.SliderAdapter
import ss.com.bannerslider.event.OnSlideChangeListener
import ss.com.bannerslider.viewholder.ImageSlideViewHolder

//Extensión para imprimir porcentajes en los valores
fun Float.toPercentage() : String{
    return "%.2f".format(this)
}

class RafdViewFragment : Fragment() {

    private val TAG : String = "RafdViewFragment"

    private lateinit var openCV : OpenCV

    private val ROOT_FOLDER_RAFD = "rafd_images"

    private lateinit var textEnojado : TextView
    private lateinit var textMiedo : TextView
    private lateinit var textNeutral : TextView
    private lateinit var textSorpresa : TextView
    private lateinit var textTriste : TextView
    private lateinit var textFeliz : TextView

    inner class MainSliderAdapter : SliderAdapter(){

        private val imagenes : Array<String> = context?.assets?.list(ROOT_FOLDER_RAFD) ?: emptyArray()

        override fun getItemCount(): Int {
            return imagenes.size
        }

        override fun onBindImageSlide(position: Int, imageSlideViewHolder: ImageSlideViewHolder?) {
            imageSlideViewHolder?.bindImageSlide("${imagenes[position]}$position")
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        val view : View = inflater.inflate(R.layout.rafd_view_fragment, container, false)

        textEnojado = view.findViewById(R.id.textEnojado)
        textFeliz = view.findViewById(R.id.textFeliz)
        textMiedo = view.findViewById(R.id.textMiedo)
        textNeutral = view.findViewById(R.id.textNeutral)
        textSorpresa = view.findViewById(R.id.textSorpresa)
        textTriste = view.findViewById(R.id.textTriste)


        val slider : Slider = view.findViewById(R.id.sliderView)
        slider.setAdapter(MainSliderAdapter())
        slider.onSlideChangeListener = object : OnSlideChangeListener{
            override fun onSlideChange(position: Int) {
                //Traer resultados
                val results = HandleImage.getHandleImage(context as Context).results[position]
                colocarTextos(results.second)
            }
        }

        HandleImage.getHandleImage(context as Context).onChangeResult { result,index ->
            if (slider.selectedSlidePosition==index) {
                //Rostro detectado
                if (result.first) {
                    colocarTextos(result.second)
                }
            }
        }

        return view
    }

    fun colocarTextos(calificaciones : OpenCV.CalificacionEmociones){
        with(calificaciones) {
            textEnojado.text = enojado.toPercentage()
            textFeliz.text = feliz.toPercentage()
            textMiedo.text = miedo.toPercentage()
            textNeutral.text = neutral.toPercentage()
            textSorpresa.text = sorpresa.toPercentage()
            textTriste.text = triste.toPercentage()
        }
    }

}
