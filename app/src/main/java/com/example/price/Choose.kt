package com.example.price

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_choose.*

class Choose : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose)

        adminPanel.setOnClickListener {
            startActivity(Intent(this,AdminActivity::class.java))
        }

        visitorPanel.setOnClickListener {
            startActivity(Intent(this,MainActivity::class.java))
        }
    }


}