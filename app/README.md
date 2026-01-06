# 🎓 University Attendance App

The **University Attendance App** is an Android application developed using **Jetpack Compose** that enables students to mark attendance securely using **location-based (geofencing) verification**. Attendance can only be marked when the student is physically present inside the university campus.

---

## ✨ Features

- 📍 Location-based attendance using GPS (Geofencing)
- 📅 Course, date, and time selection
- 📸 Profile image capture and upload (Camera + imgBB)
- 🔐 Student registration and login
- ☁️ Firebase Realtime Database integration
- 🗺️ Google Maps integration for live location
- 👤 Profile screen with image, class, and logout option
- 📊 Attendance stored with course, date, and time

---

## 🛠️ Tech Stack

- **Programming Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Backend**: Firebase Realtime Database
- **Maps & Location**: Google Maps SDK, Fused Location Provider
- **Image Hosting**: imgBB
- **Image Loading**: Coil

---


## 🔑 Required Configuration

### 🔹 Firebase Setup
1. Create a project in Firebase Console
2. Add an Android app to the project
3. Download `google-services.json`
4. Place it inside the `app/` folder
5. Enable **Firebase Realtime Database**



### How to Run the Project
1. Clone the Repository
git clone https://github.com/thirumaleshtees/UniversityAttendanceApp.git

2. Open Project

Open Android Studio

Click Open

Select the project folder

3. Sync Gradle

Wait for Gradle sync to complete

Resolve any missing dependencies if prompted

4. Connect Firebase

Ensure google-services.json is correctly placed

Verify Firebase dependencies are added

5. Run the App

Connect a physical Android device OR start an emulator with Google Play Services

Click ▶ Run in Android Studio