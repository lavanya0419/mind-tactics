import os
import time
import pytest
from appium import webdriver
from appium.options.android import UiAutomator2Options

# Default local paths and packages
DEFAULT_APK_PATH = os.path.abspath(os.path.join(os.path.dirname(__file__), "../mind_tatics/app/build/outputs/apk/debug/app-debug.apk"))
APP_PACKAGE = "com.simats.mind_tatics"
APP_ACTIVITY = ".MainActivity"
APPIUM_SERVER_URL = "http://127.0.0.1:4723"

# Physical device settings
DEVICE_UDID = "adb-10BEBL05M8005DL-mnLPbO._adb-tls-connect._tcp"
PLATFORM_VERSION = "16"

# Dictionary to hold test run stats programmatically for report generation
TEST_RESULTS = []

@pytest.fixture(scope="function")
def driver(request):
    """Fixture to initialize and teardown the Appium driver."""
    options = UiAutomator2Options()
    options.platform_name = "Android"
    options.automation_name = "UiAutomator2"
    options.device_name = "Android Device"
    options.udid = DEVICE_UDID
    options.platform_version = PLATFORM_VERSION
    
    # Use APK if it exists, otherwise launch already-installed app
    if os.path.exists(DEFAULT_APK_PATH):
        options.app = DEFAULT_APK_PATH
    else:
        # App is already installed on device — launch by package + activity
        options.app_package = APP_PACKAGE
        options.app_activity = APP_ACTIVITY
        
    options.no_reset = True   # Don't reset app state between runs
    options.full_reset = False
    options.new_command_timeout = 300

    print(f"Connecting to Appium server at {APPIUM_SERVER_URL}...")
    try:
        driver = webdriver.Remote(APPIUM_SERVER_URL, options=options)
    except Exception as e:
        pytest.fail(f"Could not connect to Appium server at {APPIUM_SERVER_URL}. "
                    f"Make sure the Appium server is running. Error: {e}")
        
    # Set implicit wait
    driver.implicitly_wait(10)
    
    # Store driver name in request to allow screenshot capturing
    request.node.funcargs["driver"] = driver
    
    yield driver
    
    # Teardown
    print("Quitting Appium driver session...")
    try:
        driver.quit()
    except Exception as e:
        print(f"Error quitting driver: {e}")

@pytest.hookimpl(tryfirst=True, hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """Hook to capture screenshot on test failure and record results."""
    outcome = yield
    rep = outcome.get_result()
    
    # We only care about the actual test execution call
    if rep.when == "call":
        category = "Functional Testing"
        if "signup" in item.name:
            category = "Functional Testing"
        elif "login" in item.name:
            category = "Functional Testing"
        elif "dashboard" in item.name or "navigation" in item.name:
            category = "UI-UX Testing"
        elif "logout" in item.name or "profile" in item.name:
            category = "Regression Testing"
        elif "gameplay" in item.name or "reflex" in item.name:
            category = "E2E Testing"

        test_info = {
            "name": item.name,
            "description": item.obj.__doc__ or "No description",
            "status": "Passed" if rep.passed else ("Failed" if rep.failed else "Skipped"),
            "duration": round(rep.duration, 2),
            "error_msg": "",
            "screenshot": "",
            "category": category
        }
        
        if rep.failed:
            test_info["error_msg"] = str(rep.longrepr)
            
            # Capture screenshot
            driver = item.funcargs.get("driver")
            if driver:
                screenshots_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), "screenshots"))
                os.makedirs(screenshots_dir, exist_ok=True)
                filename = f"{item.name}_{int(time.time())}.png"
                filepath = os.path.join(screenshots_dir, filename)
                try:
                    driver.save_screenshot(filepath)
                    test_info["screenshot"] = filepath
                    print(f"Screenshot saved to: {filepath}")
                except Exception as e:
                    print(f"Failed to capture screenshot: {e}")
                    
        TEST_RESULTS.append(test_info)
