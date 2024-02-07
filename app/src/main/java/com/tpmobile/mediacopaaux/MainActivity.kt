package com.tpmobile.mediacopaaux

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.tpmobile.mediacopaaux.helpers.MapsFunctions.Companion.findMidpoint
import com.tpmobile.mediacopaaux.ui.theme.MediaCopaTheme

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val viewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        askPermissions()
        setContent {
            MediaCopaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    MyGoogleMaps()
                }
            }
        }
    }


    // ESTO TODAVIA NO SE USA
    @Preview
    @Composable
    fun MidpointScreen() {
        val location1 = remember { mutableStateOf("") }
        val location2 = remember { mutableStateOf("") }
        val midpoint = remember { mutableStateOf("") }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            OutlinedTextField(
                value = location1.value,
                onValueChange = { location1.value = it },
                label = { Text(text = "Location 1") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = location2.value,
                onValueChange = { location2.value = it },
                label = { Text(text = "Location 2") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            val context = LocalContext.current

            Button(
                onClick = {
                    val location1LatLng = getLatLngFromAddress(location1.value, context)
                    val location2LatLng = getLatLngFromAddress(location2.value, context)

                    if (location1LatLng != null && location2LatLng != null) {
                        val midpointLatLng = findMidpoint(location1LatLng, location2LatLng)
                        midpoint.value = "Midpoint: ${midpointLatLng.latitude}, ${midpointLatLng.longitude}"
                    } else {
                        midpoint.value = "Error: Invalid addresses"
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Find Midpoint")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = midpoint.value,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }


    fun getLatLngFromAddress(address: String, context: Context): LatLng? {
        var latLongResult = LatLng(0.0, 0.0)
        val geocoder = Geocoder(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val listener = Geocoder.GeocodeListener { addresses ->
                latLongResult = if (addresses.isNotEmpty()) LatLng(
                    addresses[0].latitude,
                    addresses[0].longitude
                ) else latLongResult
            }

            // esta funcion "getFromLocationName" recibe un parametro mas a partir de la version de android "tiramisu"
            // por eso el if Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                    // si lo abris desde un android nuevo, usa la funcion con 3 parametros,
                    // sino, con 2
            geocoder.getFromLocationName(address, 1, listener)
        }
        else {
            val results = geocoder.getFromLocationName(address, 1)
            latLongResult = if (results != null && results.isNotEmpty()) LatLng(results[0].latitude, results[0].longitude) else latLongResult
        }

        return latLongResult
    }

    // region Permiso de ubicacion
    private fun askPermissions() = when {
        ContextCompat.checkSelfPermission(
            this,
            ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED -> {
            viewModel.getDeviceLocation(fusedLocationProviderClient)
        }
        else -> {
            requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }
    // si bien el codigo todavia no hace nada con la ubicacion, queda para cuando querramos usarlo
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                viewModel.getDeviceLocation(fusedLocationProviderClient)
            }
        }
    // endregion
}

//@Preview(showBackground = true)
@Composable
fun MyGoogleMaps() {
    GoogleMap(modifier = Modifier.fillMaxSize())
}

