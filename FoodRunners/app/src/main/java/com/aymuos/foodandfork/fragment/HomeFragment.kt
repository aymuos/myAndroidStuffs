package com.aymuos.foodandfork.fragment

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aymuos.foodandfork.R
import com.aymuos.foodandfork.adapter.AllRestaurantsAdapter
import com.aymuos.foodandfork.model.Restaurants
import com.aymuos.foodandfork.util.ConnectionManager
import com.aymuos.foodandfork.util.FETCH_RESTAURANTS
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import kotlin.Comparator
import kotlin.collections.HashMap

class HomeFragment : Fragment() {

    private lateinit var recyclerRestaurant: RecyclerView
    private lateinit var allRestaurantsAdapter: AllRestaurantsAdapter
    private var restaurantList = arrayListOf<Restaurants>()
    private lateinit var progressBar: ProgressBar
    private lateinit var progressLayout: RelativeLayout
    lateinit var message:String



    private var costComparator=Comparator<Restaurants>{ res1, res2 ->
        if(res1.costForTwo==res2.costForTwo){
            res1.name.compareTo(res2.name,true)
        }
        else{
            res1.costForTwo.compareTo(res2.costForTwo)
        }

    }

    private var ratingComparator=Comparator<Restaurants>{ res1, res2 ->
        if(res1.rating.compareTo(res2.rating , true)==0){
            res1.name.compareTo(res2.name,true)
        }
        else{
            res1.rating.compareTo(res2.rating , true)
        }

    }



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_home, container, false)

        setHasOptionsMenu(true)
        progressBar = view?.findViewById(R.id.progressBar) as ProgressBar
        progressLayout = view.findViewById(R.id.progressLayout) as RelativeLayout
        progressLayout.visibility = View.VISIBLE

        /*A separate method for setting up our recycler view*/
        setUpRecycler(view)

        return view
    }

    private fun setUpRecycler(view: View) {
        recyclerRestaurant = view.findViewById(R.id.recyclerRestaurants) as RecyclerView


        /*Create a queue for sending the request*/
        val queue = Volley.newRequestQueue(activity as Context)


        /*Check if the internet is present or not*/
        if (ConnectionManager().isNetworkAvailable(activity as Context)) {

            /*Create a JSON object request*/
            val jsonObjectRequest = object : JsonObjectRequest(
                Request.Method.GET,
                FETCH_RESTAURANTS,
                null,
                Response.Listener<JSONObject> { response ->
                    progressLayout.visibility = View.GONE

                    /*Once response is obtained, parse the JSON accordingly*/
                    try {
                        val data = response.getJSONObject("data")
                        val success = data.getBoolean("success")
                        if (success) {

                            val resArray = data.getJSONArray("data")
                            for (i in 0 until resArray.length()) {
                                val resObject = resArray.getJSONObject(i)
                                val restaurant = Restaurants(
                                    resObject.getString("id").toInt(),
                                    resObject.getString("name"),
                                    resObject.getString("rating"),
                                    resObject.getString("cost_for_one").toInt(),
                                    resObject.getString("image_url")
                                )
                                restaurantList.add(restaurant)
                                if (activity != null) {
                                    allRestaurantsAdapter =
                                        AllRestaurantsAdapter(restaurantList, activity as Context)
                                    val mLayoutManager = LinearLayoutManager(activity)
                                    recyclerRestaurant.layoutManager = mLayoutManager
                                    recyclerRestaurant.itemAnimator = DefaultItemAnimator()
                                    recyclerRestaurant.adapter = allRestaurantsAdapter
                                    recyclerRestaurant.setHasFixedSize(true)
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

        } else {
            val builder = AlertDialog.Builder(activity as Context)
            builder.setTitle("Error")
            builder.setMessage("No Internet Connection found. Please connect to the internet")
            builder.setCancelable(false)
            builder.setPositiveButton("Open Settings"){text,listener ->

                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()

            }
            builder.setNegativeButton("Exit"){text,listener ->
                //Do nothing
                ActivityCompat.finishAffinity(activity as Activity)

            }
            builder.create()
            builder.show()
        }



    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {

        inflater.inflate(R.menu.menu_option, menu)


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id= item.itemId
        var n=4
        if(id==R.id.action_sort){
            val listItem=arrayOf("Cost(Low to High)","Cost(High to Low)","Rating")
            val builder = AlertDialog.Builder(activity as Context)
            builder.setTitle("Sort By?")
                .setSingleChoiceItems(listItem, -1){
                        _, i ->
                    n=i
                }
                .setPositiveButton("Ok") { dialog, _ ->
                    //Sorts the List
                    when(n){
                        0 -> {
                            //Sorts the list according to cost(low to High)
                            Collections.sort(restaurantList, costComparator)
                            allRestaurantsAdapter.notifyDataSetChanged()
                        }

                        1 ->{
                            //Sorts the list according to cost(High to Low)
                            Collections.sort(restaurantList,costComparator)
                            restaurantList.reverse()
                            allRestaurantsAdapter.notifyDataSetChanged()
                        }
                        2 ->{
                            //Sorts the list according to rating(High to Low)
                            Collections.sort(restaurantList,ratingComparator)
                            restaurantList.reverse()
                            allRestaurantsAdapter.notifyDataSetChanged()
                        }
                        else -> dialog.cancel()

                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    //dialog box disappears
                    dialog.cancel()
                }
                .create()
                .show()




        }

        return super.onOptionsItemSelected(item)
    }








}