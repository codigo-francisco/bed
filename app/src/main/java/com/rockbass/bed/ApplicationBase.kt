package com.rockbass.bed

import android.app.Application
import android.graphics.BitmapFactory
import android.widget.ImageView
import ss.com.bannerslider.ImageLoadingService
import ss.com.bannerslider.Slider

class ApplicationBase : Application(){

    override fun onCreate() {
        super.onCreate()

        Slider.init(object : ImageLoadingService {
            override fun loadImage(url: String?, imageView: ImageView?) {
                //No es una url, es una cadena para los recursos
                val inputStream = assets?.open("rafd_images/$url")
                val bitMap = BitmapFactory.decodeStream(inputStream)
                imageView?.setImageBitmap(bitMap)
            }

            override fun loadImage(resource: Int, imageView: ImageView?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun loadImage(url: String?, placeHolder: Int, errorDrawable: Int, imageView: ImageView?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

        })
    }

}