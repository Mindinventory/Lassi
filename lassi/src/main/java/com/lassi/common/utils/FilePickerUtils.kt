package com.lassi.common.utils

import android.content.ContentResolver
import android.content.Context
import android.media.MediaScannerConnection
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.util.*

object FilePickerUtils {

    fun contains(types: Array<String>, path: String): Boolean {
        for (string in types) {
            if (path.lowercase(Locale.getDefault()).endsWith(string)) return true
        }
        return false
    }

    fun notifyGalleryUpdateNewFile(
        context: Context,
        filePath: String,
        mimeType: String = "image/*",
        onFileScanComplete: (uri: Uri?, path: String?) -> Unit
    ) {
        context.let {
            MediaScannerConnection.scanFile(
                it,
                arrayOf(filePath),
                arrayOf(mimeType)
            ) { path, uri ->
                Log.d("ExternalStorage", "Scanned $path")
                onFileScanComplete(uri, path)
            }
        }
    }

    private fun getFileExtension(context: Context, uri: Uri): String? =
        if (uri.scheme == ContentResolver.SCHEME_CONTENT)
            MimeTypeMap.getSingleton()
                .getExtensionFromMimeType(context.contentResolver.getType(uri))
        else uri.path?.let {
            MimeTypeMap.getFileExtensionFromUrl(
                Uri.fromFile(File(it)).toString()
            )
        }

    fun getFilePathFromUri(context: Context, uri: Uri, uniqueName: Boolean): String =
        if (uri.path?.contains("file://") == true) uri.path!!
        else getFileFromContentUri(context, uri, uniqueName).path

    private fun getFileFromContentUri(
        context: Context,
        contentUri: Uri,
        uniqueName: Boolean,
    ): File {
        // Preparing Temp file name
        val fileExtension = getFileExtension(context, contentUri) ?: ""
        val fileName =
            ("file" + if (uniqueName) System.currentTimeMillis() else "") + ".$fileExtension"

        // Creating Temp file
        val tempFile = File(context.cacheDir, fileName)
        tempFile.createNewFile()
        // Initialize streams
        var oStream: FileOutputStream? = null
        var inputStream: InputStream? = null

        try {
            oStream = FileOutputStream(tempFile)
            inputStream = context.contentResolver.openInputStream(contentUri)

            inputStream?.let { copy(inputStream, oStream) }
            oStream.flush()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            // Close streams
            inputStream?.close()
            oStream?.close()
        }
        return tempFile
    }

    private fun copy(source: InputStream, target: OutputStream) {
        val buf = ByteArray(8192)
        var length: Int
        while (source.read(buf).also { length = it } > 0) {
            target.write(buf, 0, length)
        }
    }

}
