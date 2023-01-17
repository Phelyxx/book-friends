package com.bookfriends.nearbybookstores

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.bookfriends.home.social_pink
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.compose.*


//User position
var _userCurrentLat = mutableStateOf(0.0)
var userCurrentLat: MutableState<Double> = _userCurrentLat
var _userCurrentLong = mutableStateOf(0.0)
var userCurrentLong: MutableState<Double> = _userCurrentLong
val marker = LatLng(userCurrentLat.value, userCurrentLong.value)

fun currentUserGeoCoord(latLng: LatLng){
    _userCurrentLat.value = latLng.latitude
    _userCurrentLong.value = latLng.longitude
}

@ExperimentalPermissionsApi
@Composable
fun NearbyBookstoresScreen(
    nearbyBookstoresViewModel: NearbyBookstoresViewModel? = null,
) {
    val permissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(
        key1 = lifecycleOwner,
        effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    permissionsState.launchMultiplePermissionRequest()
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    )



    //Permissions
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        connectionChecker(LocalContext.current, true)

        permissionsState.permissions.forEach { perm ->
            when (perm.permission) {
                Manifest.permission.ACCESS_FINE_LOCATION -> {
                    when {
                        perm.hasPermission -> {
                            Log.d("Succes", "Permissions accepted")

                            //NearbyBookstoresBodyContent()

                        }
                        perm.shouldShowRationale -> {
                            Log.d("Exception", " Permissions denied")
                        }
                        !perm.hasPermission -> {
                            //NearbyBookstoresBodyContentNoPermissions()
                        }
                    }
                }
                Manifest.permission.ACCESS_COARSE_LOCATION -> {
                    when {
                        perm.hasPermission -> {

                        }
                        perm.shouldShowRationale -> {

                        }
                    }
                }
            }
        }
    }

}

@Composable
fun connectionChecker(
    context: Context,
    c: Boolean
) {

    // on below line creating a variable
    // for connection status.
    val connectionType = remember {
        mutableStateOf("")
    }
    // on below line we are creating a column,
    Column(
        // on below line we are adding
        // a modifier to it,
        modifier = Modifier
            .fillMaxSize()
            // on below line we are
            // adding a padding.
            .padding(all = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {

        // A Thread that will continuously
        // monitor the Connection Type
        Thread(Runnable {
            while (true) {

                // Invoking the Connectivity Manager
                val cm =
                    context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

                // Fetching the Network Information
                val netInfo = cm.allNetworkInfo

                // on below line finding if connection
                // type is wifi or mobile data.
                for (ni in netInfo) {
                    if (ni.typeName.equals("WIFI", ignoreCase = true))
                        if (ni.isConnected) connectionType.value = "WIFI"
                    if (ni.typeName.equals("MOBILE", ignoreCase = true))
                        if (ni.isConnected) connectionType.value = "MOBILE DATA"
                    if (ni.typeName.equals("NOT CONNECTED", ignoreCase = true))
                        if (!ni.isConnected) connectionType.value = "NOT CONNECTED"
                }
            }
        }).start() // Starting the thread

        //NearbyBookstoresBodyContent()


        // on below line we are adding a text for heading.
        Text(
            // on below line we are specifying text
            text = "Internet Connectivity Checker",
            // on below line we are specifying text color,
            // font size and font weight
            color = social_pink,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // on below line adding a spacer.
        Spacer(modifier = Modifier.height(15.dp))

        // on below line we are adding a text
        // for displaying connection type.
        Text(
            // on below line we are specifying text
            text = connectionType.value,
            // on below line we are specifying text color,
            // font size and font weight
            color = social_pink, fontSize = 15.sp, fontWeight = FontWeight.Bold
        )

        if(connectionType.value.equals("Not Connected", ignoreCase = true)){
            Text(
                // on below line we are specifying text
                text = "You need to be logged in to use this functionality.",
                // on below line we are specifying text color,
                // font size and font weight
                color = social_pink, fontSize = 15.sp, fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun NearbyBookstoresBodyContentNoPermissions() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,

    ) {
        MapCoverImage()
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = "Nearby Bookstores")
        Spacer(modifier = Modifier.size(8.dp))
        Text(textAlign = TextAlign.Center, text = "To be able to use the function, change the permissions of the application from settings")
    }
}

@Composable
fun NearbyBookstoresBodyContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MapCoverImage()
        Spacer(modifier = Modifier.size(8.dp))
        Text(text = "Nearby Bookstores")
        Spacer(modifier = Modifier.size(8.dp))
        MyGoogleMaps()
    }
}

@Composable
fun MapCoverImage() {
    Card(
        shape = RectangleShape,
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
    ) {
        Image(
            painter = painterResource(com.bookfriends.R.drawable.nearby_bookstores_cover),
            "Profile cover image",
            modifier = Modifier
                .wrapContentSize()
                .border(color = Color.LightGray, width = 1.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun MyGoogleMaps() {
    //Map properties and settings
    val properties by remember { mutableStateOf(MapProperties()) }
    val uiSettings by remember { mutableStateOf(MapUiSettings()) }

    val marker = LatLng(4.602823, -74.065288)

    //Auto-Zoom map
    val initialZoom = 6f
    val finalZoom = 15f
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(marker, initialZoom)
    }

    val markerOptions = MarkerOptions().title("Start position").position(marker)

    LaunchedEffect(key1 = true) {
        cameraPositionState.animate(
            update = CameraUpdateFactory.newCameraPosition(
                CameraPosition(marker, finalZoom, 0f, 0f)
            )
        )
    }

    GoogleMap(
        modifier = Modifier.fillMaxSize(),
        cameraPositionState = cameraPositionState,
        properties = properties,
        uiSettings = uiSettings
    ) {
        //Marker(markerOptions.position)
    }
}

