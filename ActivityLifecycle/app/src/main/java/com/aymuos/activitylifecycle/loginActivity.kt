package com.aymuos.activitylifecycle

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast

class loginActivity : AppCompatActivity() {

    /*creating variables*/
  lateinit  var etMobNo:EditText
    lateinit var etPassword:EditText
    lateinit var btnGo: Button
    lateinit var txtForgotPass:TextView
    lateinit var txtRegister:TextView
    /*match user input values*/
    val validMobileNumber ="0123456789"
    val validPassword= arrayOf("tony","Steve","bruce", "thanos")

    //declaring shared preference variable
    lateinit var sharedPreferences: SharedPreferences



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences=getSharedPreferences(getString(R.string.preference_file_name), Context.MODE_PRIVATE)
        setContentView(R.layout.activity_login)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)
        //check if the boolean is true or false


        if(isLoggedIn){
            val intent=Intent(this@loginActivity,MainActivity::class.java)
        } else {
            setContentView(R.id.activity_login)

        }


        title="Log in"
        etMobNo=findViewById(R.id.etMobNo)
        etPassword=findViewById(R.id.etPassword)
        btnGo=findViewById(R.id.btnGo)
        txtForgotPass=findViewById(R.id.txtForgotPass)
        txtRegister=findViewById(R.id.txtRegister)






        btnGo.setOnClickListener{
            val mobileNumber = etMobNo.text.toString()
            val password=etPassword.text.toString()

            val intent = Intent(this@loginActivity, MainActivity::class.java)
            var nameOfAvenger = "Avenger"
            if((mobileNumber==validMobileNumber)){

                if (password == validPassword[0]){
                    savePreferences()
                    nameOfAvenger="Iron Man"
                    intent.putExtra("Name",nameOfAvenger)

                       startActivity(intent)
                    //we want the screen to open if any of the passwords are correct
                }
                else if (password == validPassword[1]){
                    savePreferences()
                    nameOfAvenger="Captain AMerica"
                    intent.putExtra("Name",nameOfAvenger)

                      startActivity(intent)
                } else if (password == validPassword[2]){
                    savePreferences()
                    nameOfAvenger="Hulk"
                    intent.putExtra("Name",nameOfAvenger)
                      startActivity(intent)
                } else if (password == validPassword[3]){
                    savePreferences()
                    nameOfAvenger="Thanos"
                    intent.putExtra("Name",nameOfAvenger)

                     startActivity(intent)
                    SharedPreferences.edit().
                }


         // if (( mobileNumber == validMobileNumber) && (validPassword.contains(password))) {
            //  val intent = Intent(this@loginActivity, MainActivity::class.java)
            //    startActivity(intent)
          }
          else {
              Toast.makeText(this@loginActivity,"Incorrect Details",Toast.LENGTH_LONG)
          }
        }

       /* */ 
        val mobileNumber = sharedPreferences.edit().getString("mobileNumber").apply()
  





    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    fun savePreferences(){
        sharedPreferences.edit().putBoolean("isLoggedIn" ,true).apply()
    }

}