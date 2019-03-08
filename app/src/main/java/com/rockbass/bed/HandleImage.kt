package com.rockbass.bed

import android.content.Context
import android.graphics.BitmapFactory
import android.widget.ImageView
import ss.com.bannerslider.ImageLoadingService

class HandleImage private constructor(private val context: Context) : ImageLoadingService {

    object HandleImageConfig{
        private var handleImage : HandleImage? = null

        fun getHandleImage(context: Context) : HandleImage{
            return handleImage?:{
                handleImage = HandleImage(context)
                handleImage as HandleImage
            }()
        }
    }

    companion object {
        fun getHandleImage(context: Context) = HandleImageConfig.getHandleImage(context)
    }

    private val openCV = OpenCV.getOpenCV(context)
    private lateinit var callbackChangeResult : (result :Pair<Boolean, OpenCV.CalificacionEmociones>, index : Int) -> Unit
    val results : Array<Pair<Boolean, OpenCV.CalificacionEmociones>> = Array(6) {Pair(false,
        OpenCV.CalificacionEmociones(0f,0f,0f,0f,0f,0f,0f))}

    fun getResult(index: Int) : Pair<Boolean, OpenCV.CalificacionEmociones> {
        return results[index]
    }

    fun onChangeResult(callback : (result : Pair<Boolean, OpenCV.CalificacionEmociones>, index : Int) -> Unit){
        callbackChangeResult = callback
    }

    override fun loadImage(url: String?, imageView: ImageView?) {
        //No es una url, es una cadena para los recursos
        //Se extra el indice
        val index = url!!.last().toString().toInt()
        val realUrl = url.removeRange(url.lastIndex,url.lastIndex+1)
        val inputStream = context.assets?.open("rafd_images/$realUrl")
        val bitMap = BitmapFactory.decodeStream(inputStream)

        val ultimoResultado = openCV.markFaceAndDetectEmotion(bitMap)

        results[index] = Pair(ultimoResultado.rostroEncontrado, ultimoResultado.calificacionEmociones)

        callbackChangeResult(results[index], index)

        imageView?.setImageBitmap(ultimoResultado.image)
        //imageView?.contentDescription = url
    }

    override fun loadImage(resource: Int, imageView: ImageView?) {
    }

    override fun loadImage(url: String?, placeHolder: Int, errorDrawable: Int, imageView: ImageView?) {
    }

}