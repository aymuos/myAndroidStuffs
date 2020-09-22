package com.aymuos.bookhub.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aymuos.bookhub.R
import com.aymuos.bookhub.activity.DescriptionActivity
import com.aymuos.bookhub.model.Book
import com.squareup.picasso.Picasso

class DashboardRecyclerAdapter(val context:Context,val itemList:ArrayList<Book>): //check here
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {



    class DashboardViewHolder(view:View):RecyclerView.ViewHolder(view){

        val txtBookName:TextView=view.findViewById(R.id.txtBookName)
        val txtBookAuthor:TextView=view.findViewById(R.id.txtBookAuthor)
        val txtBookPrice:TextView=view.findViewById(R.id.txtBookPrice)
        val txtBookRating:TextView=view.findViewById(R.id.txtBookRating)
        val imgBookImage:ImageView=view.findViewById(R.id.imgBookImage)

        //  m4t3v7 Making THEM clickable
        val llContent:LinearLayout=view.findViewById(R.id.llContent)
        //Click lIstener is added inside onBindView



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DashboardViewHolder {
         //
        val view=LayoutInflater.from(parent.context).inflate(R.layout.recycler_dashboard_single_row,parent,false)
        return DashboardViewHolder(view)
        //we pass this view which shall be created

    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: DashboardViewHolder, position: Int) {
        //05:27

        val book= itemList[position]
        holder.txtBookName.text = book.bookName
        holder.txtBookAuthor.text = book.bookAuthor
        holder.txtBookPrice.text = book.bookPrice
        holder.txtBookRating.text = book.bookRating
        Picasso.get().load(book.bookImage).error(R.drawable.book_app_icon_web).into(holder.imgBookImage)

        holder.llContent.setOnClickListener {
           // Toast.makeText(context,"Clicked on ${holder.txtBookName.text}",Toast.LENGTH_LONG).show()

            //Making th ePOST REQUEST M4T7V2
            val intent= Intent(context,DescriptionActivity::class.java)
            intent.putExtra("book_id",book.bookId)
            context.startActivity(intent)

        }


    }

}