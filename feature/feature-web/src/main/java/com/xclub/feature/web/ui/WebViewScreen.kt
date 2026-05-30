package com.xclub.feature.web.ui

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.xclub.feature.web.viewmodel.WebBookmarkViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebViewScreen(bookmarkId: Long, onBack: () -> Unit, viewModel: WebBookmarkViewModel = hiltViewModel()) {
    val bookmarks by viewModel.bookmarks.collectAsState()
    val bookmark = bookmarks.firstOrNull { it.id == bookmarkId }
    var pageTitle by remember { mutableStateOf(bookmark?.title ?: "") }

    if (bookmark == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        return
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(pageTitle, maxLines = 1) }, navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回") } })
    }) { padding ->
        AndroidView(factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() { override fun onPageFinished(view: WebView?, url: String?) { super.onPageFinished(view, url); pageTitle = view?.title ?: bookmark.title } }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                loadUrl(bookmark.url)
            }
        }, modifier = Modifier.fillMaxSize().padding(padding))
    }
}
