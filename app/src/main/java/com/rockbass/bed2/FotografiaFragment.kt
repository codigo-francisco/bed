package com.rockbass.bed2


import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView

private val REQUEST_IMAGE = 1

class FotografiaFragment : Fragment() {

    private lateinit var textEnojado : TextView
    private lateinit var textMiedo : TextView
    private lateinit var textNeutral : TextView
    private lateinit var textSorpresa : TextView
    private lateinit var textTriste : TextView
    private lateinit var textFeliz : TextView
    private lateinit var imageview : ImageView
    private val openCV = OpenCV.getOpenCV(context)

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?,
                               savedInstanceState: Bundle? ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_fotografia, container, false)

        textEnojado = view.findViewById(R.id.textEnojado)
        textFeliz = view.findViewById(R.id.textFeliz)
        textMiedo = view.findViewById(R.id.textMiedo)
        textNeutral = view.findViewById(R.id.textNeutral)
        textSorpresa = view.findViewById(R.id.textSorpresa)
        textTriste = view.findViewById(R.id.textTriste)

        imageview = view.findViewById(R.id.fotografia_imageview)

        val buttonSeleccionar : Button = view.findViewById(R.id.button_seleccionar_imagen)
        buttonSeleccionar.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"

            startActivityForResult(Intent.createChooser(intent, "Seleccione una iamgen"), REQUEST_IMAGE)
        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_IMAGE){
            if (resultCode == Activity.RESULT_OK) {
                var bitMap : Bitmap? = MediaStore.Images.Media.getBitmap(context?.contentResolver, intent?.data)

                bitMap?.let {
                    val result = openCV.markFaceAndDetectEmotion(it)
                    if (result.rostroEncontrado) {
                        bitMap = result.image
                    }
                    colocarTextos(calificaciones = result.calificacionEmociones)
                }

                imageview.setImageBitmap(bitMap)
            }
        }
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