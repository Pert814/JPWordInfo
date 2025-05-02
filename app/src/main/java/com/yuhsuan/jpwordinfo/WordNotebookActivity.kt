package com.yuhsuan.jpwordinfo

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class WordNotebookActivity : AppCompatActivity() {
    private lateinit var tvNotebookTitle: TextView
    private lateinit var rvWordList: RecyclerView
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_word_notebook)

        tvNotebookTitle = findViewById(R.id.tvNotebookTitle)
        rvWordList = findViewById(R.id.rvWordList)

        rvWordList.layoutManager = LinearLayoutManager(this)
        rvWordList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        val words = loadWordsFromNotebook()
        if (words.isEmpty()) {
            tvNotebookTitle.text = getString(R.string.no_words)
        }
        rvWordList.adapter = WordAdapter(words)
    }

    private fun loadWordsFromNotebook(): List<WordResponse> {
        val prefs = getSharedPreferences("WordNotebook", MODE_PRIVATE)
        val notebookJson = prefs.getString("notebook", null)
        android.util.Log.d("WordNotebookActivity", "Loaded notebookJson: $notebookJson")
        return if (notebookJson != null) {
            val type = object : TypeToken<List<WordResponse>>() {}.type
            val words: List<WordResponse> = gson.fromJson(notebookJson, type) ?: emptyList()
            android.util.Log.d("WordNotebookActivity", "Parsed words: $words")
            words
        } else {
            android.util.Log.d("WordNotebookActivity", "Notebook is empty")
            emptyList()
        }
    }
}

class WordAdapter(private val words: List<WordResponse>) : RecyclerView.Adapter<WordAdapter.WordViewHolder>() {
    class WordViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView) {
        val tvWordItem: TextView = itemView.findViewById(R.id.tvWordItem)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): WordViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_word, parent, false)
        return WordViewHolder(view)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = words[position]
        holder.tvWordItem.text = holder.itemView.context.getString(
            R.string.word_result_template,
            word.word,
            word.reading,
            word.accent,
            word.partOfSpeech,
            word.meaning,
            word.example
        )
    }

    override fun getItemCount(): Int = words.size
}