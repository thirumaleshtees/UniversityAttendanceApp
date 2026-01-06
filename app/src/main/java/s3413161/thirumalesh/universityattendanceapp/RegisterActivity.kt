package s3413161.thirumalesh.universityattendanceapp

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.util.concurrent.TimeUnit


class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { RegistrationScreen() }
    }
}

fun createImageUri(context: Context): Uri {
    val values = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "student_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(
            MediaStore.Images.Media.RELATIVE_PATH,
            Environment.DIRECTORY_PICTURES + "/UniversityAttendance"
        )
    }
    return context.contentResolver.insert(
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        values
    )!!
}

@Composable
fun AddStudentImage(
    imageUri: Uri?,
    onImageCaptured: (Uri) -> Unit
) {
    val context = LocalContext.current
    var tempUri by remember { mutableStateOf<Uri?>(null) }

    val captureLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && tempUri != null) {
                onImageCaptured(tempUri!!)
            }
        }

    val permissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                tempUri = createImageUri(context)
                captureLauncher.launch(tempUri!!)
            }
        }

    Column(horizontalAlignment = Alignment.CenterHorizontally) {

        Image(
            painter = if (imageUri != null)
                rememberAsyncImagePainter(imageUri)
            else painterResource(R.drawable.ic_add_image),
            contentDescription = "Student Photo",
            modifier = Modifier
                .size(120.dp)
                .clickable {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        tempUri = createImageUri(context)
                        captureLauncher.launch(tempUri!!)
                    } else {
                        permissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }
        )

        Spacer(Modifier.height(8.dp))
        Text(if (imageUri == null) "Tap to add photo" else "Photo added")
    }
}


@Composable
fun RegistrationScreen() {

    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var className by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(colorResource(R.color.white))
            .padding(WindowInsets.systemBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.height(40.dp))

        Text(
            text = "Register",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.headlineLarge,
            color = colorResource(R.color.p1)
        )

        Spacer(Modifier.height(20.dp))

        AddStudentImage(
            imageUri = imageUri,
            onImageCaptured = { imageUri = it }
        )

        Spacer(Modifier.height(24.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = className,
                onValueChange = { className = it },
                label = { Text("Class") },
                modifier = Modifier.fillMaxWidth(0.9f)
            )

            Spacer(Modifier.height(10.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(0.9f)
            )
        }

        Spacer(Modifier.height(24.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Button(
                modifier = Modifier.fillMaxWidth(0.9f),
                onClick = {

                    if (imageUri == null) {
                        Toast.makeText(context, "Please add photo", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    isLoading = true

                    uploadToImgBB(context, imageUri!!) { imageUrl ->

                        val student = Student(
                            name = name,
                            email = email,
                            section = className,
                            password = password,
                            photoUrl = imageUrl
                        )

                        FirebaseDatabase.getInstance()
                            .getReference("StudentsAccounts")
                            .child(email.replace(".", ","))
                            .setValue(student)
                            .addOnSuccessListener {
                                isLoading = false
                                Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()
                                context.startActivity(Intent(context, LoginActivity::class.java))
                            }
                            .addOnFailureListener {
                                isLoading = false
                                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                            }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.p2))
            ) {
                Text("Register")
            }
        }

        Spacer(Modifier.height(30.dp))
    }
}

private const val IMGBB_API_KEY = "2308559d42036fd9c4cdf1cf81fac626"


fun uploadToImgBB(
    context: Context,
    imageUri: Uri,
    onSuccess: (String) -> Unit
) {

    val bytes = context.contentResolver.openInputStream(imageUri)!!.readBytes()

    val body = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart(
            "image",
            "photo.jpg",
            RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
        )
        .build()

    val request = Request.Builder()
        .url("https://api.imgbb.com/1/upload?key=$IMGBB_API_KEY")
        .post(body)
        .build()

    OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()
        .newCall(request)
        .enqueue(object : Callback {

            override fun onFailure(call: Call, e: java.io.IOException) {
                (context as ComponentActivity).runOnUiThread {
                    Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                val url =
                    Gson().fromJson(response.body!!.string(), ImgBBResponse::class.java).data.url
                (context as ComponentActivity).runOnUiThread {
                    onSuccess(url)
                }
            }
        })
}


data class ImgBBResponse(val data: ImgData)
data class ImgData(val url: String)
