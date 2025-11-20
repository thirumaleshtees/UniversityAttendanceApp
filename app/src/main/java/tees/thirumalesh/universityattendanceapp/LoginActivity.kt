package tees.thirumalesh.universityattendanceapp

import android.app.Activity
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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import kotlin.jvm.java

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen()
        }

    }
}


@Composable
fun LoginScreen() {
    var useremail by remember { mutableStateOf("") }
    var userpassword by remember { mutableStateOf("") }

    val context = LocalContext.current as Activity

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(id = R.color.white))
            .padding(WindowInsets.systemBars.asPaddingValues()),
    ) {

        Column(
            modifier = Modifier.padding(16.dp),
        ) {

            Spacer(modifier = Modifier.height(100.dp))

            Text(
                text = "Login",
                color = colorResource(id = R.color.p1),
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier
                    .padding(bottom = 4.dp)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = "Hello, Welcome Back!",
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
                    .padding(16.dp),

                )
            {

                Text(
                    text = "Username",
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
                            useremail.isEmpty() -> {
                                Toast.makeText(context, " We’ll need your email before moving ahead", Toast.LENGTH_SHORT).show()
                            }

                            userpassword.isEmpty() -> {
                                Toast.makeText(context, "We’ll need your password before moving ahead", Toast.LENGTH_SHORT)
                                    .show()
                            }

                            else -> {

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
                    Text("Continue")
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
            ) {
                Text(
                    text = "Not a member yet? ",
                    color = colorResource(id = R.color.p1),
                    style = MaterialTheme.typography.bodyLarge,
                )

                Text(
                    text = "Join now",
                    color = colorResource(id = R.color.p3),
                    style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Black),
                    modifier = Modifier.clickable {
                        context.startActivity(Intent(context, RegisterActivity::class.java))
                        context.finish()
                    }
                )

            }

            Spacer(modifier = Modifier.height(24.dp))


        }
    }

}