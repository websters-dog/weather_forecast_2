package io.github.websters_dog.weather_forecast_2.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import io.reactivex.rxjava3.core.Single


class GpsLocationSource (
    private val context: Context
) : LocationSource {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    override fun getLocation(): Single<Coords> {
        return Single.create {
            val permission = ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
            if (permission == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        it.onSuccess(Coords(
                            location.latitude.toFloat(),
                            location.longitude.toFloat()))
                    } else {
                        it.onError(LocationDisabledException())
                    }
                }
            } else {
                it.onError(LocationPermissionException())
            }
        }
    }

}


class LocationDisabledException : Exception("Location is null")
class LocationPermissionException : Exception("Permission required")