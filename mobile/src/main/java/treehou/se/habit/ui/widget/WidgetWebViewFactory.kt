package treehou.se.habit.ui.widget

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import se.treehou.ng.ohcommunicator.connector.models.OHLinkedPage
import se.treehou.ng.ohcommunicator.connector.models.OHServer
import se.treehou.ng.ohcommunicator.services.IServerHandler
import treehou.se.habit.R
import treehou.se.habit.ui.adapter.WidgetAdapter
import treehou.se.habit.util.logging.Logger
import javax.inject.Inject

class WidgetWebViewFactory @Inject constructor() : WidgetFactory {

    @Inject lateinit var logger: Logger
    @Inject lateinit var server: OHServer
    @Inject lateinit var page: OHLinkedPage
    @Inject lateinit var serverHandler: IServerHandler

    override fun createViewHolder(parent: ViewGroup): WidgetAdapter.WidgetViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.widget_web_view, parent, false)
        return WebWidgetViewHolder(view)
    }

    inner class WebWidgetViewHolder(view: View) : WidgetBaseHolder(view, server, page) {

        private var webView: WebView = view as WebView

        init {
            setupWebView()
        }

        override fun bind(itemWidget: WidgetAdapter.WidgetItem) {
            super.bind(itemWidget)

            val lp = webView.layoutParams
            val heightInRows = widget.height
            val desiredHeightPixels = if (heightInRows is Int && heightInRows > 0) (heightInRows * context.resources.getDimension(R.dimen.webview_row_height)).toInt() else ViewGroup.LayoutParams.WRAP_CONTENT

            if (lp.height != desiredHeightPixels) {
                lp.height = desiredHeightPixels
                webView.layoutParams = lp
            }

            if (widget.url != null) {
                webView.loadUrl(widget.url)
            }
        }

        @SuppressLint("SetJavaScriptEnabled")
        private fun setupWebView() {
            val client = WebViewClient()
            webView.webViewClient = client

            val settings = webView.settings
            settings.javaScriptEnabled = true
            settings.javaScriptCanOpenWindowsAutomatically = true
            webView.isFocusableInTouchMode = true
            settings.cacheMode = WebSettings.LOAD_NO_CACHE
            settings.domStorageEnabled = true
            settings.databaseEnabled = true
            settings.setAppCacheEnabled(true)
        }
    }

    companion object {
        const val TAG = "WidgetSwitchFactory"
    }
}