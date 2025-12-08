package tees.thirumalesh.universityattendanceapp

import android.content.Context



object CollegePreferences {

    private const val PREF_NAME = "COLLEGE_APP_PREFS"
    private const val KEY_LOGIN_STATUS = "KEY_LOGIN_STATUS"
    private const val KEY_STUDENT_NAME = "KEY_STUDENT_NAME"
    private const val KEY_STUDENT_EMAIL = "KEY_STUDENT_EMAIL"
    private const val KEY_STUDENT_PHOTO = "KEY_STUDENT_PHOTO"

    fun setLoginStatus(context: Context, isLoggedIn: Boolean) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_LOGIN_STATUS, isLoggedIn).apply()
    }

    fun getLoginStatus(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_LOGIN_STATUS, false)
    }

    fun setStudentName(context: Context, name: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_STUDENT_NAME, name).apply()
    }

    fun getStudentName(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_STUDENT_NAME, "") ?: ""
    }

    fun setStudentEmail(context: Context, email: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_STUDENT_EMAIL, email).apply()
    }

    fun getStudentEmail(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_STUDENT_EMAIL, "") ?: ""
    }

    fun setStudentPhoto(context: Context, photoUrl: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_STUDENT_PHOTO, photoUrl).apply()
    }

    fun getStudentPhoto(context: Context): String {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_STUDENT_PHOTO, "") ?: ""
    }
}
