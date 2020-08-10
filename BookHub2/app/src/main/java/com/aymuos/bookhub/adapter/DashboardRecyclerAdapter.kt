package com.aymuos.bookhub.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aymuos.bookhub.R
import com.aymuos.bookhub.model.Book

class DashboardRecyclerAdapter(val context:Context,val itemList:ArrayList<Book>): //check here
    RecyclerView.Adapter<DashboardRecyclerAdapter.DashboardViewHolder>() {



    class DashboardViewHolder(view:View):RecyclerView.ViewHolder(view){

        val txtBookName:TextView=view.findViewById(R.id.txtBookName)
        val txtBookAuthor:TextView=view.findViewById(R.id.txtBookAuthor)
        val txtBookPrice:TextView=view.findViewById(R.id.txtBookPrice)
        val txtBookRating:TextView=view.findViewById(R.id.txtBookRating)
        val imgBookImage:ImageView=view.findViewById(R.id.imgBookImage)


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
        val book= itemList[position]
        holder.txtBookName.text = book.bookName
        holder.txtBookAuthor.text = book.bookName
        holder.txtBookPrice.text = book.bookName
        holder.txtBookRating.text = book.bookName
        holder.imgBookImage.setBackgroundResource(book.bookImage)


    }

}