package com.aymuos.bookhub.activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.*
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.aymuos.bookhub.R
import com.aymuos.bookhub.database.BookDatabase
import com.aymuos.bookhub.database.BookEntity
import com.aymuos.bookhub.util.ConnectionManager
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_description.*
import org.json.JSONObject
import java.lang.Exception

class DescriptionActivity : AppCompatActivity() {
    //M4T7V2
    lateinit var  txtBookName:TextView
    lateinit var  txtBookAuthor:TextView
    lateinit var  txtBookPrice:TextView
    lateinit var  txtBookRating:TextView
    lateinit var  txtBookDesc:TextView
    private lateinit var  txtBookImg:ImageView
    private lateinit var  btnAddToFav:Button
    private lateinit var progressBar:ProgressBar
    lateinit var progressLayout:RelativeLayout

    private lateinit var toolbar: Toolbar

    var bookId:String? ="100"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)



        txtBookName=findViewById(R.id.txtBookName)
        txtBookAuthor=findViewById(R.id.txtBookAuthor)
        txtBookPrice=findViewById(R.id.txtBookPrice)
        txtBookRating=findViewById(R.id.txtBookRating)
        txtBookDesc=findViewById(R.id.txtBookDescrp)
        txtBookImg=findViewById(R.id.imgBookImage)
        btnAddToFav=findViewById(R.id.btnAddToFav)
        progressBar=findViewById(R.id.progressBar)
        progressBar.visibility= View.VISIBLE
        progressLayout=findViewById(R.id.progressLayout)
        progressLayout.visibility= View.VISIBLE

        toolbar=findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"
//both are kept visible as we want to show them until data is received from server


        //Now to get the value that is passed via intent using if else
        if(intent != null) {
            bookId=intent.getStringExtra("book_id")
        } else {
            finish()
            Toast.makeText(this@DescriptionActivity,"Some Unexpected ERROR" ,Toast.LENGTH_LONG).show()

        }
        if(bookId == "100"){

            finish()
            Toast.makeText(this@DescriptionActivity,"Some Unexpected ERROR" ,Toast.LENGTH_LONG).show()

        }
//writing th POST
        val queue = Volley.newRequestQueue(this@DescriptionActivity)
        val url="http://13.235.250.119/v1/book/get_book"

        //check if device has internet
        if(ConnectionManager().checkConnectivity(this@DescriptionActivity)) {


            val jsonParams = JSONObject()
            jsonParams.put("book_id", bookId)

            val jsonRequest =
                object : JsonObjectRequest(Method.POST, url, jsonParams, Response.Listener {

                    try {

                        val success = it.getBoolean("success")
                        if (success) {
                            val bookJsonObject = it.getJSONObject("book_data")
                            progressLayout.visibility = View.GONE

                            val bookImageUrl = bookJsonObject.getString("image")

                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.book_app_icon_web).into(imgBookImage)
                            txtBookName.text = bookJsonObject.getString("name")
                            txtBookAuthor.text = bookJsonObject.getString("author")
                            txtBookPrice.text = bookJsonObject.getString("price")
                            txtBookRating.text = bookJsonObject.getString("rating")
                            txtBookDesc.text = bookJsonObject.getString("description")


                            //M4T9V6
                            val bookEntity =BookEntity(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookRating.text.toString(),
                                txtBookDesc.text.toString(),
                                bookImageUrl

                            )

                            val checkFav =DBAsyncTask(applicationContext,bookEntity,1).execute()
                            val isFav = checkFav.get()
                            if (isFav){
                                btnAddToFav.text ="Remove from Favourites"
                                val favColor= ContextCompat.getColor(applicationContext,R.color.colorFav)
                                btnAddToFav.setBackgroundColor(favColor)
                            } else {
                                btnAddToFav.text = "add to favourites"
                                val nofavColor =ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                btnAddToFav.setBackgroundColor(nofavColor)
                            }

                            btnAddToFav.setOnClickListener{

                                //will be executed when a book is not in favourite
                                if (!DBAsyncTask(applicationContext,bookEntity,1).execute().get()) {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                        val result = async.get() //true when book is favouites
                                        if (result){
                                            Toast.makeText(this@DescriptionActivity,"Book added to favourites",Toast.LENGTH_LONG).show()
                                            btnAddToFav.text="Remove from Favourites"
                                            val favColor = ContextCompat.getColor(applicationContext,R.color.colorFav)
                                            btnAddToFav.setBackgroundColor((favColor))
                                        } else {

                                            Toast.makeText(this@DescriptionActivity,"Error Occured while adding ",Toast.LENGTH_LONG).show()

                                        }
                                } else{
                                    val async =DBAsyncTask(applicationContext,bookEntity,3).execute()
                                    val result = async.get()

                                    if(result){
                                        Toast.makeText(this@DescriptionActivity,"Book removed from favourites",Toast.LENGTH_LONG).show()

                                        btnAddToFav.text="Add to Favourites"
                                        val nofavColor = ContextCompat.getColor(applicationContext,R.color.colorPrimary)
                                        btnAddToFav.setBackgroundColor((nofavColor))


                                    } else {
                                        Toast.makeText(this@DescriptionActivity,"Error Occured while adding ",Toast.LENGTH_LONG).show()

                                    }
                                }
                            }

                        } else {
                            Toast.makeText(
                                this@DescriptionActivity,
                                "JSONexception occured",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@DescriptionActivity,
                            "JSONexception occured",
                            Toast.LENGTH_LONG
                        ).show()

                    }

                }, Response.ErrorListener {
                    Toast.makeText(
                        this@DescriptionActivity,
                        "Volley exception occured",
                        Toast.LENGTH_LONG
                    ).show()


                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "f7bddb8be3c1be"

                        return super.getHeaders()
                    }
                }

            queue.add(jsonRequest)
        } else {
            val dialog = AlertDialog.Builder(this@DescriptionActivity)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection not found")
            dialog.setPositiveButton("OPEN SETTINGS") { test, listener ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            dialog.setNegativeButton("EXIT") { text, listener ->
                //FOLLOWING CODE WILL CLOSE THE APP
                ActivityCompat.finishAffinity(this@DescriptionActivity)
                

            }
            dialog.create()
            dialog.show()

        }

    }

    //ASYNC TASK CODE m4t9v5



    class DBAsyncTask(val context:Context,val bookEntity: BookEntity,val mode:Int) : AsyncTask<Void,Void,Boolean>() {
        override fun doInBackground(vararg params: Void?): Boolean {

            val db= Room.databaseBuilder(context,BookDatabase::class.java,"books-db").build()

            /* mode 1: Check if book is present in db or not
                mode 2: Save book in db as favourites
                mode 3: Remove the favourite book
             */

            when(mode){
                1 -> {

                    //check if the book is fav or not
                    val book:BookEntity? = db.bookDao().getBookById((bookEntity.book_id.toString()))
                    db.close()
                    return book!=null // returns null if the book is null
                }

                2 ->{

                    // check if the book is favourite
                    db.bookDao().insertBook(bookEntity)
                    db.close()
                    return true




                }

                3 ->{
                    // remove the favourite book
                    db.bookDao().deleteBook(bookEntity)
                    db.close()
                    return true

                }
            }
           return false
        }
    }
}