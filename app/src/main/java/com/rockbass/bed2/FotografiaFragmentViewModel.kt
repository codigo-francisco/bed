package com.rockbass.bed2

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class FotografiaFragmentViewModel(application : Application) : AndroidViewModel(application){

    private val data : MutableLiveData<OpenCV.ResultDetectEmotion> = MutableLiveData()
    private val openCV  = OpenCV.getOpenCV(application)

    fun receivePhoto(bitMap : Bitmap){
        data.value = openCV.markFaceAndDetectEmotion(bitMap)
    }

    fun getPredictions() : LiveData<OpenCV.ResultDetectEmotion>{
        return data
    }
}