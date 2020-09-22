package com.aymuos.foodandfork.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.aymuos.foodandfork.R
import com.aymuos.foodandfork.adapter.CartAdapter
import com.aymuos.foodandfork.adapter.RestaurantMenuAdapter
import com.aymuos.foodandfork.database.OrderEntity
import com.aymuos.foodandfork.database.RestaurantDatabase
import com.aymuos.foodandfork.model.ResMenu
import com.aymuos.foodandfork.util.ConnectionManager
import com.aymuos.foodandfork.util.PLACE_ORDER
import org.json.JSONArray
import org.json.JSONObject

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutManager: RecyclerView.LayoutManager
    private lateinit var coordinateLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
   // private val orders=ArrayList<ResMenu>()
    private lateinit var progressLayout: RelativeLayout
    private lateinit var progressBar: ProgressBar
    lateinit var rlMyCart: RelativeLayout
    private lateinit var txtResName: TextView
    private lateinit var recyclerAdapter: CartAdapter
    private lateinit var frameLayout: FrameLayout
    lateinit var message:String
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var btnOrder: Button
    lateinit var orders:List<ResMenu>
    var resId:Int=100000
    private var resName:String?="Cart"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        sharedPreferences = getSharedPreferences("my_pref", Context.MODE_PRIVATE)
        recyclerView = findViewById(R.id.recyclerView)
        layoutManager = LinearLayoutManager(this@CartActivity)
        coordinateLayout = findViewById(R.id.coordinateLayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frameLayout)
        txtResName = findViewById(R.id.txtResName)

        progressBar = findViewById(R.id.progressBar)
        rlMyCart = findViewById(R.id.rlMyCart)
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.GONE
        btnOrder=findViewById(R.id.btnOrder)
        btnOrder.visibility = View.VISIBLE

        setUpToolbar()

        if(intent!=null){
            resId=intent.getIntExtra("resId",100000)
            resName=intent.getStringExtra("resName")
            var str=intent.getStringExtra("orderDetails")
            orders=Gson().fromJson(str,Array<ResMenu>::class.java).asList()
            supportActionBar?.title=resName

        } else {
            finish()
            Toast.makeText(this@CartActivity,"Code 16:Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
        }
        if(resId==100000){
            finish()
            Toast.makeText(this@CartActivity,"code 17:Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
        }


        cartList()
        placeOrder()
    }



    class ClearDBAsync(context: Context, private val resId:Int): AsyncTask<Void, Void, Boolean>(){
        val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {
            db.orderDao().deleteOrders(resId)
            db.close()
            return true
        }
    }

    class GetItemsDBAsync(context:Context): AsyncTask<Void, Void, List<OrderEntity>>()
    {
        val db= Room.databaseBuilder( context, RestaurantDatabase::class.java,"restaurants-db").build()
        override fun doInBackground(vararg params: Void?): List<OrderEntity> {
            return db.orderDao().getAllOrders()
        }
    }


    private  fun cartList() {

       /* val list=GetItemsDBAsync(applicationContext).execute().get()
        for(element in list){

            orders.addAll(Gson().fromJson(element.foodItems,Array<ResMenu>::class.java).asList())
        } */
        if(orders.isEmpty())
        {
            rlMyCart.visibility=View.GONE
            progressLayout.visibility=View.VISIBLE
        }
        else{
            rlMyCart.visibility=View.VISIBLE
            progressLayout.visibility=View.GONE
        }

        recyclerAdapter= CartAdapter(orders,this@CartActivity)
        layoutManager = LinearLayoutManager(this@CartActivity)
        recyclerView.layoutManager=layoutManager
        recyclerView.itemAnimator= DefaultItemAnimator()
        recyclerView.adapter=recyclerAdapter
    }

    private fun placeOrder() {
        var sum = 0
        for (i in 0 until orders.size) {
            sum += orders[i].cost_for_one.toInt()
        }
        val total = "Place Order(Total: Rs. $sum)"
        btnOrder.text = total
        btnOrder.setOnClickListener {
            progressLayout.visibility = View.VISIBLE
            sendRequest()
        }
    }

    private  fun sendRequest() {

        val queue = Volley.newRequestQueue(this@CartActivity)
        val jsonParams = JSONObject()

        jsonParams.put("user_id", sharedPreferences.getString("user_id","user_id"))

        jsonParams.put("restaurant_id", resId.toString())
        var total = 0
        for (i in 0 until orders.size) {
            total += orders[i].cost_for_one.toInt()
        }
        jsonParams.put("total_cost", total.toString())
        val dishArray = JSONArray()
        for (i in 0 until orders.size) {
            val dishId = JSONObject()
            dishId.put("food_item_id", orders[i].id)
            dishArray.put(i, dishId)
        }
        jsonParams.put("food", dishArray)

        if (ConnectionManager().isNetworkAvailable(this@CartActivity)) {
            try {
                progressLayout.visibility = View.GONE

                val jsonObjectRequest =
                    object : JsonObjectRequest(Method.POST, PLACE_ORDER, jsonParams, Response.Listener {

                        val obj = it.getJSONObject("data")
                        val success = obj.getBoolean("success")
                        if (success) {
                            ClearDBAsync(applicationContext, resId).execute().get()
                            RestaurantMenuAdapter.isCartEmpty = true
                            Toast.makeText(this@CartActivity,"Order Placed",Toast.LENGTH_SHORT).show()
                            val intent= Intent(this,OrderActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        } else {
                            rlMyCart.visibility = View.VISIBLE

                            message=obj.getString("errorMessage")
                            Toast.makeText(this@CartActivity, message, Toast.LENGTH_SHORT).show()


                        }
                    }, Response.ErrorListener {
                        rlMyCart.visibility = View.VISIBLE
                        Toast.makeText(
                            this@CartActivity,
                            "UNFORTUNATELY, Volley Error Occurred",
                            Toast.LENGTH_LONG
                        ).show()
                    }) {
                        override fun getHeaders(): MutableMap<String, String> {
                            val headers = HashMap<String, String>()
                            headers["Content-type"] = "application/json"
                            headers["token"] = "f7bddb8be3c1be"
                            return headers
                        }
                    }
                queue.add(jsonObjectRequest)
            } catch (e: Exception) {
                rlMyCart.visibility = View.VISIBLE
                e.printStackTrace()
            }
        } else {

            val dialog= AlertDialog.Builder(this@CartActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not Found")
            dialog.setPositiveButton("Open Settings"){ _, _ ->
                //Do nothing
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()

            }
            dialog.setNegativeButton("Exit"){ _, _ ->
                //Do nothing
                ActivityCompat.finishAffinity(this@CartActivity)

            }
            dialog.create()
            dialog.show()
        }
    }


    private  fun setUpToolbar() {

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {
        val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
        alterDialog.setTitle("Alert!")
        alterDialog.setMessage("Going back will remove everything from cart")
        alterDialog.setPositiveButton("Okay") { _, _ ->
            ClearDBAsync(applicationContext, resId).execute().get()
            RestaurantMenuAdapter.isCartEmpty=true
            super.onBackPressed()
        }
        alterDialog.setNegativeButton("No") { _, _ ->

        }
        alterDialog.show()
    }

    override fun onStop() {
        ClearDBAsync(applicationContext, resId).execute().get()
        RestaurantMenuAdapter.isCartEmpty=true
        super.onStop()
    }
}