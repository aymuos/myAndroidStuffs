package com.aymuos.foodsandforkapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.Toast

class LogInActivity : AppCompatActivity() {


    private lateinit var etEmailInput:EditText
    private lateinit var etPassword:EditText
    private lateinit var btnLoginMain:EditText
    private lateinit var txt3:EditText
    private lateinit var txt4:EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //linking the activity
        setContentView(R.layout.activity_log_in)

        etEmailInput=findViewById(R.id.etEmailInput)
        etPassword=findViewById(R.id.etPasswrd)
        btnLoginMain=findViewById(R.id.btnLoginMain)
        txt3=findViewById(R.id.txt3)
        txt4=findViewById(R.id.txt4)

        btnLoginMain.setOnClickListener {
            Toast.makeText(this@LogInActivity, "Succesfully LoggediN", Toast.LENGTH_LONG).show()

            val intent_btn = Intent(this@LogInActivity, MainActivity::class.java)
            startActivity(intent_btn)
        }
    }
}