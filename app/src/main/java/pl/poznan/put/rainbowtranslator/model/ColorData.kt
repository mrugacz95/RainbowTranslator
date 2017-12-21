package pl.poznan.put.rainbowtranslator.model

import org.json.JSONObject
import pl.poznan.put.rainbowtranslator.color.ColorActivity

data class ColorData(
        val color: String,
        val rgb: String
) {
    fun toJson(): JSONObject {
        val colorJson = JSONObject()
        colorJson.put(ColorActivity.RGB, rgb)
        colorJson.put(ColorActivity.COLOR, color)
        return colorJson
    }
}