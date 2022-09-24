package com.akr.customgallery.ui.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentResolver
import android.content.Context
import android.database.Cursor
import android.database.MergeCursor
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.akr.customgallery.data.model.DirectoryModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.*


class DirectoryViewModel(context: Application) : AndroidViewModel(context) {

    private val mContext = context
    fun getMediaDirectory(): Flow<List<DirectoryModel>>? {

        val latestNews: Flow<List<DirectoryModel>> = flow {

            val latestNews = getAllMediaFilesOnDevice(mContext)//getImageDirectories()
            emit(latestNews) // Emits the result of the request to the flow
            // delay(refreshIntervalMs) // Suspends the coroutine for some time
        }
        return latestNews
    }

    @SuppressLint("Range")
    private fun getImageDirectories(): List<DirectoryModel> {
        val directories: ArrayList<String> = ArrayList()
        val list: MutableList<DirectoryModel> = ArrayList()
        val contentResolver: ContentResolver = mContext.contentResolver
        val queryUri: Uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Images.Media.DATA
        )
        val includeImages = MediaStore.Images.Media.MIME_TYPE + " LIKE 'image/%' "
        val excludeGif =
            " AND " + MediaStore.Images.Media.MIME_TYPE + " != 'image/gif' " + " AND " + MediaStore.Images.Media.MIME_TYPE + " != 'image/giff' "
        val selection = includeImages + excludeGif
        val strOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC"
        val cursor: Cursor? = contentResolver.query(queryUri, projection, selection, null, strOrder)
        var count: Int = 0
        if (cursor != null && cursor.moveToFirst()) {
            do {
                val photoUri: String = cursor.getString(cursor.getColumnIndex(projection[0]))
                var fileName = File(photoUri).parent
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length)

                if (!directories.contains(fileName)) {
                    directories.add(fileName)
                    val mediaModel = DirectoryModel(fileName, photoUri, cursor.position)
                    if (list.size > 0) {
                        list[list.size - 1].totalFiles = count
                    }
                    count = 1;
                    if (list.size == 0) {
                        val mediaModel1 = DirectoryModel("All", photoUri, cursor.count)
                        list.add(mediaModel1)
                    }
                    list.add(mediaModel)


                } else {
                    count++
                }

            } while (cursor.moveToNext())
        }
        if(list.size>0)
            list[list.size - 1].totalFiles = count
        return list
    }

    fun getAllMediaFilesOnDevice(context: Context): List<DirectoryModel> {
        val files: MutableList<DirectoryModel> = ArrayList()
        val directories: ArrayList<String> = ArrayList()
        try {
            val columns = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
            val cursor = MergeCursor(
                arrayOf(
                    context.getContentResolver().query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        columns,
                        null,
                        null,
                        null
                    ),
                    context.getContentResolver().query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        columns,
                        null,
                        null,
                        null
                    ),
                    context.getContentResolver().query(
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                        columns,
                        null,
                        null,
                        null
                    ),
                    context.getContentResolver().query(
                        MediaStore.Video.Media.INTERNAL_CONTENT_URI,
                        columns,
                        null,
                        null,
                        null
                    )
                )
            )
            cursor.moveToFirst()
            files.clear()
            var count: Int = 0
            while (!cursor.isAfterLast) {
                var path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                val lastPoint = path.lastIndexOf(".")
                path = path.substring(0, lastPoint) + path.substring(lastPoint)
                    .lowercase(Locale.getDefault())
                Log.e("path","path "+path);
                //files.add(File(path))

                val photoUri: String = path
                var fileName = File(photoUri).parent
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length)

                if (!directories.contains(fileName)) {
                    directories.add(fileName)
                    val mediaModel = DirectoryModel(fileName, photoUri, cursor.position)
                    if (files.size > 0) {
                        files[files.size - 1].totalFiles = count
                    }
                    count = 1;
                    if (files.size == 0) {
                        val mediaModel1 = DirectoryModel("All", photoUri, cursor.count)
                        files.add(mediaModel1)
                    }
                    files.add(mediaModel)


                } else {
                    count++
                }
                cursor.moveToNext()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return files
    }


}