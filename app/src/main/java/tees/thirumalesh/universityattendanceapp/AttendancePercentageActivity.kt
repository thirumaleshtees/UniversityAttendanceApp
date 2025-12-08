package tees.thirumalesh.universityattendanceapp

import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AttendancePercentageActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OverallAttendancePercentageScreen(email = CollegePreferences.getStudentEmail(this))
        }
    }
}

@Composable
fun OverallAttendancePercentageScreen(email: String) {
    var percentage by remember { mutableDoubleStateOf(0.0) }

    var totalClasses by remember { mutableIntStateOf(0) }
    var presentClasses by remember { mutableIntStateOf(0) }

    val context = LocalContext.current

    LaunchedEffect(email) {
        val emailKey = email.replace(".", ",")
        val ref = FirebaseDatabase.getInstance().getReference("Attendance").child(emailKey)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var present = 0
                var total = 0

                for (child in snapshot.children) {
                    val data = child.getValue(AttendanceData::class.java)
                    data?.let {
                        if (it.status == "Present") present++
                        total++
                    }
                }

                presentClasses = present
                totalClasses = total

                percentage = if (total > 0) (present * 100.0) / total else 0.0
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(WindowInsets.systemBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally
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
                modifier = Modifier.size(36.dp).clickable {
                    (context as Activity).finish()

                },
                painter = painterResource(id = R.drawable.baseline_arrow_back_36),
                contentDescription = "Back",
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                text = "Overall Attendance",
                color = Color.White,
                fontSize = 22.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold
            )
        }

        AttendanceCircularIndicator(percentage.toFloat(), totalClasses, presentClasses)

        if (false)
            Column(
                modifier = Modifier.padding(12.dp)
            ) {


                Spacer(Modifier.height(8.dp))
                Text(
                    "${"%.2f".format(percentage)}%",
                    style = MaterialTheme.typography.headlineMedium
                )

                Spacer(Modifier.height(32.dp))

                Text("Total Classes : $totalClasses", style = MaterialTheme.typography.titleMedium)

                Spacer(Modifier.height(8.dp))

                Text(
                    "Present Classes : $presentClasses",
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Absent Classes : ${totalClasses - presentClasses}",
                    style = MaterialTheme.typography.titleMedium
                )

            }

    }
}

@Composable
fun AttendanceCircularIndicator(
    percentage: Float, // e.g., 75f means 75%
    totalClasses: Int,
    presentClasses: Int
) {
    val animatedProgress by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "CircularProgressAnimation"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                progress = animatedProgress,
                strokeWidth = 12.dp,
                color = Color(0xFF4CAF50),
                trackColor = Color.LightGray.copy(alpha = 0.3f), // Light color for unfilled area
                modifier = Modifier.size(150.dp)
            )
            Text(
                text = "${"%.1f".format(percentage)}%",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {

                TextCard(
                    totalClasses = totalClasses,
                    title = "Total Classes",
                    Modifier.fillMaxWidth()
                )
//                Text("Total Classes: $totalClasses", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                ) {
                    TextCard(
                        totalClasses = presentClasses,
                        title = "Present Classes",
                        Modifier.weight(1f)
                    )

                    val absentees = totalClasses - presentClasses

                    TextCard(
                        totalClasses = absentees,
                        title = "Absent Classes",
                        Modifier.weight(1f)
                    )
                }
//                Text(
//                    "Present Classes: $presentClasses",
//                    style = MaterialTheme.typography.titleMedium
//                )
//                Spacer(Modifier.height(8.dp))
//                Text(
//                    "Absent Classes: ${totalClasses - presentClasses}",
//                    style = MaterialTheme.typography.titleMedium
//                )
            }
        }
    }
}

@Composable
fun TextCard(totalClasses: Int, title: String, modifier: Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium)

        Text(
            "$totalClasses",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
    }
}