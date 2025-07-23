package com.example.storyapp.ui.widget

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.pref.UserPreference
import com.example.storyapp.data.pref.dataStore
import com.example.storyapp.data.response.Story
import com.example.storyapp.data.retrofit.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class StackWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext)
    }
}

class StackRemoteViewsFactory(private val mContext: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val storyItems = ArrayList<Story>()
    private lateinit var userPreference: UserPreference

    override fun onCreate() {
        userPreference = UserPreference.getInstance(mContext.dataStore)
    }

    override fun onDataSetChanged() {
        runBlocking {
            val token = userPreference.getSession().first().token
            if (token.isEmpty()) {
                storyItems.clear()
                return@runBlocking
            }

            try {
                val apiService = ApiConfig.getApiService()
                val response = apiService.getStories("Bearer $token")

                response.listStory?.let { stories ->
                    storyItems.clear()
                    storyItems.addAll(stories)
                }
            } catch (e: Exception) {
                storyItems.clear()
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {}

    override fun getCount(): Int = storyItems.size

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        try {
            val bitmap: Bitmap = Glide.with(mContext)
                .asBitmap()
                .load(storyItems[position].photoUrl)
                .submit()
                .get()
            rv.setImageViewBitmap(R.id.imageView, bitmap)
        } catch (e: Exception) {
            rv.setImageViewResource(R.id.imageView, R.drawable.ic_launcher_background)
            e.printStackTrace()
        }

        val fillInIntent = Intent()
        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(i: Int): Long = i.toLong()

    override fun hasStableIds(): Boolean = true
}