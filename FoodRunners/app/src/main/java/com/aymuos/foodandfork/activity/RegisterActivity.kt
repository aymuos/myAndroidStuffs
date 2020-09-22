package com.aymuos.foodandfork.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aymuos.foodandfork.R
import com.aymuos.foodandfork.util.ConnectionManager
import com.aymuos.foodandfork.util.REGISTER
import org.json.JSONException
import org.json.JSONObject

class RegisterActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var btnRegister: Button
    lateinit var etName: EditText
    lateinit var etPhoneNumber: EditText
    lateinit var etRegisterPassword: EditText
    lateinit var etEmail: EditText
    lateinit var etAddress: EditText
    lateinit var etConfirmPassword: EditText
    lateinit var sharedPreferences: SharedPreferences
    lateinit var message:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedPreferences = getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        setContentView(R.layout.activity_register)



        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        etName = findViewById(R.id.etName)
        etPhoneNumber = findViewById(R.id.etRegisterMobileNumber)
        etEmail = findViewById(R.id.etEmail)
        etRegisterPassword = findViewById(R.id.etRegisterPassword)
        etConfirmPassword = findViewById(R.id.etConfirmPassword)
        etAddress = findViewById(R.id.etDeliveryAdd)
        btnRegister = findViewById(R.id.btnRegister)

        btnRegister.setOnClickListener {
            val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
            /*val bundle = Bundle()
            bundle.putString("data", "register")
            bundle.putString("name", etName.text.toString())
            bundle.putString("mobile", etPhoneNumber.text.toString())
            bundle.putString("password", etRegisterPassword.text.toString())
            bundle.putString("address", etAddress.text.toString())
            intent.putExtra("details", bundle)
            startActivity(intent)*/

            val queue= Volley.newRequestQueue(this@RegisterActivity)
            val jsonParams=JSONObject()
            jsonParams.put("name",etName.text.toString())
            jsonParams.put("mobile_number", etPhoneNumber.text.toString())
            jsonParams.put("password", etRegisterPassword.text.toString())
            jsonParams.put("address", etAddress.text.toString())
            jsonParams.put("email",etEmail.text.toString())

            if(ConnectionManager().isNetworkAvailable(this@RegisterActivity)) {
                val jsonRequest = object : JsonObjectRequest(
                    Method.POST,
                    REGISTER, jsonParams, Response.Listener {
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

                                Toast.makeText(this@RegisterActivity, "User Registered Successfully", Toast.LENGTH_SHORT).show()

                                startActivity(intent)
                                finish()
                            }else{
                                message=data.getString("errorMessage")
                                Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()

                            }

                        }catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener {error: VolleyError? ->
                        Toast.makeText(this@RegisterActivity, message, Toast.LENGTH_SHORT).show()
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
                val dialog= AlertDialog.Builder(this@RegisterActivity)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not Found")
                dialog.setPositiveButton("Open Settings"){text,listener ->
                    //Do nothing
                    val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(settingsIntent)
                    finish()

                }
                dialog.setNegativeButton("Exit"){text,listener ->
                    //Do nothing
                    ActivityCompat.finishAffinity(this@RegisterActivity)

                }
                dialog.create()
                dialog.show()

            }



        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


}