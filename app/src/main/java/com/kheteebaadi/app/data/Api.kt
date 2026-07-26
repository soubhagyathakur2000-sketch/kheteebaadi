package com.kheteebaadi.app.data

import android.content.Context
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Thin client over the existing kheteebaadi.com PHP backend.
 * Every call is POST + JSON. Cookies are persisted by [PersistentCookieJar].
 * "Write" actions must send the CSRF token as the X-CSRF-Token header.
 */
object Api {
    const val BASE = "https://kheteebaadi.com/api.php"

    @Volatile var csrf: String = ""

    private lateinit var client: OkHttpClient
    private val JSON = "application/json; charset=utf-8".toMediaType()

    fun init(context: Context) {
        client = OkHttpClient.Builder()
            .cookieJar(PersistentCookieJar(context.applicationContext))
            .connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .build()
    }

    /**
     * Performs a POST to ?action=[action]. Throws [java.io.IOException] on
     * network failure. Returns the parsed JSON body ({"ok":true,...} or
     * {"ok":false,"error":"code"}).
     */
    fun post(action: String, body: JSONObject? = null, sendCsrf: Boolean = false): JSONObject {
        val payload = (body ?: JSONObject()).toString().toRequestBody(JSON)
        val req = Request.Builder()
            .url("$BASE?action=$action")
            .post(payload)
            .header("Accept", "application/json")
        if (sendCsrf && csrf.isNotEmpty()) req.header("X-CSRF-Token", csrf)

        client.newCall(req.build()).execute().use { resp ->
            val text = resp.body?.string().orEmpty()
            if (text.isBlank()) return JSONObject().put("ok", false).put("error", "empty_response")
            return try {
                JSONObject(text)
            } catch (e: Exception) {
                JSONObject().put("ok", false).put("error", "bad_response")
            }
        }
    }

    /** Captures the csrf token whenever the backend returns one. */
    fun captureCsrf(resp: JSONObject) {
        val t = resp.optString("csrf", "")
        if (t.isNotEmpty()) csrf = t
    }
}
