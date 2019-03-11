package com.rockbass.bed2

import android.content.Context
import android.graphics.Bitmap
import org.opencv.android.Utils
import org.opencv.core.*
import org.opencv.objdetect.CascadeClassifier
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel
import java.util.*

class OpenCV private constructor(context: Context?){

    private object OpenCVConfig {
        private var openCV : OpenCV? = null

        fun Config(context :  Context?) : OpenCV{
            return openCV ?: {
                openCV = OpenCV(context)

                openCV as OpenCV
            }()
        }
    }

    companion object {
        fun getOpenCV(context: Context?) : OpenCV = OpenCVConfig.Config(context)
    }

    private lateinit var cascadeClassifier : CascadeClassifier
    private lateinit var interpreter : Interpreter
    private val ancho = 240
    private val alto = 240
    private val channel = 3
    private val byteSize = 4
    private val buffer : ByteBuffer = ByteBuffer.allocateDirect(ancho * alto * channel * byteSize)
    private val output = arrayOf(FloatArray(6))

    init {
        buildCascade(context)
        buffer.order(ByteOrder.nativeOrder())
        buildInterpreter(context)
    }

    private fun buildInterpreter(context: Context?){
        val assetFileDescriptor = context?.assets?.openFd("model.tflite")
        val fileInputStream = FileInputStream(assetFileDescriptor?.fileDescriptor)
        val fileChannel = fileInputStream.channel
        val startOffset = assetFileDescriptor?.startOffset ?: 0
        val declaredLength = assetFileDescriptor?.declaredLength ?: 0
        val byteBuffer = fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength) as ByteBuffer

        interpreter = Interpreter(byteBuffer)
    }

    private fun buildCascade(context: Context?){
        val inputStream = context?.assets?.open("haarcascade_frontalface_default.xml")
        val buffer: ByteArray? = inputStream?.readBytes()
        inputStream?.close()

        val fileTemp = File.createTempFile("cascade", ".xml")
        val output = FileOutputStream(fileTemp)
        output.write(buffer)
        output.close()

        cascadeClassifier = CascadeClassifier(fileTemp.absolutePath)
        cascadeClassifier.load(fileTemp.absolutePath)

        fileTemp.delete()
    }

    data class ResultDetectEmotion(val image : Bitmap, val calificacionEmociones : CalificacionEmociones, val rostroEncontrado : Boolean)

    fun markFaceAndDetectEmotion(bitmap : Bitmap) : ResultDetectEmotion{
        var calificacionEmociones : CalificacionEmociones? = null
        var rostroEncontrado : Boolean = false

        val rectangles = MatOfRect()

        val img = Mat(bitmap.height, bitmap.width, org.opencv.core.CvType.CV_8U)
        Utils.bitmapToMat(bitmap, img)

        cascadeClassifier.detectMultiScale(img, rectangles)

        if (rectangles.toArray().isNotEmpty()){
            rostroEncontrado = true
            val rectangle : Rect = rectangles.toArray()[0]

            val face = Mat(img,rectangle)
            org.opencv.imgproc.Imgproc.resize(face, face, Size(this.ancho.toDouble(), this.alto.toDouble()))
            calificacionEmociones = classyEmotion(face)

            org.opencv.imgproc.Imgproc.rectangle(img,
                Point(rectangle.x.toDouble(), rectangle.y.toDouble()),
                Point((rectangle.x+rectangle.width).toDouble(),(rectangle.y+rectangle.height).toDouble()),
                Scalar(125.0,255.0,135.0),
                5
            )
        }

        Utils.matToBitmap(img, bitmap)

        return ResultDetectEmotion(bitmap,
            calificacionEmociones ?: CalificacionEmociones(0f,0f,0f,0f,0f,0f,0f),
            rostroEncontrado)
    }

    data class CalificacionEmociones(
        val enojado : Float,
        val feliz : Float,
        val miedo : Float,
        val neutral : Float,
        val sorpresa : Float,
        val triste : Float,
        val mejor : Float
    )

    private fun classyEmotion(mat : Mat) : CalificacionEmociones{

        //Alistar el buffer
        buffer.rewind()
        var values : DoubleArray
        for ( row in 0 until mat.rows()){
            for ( col in 0 until mat.cols()){
                values = mat.get(row, col)
                buffer.putFloat(values[0].toFloat())
                buffer.putFloat(values[1].toFloat())
                buffer.putFloat(values[2].toFloat())
            }
        }

        Arrays.fill(output[0], 0f)
        interpreter.run(buffer, output)

        val finalProb = output[0]

        return CalificacionEmociones(
            finalProb[0],
            finalProb[1],
            finalProb[2],
            finalProb[3],
            finalProb[4],
            finalProb[5],
            argmax(finalProb).first.toFloat()
        )
    }

    private fun argmax(array : FloatArray) : Pair<Int, Float>{
        var best = -1
        var bestConfidence = 0.0f

        array.forEachIndexed {index, value ->
            if (value > bestConfidence){
                bestConfidence = value
                best = index
            }
        }

        return Pair(best, bestConfidence)
    }
}