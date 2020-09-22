package com.aymuos.bookhub.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

class ConnectionManager {
    fun checkConnectivity(context: Context):Boolean{
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        // upper line gives info about the currently active network ,all apps should have this
    val activeNetwork:NetworkInfo?=connectivityManager.activeNetworkInfo
        //checking if network is available
        if (activeNetwork?.isConnected!= null){
            return activeNetwork.isConnected
        } else {
            return false
        }


    }

}