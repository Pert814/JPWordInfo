package com.yuhsuan.jpwordinfo

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson

class WordDetailActivity : AppCompatActivity() {
    private lateinit var etWord: EditText
    private lateinit var etReading: EditText
    private lateinit var etAccent: EditText
    private lateinit var etPartOfSpeech: EditText
    private lateinit var etMeaning: EditText
    private lateinit var etExample: EditText
    private lateinit var btnSave: Button
    private lateinit var ivBack: ImageView
    private val gson = Gson()
    private lateinit var word: WordResponse
    private lateinit var notebookName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_detail)

        // 初始化 UI 元件
        etWord = findViewById(R.id.etWord)
        etReading = findViewById(R.id.etReading)
        etAccent = findViewById(R.id.etAccent)
        etPartOfSpeech = findViewById(R.id.etPartOfSpeech)
        etMeaning = findViewById(R.id.etMeaning)
        etExample = findViewById(R.id.etExample)
        btnSave = findViewById(R.id.btnSave)
        ivBack = findViewById(R.id.ivBack)

        // 從 Intent 獲取單字資訊
        val wordJson = intent.getStringExtra("wordJson")
        notebookName = intent.getStringExtra("notebookName") ?: ""
        Log.d("WordDetailActivity", "Received wordJson: $wordJson, notebookName: $notebookName")

        if (wordJson != null && notebookName.isNotEmpty()) {
            word = gson.fromJson(wordJson, WordResponse::class.java)

            // 顯示單字資訊
            etWord.setText(word.word ?: "無單字")
            etReading.setText(word.reading ?: "無讀音")
            etAccent.setText(word.accent ?: "無重音")
            etPartOfSpeech.setText(word.partOfSpeech ?: "無詞性")
            etMeaning.setText(word.meaning ?: "無意思")
            etExample.setText(word.example ?: "無例句")
        } else {
            Log.e("WordDetailActivity", "Missing wordJson or notebookName")
            finish()
            return
        }

        // 返回按鈕點擊事件
        ivBack.setOnClickListener {
            Log.d("WordDetailActivity", "Back button clicked")
            finish()
        }

        // 保存按鈕點擊事件
        btnSave.setOnClickListener {
            Log.d("WordDetailActivity", "Save button clicked")
            // 更新單字資訊
            val updatedWord = WordResponse(
                word = etWord.text.toString().trim(),
                reading = etReading.text.toString().trim(),
                accent = etAccent.text.toString().trim(),
                partOfSpeech = etPartOfSpeech.text.toString().trim(),
                meaning = etMeaning.text.toString().trim(),
                example = etExample.text.toString().trim()
            )

            // 更新資料
            updateWordInNotebook(updatedWord)

            // 返回結果
            setResult(Activity.RESULT_OK)
            finish()
        }
    }

    private fun updateWordInNotebook(updatedWord: WordResponse) {
        val notebooks = loadNotebooks(this).toMutableList()
        val notebook = notebooks.find { it.name == notebookName }
        if (notebook != null) {
            val words = notebook.words.toMutableList()
            val index = words.indexOfFirst { it.word == word.word }
            if (index != -1) {
                words[index] = updatedWord
                val updatedNotebook = notebook.copy(words = words)
                val notebookIndex = notebooks.indexOf(notebook)
                notebooks[notebookIndex] = updatedNotebook
                saveNotebooks(notebooks, this)
            }
        }
    }

    private fun saveNotebooks(notebooks: List<Notebook>, context: Context) {
        val prefs = context.getSharedPreferences("NotebookPrefs", MODE_PRIVATE)
        val editor = prefs.edit()
        val notebookJson = gson.toJson(notebooks)
        editor.putString("notebook_directory", notebookJson)
        editor.apply()
    }
}