package com.yuhsuan.jpwordinfo

import com.google.gson.annotations.SerializedName

data class WordResponse(
    @SerializedName("単語") val word: String?,
    @SerializedName("読み方") val reading: String?,
    @SerializedName("アクセント") val accent: String?,
    @SerializedName("品詞") val partOfSpeech: String?,
    @SerializedName("意味") val meaning: String?,
    @SerializedName("例文") val example: String?
)