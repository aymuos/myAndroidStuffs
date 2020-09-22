package com.aymuos.foodandfork.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aymuos.foodandfork.R
import com.aymuos.foodandfork.util.ConnectionManager
import com.aymuos.foodandfork.util.FORGOT_PASSWORD
import org.json.JSONException
import org.json.JSONObject

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var toolbar: Toolbar
    lateinit var etForgotMobile: EditText
    lateinit var etForgotEmail: EditText
    lateinit var btnForgotNext: Button
    lateinit var message:String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etForgotMobile = findViewById(R.id.etForgotMobileNumber)
        etForgotEmail = findViewById(R.id.etForgotEmail)
        btnForgotNext = findViewById(R.id.btnNext)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.title = "Register Yourself"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        btnForgotNext.setOnClickListener {
            val intent=Intent(this@ForgotPasswordActivity, ResetActivity::class.java)
            intent.putExtra("mobile_number",etForgotMobile.text.toString())

            /*val bundle = Bundle()
            bundle.putString("data", "forgot")
            bundle.putString("mobile", etForgotMobile.text.toString())
            bundle.putString("email", etForgotEmail.text.toString())
            intent.putExtra("details", bundle)
            startActivity(intent)*/
            val queue= Volley.newRequestQueue(this@ForgotPasswordActivity)
            val jsonParams= JSONObject()
            jsonParams.put("mobile_number", etForgotMobile.text.toString())
            jsonParams.put("email", etForgotEmail.text.toString())


            if(ConnectionManager().isNetworkAvailable(this@ForgotPasswordActivity)) {
                val jsonRequest = object : JsonObjectRequest(
                    Method.POST,
                    FORGOT_PASSWORD, jsonParams, Response.Listener {
                        try{
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")

                            if (success){

                                val firstTry=data.getBoolean("first_try")
                                //Receiving data from the server
                                if(firstTry){

                                    Toast.makeText(this@ForgotPasswordActivity, "The OTP has been sent to your registered email ID", Toast.LENGTH_SHORT).show()


                                }else{
                                    Toast.makeText(this@ForgotPasswordActivity, "Please refer to the OTP sent before", Toast.LENGTH_SHORT).show()


                                }

                                startActivity(intent)
                                finish()
                            }else{
                                message=data.getString("errorMessage")
                                Toast.makeText(this@ForgotPasswordActivity, message , Toast.LENGTH_SHORT).show()


                            }

                        }catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { error: VolleyError? ->
                        Toast.makeText(this@ForgotPasswordActivity, message , Toast.LENGTH_SHORT).show()
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
                val dialog= AlertDialog.Builder(this@ForgotPasswordActivity)
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
                    ActivityCompat.finishAffinity(this@ForgotPasswordActivity)

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