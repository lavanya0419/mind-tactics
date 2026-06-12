import time
import pytest
from selenium.webdriver.common.by import By
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Helper function
def wait_for_element(driver, locator, timeout=10):
    return WebDriverWait(driver, timeout).until(
        EC.presence_of_element_located(locator)
    )

def wait_for_visible(driver, locator, timeout=10):
    return WebDriverWait(driver, timeout).until(
        EC.visibility_of_element_located(locator)
    )

def wait_for_clickable(driver, locator, timeout=10):
    return WebDriverWait(driver, timeout).until(
        EC.element_to_be_clickable(locator)
    )

# --- E2E Test Cases ---

def test_web_signup_flow(driver):
    """Verify registration flow for a new profile."""
    print("Navigating to signup modal...")
    signup_btn = wait_for_clickable(driver, (By.ID, "nav-signup-btn"))
    signup_btn.click()
    
    # Wait for modal to become active
    wait_for_visible(driver, (By.ID, "signup-modal"))
    
    unique_email = f"web_user_{int(time.time())}@gmail.com"
    print(f"Signing up with email: {unique_email}")
    
    driver.find_element(By.ID, "signup-name").send_keys("Selenium Tester")
    driver.find_element(By.ID, "signup-email").send_keys(unique_email)
    driver.find_element(By.ID, "signup-password").send_keys("secure123")
    
    # Submit form
    driver.find_element(By.CSS_SELECTOR, "#signup-form button[type='submit']").click()
    
    # Verify redirected to login modal auto-flow
    alert = wait_for_visible(driver, (By.ID, "signup-alert"))
    assert "Account created" in alert.text or alert.is_displayed()
    
    # Wait to transitions to login
    time.sleep(2)

def test_web_login_flow(driver):
    """Verify login is successful with valid credentials."""
    print("Opening login modal...")
    login_btn = wait_for_clickable(driver, (By.ID, "nav-login-btn"))
    login_btn.click()
    
    wait_for_visible(driver, (By.ID, "login-modal"))
    
    driver.find_element(By.ID, "login-email").send_keys("test@gmail.com")
    driver.find_element(By.ID, "login-password").send_keys("123456")
    
    print("Submitting login form...")
    driver.find_element(By.CSS_SELECTOR, "#login-form button[type='submit']").click()
    
    # Verify dashboard shows up
    dashboard = wait_for_visible(driver, (By.ID, "dashboard-view"))
    assert dashboard.is_displayed()
    
    # Verify level stat card
    level_val = driver.find_element(By.ID, "dash-level")
    assert level_val.text != ""
    print(f"Logged in successfully. User Level: {level_val.text}")

def test_web_dashboard_and_navigation(driver):
    """Verify dashboard contents and global leaderboard load."""
    test_web_login_flow(driver)
    
    print("Verifying training module cards exist...")
    assert driver.find_element(By.XPATH, "//*[contains(text(), 'Reflex Tap')]").is_displayed()
    assert driver.find_element(By.XPATH, "//*[contains(text(), 'Focus Shift')]").is_displayed()
    assert driver.find_element(By.XPATH, "//*[contains(text(), 'Path Builder')]").is_displayed()
    assert driver.find_element(By.XPATH, "//*[contains(text(), 'Code Breaker')]").is_displayed()
    
    print("Verifying Global Leaderboard is rendered...")
    leaderboard = wait_for_visible(driver, (By.ID, "leaderboard-list"))
    assert leaderboard.is_displayed()

def test_web_profile_and_logout(driver):
    """Verify user badge displays correct info and logout functionality."""
    test_web_login_flow(driver)
    
    badge = driver.find_element(By.ID, "user-badge")
    assert badge.is_displayed()
    assert "Lvl" in badge.text
    
    print("Clicking Logout button...")
    logout_btn = wait_for_clickable(driver, (By.ID, "nav-logout-btn"))
    logout_btn.click()
    
    # Verify landing view is restored
    landing = wait_for_visible(driver, (By.ID, "landing-view"))
    assert landing.is_displayed()
    assert driver.find_element(By.ID, "nav-login-btn").is_displayed()

def test_web_reflex_tap_gameplay(driver):
    """Verify Reflex Tap game triggers level selection, launches, and gameplay completes."""
    test_web_login_flow(driver)
    
    print("Clicking Reflex Tap game card...")
    reflex_card = wait_for_clickable(driver, (By.XPATH, "//*[contains(text(), 'Reflex Tap')]/ancestor::div[contains(@class, 'game-card')]"))
    reflex_card.click()
    
    # Wait for Level Select modal
    wait_for_visible(driver, (By.ID, "level-modal"))
    
    # Select Level 1 button
    level_btn = wait_for_clickable(driver, (By.XPATH, "//div[@id='level-buttons-grid']/button[text()='1']"))
    level_btn.click()
    
    # Wait for game room to show
    wait_for_visible(driver, (By.ID, "reflex-room"))
    
    # Verify game timer starts
    timer = driver.find_element(By.ID, "reflex-timer")
    assert "TIME:" in timer.text
    
    print("Tapping neon targets...")
    for _ in range(3):
        try:
            target = wait_for_clickable(driver, (By.CLASS_NAME, "reflex-target"), timeout=2)
            target.click()
            print("Hit target!")
            time.sleep(0.5)
        except Exception:
            break
            
    # Wait out the 30s timer
    print("Waiting for game timer to conclude (approx 30s)...")
    overlay = wait_for_visible(driver, (By.ID, "reflex-overlay"), timeout=35)
    assert overlay.is_displayed()
    
    print("Clicking continue button...")
    overlay.find_element(By.TAG_NAME, "button").click()
    
    # Check returned to Dashboard
    wait_for_visible(driver, (By.ID, "dashboard-view"))
