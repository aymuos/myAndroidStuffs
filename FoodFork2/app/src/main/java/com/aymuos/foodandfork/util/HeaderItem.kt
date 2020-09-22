package com.aymuos.foodandfork.util

import android.view.View
import android.widget.TextView

class HeaderItem(val resName:String, val date:String):ListItem {
    override fun isHeader(): Boolean{
        return true;
    }


}