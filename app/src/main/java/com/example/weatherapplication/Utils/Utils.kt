package com.example.weatherapplication.Utils

import android.content.Context
import android.content.DialogInterface
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AlertDialog
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

fun convertToCelsiusString(temperatureCelsius: Double): String {
    val formattedTemperature = String.format("%.1f", temperatureCelsius)
    return "$formattedTemperature°C"
}
fun getTodayDate(): String {
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.ENGLISH)
    return dateFormat.format(calendar.time)
}
fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork
    val capabilities =
        connectivityManager.getNetworkCapabilities(network)

    return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true

}
 fun showCityNotFoundDialog(context: Context) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
    builder.setTitle("City Not Found")
    builder.setMessage("The city you entered could not be found. Please try again.")
    builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
    builder.show()
}
fun showNoInternetDialog(context: Context) {
    val builder: AlertDialog.Builder = AlertDialog.Builder(context)
    builder.setTitle("No Internet Connection")
    builder.setMessage("Please check your internet connection and try again.")
    builder.setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which -> dialog.dismiss() })
    builder.show()
}