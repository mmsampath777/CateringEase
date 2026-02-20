package com.example.caterease

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class EventDetailsActivity : AppCompatActivity() {

    private lateinit var eventDate: EditText
    private lateinit var eventType: EditText
    private lateinit var numPeople: EditText
    private lateinit var address: EditText
    private lateinit var phoneNumber: EditText
    private lateinit var locationIcon: ImageView

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            getCurrentLocation()
        } else {
            Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_details)

        val mainLayout = findViewById<ScrollView>(R.id.main)
        val initialPaddingTop = mainLayout.paddingTop
        val initialPaddingBottom = mainLayout.paddingBottom
        ViewCompat.setOnApplyWindowInsetsListener(mainLayout) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                initialPaddingTop + systemBars.top,
                view.paddingRight,
                initialPaddingBottom + systemBars.bottom
            )
            insets
        }

        eventDate = findViewById(R.id.etEventDate)
        eventType = findViewById(R.id.etEventType)
        numPeople = findViewById(R.id.etNumPeople)
        address = findViewById(R.id.etAddress)
        phoneNumber = findViewById(R.id.etPhoneNumber)
        locationIcon = findViewById(R.id.ivLocation)
        val proceedToOrderButton = findViewById<Button>(R.id.btnProceedToOrder)

        eventDate.setOnClickListener {
            showDatePickerDialog()
        }

        eventType.setOnClickListener {
            showEventTypeMenu()
        }

        locationIcon.setOnClickListener {
            requestLocationPermission()
        }

        proceedToOrderButton.setOnClickListener {
            if (validateInput()) {
                requestNotificationPermission()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(selectedYear, selectedMonth, selectedDay)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                eventDate.setText(dateFormat.format(selectedDate.time))
            },
            year, month, day
        )
        datePickerDialog.datePicker.minDate = System.currentTimeMillis() - 1000
        datePickerDialog.show()
    }

    private fun showEventTypeMenu() {
        val popup = PopupMenu(this, eventType)
        val eventTypes = arrayOf("Birthday", "Wedding", "Anniversary", "Corporate", "Other")
        for (type in eventTypes) {
            popup.menu.add(type)
        }

        popup.setOnMenuItemClickListener { item ->
            val selectedType = item.title.toString()
            if (selectedType == "Other") {
                eventType.setText("")
                eventType.isFocusable = true
                eventType.isFocusableInTouchMode = true
                eventType.requestFocus()
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(eventType, InputMethodManager.SHOW_IMPLICIT)
            } else {
                eventType.setText(selectedType)
                eventType.isFocusable = false
                eventType.isFocusableInTouchMode = false
            }
            true
        }
        popup.show()
    }

    private fun requestLocationPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                getCurrentLocation()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) -> {
                Toast.makeText(this, "Location permission is required to fetch your address.", Toast.LENGTH_LONG).show()
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
            else -> {
                locationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            }
        }
    }

    private fun getCurrentLocation() {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                if (location != null) {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        geocoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                            if (addresses.isNotEmpty()) {
                                val fullAddress = addresses[0].getAddressLine(0)
                                runOnUiThread { address.setText(fullAddress) }
                            } else {
                                runOnUiThread { Toast.makeText(this, "Address not found.", Toast.LENGTH_SHORT).show() }
                            }
                        }
                    } else {
                        try {
                            @Suppress("DEPRECATION")
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            if (addresses != null && addresses.isNotEmpty()) {
                                val fullAddress = addresses[0].getAddressLine(0)
                                address.setText(fullAddress)
                            } else {
                                Toast.makeText(this, "Address not found.", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(this, "Error getting address.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    Toast.makeText(this, "Unable to get current location.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to get location.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun validateInput(): Boolean {
        if (eventDate.text.isBlank()) {
            eventDate.error = "Event date cannot be empty"
            return false
        }
        if (eventType.text.isBlank()) {
            eventType.error = "Event type cannot be empty"
            return false
        }
        if (numPeople.text.isBlank()) {
            numPeople.error = "Number of people cannot be empty"
            return false
        }
        if (address.text.isBlank()) {
            address.error = "Address cannot be empty"
            return false
        }
        if (phoneNumber.text.isBlank()) {
            phoneNumber.error = "Phone number cannot be empty"
            return false
        }
        return true
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED -> {
                    proceedToOrderSummary()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS) -> {
                    Toast.makeText(this, "Notification permission is required to receive order updates.", Toast.LENGTH_LONG).show()
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
                else -> {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        } else {
            proceedToOrderSummary()
        }
    }

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            proceedToOrderSummary()
        } else {
            Toast.makeText(this, "Notification permission denied. You may not receive order updates.", Toast.LENGTH_LONG).show()
            proceedToOrderSummary()
        }
    }

    private fun proceedToOrderSummary() {
        val details = EventDetails(
            eventDate = eventDate.text.toString(),
            eventType = eventType.text.toString(),
            numPeople = numPeople.text.toString(),
            address = address.text.toString(),
            phoneNumber = phoneNumber.text.toString()
        )

        val intent = Intent(this, OrderSummaryActivity::class.java).apply {
            putExtra("EVENT_DETAILS", details)
        }
        startActivity(intent)
    }
}
