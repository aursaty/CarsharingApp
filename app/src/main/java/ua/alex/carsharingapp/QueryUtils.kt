package ua.alex.carsharingapp

import android.util.Log
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.nio.charset.Charset

class QueryUtils {

    companion object {
        private const val SERVER_ADDRESS = "http://192.168.1.177:8080"
        private val LOG_TAG = QueryUtils::class.java.simpleName

        fun fetchData(requestString: String, requestMethod: String, stringJson: String): String {
            val url = createUrl(SERVER_ADDRESS + requestString)
            return makeHttpRequest(url, requestMethod, stringJson)!!
        }

        private fun makeHttpRequest(url: URL?, method: String, json: String): String? {
            var jsonResponse = ""
            var urlConnection: HttpURLConnection? = null
            var inputStream: InputStream? = null
            try {
                Log.d(LOG_TAG, url.toString())
                urlConnection = url!!.openConnection() as HttpURLConnection
                urlConnection.requestMethod = method
                urlConnection.readTimeout = 5000
                urlConnection.connectTimeout = 5000
                if (method == "PUT" || method == "POST") {
                    urlConnection.setRequestProperty("Content-Type", "application/json")
                    val out = OutputStreamWriter(urlConnection.outputStream)
                    out.write(json)
                    out.close()
                }
                urlConnection.connect()
                if (urlConnection.responseCode == 200) {
                    inputStream = urlConnection.inputStream
                    jsonResponse = readFromStream(inputStream)
                    return if (method == "GET")
                        jsonResponse
                    else
                        ""
                }
            } catch (e: Exception) {
                e.printStackTrace()
//                doAsync {
//                    uiThread {
//                        reference.get()?.alert("Connection error") {
//                            title = "Sign In failed"
//                            yesButton { }
//                        }?.show()
//                    }
//                }
//                if (!isCancelled) {
//                    cancel(true)
//                }
                return null
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect()
                }
                if (inputStream != null) {
                    inputStream.close()
                }
            }
            return jsonResponse
        }

        private fun createUrl(stringUrl: String): URL? {
            val url: URL
            try {
                url = URL(stringUrl)
            } catch (e: MalformedURLException) {
                Log.e(LOG_TAG, "Error with creating URL", e)
                return null
            }

            return url
        }

        private fun readFromStream(inputStream: InputStream?): String {
            val output = StringBuilder()
            if (inputStream != null) {
                val inputStreamReader = InputStreamReader(inputStream, Charset.forName("utf-8"))
                val reader = BufferedReader(inputStreamReader)
                var line = reader.readLine()
                while (line != null) {
                    output.append(line)
                    line = reader.readLine()
                }
            }
            return output.toString()
        }
    }
}