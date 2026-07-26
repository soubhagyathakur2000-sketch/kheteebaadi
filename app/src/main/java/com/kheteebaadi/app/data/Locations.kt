package com.kheteebaadi.app.data

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

/**
 * Loads the bundled UP location dataset (75 districts, 332 tehsils, 875
 * development blocks) for the cascading dropdowns. District -> Tehsil and
 * District -> Block; block is NOT nested under tehsil.
 */
object Locations {
    var districts: List<String> = emptyList()
        private set
    private var tehsils = JSONObject()
    private var blocks = JSONObject()

    fun load(context: Context) {
        if (districts.isNotEmpty()) return
        val txt = context.assets.open("up_locations.json")
            .bufferedReader().use { it.readText() }
        val o = JSONObject(txt)
        val d = o.getJSONArray("districts")
        districts = (0 until d.length()).map { d.getString(it) }
        tehsils = o.getJSONObject("tehsils")
        blocks = o.getJSONObject("blocks")
    }

    fun tehsils(district: String): List<String> = arr(tehsils.optJSONArray(district))
    fun blocks(district: String): List<String> = arr(blocks.optJSONArray(district))

    private fun arr(a: JSONArray?): List<String> =
        if (a == null) emptyList() else (0 until a.length()).map { a.getString(it) }
}
