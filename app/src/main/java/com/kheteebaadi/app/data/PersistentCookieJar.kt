package com.kheteebaadi.app.data

import android.content.Context
import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import org.json.JSONArray
import org.json.JSONObject

/**
 * A cookie jar that persists cookies (notably the PHP session cookie) to
 * SharedPreferences so the user stays logged in across app restarts.
 */
class PersistentCookieJar(context: Context) : CookieJar {

    private val prefs = context.getSharedPreferences("kb_cookies", Context.MODE_PRIVATE)
    private val cache = LinkedHashMap<String, Cookie>()

    init { load() }

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        for (c in cookies) cache[c.name] = c
        persist()
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        val now = System.currentTimeMillis()
        val valid = cache.values.filter { it.expiresAt > now }
        if (valid.size != cache.size) {
            cache.clear()
            valid.forEach { cache[it.name] = it }
            persist()
        }
        return valid
    }

    @Synchronized
    fun clear() {
        cache.clear()
        prefs.edit().clear().apply()
    }

    private fun persist() {
        val arr = JSONArray()
        for (c in cache.values) {
            arr.put(JSONObject().apply {
                put("name", c.name)
                put("value", c.value)
                put("expiresAt", c.expiresAt)
                put("domain", c.domain)
                put("path", c.path)
                put("secure", c.secure)
                put("httpOnly", c.httpOnly)
                put("hostOnly", c.hostOnly)
            })
        }
        prefs.edit().putString("jar", arr.toString()).apply()
    }

    private fun load() {
        val s = prefs.getString("jar", null) ?: return
        try {
            val arr = JSONArray(s)
            for (i in 0 until arr.length()) {
                val o = arr.getJSONObject(i)
                val b = Cookie.Builder()
                    .name(o.getString("name"))
                    .value(o.getString("value"))
                    .path(o.optString("path", "/"))
                    .expiresAt(o.optLong("expiresAt", Long.MAX_VALUE))
                val domain = o.optString("domain", "kheteebaadi.com")
                if (o.optBoolean("hostOnly", true)) b.hostOnlyDomain(domain) else b.domain(domain)
                if (o.optBoolean("secure")) b.secure()
                if (o.optBoolean("httpOnly")) b.httpOnly()
                cache[o.getString("name")] = b.build()
            }
        } catch (_: Exception) {
            // corrupt store — start fresh
            prefs.edit().clear().apply()
        }
    }
}
