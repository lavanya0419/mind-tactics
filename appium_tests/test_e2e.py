import time
import pytest
from appium.webdriver.common.appiumby import AppiumBy
from selenium.webdriver.support.ui import WebDriverWait
from selenium.webdriver.support import expected_conditions as EC

# Helper to find elements with explicit wait
def wait_for_element(driver, by, locator, timeout=15):
    """Wait for an element to be present and return it."""
    return WebDriverWait(driver, timeout).until(
        EC.presence_of_element_located((by, locator))
    )

def wait_for_element_clickable(driver, by, locator, timeout=15):
    """Wait for an element to be clickable and return it."""
    return WebDriverWait(driver, timeout).until(
        EC.element_to_be_clickable((by, locator))
    )

def enter_text(driver, xpath_locator, text_value):
    """Clear text field and enter new text."""
    element = wait_for_element_clickable(driver, AppiumBy.XPATH, xpath_locator)
    element.click()
    element.clear()
    element.send_keys(text_value)
    # Hide keyboard to avoid obscuring other elements
    try:
        driver.hide_keyboard()
    except:
        pass

def click_element(driver, xpath_locator):
    """Click on element located by XPath."""
    element = wait_for_element_clickable(driver, AppiumBy.XPATH, xpath_locator)
    element.click()

# --- E2E Test Cases ---

def test_signup_flow(driver):
    """Verify registration flow with a new user account."""
    # 1. Wait for splash screen to complete and transition to login screen
    print("Waiting for Login screen to load...")
    wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'WELCOME BACK')]")
    
    # 2. Click on 'Sign Up' link
    print("Navigating to Signup screen...")
    click_element(driver, "//*[contains(@text, 'Sign Up') or contains(@text, 'SIGN UP')]")
    
    # 3. Verify on Signup screen
    wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'CREATE ACCOUNT')]")
    
    # 4. Input credentials
    unique_email = f"user_{int(time.time())}@gmail.com"
    print(f"Creating account with email: {unique_email}")
    
    enter_text(driver, "//android.widget.EditText[contains(@text, 'Name') or contains(@text, 'Full Name') or @index=0]", "Appium Tester")
    enter_text(driver, "//android.widget.EditText[contains(@text, 'Email') or @index=1]", unique_email)
    enter_text(driver, "//android.widget.EditText[contains(@text, 'Password') or @index=2]", "secure123")
    
    # 5. Click SIGN UP button
    click_element(driver, "//android.widget.Button//*[contains(@text, 'SIGN UP')] | //android.widget.Button[contains(@text, 'SIGN UP')] | //*[contains(@text, 'SIGN UP')]")
    
    # 6. Verify redirection to Login Screen
    print("Verifying successful signup redirection...")
    welcome_msg = wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'WELCOME BACK') or contains(@text, 'Login to continue')]", timeout=20)
    assert welcome_msg is not None

def test_login_flow(driver):
    """Verify login is successful with valid credentials."""
    # 1. Wait for login screen to load
    print("Waiting for Login screen to load...")
    wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'WELCOME BACK')]")
    
    # 2. Fill login inputs
    print("Entering test credentials...")
    enter_text(driver, "//android.widget.EditText[contains(@text, 'Email') or @index=0]", "test@gmail.com")
    enter_text(driver, "//android.widget.EditText[contains(@text, 'Password') or @index=1]", "123456")
    
    # 3. Click LOGIN
    print("Clicking LOGIN button...")
    click_element(driver, "//android.widget.Button//*[contains(@text, 'LOGIN')] | //android.widget.Button[contains(@text, 'LOGIN')] | //*[contains(@text, 'LOGIN')]")
    
    # 4. Verify we reached the dashboard
    print("Verifying Dashboard has loaded...")
    dashboard_header = wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'TRAINING MODULES') or contains(@text, 'Hello,')]", timeout=20)
    assert dashboard_header is not None

def test_dashboard_and_navigation(driver):
    """Verify dashboard contents and bottom bar navigation tab switching."""
    # 1. Login first
    test_login_flow(driver)
    
    # 2. Check training modules exist
    print("Verifying dashboard cards...")
    wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'Reflex Tap')]")
    wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'Path Builder')]")
    wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'Code Breaker')]")
    wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'Focus Shift')]")
    
    # 3. Click on Leaderboard/Rank Tab
    print("Navigating to Leaderboard (Rank)...")
    click_element(driver, "//*[contains(@text, 'Rank') or @content-desc='Rank']")
    wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'GLOBAL RANKING')]", timeout=15)
    
    # 4. Click on Profile Tab
    print("Navigating to Profile...")
    click_element(driver, "//*[contains(@text, 'Profile') or @content-desc='Profile']")
    wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'TOTAL XP') or contains(@text, 'LOGOUT')]", timeout=15)
    
    # 5. Click on Home Tab to return to Dashboard
    print("Navigating back to Home...")
    click_element(driver, "//*[contains(@text, 'Home') or @content-desc='Home']")
    wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'TRAINING MODULES')]", timeout=15)

def test_profile_and_logout(driver):
    """Verify profile screen user details and logout functionality."""
    # 1. Login first
    test_login_flow(driver)
    
    # 2. Go to Profile Tab
    print("Navigating to Profile tab...")
    click_element(driver, "//*[contains(@text, 'Profile') or @content-desc='Profile']")
    wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'TOTAL XP')]", timeout=15)
    
    # 3. Verify user stats are populated
    user_email_text = wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'test@gmail.com')]")
    assert user_email_text is not None
    
    # 4. Click LOGOUT button
    print("Clicking LOGOUT button...")
    click_element(driver, "//*[contains(@text, 'LOGOUT')]")
    
    # 5. Verify redirected back to Login screen
    print("Verifying login redirection post-logout...")
    login_screen_elem = wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'WELCOME BACK')]", timeout=15)
    assert login_screen_elem is not None

def test_reflex_tap_gameplay(driver):
    """Verify Reflex Tap game launching, gameplay session, and score submission."""
    # 1. Login first
    test_login_flow(driver)
    
    # 2. Click on 'Reflex Tap' module
    print("Opening Reflex Tap game module...")
    click_element(driver, "//*[contains(@text, 'Reflex Tap')]")
    
    # 3. Wait for level selection screen
    print("Waiting for Level Selection...")
    wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'REFLEX TAP')]", timeout=15)
    
    # 4. Select Level 1
    print("Selecting Level 1...")
    click_element(driver, "//*[@text='1']")
    
    # 5. Verify Game screen loaded (score starts at 0, time starts at 30)
    print("Verifying gameplay UI...")
    score_label = wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'SCORE:')]", timeout=15)
    assert score_label is not None
    
    # 6. Play the game (simulate tapping the target if it appears)
    # The target is a circle (clickable Box). Let's attempt to click it to score points.
    # We can try to locate it. In Compose, it has a circular background without text.
    # It might be the only clickable box under the top scores, or we can locate it by XPath.
    # Let's try tapping it or just waiting out the 30s game timer until 'LEVEL COMPLETE' is shown.
    print("Running game session. Waiting for game to conclude (approx 30s)...")
    
    # Let's try to tap the target button 5 times to gain points
    for i in range(5):
        try:
            # Look for a clickable frame/box or view that represents the target
            # In ReflexTapScreen: Box -> size(60.dp), background(Brush.radialGradient), clickable
            # We can find it using class android.view.View or android.widget.TextView with index/attributes,
            # or we can click near center of the screen
            target = driver.find_element(AppiumBy.XPATH, "//android.view.View[@clickable='true' and not(@text)]")
            target.click()
            print(f"Tapped target target {i+1} times!")
            time.sleep(1)
        except Exception:
            # If target locator fails, sleep
            time.sleep(1)
            
    # Wait for LEVEL COMPLETE popup to appear (max 30s)
    print("Waiting for Level Complete screen...")
    level_complete_popup = wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'LEVEL COMPLETE')]", timeout=35)
    assert level_complete_popup is not None
    
    # 7. Click CONTINUE to submit score and return
    print("Clicking CONTINUE to submit score...")
    click_element(driver, "//*[contains(@text, 'CONTINUE')]")
    
    # 8. Verify returned to Level Selection Screen
    print("Verifying returned to level selection screen...")
    wait_for_element(driver, AppiumBy.XPATH, "//*[contains(@text, 'REFLEX TAP')]", timeout=15)
