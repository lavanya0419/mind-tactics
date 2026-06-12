# MindTactics - Appium End-to-End Automated Testing Suite

This folder contains the complete, automated end-to-end (E2E) Appium testing framework for the **MindTactics** Android application. It interacts with the UI elements of the Jetpack Compose app, records logs, captures screenshots on failures, and outputs a highly detailed, professional Excel report with charts.

---

## 🛠️ Prerequisites & Setup

### 1. Appium & Driver Installation
You need Node.js installed on your machine. Install Appium and the `uiautomator2` driver by running the following commands in your command line:
```bash
# Install Appium globally
npm install -g appium

# Install UIAutomator2 driver for Android
appium driver install uiautomator2
```

### 2. Python Environment & Dependencies
Ensure you have Python 3.8+ installed. Navigate to this folder (`appium_tests`) and install the required libraries:
```bash
pip install -r requirements.txt
```

### 3. Connect Android Device / Emulator
* **Emulator**: Start an Android Emulator via Android Studio Device Manager.
* **Physical Device**: Connect your device via USB, enable **USB Debugging** in Developer Options, and make sure `adb devices` shows your device connected.
* Ensure your computer's local IP address or localhost configuration matches the backend endpoint settings in the app.

### 4. Build the Application APK
Build the debug version of the Android app using Android Studio, or run the command below from the root of the `mind_tatics` project folder:
```bash
./gradlew assembleDebug
```
This will compile and generate the APK at:
`mind_tatics/app/build/outputs/apk/debug/app-debug.apk`

---

## 🚀 Running the Test Suite

Follow these steps in order:

### Step 1: Start your Local Flask Backend
Ensure the Flask backend is running on your host machine:
```bash
cd mind_tatics_backend
python app.py
```

### Step 2: Start the Appium Server
Open a new terminal window/command prompt and start the Appium server on default port `4723`:
```bash
appium
```

### Step 3: Run the Orchestrator Runner Script
Open another terminal, navigate to the `appium_tests` directory, and run the Python orchestrator script:
```bash
cd appium_tests
python run_tests.py
```

---

## 📊 Outputs & Reports

Once the test run completes, the runner automatically outputs:

1. **Excel Report (`test_report.xlsx`)**:
   - Located in the `appium_tests` folder.
   - **Executive Dashboard**: A visual summary of Passed/Failed counts, Pass Rate %, and execution timestamps.
   - **Detailed Logs**: Exact indexes, test names, descriptions, status colors, and completion durations for every test case.
   - **Interactive Chart**: A 3D/Flat Pie Chart showing the test result distribution.
   - **Failure Screenshots Integration**: If any test fails, a hyperlink titled `"View Screenshot"` is added to that row. Clicking it opens the screenshot immediately.

2. **Screenshots Directory (`screenshots/`)**:
   - Automatically created inside `appium_tests/`.
   - Stores PNG image files captured at the exact moment a test fails.
