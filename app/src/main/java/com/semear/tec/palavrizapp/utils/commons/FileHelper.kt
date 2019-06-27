package com.semear.tec.palavrizapp.utils.commons

import android.app.DownloadManager
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.semear.tec.palavrizapp.R
import com.semear.tec.palavrizapp.utils.constants.Helper.*
import java.io.File
import java.io.FileOutputStream


object FileHelper {

    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED
    }

    fun isExternalStorageReadable(): Boolean {
        return Environment.getExternalStorageState() in
                setOf(Environment.MEDIA_MOUNTED, Environment.MEDIA_MOUNTED_READ_ONLY)
    }

    fun saveBitmpao(filename: String, bm: Bitmap){
        val root = Environment.getExternalStorageDirectory().toString()
        val myDir = File("$root/req_images")
        myDir.mkdirs()

        val file = File(myDir, filename)
        if (file.exists())
            file.delete()
        try {
            val out = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun getPathPdf(context: Context, contentUri: Uri): String?{
        var uri = contentUri
        if (Build.VERSION.SDK_INT > 26){
            val file = File(uri.path)//create path from uri
            val split = file.path.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()//split the path.
            return split[1]//assign it to a string(your choice).
        }else{
            var selection: String? = null
            var selectionArgs: Array<String>? = null
            // Uri is different in versions after KITKAT (Android 4.4), we need to
            if (DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
                if (isExternalStorageDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]
                } else if (isDownloadsDocument(uri)) {
                    val id = DocumentsContract.getDocumentId(uri)
                    uri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id))
                } else if (isMediaDocument(uri)) {
                    val docId = DocumentsContract.getDocumentId(uri)
                    val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                    val type = split[0]
                    if ("image" == type) {
                        uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    } else if ("video" == type) {
                        uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    } else if ("audio" == type) {
                        uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    }
                    selection = "_id=?"
                    selectionArgs = arrayOf(split[1])
                }
            }
            if ("content".equals(uri.getScheme(), ignoreCase = true)) {


                if (isGooglePhotosUri(uri)) {
                    return uri.getLastPathSegment()
                }

                val projection = arrayOf(MediaStore.Images.Media.DATA)
                var cursor: Cursor? = null
                try {
                    cursor = context.contentResolver
                            .query(uri, projection, selection, selectionArgs, null)
                    val column_index = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                    if (cursor.moveToFirst()) {
                        return cursor.getString(column_index)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            } else if ("file".equals(uri.getScheme(), ignoreCase = true)) {
                return uri.path
            }
            return null
        }
    }

    fun getRealPathFromURI(context: Context, contentUri: Uri): String? {
        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        if (cursor != null && cursor.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
            cursor.close()
        }
        return res
    }



    fun downloadFile(context: Context, fileName: String, fileExtension: String, destinationDirectory: String, url: String): Long {


        val downloadmanager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val uri = Uri.parse(url)
        val request = DownloadManager.Request(uri)

        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
        request.setDestinationInExternalFilesDir(context, destinationDirectory, "$fileName.$fileExtension")

        return downloadmanager.enqueue(request)
    }


    fun openPdf(context: Context, file: File){
        //config the flags of the intent
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        //Use file provider on versions > 24
        if(Build.VERSION.SDK_INT>=24){
            val uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".com.semear.tec.palavrizapp.provider", file)
            intent.setDataAndType(uri,"application/pdf")
        }else {
            intent.setDataAndType(Uri.fromFile(file), "application/pdf")
        }

        //Intent choose to let the user choose your favorite app to open the pdf
        val intentChooser = Intent.createChooser(intent, context.getString(R.string.open_pdf_theme))
        try {
            context.startActivity(intentChooser)
        } catch (e: Exception) {
            //publish the error loading the pdf
        }
    }
}