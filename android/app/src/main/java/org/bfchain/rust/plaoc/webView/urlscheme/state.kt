package org.bfchain.rust.plaoc.webView.urlscheme

import android.content.res.AssetManager
import android.webkit.MimeTypeMap
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import androidx.compose.runtime.Stable
import java.io.ByteArrayInputStream
import java.io.InputStream
import java.net.URI
import kotlin.io.path.Path
import kotlin.io.path.extension

@Stable
data class UrlState(
    val href: String,
    val ext: String,
    val mime: String,
    val encoding: String,
    val headers: Map<String, String>,
    val method: String,
    val isRedirect: Boolean,
    val isForMainFrame: Boolean,
)

private const val TAG = "URL_STATE"

class CustomUrlScheme(
    val scheme: String,
    val host: String,
    val requestHandler: RequestHandler,
) {
    private val mimeMap: MimeTypeMap by lazy { MimeTypeMap.getSingleton() }
    fun getMimeTypeFromExtension(extension: String): String {
        return mimeMap.getMimeTypeFromExtension(extension) ?: "text/plain"
    }

    val origin by lazy { "$scheme://$host" }

    fun resolveUrl(path: String): String {
        val pathname = if (path.startsWith("/")) {
            path
        } else {
            "/$path"
        }
        return origin + pathname
    }

    fun getEncodingByMimeType(mimeType: String): String {
        return if (mimeType.startsWith("application/")) {
            if (mimeType.contains("json") || mimeType.contains("xml")) {
                "utf-8"
            } else {
                "binary"
            }
        } else if (mimeType.startsWith("text/")) {
            "utf-8"
        } else {
            /**
             * image/
             * video/
             * audio/
             * etc...
             */
            "binary"
        }
    }

    fun isMatch(req: WebResourceRequest) = req.url.scheme == scheme && req.url.host == host


    /**
     * @TODO 增加跨域白名单的配置功能
     */
    fun isCrossDomain(req: WebResourceRequest) =
        req.url.scheme == scheme && req.url.host != host


    fun handleRequest(req: WebResourceRequest, url: String): WebResourceResponse {
        val urlExt = req.url.lastPathSegment?.let { filename ->
            Path(filename).extension
        } ?: ""
        val urlMimeType = getMimeTypeFromExtension(urlExt)
        val urlEncoding = getEncodingByMimeType(urlMimeType)

        val urlState = UrlState(
            href = url,
            ext = urlExt,
            mime = urlMimeType,
            encoding = urlEncoding,
            headers = req.requestHeaders,
            method = req.method,
            isRedirect = req.isRedirect,
            isForMainFrame = req.isForMainFrame,
        )
//        Log.d(TAG, "handleRequest urlMimeType: $urlMimeType")
//        Log.d(TAG, "handleRequest urlExt: $urlExt")
//        Log.d(TAG, "handleRequest url: ${req.url}")
//        Log.d(TAG, "handleRequest urlState url: $urlEncoding")
        val responseBodyStream = requestHandler.onHttpRequest(urlState)
            ?: return WebResourceResponse(
                urlMimeType, urlEncoding, 404, "Resource No Found", mapOf(),
                nullInputStream
            )
        return WebResourceResponse(urlMimeType, urlEncoding, responseBodyStream)
    }

}

val nullInputStream by lazy { ByteArrayInputStream(byteArrayOf()) }

//typealias  RequestHandler = (req: UrlState) -> InputStream?
interface RequestHandler {
    fun onRequest(req: UrlState): InputStream?
    fun onHttpRequest(req: UrlState): InputStream?
}

//
fun requestHandlerFromAssets(assetManager: AssetManager, basePath: String): RequestHandler {
    return object : RequestHandler {
        private fun openInputStream(path: String): InputStream? {
            return try {
                assetManager.open(path)
            } catch (e: java.io.FileNotFoundException) {
                null
            }
        }

        override fun onRequest(req: UrlState): InputStream? {
            val uri = URI(req.href)
            var urlPath = Path(basePath, uri.path).toString()
            // 使用 context.assets.open 来读取文件
            var inputStream = openInputStream(urlPath)
            // 判断 isFile，不是的话就看 isDirectory，如果是的话就尝试访问 index.html
            if (inputStream == null) {
                val fileLists = assetManager.list(urlPath) ?: return null
                if (fileLists.isEmpty()) {
                    return null
                }
                val indexName = fileLists.find { it == "index.html" } ?: return null
                // 如果加上 index.html 后 isFile 仍然是 false，那么返回 null
                return openInputStream(Path(urlPath, indexName).toString())
            }
            return inputStream
        }

        override fun onHttpRequest(req: UrlState): InputStream? {
            var urlPath = req.href
            // 本地文件前面不能有 /,必须直接写不然读不到，下面再做一成保险，帮用户去掉前缀
            if (urlPath.startsWith("/")) {
                urlPath = urlPath.substring(urlPath.indexOf("/") + 1)
            }
            // 使用 context.assets.open 来读取文件
            val inputStream = openInputStream(urlPath)
            // 判断 isFile，不是的话就看 isDirectory，如果是的话就尝试访问 index.html
            if (inputStream == null) {
                val fileLists = assetManager.list(urlPath) ?: return null
                if (fileLists.isEmpty()) {
                    return null
                }
                val indexName = fileLists.find { it == "index.html" } ?: return null
                // 如果加上 index.html 后 isFile 仍然是 false，那么返回 null
                return openInputStream(Path(urlPath, indexName).toString())
            }
            return inputStream
        }
    }
}