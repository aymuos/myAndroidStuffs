package com.aymuos.foodsandforkapp.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.aymuos.foodsandforkapp.R

class LogInActivity : AppCompatActivity() {


    private lateinit var registerYourself: TextView
    private lateinit var login: Button
    private lateinit var etMobileNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var txtForgotPassword: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //linking the activity
        setContentView(R.layout.activity_log_in)

        /*Initialising the views with the ones defined in the XML*/
        etMobileNumber = findViewById(R.id.etMobileNumber)
        etPassword = findViewById(R.id.etPassword)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        registerYourself = findViewById(R.id.txtRegisterYourself)
        login = findViewById(R.id.btnLogin)

        /*Handling the clicks using the setOnClickListener method*/
        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
        registerYourself.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }


        login.setOnClickListener {

            /*Declaring the intent which sets up the route for the navigation of the activity*/
            val intent = Intent(this@LogInActivity, DashboardActivity::class.java)

            /*Declaring the bundle object which will carry the data
            * You can send the data inside intents
            *
            * We specifically used Bundle just to demonstrate a new technique*/
            val bundle = Bundle()

            /*Setting a value data which is activity specific. This will be used to identify from where the data was sent*/
            bundle.putString("data", "login")

            /*Putting the values in Bundle*/
            bundle.putString("mobile", etMobileNumber.text.toString())
            bundle.putString("password", etPassword.text.toString())

            /*Putting the Bundle to be shipped with the intent*/
            intent.putExtra("details", bundle)

            /*Starting the new activity by sending the intent in the startActivity method*/
            startActivity(intent)
        }
    }
}