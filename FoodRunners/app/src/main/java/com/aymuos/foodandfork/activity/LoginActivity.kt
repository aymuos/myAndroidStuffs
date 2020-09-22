package com.aymuos.foodandfork.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aymuos.foodandfork.R
import com.aymuos.foodandfork.util.ConnectionManager
import com.aymuos.foodandfork.util.LOGIN
import org.json.JSONException
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var etMobileNumber: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var txtForgotPassword : TextView
    private lateinit var txtRegisterYourself: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var message:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false)
        setContentView(R.layout.activity_login)
        if(isLoggedIn){
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }




        etMobileNumber = findViewById(R.id.etMobileNumber)!!
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        txtForgotPassword = findViewById(R.id.txtForgotPassword)
        txtRegisterYourself = findViewById(R.id.txtRegister)

        txtForgotPassword.setOnClickListener {
            startActivity(Intent(this@LoginActivity, ForgotPasswordActivity::class.java))
        }
        txtRegisterYourself.setOnClickListener {
            startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
        }

        btnLogin.setOnClickListener {
            val intent = Intent(this@LoginActivity, HomeActivity::class.java)
            /*val bundle = Bundle()
            bundle.putString("data", "login")
            bundle.putString("mobile", etMobileNumber.text.toString())
            bundle.putString("password", etPassword.text.toString())
            intent.putExtra("details", bundle)
            startActivity(intent)*/
            val queue= Volley.newRequestQueue(this@LoginActivity)
            val jsonParams= JSONObject()
            jsonParams.put("mobile_number", etMobileNumber.text.toString())
            jsonParams.put("password", etPassword.text.toString())


            if(ConnectionManager().isNetworkAvailable(this@LoginActivity)) {
                val jsonRequest = object : JsonObjectRequest(
                    Method.POST,
                    LOGIN, jsonParams, Response.Listener {
                        try{
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success){
                                //Receiving data from the server
                                val resData=data.getJSONObject("data")
                                sharedPreferences.edit().putString("user_id",resData.getString("user_id")).apply()
                                sharedPreferences.edit().putString("name",resData.getString("name")).apply()
                                sharedPreferences.edit().putString("email",resData.getString("email")).apply()
                                sharedPreferences.edit().putString("mobile_number",resData.getString("mobile_number")).apply()
                                sharedPreferences.edit().putString("address",resData.getString("address")).apply()
                                sharedPreferences.edit().putBoolean("isLoggedIn",true).apply()

                                Toast.makeText(this@LoginActivity, "Logged In", Toast.LENGTH_SHORT).show()

                                startActivity(intent)
                                finish()


                            }else{
                                message=data.getString("errorMessage")
                                Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                            }

                        }catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener {
                        Toast.makeText(this@LoginActivity, message, Toast.LENGTH_SHORT).show()
                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "21979fa5956e7b"
                        return headers
                    }
                }
                queue.add(jsonRequest)
            }else{

                //if false
                val dialog= AlertDialog.Builder(this@LoginActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not Found")
                dialog.setPositiveButton("Open Settings"){ _, _ ->
                    //Open Settings
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()

                }
                dialog.setNegativeButton("Exit"){ _, _ ->
                    //Finishes App
                    ActivityCompat.finishAffinity(this@LoginActivity)

                }
                dialog.create()
                dialog.show()

            }
        }
    }
    }

