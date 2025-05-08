package com.yuhsuan.jpwordinfo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson

class WordNotebookActivity : AppCompatActivity() {
    private lateinit var tvNotebookTitle: TextView
    private lateinit var rvWordList: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var ivBack: ImageView
    private lateinit var notebookName: String
    private var words: List<WordResponse> = emptyList()
    private lateinit var adapter: WordAdapter
    private lateinit var editWordLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_word_notebook)
            Log.d("WordNotebookActivity", "Activity created")

            // 初始化 UI 元件
            tvNotebookTitle = findViewById(R.id.tvNotebookTitle)
            rvWordList = findViewById(R.id.rvWordList)
            bottomNavigationView = findViewById(R.id.bottom_navigation)
            ivBack = findViewById(R.id.ivBack)

            // 從 Intent 獲取單字本名稱
            notebookName = intent.getStringExtra("notebookName") ?: "無標題單字本"
            tvNotebookTitle.text = notebookName

            // 載入單字列表並設置 RecyclerView
            rvWordList.layoutManager = LinearLayoutManager(this)
            words = loadWordsFromNotebook(notebookName)
            adapter = WordAdapter(words, notebookName, this)
            rvWordList.adapter = adapter

            // 設置返回圖標點擊事件
            ivBack.setOnClickListener {
                Log.d("WordNotebookActivity", "Back button clicked")
                val intent = Intent(this, NotebookDirectoryActivity::class.java)
                startActivity(intent)
                finish()
            }

            // 初始化 ActivityResultLauncher
            editWordLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    // 重新載入數據並刷新列表
                    words = loadWordsFromNotebook(notebookName)
                    adapter = WordAdapter(words, notebookName, this)
                    rvWordList.adapter = adapter
                }
            }

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
        } catch (e: Exception) {
            Log.e("WordNotebookActivity", "Error in onCreate: ${e.message}", e)
        }
    }

    private fun loadWordsFromNotebook(notebookName: String): List<WordResponse> {
        val notebooks = loadNotebooks(this)
        return notebooks.find { it.name == notebookName }?.words ?: emptyList()
    }

    // 提供公開的 getter 方法
    fun getEditWordLauncher(): ActivityResultLauncher<Intent> = editWordLauncher
}

class WordAdapter(
    private val words: List<WordResponse>,
    private val notebookName: String,
    private val activity: WordNotebookActivity
) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {
    private val gson = Gson()

    class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tvWord)
        val tvReading: TextView = itemView.findViewById(R.id.tvReading)
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

        // 設置點擊事件，跳轉到 WordDetailActivity
        holder.itemView.setOnClickListener {
            Log.d("WordAdapter", "Clicked on word: ${word.word}")
            val intent = Intent(activity, WordDetailActivity::class.java)
            intent.putExtra("wordJson", gson.toJson(word))
            intent.putExtra("notebookName", notebookName)
            activity.getEditWordLauncher().launch(intent)
        }
    }

    override fun getItemCount(): Int = words.size
}