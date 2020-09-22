package com.aymuos.foodandfork.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aymuos.foodandfork.R
import com.aymuos.foodandfork.model.ResMenu

class CartAdapter (private val cartArray: ArrayList<ResMenu>,val context:Context)
    :RecyclerView.Adapter<CartAdapter.CartViewHolder>(){

    class CartViewHolder(view: View):RecyclerView.ViewHolder(view){
        val txtDishName: TextView =view.findViewById(R.id.txtDishName)
        val txtCost: TextView =view.findViewById(R.id.txtCost)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartAdapter.CartViewHolder {
        val view= LayoutInflater.from(parent.context).inflate(R.layout.recycler_cart_single_row,parent,false)
        return CartViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cartArray.size
    }

    override fun onBindViewHolder(holder: CartAdapter.CartViewHolder, position: Int) {
        val cartObj=cartArray[position]
        holder.txtDishName.text=cartObj.name
        val price="Rs. ${cartObj.cost_for_one}"
        holder.txtCost.text=price
    }
}