package com.yuhsuan.jpwordinfo

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

class QuizDirectoryActivity : AppCompatActivity() {
    private lateinit var rvQuizNotebooks: RecyclerView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz_directory)

        // 初始化 UI 元件
        rvQuizNotebooks = findViewById(R.id.rvQuizNotebooks)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // 設置 RecyclerView
        rvQuizNotebooks.layoutManager = LinearLayoutManager(this)
        val notebooks = loadNotebooks(this)
        rvQuizNotebooks.adapter = QuizNotebookAdapter(notebooks, this)


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
                R.id.navigation_quiz -> true
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.navigation_quiz
    }
}

class QuizNotebookAdapter(
    private val notebooks: List<Notebook>,
    private val activity: QuizDirectoryActivity
) : RecyclerView.Adapter<QuizNotebookAdapter.NotebookViewHolder>() {

    class NotebookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNotebookName: TextView = itemView.findViewById(R.id.tvNotebookName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotebookViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notebook, parent, false)
        return NotebookViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotebookViewHolder, position: Int) {
        val notebook = notebooks[position]
        holder.tvNotebookName.text = notebook.name
        holder.itemView.setOnClickListener {
            Log.d("QuizNotebookAdapter", "Clicked on notebook: ${notebook.name}")
            val intent = Intent(activity, QuizActivity::class.java)
            intent.putExtra("notebookName", notebook.name)
            activity.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = notebooks.size
}