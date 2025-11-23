package tees.thirumalesh.universityattendanceapp


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.database.FirebaseDatabase

class RegisterActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RegistrationScreen()
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen()
}


@Composable
fun RegistrationScreen() {
    var userName by remember { mutableStateOf("") }
    var useremail by remember { mutableStateOf("") }
    var userLocation by remember { mutableStateOf("") }
    var userpassword by remember { mutableStateOf("") }

    val context = LocalContext.current.findActivity()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white))
            .verticalScroll(rememberScrollState())
            .padding(WindowInsets.systemBars.asPaddingValues()),
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
        ) {

            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "Register",
                color = colorResource(id = R.color.p1),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Hello, Welcome!",
                color = colorResource(id = R.color.p1),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .padding(bottom = 32.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Column(
                modifier = Modifier
                    .background(
                        color = colorResource(id = R.color.white),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = colorResource(id = R.color.white),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .padding(16.dp)

            )
            {

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Username",
                    color = colorResource(id = R.color.black),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = userName,
                    onValueChange = { userName = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Email Id",
                    color = colorResource(id = R.color.black),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = useremail,
                    onValueChange = { useremail = it }
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Location",
                    color = colorResource(id = R.color.black),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = userLocation,
                    onValueChange = { userLocation = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Password",
                    color = colorResource(id = R.color.black),
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(modifier = Modifier.height(4.dp))

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = userpassword,
                    onValueChange = { userpassword = it }
                )

                Spacer(modifier = Modifier.height(36.dp))

                Button(
                    onClick = {
                        when {
                            userName.isEmpty() -> {
                                Toast.makeText(
                                    context,
                                    "We’ll need your username before moving ahead",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }

                            useremail.isEmpty() -> {
                                Toast.makeText(context, "We’ll need your email before moving ahead", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            userpassword.isEmpty() -> {
                                Toast.makeText(
                                    context,
                                    "We’ll need your password before moving ahead",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }

                            userLocation.isEmpty() -> {
                                Toast.makeText(
                                    context,
                                    "We’ll need your location before moving ahead",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }


                            else -> {

                                val userData = Student(
                                    name = userName,
                                    email = useremail,
                                    location = userLocation,
                                    password = userpassword
                                )


                                val db = FirebaseDatabase.getInstance()
                                val ref = db.getReference("StudentsAccounts")
                                ref.child(userData.email.replace(".", ",")).setValue(userData)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful) {
                                            Toast.makeText(context, "Registration Successful", Toast.LENGTH_SHORT).show()

                                            context!!.startActivity(
                                                Intent(
                                                    context,
                                                    LoginActivity::class.java
                                                )
                                            )
                                            (context).finish()
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "User Registration Failed: ${task.exception?.message}",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    .addOnFailureListener { exception ->
                                        Toast.makeText(
                                            context,
                                            "User Registration Failed: ${exception.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                            }

                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = R.color.p2),
                        contentColor = colorResource(id = R.color.white)
                    )
                ) {
                    Text("Register")
                }
            }


            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Already a member? ",
                    color = colorResource(id = R.color.p1),
                    style = MaterialTheme.typography.bodyLarge,
                )

                Text(
                    text = "Login now",
                    color = colorResource(id = R.color.p3),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
                    modifier = Modifier.clickable {
                        context!!.startActivity(Intent(context, LoginActivity::class.java))
                        context.finish()
                    }
                )

            }


        }
    }

}