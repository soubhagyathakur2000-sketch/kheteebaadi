package com.kheteebaadi.app.data

import android.content.Context

object Prefs {
    private const val P = "kb_prefs"
    private fun sp(c: Context) = c.getSharedPreferences(P, Context.MODE_PRIVATE)

    fun lang(c: Context): String = sp(c).getString("lang", "hi") ?: "hi"
    fun setLang(c: Context, v: String) { sp(c).edit().putString("lang", v).apply() }
}
