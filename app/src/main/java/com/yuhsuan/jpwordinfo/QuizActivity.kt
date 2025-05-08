package com.yuhsuan.jpwordinfo

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {
    private lateinit var tvQuizWord: TextView
    private lateinit var btnShowAnswer: Button
    private lateinit var btnNext: Button
    private lateinit var ivBack: ImageView
    private lateinit var notebookName: String
    private var words: List<WordResponse> = emptyList()
    private var currentIndex = 0
    private var isShowingAnswer = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        // 初始化 UI 元件
        tvQuizWord = findViewById(R.id.tvQuizWord)
        btnShowAnswer = findViewById(R.id.btnShowAnswer)
        btnNext = findViewById(R.id.btnNext)
        ivBack = findViewById(R.id.ivBack)

        // 從 Intent 獲取單字本名稱
        notebookName = intent.getStringExtra("notebookName") ?: "無標題單字本"
        Log.d("QuizActivity", "Received notebookName: $notebookName")

        // 載入單字本的單字
        words = loadWordsFromNotebook(notebookName)
        if (words.isEmpty()) {
            tvQuizWord.text = "該單字本沒有單字"
            btnShowAnswer.isEnabled = false
            btnNext.isEnabled = false
            return
        }

        // 顯示第一張單字卡
        showCurrentWord()

        // 設置按鈕點擊事件
        btnShowAnswer.setOnClickListener {
            Log.d("QuizActivity", "Show answer button clicked")
            toggleAnswer()
        }

        btnNext.setOnClickListener {
            Log.d("QuizActivity", "Next button clicked")
            isShowingAnswer = false
            showNextWord()
        }

        ivBack.setOnClickListener {
            Log.d("QuizActivity", "Back button clicked")
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun showCurrentWord() {
        val word = words[currentIndex]
        tvQuizWord.text = if (!isShowingAnswer) {
            word.word ?: "無單字"
        } else {
            val answer = """
                讀音：${word.reading ?: "無讀音"}
                重音：${word.accent ?: "無重音"}
                詞性：${word.partOfSpeech ?: "無詞性"}
                意思：${word.meaning ?: "無意思"}
                例句：${word.example ?: "無例句"}
            """.trimIndent()
            answer
        }
    }

    private fun toggleAnswer() {
        isShowingAnswer = !isShowingAnswer
        showCurrentWord()
    }

    private fun showNextWord() {
        if (currentIndex >= words.size - 1) {
            currentIndex = 0 // 循環播放
        } else {
            currentIndex++
        }
        isShowingAnswer = false
        showCurrentWord()
    }

    private fun loadWordsFromNotebook(notebookName: String): List<WordResponse> {
        val notebooks = loadNotebooks(this)
        return notebooks.find { it.name == notebookName }?.words ?: emptyList()
    }
}