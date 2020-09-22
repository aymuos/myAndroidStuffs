package com.aymuos.foodandfork.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aymuos.foodandfork.R
import com.aymuos.foodandfork.util.ConnectionManager
import com.aymuos.foodandfork.util.RESET_PASSWORD
import org.json.JSONException
import org.json.JSONObject

class ResetActivity : AppCompatActivity() {

    lateinit var btnSubmit:Button
    lateinit var etOTP:EditText
    lateinit var etNewPassword:EditText
    lateinit var etConfirmPassword:EditText
    var etNumber:String?="9876543210"
    lateinit var message:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

        etOTP=findViewById(R.id.etOTP)
        etNewPassword=findViewById(R.id.etNewPassword)
        etConfirmPassword=findViewById(R.id.etConfirmPassword)
        btnSubmit=findViewById(R.id.btnSubmit)

        etNumber=intent.getStringExtra("mobile_number")



        btnSubmit.setOnClickListener {
            if(etNewPassword.text.toString().equals(etConfirmPassword.text.toString()))
            {
                val intent = Intent(this@ResetActivity, LoginActivity::class.java)

            /*val bundle = Bundle()
            bundle.putString("data", "forgot")
            bundle.putString("mobile", etForgotMobile.text.toString())
            bundle.putString("email", etForgotEmail.text.toString())
            intent.putExtra("details", bundle)
            startActivity(intent)*/
            val queue= Volley.newRequestQueue(this@ResetActivity)
            val jsonParams= JSONObject()
            jsonParams.put("otp", etOTP.text.toString())
            jsonParams.put("password", etNewPassword.text.toString())
            jsonParams.put("mobile_number", etNumber)


            if(ConnectionManager().isNetworkAvailable(this@ResetActivity)) {
                val jsonRequest = object : JsonObjectRequest(
                    Method.POST,
                    RESET_PASSWORD, jsonParams, Response.Listener {
                        try{
                            val data = it.getJSONObject("data")
                            val success = data.getBoolean("success")
                            if (success){

                                val msg=data.getString("successMessage")


                                Toast.makeText(this@ResetActivity, msg, Toast.LENGTH_SHORT).show()
                                startActivity(intent)
                                finish()
                            }else{
                                message=data.getString("errorMessage")
                                Toast.makeText(this@ResetActivity, message , Toast.LENGTH_SHORT).show()

                            }

                        }catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }, Response.ErrorListener { error: VolleyError? ->
                        Toast.makeText(this@ResetActivity, message , Toast.LENGTH_SHORT).show()
                    }
                ) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "f7bddb8be3c1be"
                        return headers
                    }
                }
                queue.add(jsonRequest)
            }else{

                Toast.makeText(this@ResetActivity, "Internet Connection not found", Toast.LENGTH_SHORT).show()


            }
            }else{
                Toast.makeText(this@ResetActivity, "Passwords do not match!! Try Again", Toast.LENGTH_SHORT).show()

            }

        }


    }
}