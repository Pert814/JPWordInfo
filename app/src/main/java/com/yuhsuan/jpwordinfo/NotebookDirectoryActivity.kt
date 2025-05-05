package com.yuhsuan.jpwordinfo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson

class NotebookDirectoryActivity : AppCompatActivity() {
    private lateinit var tvDirectoryTitle: TextView
    private lateinit var rvNotebookList: RecyclerView
    private lateinit var btnAddNotebook: FloatingActionButton
    private lateinit var bottomNavigationView: BottomNavigationView
    private val gson = Gson()
    private val notebookPrefsKey = "notebook_directory"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notebook_directory)

        // 初始化 UI 元件
        tvDirectoryTitle = findViewById(R.id.tvDirectoryTitle)
        rvNotebookList = findViewById(R.id.rvNotebookList)
        btnAddNotebook = findViewById(R.id.btnAddNotebook)
        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // 設置 RecyclerView
        rvNotebookList.layoutManager = LinearLayoutManager(this)
        rvNotebookList.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))
        val notebooks = loadNotebooks(this)
        rvNotebookList.adapter = NotebookAdapter(notebooks) { notebook ->
            showNotebookActions(notebook)
        }

        // 設置浮動按鈕點擊事件
        btnAddNotebook.setOnClickListener {
            showAddNotebookDialog()
        }

        // 設置底部導航
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_search -> {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                    true
                }
                R.id.navigation_notebook -> true
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.navigation_notebook
    }

    private fun saveNotebooks(notebooks: List<Notebook>) {
        val prefs = getSharedPreferences("NotebookPrefs", MODE_PRIVATE)
        val editor = prefs.edit()
        val notebookJson = gson.toJson(notebooks)
        editor.putString(notebookPrefsKey, notebookJson)
        editor.apply()
    }

    private fun showAddNotebookDialog() {
        val input = EditText(this)
        input.hint = "輸入單字本名稱"
        AlertDialog.Builder(this)
            .setTitle("新增單字本")
            .setView(input)
            .setPositiveButton("確認") { _, _ ->
                val name = input.text.toString().trim()
                if (name.isNotEmpty()) {
                    val notebooks = loadNotebooks(this).toMutableList()
                    if (notebooks.none { it.name == name }) {
                        notebooks.add(Notebook(name))
                        saveNotebooks(notebooks)
                        rvNotebookList.adapter = NotebookAdapter(notebooks) { notebook ->
                            showNotebookActions(notebook)
                        }
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showNotebookActions(notebook: Notebook) {
        AlertDialog.Builder(this)
            .setTitle(notebook.name)
            .setItems(arrayOf("編輯", "刪除")) { _, which ->
                when (which) {
                    0 -> showEditNotebookDialog(notebook)
                    1 -> {
                        val notebooks = loadNotebooks(this).toMutableList()
                        notebooks.remove(notebook)
                        saveNotebooks(notebooks)
                        rvNotebookList.adapter = NotebookAdapter(notebooks) { n ->
                            showNotebookActions(n)
                        }
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }

    private fun showEditNotebookDialog(notebook: Notebook) {
        val input = EditText(this)
        input.setText(notebook.name)
        AlertDialog.Builder(this)
            .setTitle("編輯單字本")
            .setView(input)
            .setPositiveButton("確認") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty() && newName != notebook.name) {
                    val notebooks = loadNotebooks(this).toMutableList()
                    val index = notebooks.indexOf(notebook)
                    if (index != -1 && notebooks.none { it.name == newName }) {
                        notebooks[index] = notebook.copy(name = newName)
                        saveNotebooks(notebooks)
                        rvNotebookList.adapter = NotebookAdapter(notebooks) { n ->
                            showNotebookActions(n)
                        }
                    }
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
}

class NotebookAdapter(
    private val notebooks: List<Notebook>,
    private val onActionClick: (Notebook) -> Unit
) : RecyclerView.Adapter<NotebookAdapter.NotebookViewHolder>() {

    class NotebookViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNotebookName: TextView = itemView.findViewById(R.id.tvNotebookName)
        val btnActions: View = itemView.findViewById(R.id.btnActions)
    }

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): NotebookViewHolder {
        val view = android.view.LayoutInflater.from(parent.context)
            .inflate(R.layout.item_notebook, parent, false)
        return NotebookViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotebookViewHolder, position: Int) {
        val notebook = notebooks[position]
        holder.tvNotebookName.text = notebook.name
        holder.btnActions.setOnClickListener {
            onActionClick(notebook)
        }
    }

    override fun getItemCount(): Int = notebooks.size
}