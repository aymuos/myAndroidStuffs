package com.aymuos.foodandfork.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aymuos.foodandfork.R
import com.aymuos.foodandfork.adapter.HistoryAdapter
import com.aymuos.foodandfork.model.OrderDetails
import com.aymuos.foodandfork.util.BodyItem
import com.aymuos.foodandfork.util.FETCH_PREVIOUS_ORDERS
import com.aymuos.foodandfork.util.HeaderItem
import com.aymuos.foodandfork.util.ListItem
import org.json.JSONException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderHistoryAdapter: HistoryAdapter
    private var orderHistoryList = ArrayList<OrderDetails>()
    private lateinit var rlOrders: RelativeLayout
    private lateinit var rlNoOrder: RelativeLayout
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var progressLayout: RelativeLayout
    lateinit var message:String
    private var userId=""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_history, container, false)

        rlOrders = view.findViewById(R.id.rlOrder)
        rlNoOrder = view.findViewById(R.id.rlNoOrder)
        recyclerView = view.findViewById(R.id.recyclerView)
        progressLayout= view?.findViewById(R.id.progressLayout) as RelativeLayout
        progressLayout.visibility = View.VISIBLE
        sharedPreferences = context!!.getSharedPreferences("my_pref", Context.MODE_PRIVATE)

        userId = sharedPreferences.getString("user_id", "user_id").toString()
        sendRequest(userId)

        return view
    }

    private fun sendRequest(userId:String){
        val queue = Volley.newRequestQueue(activity as Context)


        val jsonObjectRequest = @RequiresApi(Build.VERSION_CODES.O)
        object :
            JsonObjectRequest(Method.GET, "$FETCH_PREVIOUS_ORDERS$userId", null, Response.Listener {
                try {
                    progressLayout.visibility = View.GONE
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val resArray = data.getJSONArray("data")
                        if (resArray.length() == 0) {
                            rlOrders.visibility = View.GONE
                            rlNoOrder.visibility = View.VISIBLE
                        } else {
                            val content:TreeMap<HeaderItem,List<BodyItem>> = TreeMap<HeaderItem,List<BodyItem>>()
                            for (i in 0 until resArray.length()) {


                                val orderObject = resArray.getJSONObject(i)

                                val resName = orderObject.getString("restaurant_name")
                                val date: String =
                                    orderObject.getString("order_placed_at")
                                   /* LocalDate.parse(orderObject.getString("order_placed_at"))
                                        .format(
                                            DateTimeFormatter.ofPattern("dd-MMMM-yyyy")
                                        )*/
                                val header = HeaderItem(resName, date)

                                val foodItems = orderObject.getJSONArray("food_items")

                                val body = arrayListOf<BodyItem>()
//
                                for (i in 0 until foodItems.length()) {
                                    val bodyItem = BodyItem(
                                        foodItems.getJSONObject(i).getString("name"),
                                        foodItems.getJSONObject(i).getString("cost")
                                    )
                                    body.add(bodyItem)

                                    //tBContinued

                                }
                                //

                                content[header] = body
                            }

                              /*  val orderDetails = OrderDetails(
                                    orderObject.getString("order_id"),
                                    orderObject.getString("restaurant_name"),
                                    orderObject.getString("order_placed_at"),
                                    foodItems
                                )*/

                             //  orderHistoryList.add(orderDetails)
                                if (content.isEmpty()) {
                                    rlOrders.visibility = View.GONE
                                    rlNoOrder.visibility = View.VISIBLE
                                } else {


                                    //
                                    var mitems = arrayListOf<ListItem>()

                                    for( header:HeaderItem in content.keys){
                                        mitems.add(header)

                                        for(body:BodyItem in content[header]!!){
                                            mitems.add(body)

                                        }


                                    }

                                    rlOrders.visibility = View.VISIBLE
                                    rlNoOrder.visibility = View.GONE
                                    if (activity != null) {
                                      val historyAdapter = HistoryAdapter(
                                           mitems,activity as Context
                                        )
                                        val layoutManager =
                                            LinearLayoutManager(activity as Context)
                                        recyclerView.layoutManager = layoutManager
                                        recyclerView.itemAnimator = DefaultItemAnimator()
                                        recyclerView.adapter = orderHistoryAdapter
                                    } else {
                                        queue.cancelAll(this::class.java.simpleName)
                                    }
                                }

                        }
                    }else{
                        message=data.getString("errorMessage")

                        Toast.makeText(activity as Context, message, Toast.LENGTH_SHORT).show()

                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            },
                Response.ErrorListener {
                    Toast.makeText(activity as Context, message, Toast.LENGTH_SHORT).show()
                }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-type"] = "application/json"
                headers["token"] = "f7bddb8be3c1be"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

}