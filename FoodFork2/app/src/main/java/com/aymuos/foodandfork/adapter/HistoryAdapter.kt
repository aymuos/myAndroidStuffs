package com.aymuos.foodandfork.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.aymuos.foodandfork.R
import com.aymuos.foodandfork.util.BodyItem
import com.aymuos.foodandfork.util.HeaderItem
import com.aymuos.foodandfork.util.ListItem

class HistoryAdapter (val mitems:ArrayList<ListItem>,context: Context):
    RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

   abstract class HistoryViewHolder(view:View):RecyclerView.ViewHolder(view) {

    }

    class HeaderViewHolder(view: View):HistoryAdapter.HistoryViewHolder(view){
     val   txtHeaderName:TextView= view.findViewById(R.id.txtHeaderName)
        var txtHeaderDate:TextView=view.findViewById((R.id.txtDate))

    }

    class BodyViewHolder(view: View):HistoryAdapter.HistoryViewHolder(view){
        val   txtDishName:TextView= view.findViewById(R.id.txtDishName)
        val txtDishCost:TextView=view.findViewById((R.id.txtCost))

    }


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {


            if (viewType == 0) {
               val view:View =LayoutInflater.from(parent.context).inflate(R.layout.header_history2,parent,false)
                return HeaderViewHolder(view)
            } else {
                val view:View =LayoutInflater.from(parent.context).inflate(R.layout.body_history,parent,false)
                return BodyViewHolder(view)



            }

        }

        override fun getItemCount(): Int {
            return mitems.size

        }

        override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {

            val type = getItemViewType(position)
            if (type == 0) {
                val header=mitems[position] as HeaderItem
                val headerHolder=holder as HeaderViewHolder
                headerHolder.txtHeaderDate.text=header.date
                headerHolder.txtHeaderName.text=header.resName



            } else {

                val body=mitems[position] as BodyItem

                val bodyHolder=holder as BodyViewHolder
                bodyHolder.txtDishName.text=body.foodName
                bodyHolder.txtDishCost.text=body.cost

            }
        }

        override fun getItemViewType(position: Int): Int {
            if (mitems[position].isHeader()) {
                return 0
                //0 for header,1 for Body

            } else {
                return 1
            }


        }


}