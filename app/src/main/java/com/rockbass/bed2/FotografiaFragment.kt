package com.rockbass.bed2


import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import java.io.File

class FotografiaFragment : Fragment() {

    private val REQUEST_IMAGE = 1
    private val REQUEST_IMAGE_CAPTURE = 2

    private lateinit var textEnojado : TextView
    private lateinit var textMiedo : TextView
    private lateinit var textNeutral : TextView
    private lateinit var textSorpresa : TextView
    private lateinit var textTriste : TextView
    private lateinit var textFeliz : TextView
    private lateinit var imageview : ImageView
    private lateinit var viewModel : FotografiaFragmentViewModel
    private var photo : File? = null
    //private val openCV = OpenCV.getOpenCV(context)

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

        viewModel = ViewModelProviders.of(this).get(FotografiaFragmentViewModel::class.java)

        viewModel.getPredictions().observe(this,
            Observer<OpenCV.ResultDetectEmotion> {
                imageview.setImageBitmap(it.image)
                colocarTextos(it.calificacionEmociones)
            }
        )

        val buttonSeleccionar : Button = view.findViewById(R.id.button_seleccionar_imagen)
        buttonSeleccionar.setOnClickListener {
            Intent(Intent.ACTION_GET_CONTENT).also {
                it.type ="image/*"
                startActivityForResult(Intent.createChooser(it, "Seleccione una imagen"), REQUEST_IMAGE)
            }
        }

        val buttonFotografia : Button = view.findViewById(R.id.button_tomar_fotografia)
        buttonFotografia.setOnClickListener{
            context?.also {
                context ->
                Intent(MediaStore.ACTION_IMAGE_CAPTURE).also {
                        takePictureIntent ->
                    context.packageManager?.also {
                            packageManager ->
                        takePictureIntent.resolveActivity(packageManager)?.also{
                            photo = File.createTempFile("image",".jpg")?.also {
                                    file ->
                                val photoURI = FileProvider.getUriForFile(context,
                                    "com.rockbass.bed2.android.fileprovider",
                                    file)
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                            }
                        }
                    }
                }
            }

        }

        return view
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == REQUEST_IMAGE){
            if (resultCode == Activity.RESULT_OK) {
                val bitMap = MediaStore.Images.Media.getBitmap(context?.contentResolver, intent?.data)

                bitMap?.let {
                    viewModel.receivePhoto(bitMap)
                }
            }
        }else if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == Activity.RESULT_OK){
                photo?.also {
                    photo ->
                    ExifInterface(photo.absolutePath).let {
                        objInt ->
                        var originalBitmap = BitmapFactory.decodeFile(photo.absolutePath)
                        val attributeOrientation = objInt.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
                        if (attributeOrientation != ExifInterface.ORIENTATION_NORMAL) {
                            val matrix = Matrix()
                            when (attributeOrientation) {
                                ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.setScale(-1f, 1f)
                                ExifInterface.ORIENTATION_ROTATE_180 -> matrix.setRotate(180f)
                                ExifInterface.ORIENTATION_FLIP_VERTICAL -> {
                                    matrix.setRotate(180f)
                                    matrix.postScale(-1f,1f)
                                }
                                ExifInterface.ORIENTATION_TRANSPOSE ->{
                                    matrix.setRotate(90f)
                                    matrix.postScale(-1f,1f)
                                }
                                ExifInterface.ORIENTATION_ROTATE_90 -> matrix.setRotate(90f)
                                ExifInterface.ORIENTATION_TRANSVERSE ->{
                                    matrix.setRotate(90f)
                                    matrix.postScale(-1f,1f)
                                }
                                ExifInterface.ORIENTATION_ROTATE_270 -> matrix.setRotate(-90f)
                            }
                            val nuevoBitmap = Bitmap.createBitmap(originalBitmap,0,0,originalBitmap.width,originalBitmap.height,matrix,true)
                            originalBitmap.recycle()
                            originalBitmap = nuevoBitmap
                        }

                        viewModel.receivePhoto(originalBitmap)
                    }
                }
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