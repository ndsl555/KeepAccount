package com.example.keepaccount.util

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.core.graphics.createBitmap

object ScreenshotUtil {
    fun captureAndSave(
        context: Context,
        view: View,
        folderName: String = "KeepAccount",
        onSuccess: (uri: android.net.Uri) -> Unit,
        onError: (() -> Unit)? = null,
    ) {
        view.post {
            try {
                val bitmap = createBitmap(view.width, view.height)
                val canvas = Canvas(bitmap)
                view.draw(canvas)

                val uri = saveBitmap(context, bitmap, folderName)
                if (uri != null) {
                    onSuccess(uri)
                } else {
                    onError?.invoke()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                onError?.invoke()
            }
        }
    }

    private fun saveBitmap(
        context: Context,
        bitmap: Bitmap,
        folderName: String,
    ): android.net.Uri? {
        val filename = "lottery_${System.currentTimeMillis()}.png"

        val values =
            ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/png")
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    Environment.DIRECTORY_PICTURES + "/$folderName",
                )
            }

        val resolver = context.contentResolver
        val uri =
            resolver.insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                values,
            ) ?: return null

        resolver.openOutputStream(uri)?.use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        return uri
    }
}
