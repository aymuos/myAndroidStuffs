package com.aymuos.foodandfork.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.aymuos.foodandfork.R

class OrderActivity : AppCompatActivity() {

    private lateinit var btnOrder : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order)

        btnOrder = findViewById(R.id.btnOrder)

        btnOrder.setOnClickListener {
            startActivity(Intent(this@OrderActivity , HomeActivity::class.java))
            finish()
        }

    }

    override fun onBackPressed() {
        //Do Nothing Press ok
    }
}