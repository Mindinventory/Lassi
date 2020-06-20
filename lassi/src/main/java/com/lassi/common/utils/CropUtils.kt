package com.lassi.common.utils

import android.content.Context
import android.net.Uri
import android.os.Environment
import androidx.fragment.app.FragmentActivity
import com.lassi.domain.media.LassiConfig
import com.lassi.presentation.cropper.CropImage
import com.lassi.presentation.cropper.CropImageView
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


object CropUtils {
    fun beginCrop(activity: FragmentActivity, source: Uri) {
        with(LassiConfig.getConfig()) {
            CropImage.activity(source)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setOutputCompressQuality((100 - compressionRation))
                .setCropShape(cropType)
                .setAspectRatio(cropAspectRatio)
                .setOutputUri(createDirectory(activity))
                .setAllowRotation(enableRotateImage)
                .setAllowFlipping(enableFlipImage)
                .start(activity)
        }
    }

    private fun getApplicationName(context: Context): String {
        val applicationInfo = context.applicationInfo
        val stringId = applicationInfo.labelRes
        return if (stringId == 0) applicationInfo.nonLocalizedLabel.toString() else context.getString(
            stringId
        )
    }

    private fun getPath(context: Context): String {
        return Environment.getExternalStorageDirectory().absolutePath + File.separator + getApplicationName(
            context
        )
    }

    private fun createDirectory(context: Context): Uri? {
        getPath(context).let {
            val storageDir = File(it)
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }

            if (!storageDir.exists()) {
                val isDirectoryCreated = File(storageDir.path).mkdirs()
                Logger.d("CropUtils", "isDirectoryCreated >> $isDirectoryCreated")
            } else {
                Logger.d("CropUtils", "directory alredy exists >> ${storageDir.path}")
            }
            val photoFile: File? = try {
                createImageFile(context)
            } catch (ex: IOException) {
                // Error occurred while creating the File
                Logger.e("CropUtils", "createDirectory $ex")
                null
            }
            // Continue only if the File was successfully created
            photoFile?.let {
                val outputUri = Uri.fromFile(photoFile)
                Logger.d("CropUtils", "outputUri >> $outputUri")
                return outputUri
            }
        }
        return null
    }

    @Throws(IOException::class)
    private fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
//        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val storageDir =
            Environment.getExternalStorageDirectory().absolutePath + File.separator + getApplicationName(
                context
            )

        return File.createTempFile(
            "IMG-${timeStamp}_", /* prefix */
            ".jpeg", /* suffix */
            File(storageDir) /* directory */
        )
    }
}