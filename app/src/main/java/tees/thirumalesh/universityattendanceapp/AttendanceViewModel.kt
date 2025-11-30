package tees.thirumalesh.universityattendanceapp


import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.time.LocalDate
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import s3399337project.rohitrajmahendrakar.collegeattendance.CollegePreferences

class AttendanceViewModel : ViewModel() {


    private val _attendanceMap = mutableStateOf<Map<LocalDate, List<AttendanceData>>>(emptyMap())
    val attendanceMap: State<Map<LocalDate, List<AttendanceData>>> = _attendanceMap




    fun fetchAttendanceHistory(
        context: Context,
        onResult: (List<AttendanceData>) -> Unit
    ) {
        val email = CollegePreferences.getStudentEmail(context).replace(".", ",")
        val reference = FirebaseDatabase.getInstance().getReference("Attendance").child(email)

        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val attendanceList = mutableListOf<AttendanceData>()

                for (entry in snapshot.children) {
                    val data = entry.getValue(AttendanceData::class.java)
                    data?.let {
                        attendanceList.add(it)
                    }
                }

                onResult(attendanceList)
            }

            override fun onCancelled(error: DatabaseError) {
                onResult(emptyList())
            }
        })
    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun getLast30Days(): List<LocalDate> {
    val today = LocalDate.now()
    return (0..29).map { today.minusDays(it.toLong()) }
}