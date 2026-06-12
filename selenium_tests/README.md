# MindTactics - Selenium End-to-End Automated Web Testing Suite

This folder contains the complete, automated end-to-end (E2E) Selenium testing framework for the **MindTactics** Web Application. It validates user authentication, dashboard sync, leaderboard, and browser gameplay modules, outputting a professional styled Excel report.

---

## 🛠️ Prerequisites & Setup

### 1. Install Chrome or Edge Browser
Ensure you have Google Chrome or Microsoft Edge installed on your system. Selenium will dynamically launch the browser in headless mode by default.

### 2. Python Environment & Dependencies
Navigate to this folder (`selenium_tests`) and install the required Python libraries:
```bash
pip install -r requirements.txt
```

### 3. Start Local Web Portal & Flask Backend
Ensure both local servers are running:
```bash
# Terminal 1: Flask Backend
cd mind_tatics_backend && python app.py

# Terminal 2: Web Server
cd mind_tatics_website && python -m http.server 8000
```

---

## 🚀 Running the Test Suite

Run the orchestrator Python script from this folder:
```bash
python run_tests.py
```

*   **Live Mode:** If your web server is running on `http://localhost:8000`, the script will launch a headless browser and perform actual user interface tests.
*   **Mock Mode:** If the web server is offline, the script will automatically switch to **Mock Simulation Mode** to verify the reporting engine, output console logs, and write the Excel report.
*   **Force Mock Mode:** You can force simulation mode by passing the `--mock` flag:
    ```bash
    python run_tests.py --mock
    ```

---

## 📊 Outputs & Reports

*   **Excel Report (`test_report.xlsx`)**:
    - Generates a summary dashboard.
    - Side-by-side **Pie Chart** (placed at `I4` to avoid overlaps) matching the Passed/Failed/Skipped statuses.
    - Links to any captured screenshots if tests fail.
*   **Screenshots Folder (`screenshots/`)**:
    - Stores PNG images taken at the exact moment of a failure.
