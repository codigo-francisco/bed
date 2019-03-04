package com.rockbass.bed

import android.app.Application
import org.opencv.android.OpenCVLoader
import ss.com.bannerslider.Slider

class ApplicationBase : Application(){

    override fun onCreate() {
        super.onCreate()

        OpenCVLoader.initDebug()

        Slider.init(HandleImage.getHandleImage(this))
    }

}