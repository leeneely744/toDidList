package com.example.kouhei.todidlist

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_diary.*
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import kotlin.concurrent.thread

class EditDiaryActivity : MyAppCompatActivity() {

    private var db: AppDatabase? = null
    private var nowTimeStamp: Long = 0
    private var selectDate: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_diary)

        db = AppDatabase.getInstance(this)

        // アプリ上部のToolbarを呼び出す
        setSupportActionBar(edit_page_toolbar)

        // 戻るボタンを表示
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // kotlinではgetIntent()は"intent"でOK
        nowTimeStamp = intent.getLongExtra(EXTRA_DATE, 0)
        selectDate = getSelectDate(nowTimeStamp)

        // Toolbarの色を、選択された月のテーマカラーに変更
        edit_page_toolbar.setBackgroundColor(getMonthColor(this, selectDate.toString().substring(4, 6)))

        // 選択してる日付の日記Entityを取得し、日記本文を表示する
        thread {
            val diary = db?.diaryDao()?.getEntityWithDate(selectDate)

            // DiaryのEntityはnullである場合がある。
            if (diary != null){
                diaryPanel.setText(diary.diaryText)
            } else {
                // TODO when click datePanel in MainActivity, sometimes error here.
                diaryPanel.setText(R.string.diary_yet)
            }
        }
    }

    /**
     * Toolbarのアイテムのどれかをクリックしたとき、システムがこのメソッドを呼び出す。
     */
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_save -> {
            saveDiary()

            // todo: Intentを作成する部分はどうにかして共通化したい
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(EXTRA_DATE, nowTimeStamp) // MainPageから来たintentをそのまま返す
            intent.putExtra(FROM_CLASS, this.localClassName)
            moveToAnotherPage(intent)
            true
        }

        // 戻るボタン
        // ※R.id.homeは自分が作ったものなので反応しない。android.R.id.homeはAndroid SDKのもの
        android.R.id.home -> {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra(EXTRA_DATE, nowTimeStamp) // MainPageから来たintentをそのまま返す
            intent.putExtra(FROM_CLASS, this.localClassName)
            moveToAnotherPage(intent)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    /**
     * 日記をsaveする
     * UpdateかInsertかはDiaryのEntityがnullかどうかで判断
     */
    fun saveDiary() {
        val diaryDao = db?.diaryDao()
        thread {
            val diary = diaryDao?.getEntityWithDate(selectDate)

            if (diary != null){
                diaryDao.updateDiaryWithDate(diaryPanel.text.toString(), selectDate)
            } else {
                val diary = Diary()
                diary.diaryText = diaryPanel.text.toString()
                diary.calendarDate = selectDate
                diaryDao?.insert(diary)
            }
        }
    }

    /**
     * Toolbarにアイコンを表示する
     */
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit, menu)
        return true
    }
}