package com.palavrizar.tec.palavrizapp.utils.commons

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import com.palavrizar.tec.palavrizapp.R
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
            val uri = FileProvider.getUriForFile(context, "com.palavrizar.tec.palavrizapp.provider", file)
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