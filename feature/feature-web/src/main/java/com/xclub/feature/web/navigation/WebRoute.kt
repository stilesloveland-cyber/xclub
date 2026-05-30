package com.xclub.feature.web.navigation

object WebRoute {
    const val list = "web_list"
    const val view = "web_view/{bookmarkId}"
    fun view(bookmarkId: Long) = "web_view/$bookmarkId"
}
