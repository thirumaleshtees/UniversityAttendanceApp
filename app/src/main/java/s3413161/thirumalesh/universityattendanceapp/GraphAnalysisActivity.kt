package s3413161.thirumalesh.universityattendanceapp

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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


class GraphAnalysisActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GraphAnalysisScreen(email = UniversityPreferences.getStudentEmail(this))
        }
    }
}

@Composable
fun GraphAnalysisScreen(email: String) {
    val courseStats = remember { mutableStateMapOf<String, Triple<Int, Int, Int>>() }

    val context = LocalContext.current

    LaunchedEffect(email) {
        val emailKey = email.replace(".", ",")
        val ref = FirebaseDatabase.getInstance().getReference("Attendance").child(emailKey)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val map =
                    mutableMapOf<String, Triple<Int, Int, Int>>() // course -> (present, absent, total)

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

    val coursePercentages = courseStats.mapValues { (_, stats) ->
        val (present, _, total) = stats
        if (total > 0) (present * 100.0) / total else 0.0
    }

    Column(modifier = Modifier.padding(WindowInsets.systemBars.asPaddingValues())) {

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
                text = "Graph Representation",
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


            AttendanceCanvasBarGraph(courseStats)

        }
    }
}

@Composable
fun AttendanceCanvasBarGraph(courseStats: Map<String, Triple<Int, Int, Int>>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        if(courseStats.isNotEmpty()) {
            courseStats.forEach { (course, stats) ->
                val (present, _, total) = stats
                val percent = if (total > 0) (present.toFloat() / total) * 100 else 0f
                AttendanceBarRow(course, percent)
                Spacer(modifier = Modifier.height(16.dp))
            }
        }else{
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

@Composable
fun AttendanceBarRow(course: String, percentage: Float) {
    val animatedPercent by animateFloatAsState(
        targetValue = percentage / 100f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "AnimatedBarFill"
    )

    val barColor = when {
        percentage > 80 -> Color(0xFF4CAF50) // Green
        percentage in 40f..80f -> Color(0xFFFFA000) // Amber/Orange
        else -> Color(0xFFD32F2F) // Red
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(
            text = "$course: ${"%.2f".format(percentage)}%",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        LinearProgressIndicator(
            progress = animatedPercent,
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = barColor,
            trackColor = Color.LightGray.copy(alpha = 0.3f)
        )
    }
}







