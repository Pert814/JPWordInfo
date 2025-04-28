package com.yuhsuan.jpwordinfo

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.app.AlertDialog
import android.view.View

class MainActivity : AppCompatActivity() {
    private lateinit var etWordInput: EditText
    private lateinit var btnSearch: Button
    private lateinit var tvResult: TextView
    private lateinit var btnHistory: Button
    private lateinit var loadingView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 初始化 UI 元件
        etWordInput = findViewById(R.id.etWordInput)
        btnSearch = findViewById(R.id.btnSearch)
        tvResult = findViewById(R.id.tvResult)
        btnHistory = findViewById(R.id.btnHistory)
        loadingView = findViewById(R.id.loadingView)

        // 設定查詢按鈕點擊事件
        btnSearch.setOnClickListener {
            val word = etWordInput.text.toString().trim()
            if (word.isNotEmpty()) {
                searchWord(word)
            } else {
                tvResult.text = "請輸入單字"
            }
        }

        // 設定歷史紀錄按鈕點擊事件
        btnHistory.setOnClickListener {
            showSearchHistoryDialog()
        }
    }

    private fun searchWord(word: String) {
        // 顯示載入狀態
        loadingView.visibility = View.VISIBLE
        // 清空
        tvResult.text = ""
        // 儲存搜尋歷史
        saveSearchHistory(word)

        val apiService = RetrofitClient.apiService
        apiService.searchWord(word).enqueue(object : Callback<WordResponse> {
            override fun onResponse(call: Call<WordResponse>, response: Response<WordResponse>) {
                if (response.isSuccessful) {
                    // 隱藏載入狀態
                    loadingView.visibility = View.GONE
                    val wordData = response.body()
                    wordData?.let {
                        tvResult.text = getString(
                            R.string.word_result_template,
                            it.word ?: "...",
                            it.reading ?: "...",
                            it.accent ?: "...",
                            it.partOfSpeech ?: "...",
                            it.meaning ?: "...",
                            it.example ?: "..."
                        )
                    } ?: run {
                        tvResult.text = getString(R.string.empty_result)
                    }
                } else {
                    tvResult.text = getString(R.string.query_failed, response.code())
                }
            }

            override fun onFailure(call: Call<WordResponse>, t: Throwable) {
                // 隱藏載入狀態
                loadingView.visibility = View.GONE
                tvResult.text = getString(R.string.network_error, t.message)}
        })
    }

    private fun saveSearchHistory(word: String) {
        val prefs = getSharedPreferences("SearchHistory", MODE_PRIVATE)
        val editor = prefs.edit()

        // 取得現有歷史（以字串形式儲存，用 | 分隔）
        val historyString = prefs.getString("history", "") ?: ""
        val historyList = if (historyString.isNotEmpty()) {
            historyString.split("|").toMutableList()
        } else {
            mutableListOf()
        }

        // 移除重複的單字（如果已存在）
        historyList.remove(word)

        // 新增最新單字到列表開頭
        historyList.add(0, word)

        // 限制最多 10 筆
        if (historyList.size > 10) {
            historyList.removeAt(historyList.size - 1)
        }

        // 將列表轉回字串並儲存
        val updatedHistoryString = historyList.joinToString("|")
        editor.putString("history", updatedHistoryString)
        editor.apply()
    }

    private fun showSearchHistoryDialog() {
        // 取得搜尋歷史
        val prefs = getSharedPreferences("SearchHistory", MODE_PRIVATE)
        val historyString = prefs.getString("history", "") ?: ""
        val historyList = if (historyString.isNotEmpty()) {
            historyString.split("|")
        } else {
            listOf(getString(R.string.no_history))
        }

        // 建立對話框
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.search_history_title)
            .setNegativeButton(android.R.string.cancel, null)
            .create()

        // 建立 ListView 顯示歷史紀錄
        val listView = ListView(this)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, historyList)
        listView.adapter = adapter

        // 設定 ListView 的點擊事件
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedWord = historyList[position]
            if (selectedWord != getString(R.string.no_history)) {
                etWordInput.setText(selectedWord)
                searchWord(selectedWord)
            }
            dialog.dismiss()
        }

        // 將 ListView 設置到對話框
        dialog.setView(listView)
        dialog.show()
    }
}