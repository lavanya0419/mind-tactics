import os
import time
import pytest
from selenium import webdriver
from selenium.webdriver.chrome.options import Options as ChromeOptions
from selenium.webdriver.edge.options import Options as EdgeOptions

SITE_URL = "http://localhost:8000"
TEST_RESULTS = []

@pytest.fixture(scope="function")
def driver(request):
    """Fixture to spin up Chrome/Edge Selenium webdriver and load site."""
    # Try Chrome Options
    chrome_opts = ChromeOptions()
    chrome_opts.add_argument("--headless=new") # Run in headless mode
    chrome_opts.add_argument("--no-sandbox")
    chrome_opts.add_argument("--disable-dev-shm-usage")
    chrome_opts.add_argument("--window-size=1280,800")
    
    driver = None
    try:
        driver = webdriver.Chrome(options=chrome_opts)
    except Exception:
        # Fallback to Edge
        try:
            edge_opts = EdgeOptions()
            edge_opts.add_argument("--headless")
            edge_opts.add_argument("--window-size=1280,800")
            driver = webdriver.Edge(options=edge_opts)
        except Exception as e:
            pytest.fail(f"Could not initialize Chrome or Edge webdriver: {e}")

    driver.implicitly_wait(5)
    
    # Store driver in request node to allow screenshot capture on failure
    request.node.funcargs["driver"] = driver
    
    print(f"Loading site at {SITE_URL}...")
    driver.get(SITE_URL)
    
    yield driver
    
    print("Quitting browser driver...")
    try:
        driver.quit()
    except Exception:
        pass

@pytest.hookimpl(tryfirst=True, hookwrapper=True)
def pytest_runtest_makereport(item, call):
    """Pytest hook to catch failures and grab screenshots."""
    outcome = yield
    rep = outcome.get_result()
    
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
            driver = item.funcargs.get("driver")
            if driver:
                screenshots_dir = os.path.abspath(os.path.join(os.path.dirname(__file__), "screenshots"))
                os.makedirs(screenshots_dir, exist_ok=True)
                filename = f"{item.name}_{int(time.time())}.png"
                filepath = os.path.join(screenshots_dir, filename)
                try:
                    driver.save_screenshot(filepath)
                    test_info["screenshot"] = filepath
                    print(f"Captured screenshot at: {filepath}")
                except Exception as e:
                    print(f"Failed to capture screenshot: {e}")
                    
        TEST_RESULTS.append(test_info)
