package com.bookfriends.nearbybookstores

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.currentCoroutineContext
import kotlin.coroutines.coroutineContext

class  NearbyBookstoresViewModel(

) : ViewModel() {
    /*
    private fun isNetworkConnected(): Boolean {

        //1
        val connectivityManager = getSystemService(getAContext.CONNECTIVITY_SERVICE) as ConnectivityManager
        //2
        val activeNetwork = connectivityManager.activeNetwork
        //3
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        //4
        return networkCapabilities != null &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
*/
}