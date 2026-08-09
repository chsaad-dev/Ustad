package com.ustad.core.media

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ImageCompressor @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun compressImage(imageFile: File, targetSizeKb: Int = 800): File {
        var quality = 90
        val bitmap = BitmapFactory.decodeFile(imageFile.absolutePath) ?: return imageFile
        var stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        
        while (stream.toByteArray().size / 1024 > targetSizeKb && quality > 10) {
            quality -= 10
            stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        }
        
        val compressedFile = File(context.cacheDir, "compressed_${imageFile.name}")
        val fos = FileOutputStream(compressedFile)
        fos.write(stream.toByteArray())
        fos.flush()
        fos.close()
        return compressedFile
    }
}
