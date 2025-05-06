package com.yuhsuan.jpwordinfo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import android.view.LayoutInflater

class WordNotebookActivity : AppCompatActivity() {
    private lateinit var tvNotebookTitle: TextView
    private lateinit var rvWordList: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_notebook)

        // 初始化 UI 元件
        tvNotebookTitle = findViewById(R.id.tvNotebookTitle)
        rvWordList = findViewById(R.id.rvWordList)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // 從 Intent 獲取單字本名稱
        val notebookName = intent.getStringExtra("notebookName") ?: "無標題單字本"
        tvNotebookTitle.text = notebookName

        // 載入單字列表並設置 RecyclerView
        rvWordList.layoutManager = LinearLayoutManager(this)
        val words = loadWordsFromNotebook(notebookName)
        rvWordList.adapter = WordAdapter(words)

        // 設置底部導航
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_notebook -> {
                    startActivity(Intent(this, NotebookDirectoryActivity::class.java))
                    finish()
                    true
                }
                else -> false
            }
        }
    }

    private fun loadWordsFromNotebook(notebookName: String): List<WordResponse> {
        val notebooks = loadNotebooks(this)
        val foundNotebook = notebooks.find { it.name == notebookName }
        return foundNotebook?.words ?: emptyList()
    }
}

class WordAdapter(
    private val words: List<WordResponse>
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {
    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tvWord)
        val tvReading: TextView = itemView.findViewById(R.id.tvReading)
        val tvAccent: TextView = itemView.findViewById(R.id.tvAccent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]
        holder.tvWord.text = word.word ?: "無單字"
        holder.tvReading.text = word.reading ?: "無讀音"
        holder.tvAccent.text = word.accent ?: "無重音"
    }

    override fun getItemCount(): Int = words.size
}