package com.aymuos.bookhub.adapter

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aymuos.bookhub.R

class FavouriteRecyclerAdapter {
    class FavouriteViewHolder: RecyclerView.Adapter<FavouriteRecyclerAdapter.FavouriteViewHolder>(){

        class FavouriteViewHolder(view:View):RecyclerView.ViewHolder(view){

            val txtBookName:TextView=view.findViewById(R.id.txtFavBookTitle)
            val txtBookAuthor:TextView=view.findViewById(R.id.txtFavBookAuthor)
            val txtBookPrice:TextView=view.findViewById(R.id.txtFavBookPrice)
            val txtBookRating:TextView=view.findViewById(R.id.txtFavBookRating)
            val txtBookImage:ImageView=view.findViewById(R.id.imgFavBookImage)
            val llContent:LinearLayout=view.findViewById(R.id.llFavContent)



        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): FavouriteRecyclerAdapter.FavouriteViewHolder {
            TODO("Not yet implemented")
        }

        override fun getItemCount(): Int {
            TODO("Not yet implemented")
        }

        override fun onBindViewHolder(
            holder: FavouriteRecyclerAdapter.FavouriteViewHolder,
            position: Int
        ) {
            TODO("Not yet implemented")
        }

    }
}