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
    lateinit var ultimoResultado : OpenCV.ResultDetectEmotion
    private lateinit var callbackChangeResult : (result : OpenCV.ResultDetectEmotion) -> Unit


    fun onChangeResult(callback : (result : OpenCV.ResultDetectEmotion) -> Unit){
        callbackChangeResult = callback
    }

    override fun loadImage(url: String?, imageView: ImageView?) {
        //No es una url, es una cadena para los recursos
        val inputStream = context.assets?.open("rafd_images/$url")
        val bitMap = BitmapFactory.decodeStream(inputStream)

        ultimoResultado = openCV.markFaceAndDetectEmotion(bitMap)

        this@HandleImage.callbackChangeResult(ultimoResultado)

        imageView?.setImageBitmap(ultimoResultado.image)
        imageView?.contentDescription = url
    }

    override fun loadImage(resource: Int, imageView: ImageView?) {
    }

    override fun loadImage(url: String?, placeHolder: Int, errorDrawable: Int, imageView: ImageView?) {
    }

}