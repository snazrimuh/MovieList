package com.simple.movielist.worker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import java.io.ByteArrayOutputStream

class BlurWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    companion object {
        const val TAG = "BlurWorker"
        const val KEY_IMAGE_URI = "IMAGE_URI"
        const val BLUR_LEVEL = 25
    }

    override suspend fun doWork(): Result {
        val imageUriString = inputData.getString(KEY_IMAGE_URI)
        return try {
            if (!imageUriString.isNullOrEmpty()) {
                val applicationContext = applicationContext
                val resolver = applicationContext.contentResolver
                val inputStream = resolver.openInputStream(Uri.parse(imageUriString))
                val bitmap = BitmapFactory.decodeStream(inputStream)

                val blurredBitmap = blurBitmap(bitmap, BLUR_LEVEL)
                val blurredImageUri = writeBitmapToFile(applicationContext, blurredBitmap)

                val outputData = workDataOf(KEY_IMAGE_URI to blurredImageUri.toString())
                Result.success(outputData)
            } else {
                Log.e(TAG, "Invalid image URI")
                Result.failure()
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error applying blur to image", e)
            Result.failure()
        }
    }

    private fun blurBitmap(inputBitmap: Bitmap, blurLevel: Int): Bitmap {
        val outputBitmap = Bitmap.createBitmap(inputBitmap)
        val rs = android.renderscript.RenderScript.create(applicationContext)
        val theIntrinsic = android.renderscript.ScriptIntrinsicBlur.create(rs, android.renderscript.Element.U8_4(rs))

        val inputAllocation = android.renderscript.Allocation.createFromBitmap(rs, inputBitmap, android.renderscript.Allocation.MipmapControl.MIPMAP_NONE, android.renderscript.Allocation.USAGE_SHARED)
        val outputAllocation = android.renderscript.Allocation.createTyped(rs, inputAllocation.type)
        theIntrinsic.setRadius(blurLevel.toFloat())
        theIntrinsic.setInput(inputAllocation)
        theIntrinsic.forEach(outputAllocation)
        outputAllocation.copyTo(outputBitmap)

        return outputBitmap
    }

    private fun writeBitmapToFile(context: Context, bitmap: Bitmap): Uri {
        val bytes = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
        val fileName = "blur_image_${System.currentTimeMillis()}.png"
        val outputUri = FileUtil.saveBitmapToFile(context, bytes.toByteArray(), fileName)
        return Uri.fromFile(outputUri)
    }
}
