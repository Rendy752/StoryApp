package com.example.storyapp

import com.example.storyapp.data.response.Story

object DataDummy {
    fun generateDummyStoryResponse(): List<Story> {
        val items: MutableList<Story> = arrayListOf()
        repeat(100) {
            val story = Story(
                id = "story-FvU4u0Vp2S3PMsFg",
                photoUrl = "https://story-api.dicoding.dev/images/stories/photos/1641628002811_5P0-f-f-.jpg",
                createdAt = "2022-01-08T06:34:18.598Z",
                name = "Dimas",
                description = "Lorem Ipsum",
                lon = -16.002,
                lat = -10.212
            )
            items.add(story)
        }
        return items
    }
}