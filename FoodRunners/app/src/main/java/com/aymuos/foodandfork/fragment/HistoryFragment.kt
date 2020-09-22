package com.aymuos.foodandfork.fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aymuos.foodandfork.R
import com.aymuos.foodandfork.adapter.HistoryAdapter
import com.aymuos.foodandfork.model.OrderDetails
import com.aymuos.foodandfork.util.FETCH_PREVIOUS_ORDERS
import org.json.JSONException


class HistoryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var orderHistoryAdapter: HistoryAdapter
    private var orderHistoryList = ArrayList<OrderDetails>()
    private lateinit var llOrders: LinearLayout
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

        llOrders = view.findViewById(R.id.llOrders)
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


        val jsonObjectRequest = object :
            JsonObjectRequest(Method.GET, "$FETCH_PREVIOUS_ORDERS$userId", null, Response.Listener {
                try {
                    progressLayout.visibility = View.GONE
                    val data = it.getJSONObject("data")
                    val success = data.getBoolean("success")
                    if (success) {
                        val resArray = data.getJSONArray("data")
                        if (resArray.length() == 0) {
                            llOrders.visibility = View.GONE
                            rlNoOrder.visibility = View.VISIBLE
                        } else {
                            for (i in 0 until resArray.length()) {
                                val orderObject = resArray.getJSONObject(i)
                                val foodItems = orderObject.getJSONArray("food_items")
                                val orderDetails = OrderDetails(
                                    orderObject.getString("order_id"),
                                    orderObject.getString("restaurant_name"),
                                    orderObject.getString("order_placed_at"),
                                    foodItems
                                )
                                orderHistoryList.add(orderDetails)
                                if (orderHistoryList.isEmpty()) {
                                    llOrders.visibility = View.GONE
                                    rlNoOrder.visibility = View.VISIBLE
                                } else {
                                    llOrders.visibility = View.VISIBLE
                                    rlNoOrder.visibility = View.GONE
                                    if (activity != null) {
                                        orderHistoryAdapter = HistoryAdapter(
                                            activity as Context,
                                            orderHistoryList
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
                headers["token"] = "21979fa5956e7b"
                return headers
            }
        }
        queue.add(jsonObjectRequest)
    }

}