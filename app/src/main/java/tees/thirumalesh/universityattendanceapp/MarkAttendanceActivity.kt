package tees.thirumalesh.universityattendanceapp

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.database.FirebaseDatabase
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.rememberCameraPositionState
import s3399337project.rohitrajmahendrakar.collegeattendance.CollegePreferences
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class MarkAttendanceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MarkAttendanceScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MarkAttendanceScreenPreview() {
    MarkAttendanceScreen()
}

@Composable
fun MarkAttendanceScreen() {

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        AttendanceFormScreen()
    }
}

@Composable
fun AttendanceFormScreen() {
//    var selectedCourse by remember { mutableStateOf("") }
    var classTime by remember { mutableStateOf("") }
    var attendanceStatus by remember { mutableStateOf("Present") }
    val gpsStatus by remember { mutableStateOf(true) }
    var confirmationMessage by remember { mutableStateOf("") }

    val attendanceOptions = listOf("Present", "Absent", "Late")

    var selectedCourse by remember { mutableStateOf("Maths") }
    var selectedTime by remember { mutableStateOf("10:00 AM to 11:00 AM") }

    var selectedLocation by remember { mutableStateOf("Pick Current Location") }

    var currentLocation by remember { mutableStateOf<LatLng?>(null) }
    var isInsideCollege by remember { mutableStateOf(false) }


    var showLocationDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    var attendanceDate by remember { mutableStateOf("") }

    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }


    val datePicker = DatePickerDialog(
        context,
        { _, y, m, d ->
//            expiryDate = String.format("%04d-%02d-%02d", y, m + 1, d)
            attendanceDate = String.format("%02d-%02d-%04d", d, m + 1, y)

        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(LatLng(17.3850, 78.4867), 12f)
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {

//            showLocationDialog = true

            getCurrentLocation(context, fusedLocationClient, cameraPositionState) { location ->
                currentLocation = location
            }
        } else {
            Log.e("Location", "Permission Denied")
        }
    }

    fun getAddressFromLatLng(context: Context, latLng: LatLng): String {
        return try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
            addresses?.get(0)?.getAddressLine(0) ?: "Unknown Location"
        } catch (e: Exception) {
            "Unknown Location"
        }
    }

    fun checkPermissionAndRequest() {
        if (ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            )
            != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
//            showLocationDialog = true

            getCurrentLocation(context, fusedLocationClient, cameraPositionState) { location ->
                currentLocation = location

                val address = getAddressFromLatLng(context, currentLocation!!)

                selectedLocation = address
                showLocationDialog = false

            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize().padding(WindowInsets.systemBars.asPaddingValues())
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(id = R.color.p3)
                )
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                modifier = Modifier
                    .size(36.dp)
                    .clickable {
                        (context as Activity).finish()
                    },
                painter = painterResource(id = R.drawable.baseline_arrow_back_36),
                contentDescription = "Back",
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                text = "Mark Attendance",
                color = Color.White,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {


            Spacer(modifier = Modifier.height(16.dp))

            // Course Dropdown
            DropdownSubject(selectedType = selectedCourse, onTypeSelected = { selectedCourse = it })


            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .height(50.dp)
                    .clickable {
                        // Handle the click event, e.g., show a date picker
                    }
                    .background(Color.LightGray, MaterialTheme.shapes.medium)
                    .padding(horizontal = 16.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = attendanceDate.ifEmpty { "Attendance Date" },
                    color = if (attendanceDate.isEmpty()) Color.Gray else Color.Black
                )
                Icon(
                    imageVector = Icons.Default.DateRange, // Replace with your desired icon
                    contentDescription = "Date Icon",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .size(24.dp)
                        .clickable {
                            datePicker.show()
                        },
                    tint = Color.DarkGray
                )
            }

            DropdownTime(selectedType = selectedTime, onTypeSelected = { selectedTime = it })


            Spacer(modifier = Modifier.height(8.dp))

            // Attendance Status Radio Buttons
            Text("Attendance Status")
            attendanceOptions.forEach { status ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    RadioButton(
                        selected = attendanceStatus == status,
                        onClick = { attendanceStatus = status }
                    )
                    Text(status)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .background(Color.LightGray, MaterialTheme.shapes.medium)
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = selectedLocation.ifEmpty { "Select your location" },
                    color = if (selectedLocation.isEmpty()) Color.Gray else Color.Black
                )


                Text(
                    modifier = Modifier
                        .wrapContentWidth()
                        .background(color = Color.Blue)
                        .padding(horizontal = 6.dp, vertical = 4.dp)
                        .clickable {
                            checkPermissionAndRequest()
                        },
                    text = "Pick Location",
                    color = Color.White
                )
            }

            if (currentLocation == null) {
                Text("Your Location : Pending")
            } else {


                val isInLocation =
                    isWithinGeofence(
                        currentLocation!!.latitude,
                        currentLocation!!.longitude,
                        51.546898, -0.023793
                    )
                isInsideCollege = isInLocation

                if (isInLocation) {
                    Text("Your Location : ✅ Inside College")
                } else {
                    Text("Your Location : ❌ Out of College")

                }
            }

            if (showLocationDialog) {
                CustomLocationPicker(
                    onDismiss = { showLocationDialog = false }
                )
            }



            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            Button(
                onClick = {


                    if (attendanceStatus != "Absent") {
                        if (isInsideCollege) {

                            val attendanceData = AttendanceData(
                                selectedCourse,
                                attendanceDate,
                                selectedTime,
                                attendanceStatus
                            )

                            markAttendance(attendanceData, context)
                        } else {
                            Toast.makeText(
                                context,
                                "Failed to mark attendance!\nYou are out of the college",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {

                        val attendanceData = AttendanceData(
                            selectedCourse,
                            attendanceDate,
                            selectedTime,
                            attendanceStatus
                        )

                        markAttendance(attendanceData, context)


                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Submit")
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (confirmationMessage.isNotEmpty()) {
                Text(
                    text = confirmationMessage,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (confirmationMessage.startsWith("✅")) Color.Green else Color.Red
                )
            }
        }
    }
}

fun getCurrentLocation(
    context: Context,
    fusedLocationClient: FusedLocationProviderClient,
    cameraPositionState: CameraPositionState,
    onLocationFetched: (LatLng) -> Unit
) {
    if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED
    ) {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            location?.let {
                val latLng = LatLng(it.latitude, it.longitude)
                onLocationFetched(latLng)  // Update State
                Log.e("Location", "Lat: ${it.latitude}, Lng: ${it.longitude}")

                cameraPositionState.position = CameraPosition.fromLatLngZoom(latLng, 15f)
            } ?: Toast.makeText(
                context,
                "Please Turn On Location and Try Again",
                Toast.LENGTH_SHORT
            ).show()
        }
    } else {
        Toast.makeText(context, "Location Permission Not Allowed", Toast.LENGTH_SHORT).show()
        Log.e("Location", "Permission not granted")
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownSubject(selectedType: String, onTypeSelected: (String) -> Unit) {
    val types = listOf("Maths", "Science", "Physics", "Biology", "History")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedType,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Subject") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor() // Important for anchoring the dropdown
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            types.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownTime(selectedType: String, onTypeSelected: (String) -> Unit) {
    val types = listOf(
        "10:00 AM to 11:00 AM",
        "11:00 AM to 12:00 PM",
        "12:00 PM to 01:00 PM",
        "01:00 PM to 02:00 PM"
    )
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedType,
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Time") },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor() // Important for anchoring the dropdown
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            types.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type) },
                    onClick = {
                        onTypeSelected(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun CustomLocationPicker(
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Your Location",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Fetching you Location...")


                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Close")
                    }
                    val context = LocalContext.current
                    Button(onClick = {
                    }) {
                        Text("Submit Location")
                    }
                }
            }
        }
    }
}

fun isWithinGeofence(
    currentLat: Double,
    currentLng: Double,
    collegeLat: Double,
    collegeLng: Double,
    radiusInMeters: Float = 100f // default geofence radius
): Boolean {
    val collegeLocation = Location("College").apply {
        latitude = collegeLat
        longitude = collegeLng
    }

    val currentLocation = Location("Current").apply {
        latitude = currentLat
        longitude = currentLng
    }

    val distance = currentLocation.distanceTo(collegeLocation) // in meters

    return distance <= radiusInMeters
}

data class AttendanceData(
    val courseName: String = "",
    val date: String = "",
    val time: String = "",
    val status: String = ""
)

private fun markAttendance(attendanceData: AttendanceData, activityContext: Context) {

    try {
        val userEmail = CollegePreferences.getStudentEmail(activityContext)
        val dateFormat = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
        val orderId = dateFormat.format(Date())

        FirebaseDatabase.getInstance().getReference("Attendance").child(userEmail.replace(".", ","))
            .child(orderId).setValue(attendanceData)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        activityContext,
                        "Attendance Marked Successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    (activityContext as Activity).finish()
                } else {
                    Toast.makeText(
                        activityContext,
                        "Product Addition Failed: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    activityContext,
                    "Product Addition Failed: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }

    } catch (e: Exception) {
        Log.e("Test", "Error Message :  ${e.message}")
    }
}