# MindTactics - Futuristic Brain Training App

MindTactics is a full-stack Android application designed to train your cognition through 4 specialized game modules with 100 levels each.

## 🚀 Getting Started

### 1. Backend Setup (Flask)
- Navigate to the `mind_tatics_backend` directory.
- Install dependencies:
  ```bash
  pip install -r requirements.txt
  ```
- Run the server:
  ```bash
  python app.py
  ```
- The server will run on `http://10.0.2.2:5000` (accessible by the Android emulator).

### 2. Frontend Setup (Android)
- Open the `mind_tatics` folder in Android Studio.
- Wait for Gradle sync to complete.
- Ensure your emulator has internet access.
- Run the `app` module.

## 🧪 Test Credentials
- **Email**: `test@gmail.com`
- **Password**: `123456`

## 🎮 Features
- **Reflex Tap**: Test your speed by tapping targets.
- **Path Builder**: Memorize and replicate complex paths.
- **Code Breaker**: Guess the secret pattern with logic.
- **Focus Shift**: Overcome the Stroop effect and identify colors.

## 🛠 Tech Stack
- **Frontend**: Kotlin, Jetpack Compose, Retrofit, MVVM, DataStore.
- **Backend**: Python Flask, SQLAlchemy, JWT Authentication.
- **Database**: SQLite.
