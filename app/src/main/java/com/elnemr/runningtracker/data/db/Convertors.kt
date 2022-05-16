package com.elnemr.runningtracker.data.db

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class Convertors {

    @TypeConverter
    fun fromBitmap(btm: Bitmap): ByteArray{
        val outPutStream = ByteArrayOutputStream()
        btm.compress(Bitmap.CompressFormat.PNG, 100, outPutStream)
        return outPutStream.toByteArray()
    }

    @TypeConverter
    fun toBitmap(bytes: ByteArray): Bitmap{
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}