package com.aymuos.foodandfork.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aymuos.foodandfork.R
import com.aymuos.foodandfork.model.ResMenu

class RestaurantMenuAdapter (private var menuItems: ArrayList<ResMenu>, val context: Context, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<RestaurantMenuAdapter.MenuViewHolder>() {


    companion object {
        var isCartEmpty = true
    }

    class MenuViewHolder(view:View):RecyclerView.ViewHolder(view){
        val txtSrNumber:TextView=view.findViewById(R.id.txtSrNumber)
        val txtDishName:TextView=view.findViewById(R.id.txtDishName)
        val txtCostForTwo:TextView=view.findViewById(R.id.txtCostForTwo)
        val btnAdd:Button=view.findViewById(R.id.btnAdd)
        val btnRemove:Button=view.findViewById(R.id.btnRemove)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view=LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_custom_row,parent,false)
        return MenuViewHolder(view)
    }

    override fun getItemCount(): Int {
        return menuItems.size
    }

    interface OnItemClickListener{
        fun onAddItemClick(dishObject:ResMenu)
        fun onRemoveItemClick(dishObject:ResMenu)

    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val menu=menuItems[position]
        holder.txtSrNumber.text="${position+1}"
        holder.txtDishName.text=menu.name
        holder.txtCostForTwo.text="Rs. ${menu.cost_for_one}"
        holder.btnAdd.setOnClickListener {
            holder.btnRemove.visibility = View.VISIBLE
            holder.btnAdd.visibility = View.GONE
            listener.onAddItemClick(menu)
        }

        holder.btnRemove.setOnClickListener {
            holder.btnRemove.visibility = View.GONE
            holder.btnAdd.visibility = View.VISIBLE
            listener.onRemoveItemClick(menu)
        }
    }

}