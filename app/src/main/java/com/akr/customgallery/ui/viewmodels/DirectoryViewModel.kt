package com.akr.customgallery.ui.viewmodels

import android.app.Application
import android.content.Context
import android.database.MergeCursor
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import com.akr.customgallery.data.model.DirectoryModel
import com.akr.customgallery.utilities.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.*


class DirectoryViewModel(context: Application) : AndroidViewModel(context) {

    private val mContext = context

    fun getMediaDirectory(): Flow<HashMap<String, MutableList<DirectoryModel>>>? {

        val latestNews: Flow<HashMap<String, MutableList<DirectoryModel>>> = flow {

            val latestNews = getAllMediaFilesOnDevice(mContext)//getImageDirectories()
            emit(latestNews) // Emits the result of the request to the flow

        }
        return latestNews
    }

    // fetch all the directory which having media files like images and videos
    private fun getAllMediaFilesOnDevice(context: Context): HashMap<String, MutableList<DirectoryModel>> {
        val hashMap: HashMap<String, MutableList<DirectoryModel>> = HashMap()
        val strOrder = MediaStore.Images.Media.DATE_TAKEN + " DESC"

        try {
            val columns = arrayOf(
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
            )
            val cursor = MergeCursor(
                arrayOf(
                    context.contentResolver.query(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        columns,
                        null,
                        null,
                        strOrder
                    ),
                    context.contentResolver.query(
                        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                        columns,
                        null,
                        null,
                        strOrder
                    ),
                    context.contentResolver.query(
                        MediaStore.Images.Media.INTERNAL_CONTENT_URI,
                        columns,
                        null,
                        null,
                        strOrder
                    ),
                    context.contentResolver.query(
                        MediaStore.Video.Media.INTERNAL_CONTENT_URI,
                        columns,
                        null,
                        null,
                        strOrder
                    )
                )
            )
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                var path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                val lastPoint = path.lastIndexOf(".")
                path = path.substring(0, lastPoint) + path.substring(lastPoint)
                    .lowercase(Locale.getDefault())

                val photoUri: String = path
                var fileName = File(photoUri).parent
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length)

                val directoryModel = DirectoryModel(fileName, photoUri)
                // load media directory wise
                if (hashMap.contains(fileName)) {
                    hashMap.get(fileName)?.add(directoryModel)
                } else {
                    val list: MutableList<DirectoryModel> = ArrayList()
                    list.add(directoryModel)
                    hashMap.put(fileName, list)
                }
                if (photoUri.endsWith(Constants.VIDEO_TYPE)) { // load all videos from any directory
                    val directoryModel = DirectoryModel(Constants.VIDEOS, photoUri)
                    if (hashMap.contains(Constants.VIDEOS)) {
                        hashMap[Constants.VIDEOS]?.add(directoryModel)
                    } else {
                        val list: MutableList<DirectoryModel> = ArrayList()
                        list.add(directoryModel)
                        hashMap[Constants.VIDEOS] = list
                    }
                } else {
                    // load all images from any directory
                    val directoryModel = DirectoryModel(Constants.IMAGES, photoUri)
                    if (hashMap.contains(Constants.IMAGES)) {
                        hashMap[Constants.IMAGES]?.add(directoryModel)
                    } else {
                        val list: MutableList<DirectoryModel> = ArrayList()
                        list.add(directoryModel)
                        hashMap[Constants.IMAGES] = list
                    }
                }

                cursor.moveToNext()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return hashMap
    }

}