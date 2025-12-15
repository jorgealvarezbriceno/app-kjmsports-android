package com.example.kjm_android.data

/**
 * A simple in-memory cache to temporarily store image URLs for categories.
 * This is a workaround and will be cleared when the app is closed.
 */
object TempCategoryImageCache {
    val cache = mutableMapOf<String, String>()
}