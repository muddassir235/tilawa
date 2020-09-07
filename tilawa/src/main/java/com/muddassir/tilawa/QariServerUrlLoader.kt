/**
 * Al Quran Al Kareem Recitation by Various Qaris القرآن الكريم
 * @author         : Muddassir Ahmed Khan
 * Contact         : muddassir.ahmed235@gmail.com
 * Github Username : muddassir235
 *
 * QariServerUrlLoader.kt
 *
 * Loads Urls of recitors from the mp3quran.net database.
 */
package com.muddassir.tilawa

import android.content.Context
import android.util.Log
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

/**
 * ServerUrlsLoadListener - Interface which can be used to listen to the event when the urls are loaded.
 */
interface ServerUrlsLoadListener {
    fun onQariUrlsLoaded(urls: Array<String?>?)
}

class QariServerUrlLoader(context: Context) {
    private var queue: RequestQueue = Volley.newRequestQueue(context) // Volley Request Queue

    /* Url where QariInfo data is present */
    private val url = "http://mp3quran.net/api/_english.php"

    var urlsLoadListener : ServerUrlsLoadListener? = null

    /**
     * loadUrls - Load tilawa urls from http://mp3quran.net/api/_english.php
     */
    fun loadUrls() {
        val pingMp3QuranDotNet = StringRequest(
            Request.Method.GET, url,
            Response.Listener<String> { response ->
                val jsonResponse = JSONObject(response)
                val qaris = jsonResponse.getJSONArray("reciters")

                urlsLoadListener?.onQariUrlsLoaded(urls = convertToQariUrlsArray(qaris))
            }, Response.ErrorListener {
                Log.e("QariServerUrlLoader", "Response: $it")
            })

        queue.add(pingMp3QuranDotNet)
    }

    /**
     * convertToQariUrlsArray - Load QariInfo Urls the JSONArray of Qaris gotten from
     * http://mp3quran.net/api/_english.php
     *
     * @param qarisJsonArray: Json array of qaris
     *
     * @return: String array of urls of the qaris in mp3quran.net
     */
    private fun convertToQariUrlsArray(qarisJsonArray: JSONArray): Array<String?> {
        val urls = arrayOfNulls<String>(13)

        for(i in 0 until qarisJsonArray.length()) {
            val qari = qarisJsonArray.getJSONObject(i)

            if(getQariName(qari).contains("sudaes")) {
                urls[0] = qari.getString("Server")
            } else if (getQariName(qari).contains("shuraim")) {
                urls[1] = qari.getString("Server")
            } else if (getQariName(qari).contains("budair")) {
                urls[2] = qari.getString("Server")
            } else if (
                getQariName(qari).contains("ali alhuthaifi")
                && qari.getString("rewaya") == "Rewayat Hafs A'n Assem"
            ) {
                urls[3] = qari.getString("Server")
            } else if (
                getQariName(qari).contains("meaqli")
                && qari.getString("rewaya") == "Rewayat Hafs A'n Assem"
            ) {
                urls[4] = qari.getString("Server")
            } else if (getQariName(qari).contains("johany")) {
                urls[5] = qari.getString("Server")
            } else if (getQariName(qari).contains("lohaidan")) {
                urls[6] = qari.getString("Server")
            } else if (
                getQariName(qari).contains("afasi")
                && qari.getString("rewaya") == "Rewayat Hafs A'n Assem"
            ) {
                urls[7] = qari.getString("Server")
            } else if (getQariName(qari).contains("jaber")) {
                urls[8] = qari.getString("Server")
            } else if (getQariName(qari).contains("balilah")) {
                urls[9] = qari.getString("Server")
            } else if (
                getQariName(qari).contains("abdulbasit")
                && qari.getString("rewaya") == "Rewayat Hafs A'n Assem"
            ) {
                urls[10] = qari.getString("Server")
            } else if (getQariName(qari).contains("kurdi")) {
                urls[11] = qari.getString("Server")
            } else if (getQariName(qari).contains("ajmy")) {
                urls[12] = qari.getString("Server")
            }
        }

        return urls
    }

    /**
     * getQariName - Gets tilawa name in lowercase from the JSONObject
     *
     * @param qari: QariInfo JSONObject from response of http://mp3quran.net/api/_english.php
     *
     * @return QariInfo name in lower case
     */
    private fun getQariName(qari: JSONObject): String {
        return qari.getString("name").toLowerCase(Locale.ROOT);
    }
}