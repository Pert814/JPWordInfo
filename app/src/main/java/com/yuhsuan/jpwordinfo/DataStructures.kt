package com.yuhsuan.jpwordinfo

import android.content.Context
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken

data class WordResponse(
    @SerializedName("単語") val word: String?,
    @SerializedName("読み方") val reading: String?,
    @SerializedName("アクセント") val accent: String?,
    @SerializedName("品詞") val partOfSpeech: String?,
    @SerializedName("意味") val meaning: String?,
    @SerializedName("例文") val example: String?
)

data class Notebook(
    val name: String,           // 單字本的名稱（如 "日常用語" 或 "初級單字"）
    val words: List<WordResponse> = emptyList() // 該單字本包含的單字列表
)

// 工具函數，載入單字本列表
fun loadNotebooks(context: Context): List<Notebook> {
    val prefs = context.getSharedPreferences("NotebookPrefs", MODE_PRIVATE)
    val notebookJson = prefs.getString("notebook_directory", null)
    return if (notebookJson != null) {
        val type = object : TypeToken<List<Notebook>>() {}.type
        val gson = Gson()
        gson.fromJson(notebookJson, type) ?: emptyList()
    } else {
        emptyList()
    }
}