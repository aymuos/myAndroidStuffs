package com.aymuos.bookhub.fragment

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aymuos.bookhub.R
import com.aymuos.bookhub.adapter.DashboardRecyclerAdapter
import com.aymuos.bookhub.model.Book
import com.aymuos.bookhub.util.ConnectionManager
import org.json.JSONException

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

class DashboardFragment : Fragment() {
    lateinit var recyclerDashboard:RecyclerView
    lateinit var layoutManager:RecyclerView.LayoutManager

    //M4T4V3
    private lateinit var btnCheckInternet:Button

    //putting data
   // val booklist= arrayListOf(" Book1 eeee","hhijfhwjfwv","fsfscsvdvwe",
    //"svsvsv","fdssvxc","dsfsdsdv","ssvsvsccvw","xccxvsdv","sddvdsv0","svvc")


    //ProgressBar Initialization
    lateinit var progressLayout:RelativeLayout
    lateinit var progressBar:ProgressBar


    lateinit var recyclerAdapter:DashboardRecyclerAdapter
  val bookInfoList = arrayListOf<Book>(
      /*  Book("P.S. I love You", "Cecelia Ahern", "Rs. 299", "4.5", R.drawable.ps_ily),
      Book("The Great Gatsby", "F. Scott Fitzgerald", "Rs. 399", "4.1", R.drawable.great_gatsby),
      Book("Anna Karenina", "Leo Tolstoy", "Rs. 199", "4.3", R.drawable.anna_kare),
      Book("Madame Bovary", "Gustave Flaubert", "Rs. 500", "4.0", R.drawable.madame),
      Book("War and Peace", "Leo Tolstoy", "Rs. 249", "4.8", R.drawable.war_and_peace),
      Book("Lolita", "Vladimir Nabokov", "Rs. 349", "3.9", R.drawable.lolita),
      Book("Middlemarch", "George Eliot", "Rs. 599", "4.2", R.drawable.middlemarch),
      Book("The Adventures of Huckleberry Finn", "Mark Twain", "Rs. 699", "4.5", R.drawable.adventures_finn),
      Book("Moby-Dick", "Herman Melville", "Rs. 499", "4.5", R.drawable.moby_dick),
      Book("The Lord of the Rings", "J.R.R Tolkien", "Rs. 749", "5.0", R.drawable.lord_of_rings)
      */

  )



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_dashboard,container,false)

        //we need to inflate the fragment before we can check button
//adding the progress bar
        progressBar=view.findViewById(R.id.progressBar)
        progressLayout=view.findViewById(R.id.progressLayout)
        // to load while the view is present
        progressLayout.visibility=View.VISIBLE




    //adding button & Click Listener
        btnCheckInternet= view.findViewById(R.id.btnCheckInternet)
        btnCheckInternet.setOnClickListener {
            if (ConnectionManager().checkConnectivity(activity as Context)){
                //NET AVAILABLE
                val dialog=AlertDialog.Builder(activity as Context)
                dialog.setTitle("Success")
                dialog.setMessage("Internet Connection found")
                dialog.setPositiveButton("Ok"){test,listener ->
                    //DO NOTHING
                }
                dialog.setNegativeButton("Cncel"){ text,listener ->

                }
                dialog.create()
                dialog.show()
                

            } else {
                // no net
                val dialog=AlertDialog.Builder(activity as Context)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection not found")
                dialog.setPositiveButton("Ok"){test,listener ->
                    //DO NOTHING
                }
                dialog.setNegativeButton("Cncel"){ text,listener ->

                }
                dialog.create()
                dialog.show()




            }
        }


        // Inflate the layout for this fragment



        //we wamt to inflate the fragment_dashboard
        recyclerDashboard=view.findViewById(R.id.recyclerDashboard)
        layoutManager=LinearLayoutManager(activity)





        //M4T6V1 Creating Request View
        val queue =Volley.newRequestQueue(activity as Context)
        val url ="http://13.235.250.119/v1/book/fetch_books/"
        //following is done to check if there is internet access
        if (ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest= object:JsonObjectRequest(Method.GET,url,null,Response.Listener {
                //we will handle response here

                //m4t6v5
                try{
                    progressLayout.visibility = View.GONE

                    //Getting the JSON response
                    val success=it.getBoolean("success")

                    if(success){
                        val data=it.getJSONArray("data")
                        for(i in 0 until data.length()){
                            val bookJsonObject = data.getJSONObject(i)
                            val bookObject = Book(
                                bookJsonObject.getString("book_id"),
                                bookJsonObject.getString("name"),
                                bookJsonObject.getString("author"),
                                bookJsonObject.getString("rating"),
                                bookJsonObject.getString("price"),
                                bookJsonObject.getString("image")


                            )

                            bookInfoList.add(bookObject)

                            recyclerAdapter= DashboardRecyclerAdapter(activity as Context,bookInfoList)
                            recyclerDashboard.adapter = recyclerAdapter
                            recyclerDashboard.layoutManager=layoutManager

                            //M4T3V6 to remove the divider space
                            recyclerDashboard.addItemDecoration(
                                DividerItemDecoration(
                                    recyclerDashboard.context,(layoutManager as LinearLayoutManager).orientation
                                )
                            )
                            //

                        }
                    }
                    else {
                        Toast.makeText(activity as Context,"Some Error Occurred ! ",Toast.LENGTH_LONG).show()

                    }


                } catch(e:JSONException){
                    Toast.makeText(activity as Context,"Some unexpected error occured !!!",Toast.LENGTH_LONG ).show()
                }



                println("Response is $it")
            },Response.ErrorListener {
                //we will handle errors here
                //Volley Errors are handled Here

                Toast.makeText(activity as Context,"Some Volley error occured !!!",Toast.LENGTH_LONG ).show()


                println("Error is $it")
            }){
                //code to get headers
                override fun getHeaders():MutableMap<String,String>{
                    val headers=HashMap<String,String>()
                    headers["Content-type"]="application/json"
                    headers["token"]="f7bddb8be3c1be"
                    return headers
                }



            }

            queue.add(jsonObjectRequest )


        }
        else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not found")
            dialog.setPositiveButton("OPEN SETTINGS") { test, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            dialog.setNegativeButton("EXIT") { text, listener ->
                //FOLLOWING CODE WILL CLOSE THE APP
                ActivityCompat.finishAffinity(activity as Activity)

            }
            dialog.create()
            dialog.show()
        }





        return view
    }


}