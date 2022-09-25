package com.akr.customgallery.ui.viewmodels

import android.app.Application
import android.content.Context
import android.database.MergeCursor
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import com.akr.customgallery.data.model.MediaModel
import com.akr.customgallery.utilities.Constants
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import java.util.*

class GalleryDetailsViewModel(context: Application) : AndroidViewModel(context) {

    private val mContext = context
    fun getMediaFromDirectory(directoryName: String): Flow<List<MediaModel>> {

        val latestNews: Flow<List<MediaModel>> = flow {

            val latestNews = getAllMediaFilesOnDevice(mContext, directoryName)
            emit(latestNews) // Emits the result of the request to the flow
        }
        return latestNews
    }

    // load media as per selected directory
    private fun getAllMediaFilesOnDevice(
        context: Context,
        directoryName: String
    ): List<MediaModel> {
        val files: MutableList<MediaModel> = java.util.ArrayList()
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
            files.clear()
            while (!cursor.isAfterLast) {
                var path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA))
                var time = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED))

                val lastPoint = path.lastIndexOf(".")
                path = path.substring(0, lastPoint) + path.substring(lastPoint)
                    .lowercase(Locale.getDefault())

                val photoUri: String = path
                var fileName = File(photoUri).parent
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length)

                if (directoryName != Constants.IMAGES && directoryName != Constants.VIDEOS) {
                    if (fileName.equals(directoryName)) {
                        val mediaModel = MediaModel(photoUri, time)
                        files.add(mediaModel)
                    }
                } else {
                    val mediaModel = MediaModel(photoUri, time)
                    if (directoryName == Constants.IMAGES) {
                        if (!photoUri.endsWith(Constants.VIDEO_TYPE))
                            files.add(mediaModel)
                    }
                    if (directoryName == Constants.VIDEOS) {
                        if (photoUri.endsWith(Constants.VIDEO_TYPE))
                            files.add(mediaModel)
                    }
                }
                cursor.moveToNext()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return files.sortedWith(compareBy { it.createdDate })
    }
}