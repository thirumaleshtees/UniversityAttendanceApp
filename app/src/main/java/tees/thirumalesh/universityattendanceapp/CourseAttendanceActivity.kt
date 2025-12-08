package tees.thirumalesh.universityattendanceapp


import android.app.Activity
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Animatable
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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


class CourseAttendanceActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AttendancePercentageByCourseScreen(email = CollegePreferences.getStudentEmail(this))
        }
    }
}

@Composable
fun AttendancePercentageByCourseScreen(email: String) {
    val courseStats =
        remember { mutableStateMapOf<String, Triple<Int, Int, Int>>() } // course -> (present, absent, total)

    val context = LocalContext.current

    LaunchedEffect(email) {
        val emailKey = email.replace(".", ",")
        val ref = FirebaseDatabase.getInstance().getReference("Attendance").child(emailKey)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = mutableMapOf<String, Triple<Int, Int, Int>>()

                for (child in snapshot.children) {
                    val data = child.getValue(AttendanceData::class.java)
                    data?.let {
                        val current = map[it.courseName] ?: Triple(0, 0, 0)
                        val present = current.first + if (it.status == "Present") 1 else 0
                        val absent = current.second + if (it.status == "Absent") 1 else 0
                        val total = current.third + 1
                        map[it.courseName] = Triple(present, absent, total)
                    }
                }

                courseStats.clear()
                courseStats.putAll(map)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }



    Column(modifier = Modifier.padding(WindowInsets.systemBars.asPaddingValues())) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = colorResource(id = R.color.p3)
                )
                .padding(horizontal = 12.dp)
                .clickable {
                    (context as Activity).finish()
                },
            verticalAlignment = Alignment.CenterVertically
        ) {

            Image(
                modifier = Modifier.size(36.dp),
                painter = painterResource(id = R.drawable.baseline_arrow_back_36),
                contentDescription = "Back",
            )

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                text = "Summary by Course",
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
            Spacer(Modifier.height(8.dp))

            if (courseStats.isNotEmpty()) {
                courseStats.forEach { (course, stats) ->
                    val (present, absent, total) = stats
                    val percentage = if (total > 0) (present * 100.0) / total else 0.0

                    // Blinking animation setup
                    val isBlinking = percentage < 60
                    val alphaAnim = remember { Animatable(1f) }

                    if (isBlinking) {
                        LaunchedEffect(course) {
                            while (true) {
                                alphaAnim.animateTo(0f, animationSpec = tween(500))
                                alphaAnim.animateTo(1f, animationSpec = tween(500))
                            }
                        }
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = "Course: $course",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Total Classes: $total")
                            Spacer(modifier = Modifier.height(6.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "📊 Attendance: ${"%.2f".format(percentage)}%",
                                    color = Color.Black,
                                    modifier = Modifier
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                if (percentage > 80) {
                                    Text(
                                        text = "Good Attendance",
                                        color = Color(0xFF4CAF50),
                                        modifier = Modifier
                                    )
                                } else if (percentage < 80 && percentage > 40) {
                                    Text(
                                        text = "Average Attendance",
                                        color = Color(0xFFFFA000),
                                        modifier = Modifier
                                    )
                                } else {
                                    Text(
                                        text = "Low Attendance",
                                        color = if (isBlinking) Color.Red else Color.Unspecified,
                                        modifier = if (isBlinking) Modifier.alpha(alphaAnim.value) else Modifier
                                    )
                                }
                            }


                            Spacer(Modifier.height(12.dp))

                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Present : $present")
                                Spacer(modifier = Modifier.weight(1f))
                                Text(text = "Absent : ${total - present}")
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            AttendanceBar(total, present)
                        }
                    }
                }
            } else {


                Text(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    text = "No Attendance Marked",
                    fontSize = 18.sp,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFFFA000)
                )
            }
        }
    }
}

@Composable
fun AttendanceBar(total: Int, present: Int) {
    val absent = total - present
    val presentRatio = if (total > 0) present.toFloat() / total else 0f
    val absentRatio = 1f - presentRatio

    val animatedPresentRatio by animateFloatAsState(
        targetValue = presentRatio.coerceIn(0f, 1f),
        label = "PresentRatio"
    )
    val animatedAbsentRatio by animateFloatAsState(
        targetValue = absentRatio.coerceIn(0f, 1f),
        label = "AbsentRatio"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(Color.LightGray.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            if (animatedPresentRatio > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(animatedPresentRatio)
                        .background(Color(0xFF4CAF50)) // Green for Present
                )
            }

            if (animatedAbsentRatio > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .weight(animatedAbsentRatio)
                        .background(Color(0xFFD32F2F)) // Red for Absent
                )
            }
        }
    }
}


@Composable
fun Bar(label: String, value: Int, color: Color, max: Int) {
    val heightRatio = if (max > 0) value.toFloat() / max else 0f
    val animatedHeight by animateFloatAsState(targetValue = heightRatio, label = "Bar Height")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = Color.LightGray)
    ) {
        Box(
            modifier = Modifier
                .width((animatedHeight * 100).dp)
                .height(20.dp)
                .background(color, shape = RoundedCornerShape(4.dp))
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "$label - ($value)",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center
        )
    }
}


@Composable
fun AttendancePercentageByCourseScreenOld(email: String) {
    val percentageMap = remember { mutableStateMapOf<String, Double>() }



    LaunchedEffect(email) {
        val emailKey = email.replace(".", ",")
        val ref = FirebaseDatabase.getInstance().getReference("Attendance").child(emailKey)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map = mutableMapOf<String, Pair<Int, Int>>() // course -> (present, total)

                for (child in snapshot.children) {
                    val data = child.getValue(AttendanceData::class.java)
                    data?.let {
                        val current = map[it.courseName] ?: (0 to 0)
                        val present = current.first + if (it.status == "Present") 1 else 0
                        val total = current.second + 1
                        map[it.courseName] = (present to total)
                    }
                }

                percentageMap.clear()
                percentageMap.putAll(map.mapValues {
                    (it.value.first * 100.0) / it.value.second
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Text("Attendance Percentage by Course", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))

        percentageMap.forEach { (course, percent) ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Course: $course", fontWeight = FontWeight.Bold)
                    Text("Attendance: ${"%.2f".format(percent)}%")
                }
            }
        }
    }
}
