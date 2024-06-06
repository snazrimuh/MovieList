package com.simple.movielist.worker

import android.content.Context
import android.os.Environment
import java.io.File
import java.io.FileOutputStream

object FileUtil {
    fun saveBitmapToFile(context: Context, byteArray: ByteArray, fileName: String): File {
        val directory = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "blurred_images")
        if (!directory.exists()) {
            directory.mkdirs()
        }
        val file = File(directory, fileName)
        FileOutputStream(file).use { output ->
            output.write(byteArray)
        }
        return file
    }
}
