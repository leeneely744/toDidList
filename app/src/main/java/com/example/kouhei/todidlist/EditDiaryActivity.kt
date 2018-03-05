package com.example.kouhei.todidlist

import android.os.Bundle
import kotlinx.android.synthetic.main.activity_edit_diary.*
import MyAppCompatActivity

class EditDiaryActivity : MyAppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_diary)

        // kotlinではgetIntent()はいらない
        var selectedDate = intent.getStringExtra(EXTRA_DATE)

        diaryText.setText("hello EditDiaryActivity !")
    }
}
