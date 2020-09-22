package com.aymuos.foodandfork.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
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
import com.aymuos.foodandfork.adapter.RestaurantMenuAdapter
import com.aymuos.foodandfork.database.OrderEntity
import com.aymuos.foodandfork.database.RestaurantDatabase
import com.aymuos.foodandfork.model.ResMenu
import com.aymuos.foodandfork.util.ConnectionManager
import com.aymuos.foodandfork.util.FETCH_RESTAURANTS
import kotlinx.android.synthetic.main.activity_detail.*
import org.json.JSONException
import org.json.JSONObject


class DetailActivity : AppCompatActivity() {

    private lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: RestaurantMenuAdapter
    private lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    private lateinit var toolbar: Toolbar
    var resid:Int=100000
    lateinit var btnProceedToCart: Button
    private var resname:String?="Menu"
    val menuList= arrayListOf<ResMenu>()
    val orderList= arrayListOf<ResMenu>()
    lateinit var message:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        recyclerDashboard=findViewById(R.id.recyclerMenu)
        layoutManager=LinearLayoutManager(this@DetailActivity)


        btnProceedToCart=findViewById(R.id.btnCart)

        progressBar = findViewById(R.id.progressBar)
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.VISIBLE

        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if(intent!=null){
            resid=intent.getIntExtra("res_id",100000)
            resname=intent.getStringExtra("res_name")
            supportActionBar?.title=resname

        } else {
            finish()
            Toast.makeText(this@DetailActivity,"Some unexpected error occurred!", Toast.LENGTH_SHORT).show()
        }
        if(resid==100000){
            finish()
            Toast.makeText(this@DetailActivity,"###Some unexpected error occurred####", Toast.LENGTH_SHORT).show()
        }


        /*A separate method for setting up our recycler view*/
        setUpRecycler()

        btnProceedToCart.setOnClickListener {
            proceedToCart()
        }
    }


    private fun setUpRecycler() {

        val queue= Volley.newRequestQueue(this@DetailActivity)
        val jsonParams= JSONObject()


        if(ConnectionManager().isNetworkAvailable(this@DetailActivity)) {
            val jsonRequest = object : JsonObjectRequest(
                Method.GET,"$FETCH_RESTAURANTS$resid",
                jsonParams, Response.Listener {
                    progressLayout.visibility = View.GONE
                    try{
                        val data = it.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success){
                            orderList.clear()
                            //Receiving data from the server
                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val resObject = resArray.getJSONObject(i)
                                val menuItems = ResMenu(
                                    resObject.getString("id"),
                                    resObject.getString("name"),
                                    resObject.getString("cost_for_one"),
                                    resObject.getString("restaurant_id")
                                )
                                menuList.add(menuItems)

                            }
                                recyclerAdapter =
                                    RestaurantMenuAdapter(menuList, this@DetailActivity, object:
                                    RestaurantMenuAdapter.OnItemClickListener{
                                        override fun onAddItemClick(dishObject: ResMenu) {
                                            orderList.add(dishObject)
                                            if(orderList.size>0){
                                                btnProceedToCart.visibility=View.VISIBLE
                                                RestaurantMenuAdapter.isCartEmpty=false
                                            }
                                        }

                                        override fun onRemoveItemClick(dishObject: ResMenu) {
                                            orderList.remove(dishObject)
                                            if(orderList.isEmpty()){
                                                btnProceedToCart.visibility=View.GONE
                                                RestaurantMenuAdapter.isCartEmpty=true
                                            }
                                        }
                                    }

                                    )



                                    recyclerMenu.layoutManager = layoutManager
                                    recyclerMenu.itemAnimator = DefaultItemAnimator()
                                    recyclerMenu.adapter = recyclerAdapter





                        }else{
                            message=data.getString("errorMessage")
                            Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()

                        }

                    }catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(this@DetailActivity, message, Toast.LENGTH_SHORT).show()
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

            //if false
            val dialog= AlertDialog.Builder(this@DetailActivity)
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
                ActivityCompat.finishAffinity(this@DetailActivity)

            }
            dialog.create()
            dialog.show()

        }

    }



    private fun proceedToCart(){
        val gson= Gson()
        val foodItems=gson.toJson(orderList)
        /*val async = CartItems(this@DetailActivity, resid , foodItems, 1).execute()
        val result = async.get()*/
        if (true) {

            val intent=Intent(this@DetailActivity,CartActivity::class.java)
            intent.putExtra("resId", resid )
            intent.putExtra("orderDetails",foodItems)
            intent.putExtra("resName", resname)
            startActivity(intent)

        } else {
            Toast.makeText(this@DetailActivity, "Some unexpected error", Toast.LENGTH_SHORT).show()
        }

    }

    class CartItems(context: Context, private val resid:Int, private val foodItems:String, val mode:Int):
        AsyncTask<Void, Void, Boolean>(){
        val db= Room.databaseBuilder(context, RestaurantDatabase::class.java,"res-db").build()
        override fun doInBackground(vararg params: Void?): Boolean {

            when (mode) {
                1 -> {
                    db.orderDao().insertOrder(OrderEntity(resid, foodItems))
                    db.close()
                    return true
                }

                2 -> {
                    db.orderDao().deleteOrder(OrderEntity(resid, foodItems))
                    db.close()
                    return true
                }
            }
            return false
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onBackPressed() {


        if(orderList.size > 0) {


            val alterDialog = androidx.appcompat.app.AlertDialog.Builder(this)
            alterDialog.setTitle("Alert!")
            alterDialog.setMessage("Going back will remove everything from cart")
            alterDialog.setPositiveButton("Okay") { _, _ ->
                super.onBackPressed()
            }
            alterDialog.setNegativeButton("Cancel") { _, _ ->

            }
            alterDialog.show()
        }else{
            super.onBackPressed()
        }

    }

    override fun onPause() {
        super.onPause()
        finish()
    }


}