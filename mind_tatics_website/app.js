const BASE_URL = 'http://127.0.0.1:5000';

// Global State
let token = localStorage.getItem('token') || null;
let currentUser = null;
let activeGame = null;
let activeLevel = 1;
let unlockedLevels = {
    "Reflex Tap": 1,
    "Focus Shift": 1,
    "Path Builder": 1,
    "Code Breaker": 1
};

// UI Elements
const landingView = document.getElementById('landing-view');
const dashboardView = document.getElementById('dashboard-view');
const loginModal = document.getElementById('login-modal');
const signupModal = document.getElementById('signup-modal');
const levelModal = document.getElementById('level-modal');
const userBadge = document.getElementById('user-badge');

const navLoginBtn = document.getElementById('nav-login-btn');
const navSignupBtn = document.getElementById('nav-signup-btn');
const navLogoutBtn = document.getElementById('nav-logout-btn');

// Initialize App
window.addEventListener('DOMContentLoaded', () => {
    setupEventListeners();
    checkAuthSession();
});

// --- AUTHENTICATION & ROUTING ---

function checkAuthSession() {
    if (token) {
        // Show Dashboard
        landingView.style.display = 'none';
        dashboardView.style.display = 'grid';
        navLoginBtn.style.display = 'none';
        navSignupBtn.style.display = 'none';
        navLogoutBtn.style.display = 'block';
        userBadge.style.display = 'block';
        
        fetchUserProfile();
        fetchLeaderboard();
        fetchUserProgress();
    } else {
        // Show Landing
        landingView.style.display = 'flex';
        dashboardView.style.display = 'none';
        navLoginBtn.style.display = 'block';
        navSignupBtn.style.display = 'block';
        navLogoutBtn.style.display = 'none';
        userBadge.style.display = 'none';
        currentUser = null;
    }
}

function setupEventListeners() {
    // Nav Actions
    navLoginBtn.addEventListener('click', () => showModal(loginModal));
    navSignupBtn.addEventListener('click', () => showModal(signupModal));
    document.getElementById('hero-get-started-btn').addEventListener('click', () => showModal(signupModal));
    
    navLogoutBtn.addEventListener('click', () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        token = null;
        checkAuthSession();
    });

    // Form Submissions
    document.getElementById('login-form').addEventListener('submit', handleLogin);
    document.getElementById('signup-form').addEventListener('submit', handleSignup);
}

function showModal(modal) {
    closeModals();
    modal.classList.add('active');
}

function closeModals() {
    document.querySelectorAll('.modal-overlay').forEach(m => m.classList.remove('active'));
    document.getElementById('login-alert').style.display = 'none';
    document.getElementById('signup-alert').style.display = 'none';
}

function showLogin() { showModal(loginModal); }
function showSignUp() { showModal(signupModal); }

// API - Login Handler
async function handleLogin(e) {
    e.preventDefault();
    const email = document.getElementById('login-email').value;
    const password = document.getElementById('login-password').value;
    const alertBox = document.getElementById('login-alert');

    try {
        const res = await fetch(`${BASE_URL}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        
        if (res.ok) {
            token = data.access_token;
            localStorage.setItem('token', token);
            localStorage.setItem('user', JSON.stringify(data.user));
            closeModals();
            checkAuthSession();
        } else {
            alertBox.textContent = data.msg || 'Login failed';
            alertBox.style.display = 'block';
        }
    } catch (err) {
        alertBox.textContent = 'Backend is unreachable. Please verify Python app.py is running.';
        alertBox.style.display = 'block';
    }
}

// API - Signup Handler
async function handleSignup(e) {
    e.preventDefault();
    const name = document.getElementById('signup-name').value;
    const email = document.getElementById('signup-email').value;
    const password = document.getElementById('signup-password').value;
    const alertBox = document.getElementById('signup-alert');

    try {
        const res = await fetch(`${BASE_URL}/auth/signup`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });
        const data = await res.json();

        if (res.ok) {
            // Auto login after signup
            document.getElementById('login-email').value = email;
            document.getElementById('login-password').value = password;
            showLogin();
            alertBox.textContent = 'Account created! Logging in...';
            alertBox.style.display = 'block';
            setTimeout(() => {
                document.getElementById('login-form').dispatchEvent(new Event('submit'));
            }, 1000);
        } else {
            alertBox.textContent = data.msg || 'Signup failed';
            alertBox.style.display = 'block';
        }
    } catch (err) {
        alertBox.textContent = 'Backend is unreachable. Please verify Python app.py is running.';
        alertBox.style.display = 'block';
    }
}

// --- DASHBOARD DATA & UPDATES ---

async function fetchUserProfile() {
    try {
        const res = await fetch(`${BASE_URL}/api/profile`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (res.ok) {
            const data = await res.json();
            currentUser = data;
            
            // Render user status
            userBadge.textContent = `Lvl ${data.level} | ${data.xp} XP`;
            document.getElementById('dash-level').textContent = data.level;
            document.getElementById('dash-xp').textContent = data.xp;
            document.getElementById('dash-streak').textContent = `${data.streak} 🔥`;
        }
    } catch (err) {
        console.error('Error fetching user profile:', err);
    }
}

async function fetchUserProgress() {
    try {
        const res = await fetch(`${BASE_URL}/api/progress`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (res.ok) {
            const data = await res.json();
            
            // Reset levels unlocked
            unlockedLevels = {
                "Reflex Tap": 1,
                "Focus Shift": 1,
                "Path Builder": 1,
                "Code Breaker": 1
            };
            
            data.forEach(p => {
                unlockedLevels[p.game_name] = p.level_unlocked;
            });

            // Update UI card indicators
            document.getElementById('unlocked-reflex').textContent = `Level Unlocked: ${unlockedLevels["Reflex Tap"]}`;
            document.getElementById('unlocked-focus').textContent = `Level Unlocked: ${unlockedLevels["Focus Shift"]}`;
            document.getElementById('unlocked-path').textContent = `Level Unlocked: ${unlockedLevels["Path Builder"]}`;
            document.getElementById('unlocked-code').textContent = `Level Unlocked: ${unlockedLevels["Code Breaker"]}`;
        }
    } catch (err) {
        console.error('Error fetching user progress:', err);
    }
}

async function fetchLeaderboard() {
    const list = document.getElementById('leaderboard-list');
    try {
        const res = await fetch(`${BASE_URL}/api/leaderboard`);
        if (res.ok) {
            const data = await res.json();
            list.innerHTML = '';
            
            data.forEach((u, i) => {
                const item = document.createElement('div');
                item.className = 'leaderboard-item';
                item.innerHTML = `
                    <span class="leaderboard-rank">#${i + 1}</span>
                    <span class="leaderboard-user">${u.name}</span>
                    <span class="leaderboard-xp">${u.xp} XP</span>
                `;
                list.appendChild(item);
            });
        }
    } catch (err) {
        list.innerHTML = '<p style="color: var(--red); text-align: center;">Unable to sync leaderboard.</p>';
    }
}

// --- LEVEL SELECTION ---

window.openLevelSelect = function(gameName) {
    if (!token) {
        showLogin();
        return;
    }
    
    activeGame = gameName;
    const maxUnlocked = unlockedLevels[gameName] || 1;
    const title = document.getElementById('level-modal-title');
    const desc = document.getElementById('level-modal-desc');
    const grid = document.getElementById('level-buttons-grid');
    
    title.textContent = gameName;
    desc.textContent = `Choose your training level. Max level unlocked: ${maxUnlocked}`;
    grid.innerHTML = '';
    
    // Generate 20 levels. Lock levels greater than maxUnlocked.
    for (let i = 1; i <= 20; i++) {
        const btn = document.createElement('button');
        btn.className = 'btn';
        btn.textContent = i;
        
        if (i <= maxUnlocked) {
            btn.classList.add('btn-outline');
            btn.onclick = () => {
                closeModals();
                startGame(gameName, i);
            };
        } else {
            btn.classList.add('btn-secondary');
            btn.style.opacity = '0.35';
            btn.style.cursor = 'not-allowed';
            btn.title = "Complete previous levels to unlock!";
        }
        grid.appendChild(btn);
    }
    
    showModal(levelModal);
};

// --- GAME ORCHESTRATOR & API SUBMIT ---

function startGame(gameName, level) {
    activeLevel = level;
    
    // Hide standard views
    landingView.style.display = 'none';
    dashboardView.style.display = 'none';
    
    // Reset/Launch rooms
    if (gameName === "Reflex Tap") {
        initReflexTap(level);
    } else if (gameName === "Focus Shift") {
        initFocusShift(level);
    } else if (gameName === "Path Builder") {
        initPathBuilder(level);
    } else if (gameName === "Code Breaker") {
        initCodeBreaker(level);
    }
}

window.exitGame = function() {
    // Stop any game loops
    if (gameInterval) clearInterval(gameInterval);
    
    // Hide game rooms
    document.querySelectorAll('.game-room').forEach(r => {
        r.style.display = 'none';
        // Reset overlays
        const overlay = r.querySelector('.room-overlay');
        if (overlay) overlay.style.display = 'none';
    });
    
    checkAuthSession();
};

async function submitGameScore(gameName, score, level) {
    try {
        const res = await fetch(`${BASE_URL}/api/submit-score`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${token}`
            },
            body: JSON.stringify({ game_name: gameName, score: score, level: level })
        });
        if (res.ok) {
            console.log(`Score submitted successfully for ${gameName}: ${score} pts`);
        }
    } catch (err) {
        console.error('Failed to submit score to database:', err);
    }
}

// --- GAME ENGINES ---

let gameInterval = null;

// 1. REFLEX TAP
function initReflexTap(level) {
    const room = document.getElementById('reflex-room');
    const scoreVal = document.getElementById('reflex-score');
    const timerVal = document.getElementById('reflex-timer');
    const levelBadge = document.getElementById('reflex-level-badge');
    const arena = document.getElementById('reflex-arena');
    const overlay = document.getElementById('reflex-overlay');
    const finalScore = document.getElementById('reflex-final-score');
    
    room.style.display = 'flex';
    levelBadge.textContent = `Level ${level}`;
    
    let score = 0;
    let timeLeft = 30;
    
    scoreVal.textContent = `SCORE: ${score}`;
    timerVal.textContent = `TIME: ${timeLeft}s`;
    timerVal.classList.remove('warning');
    arena.innerHTML = '';
    
    // Spawn first target
    spawnReflexTarget(arena, () => {
        score += 10 + (level * 2);
        scoreVal.textContent = `SCORE: ${score}`;
    });
    
    gameInterval = setInterval(() => {
        timeLeft--;
        timerVal.textContent = `TIME: ${timeLeft}s`;
        
        if (timeLeft < 5) {
            timerVal.classList.add('warning');
        }
        
        if (timeLeft <= 0) {
            clearInterval(gameInterval);
            arena.innerHTML = '';
            
            // Finish
            finalScore.textContent = `Your Score: ${score}`;
            overlay.style.display = 'flex';
            submitGameScore("Reflex Tap", score, level);
        }
    }, 1000);
}

function spawnReflexTarget(arena, onHit) {
    arena.innerHTML = '';
    const target = document.createElement('div');
    target.className = 'reflex-target';
    
    // Math to position it safely within the boundaries
    const maxWidth = arena.clientWidth - 80;
    const maxHeight = arena.clientHeight - 80;
    
    const x = Math.max(10, Math.floor(Math.random() * maxWidth));
    const y = Math.max(10, Math.floor(Math.random() * maxHeight));
    
    target.style.left = `${x}px`;
    target.style.top = `${y}px`;
    
    target.onclick = () => {
        onHit();
        spawnReflexTarget(arena, onHit);
    };
    
    arena.appendChild(target);
}

// 2. FOCUS SHIFT
const colors = ['#FF4D4D', '#2ECC71', '#3B82F6', '#FFD166', '#EC4899', '#06B6D4']; // HEX values
const colorNames = ['RED', 'GREEN', 'BLUE', 'YELLOW', 'MAGENTA', 'CYAN'];

function initFocusShift(level) {
    const room = document.getElementById('focus-room');
    const scoreVal = document.getElementById('focus-score');
    const timerVal = document.getElementById('focus-timer');
    const levelBadge = document.getElementById('focus-level-badge');
    const overlay = document.getElementById('focus-overlay');
    const finalScore = document.getElementById('focus-final-score');
    
    room.style.display = 'flex';
    levelBadge.textContent = `Level ${level}`;
    
    let score = 0;
    let timeLeft = 20;
    
    scoreVal.textContent = `SCORE: ${score}`;
    timerVal.textContent = `TIME: ${timeLeft}s`;
    timerVal.classList.remove('warning');
    
    // Round generator helper
    const setupRound = () => {
        const textWord = colorNames[Math.floor(Math.random() * colorNames.length)];
        const textColor = colors[Math.floor(Math.random() * colors.length)];
        
        const textElem = document.getElementById('stroop-word');
        textElem.textContent = textWord;
        textElem.style.color = textColor;
        
        // Options grid (shuffle and take 4)
        let options = [...colors].sort(() => 0.5 - Math.random()).slice(0, 4);
        if (!options.includes(textColor)) {
            options[Math.floor(Math.random() * 4)] = textColor;
        }
        
        const optionsGrid = document.getElementById('stroop-options');
        optionsGrid.innerHTML = '';
        
        options.forEach(colorHex => {
            const btn = document.createElement('div');
            btn.className = 'stroop-btn';
            btn.style.backgroundColor = colorHex;
            btn.onclick = () => {
                if (colorHex === textColor) {
                    score += 50 + (level * 5);
                } else {
                    score = Math.max(0, score - 20);
                }
                scoreVal.textContent = `SCORE: ${score}`;
                setupRound();
            };
            optionsGrid.appendChild(btn);
        });
    };
    
    setupRound();
    
    gameInterval = setInterval(() => {
        timeLeft--;
        timerVal.textContent = `TIME: ${timeLeft}s`;
        
        if (timeLeft < 5) {
            timerVal.classList.add('warning');
        }
        
        if (timeLeft <= 0) {
            clearInterval(gameInterval);
            document.getElementById('stroop-options').innerHTML = '';
            
            // Finish
            finalScore.textContent = `Final Score: ${score}`;
            overlay.style.display = 'flex';
            submitGameScore("Focus Shift", score, level);
        }
    }, 1000);
}

// 3. PATH BUILDER
function initPathBuilder(level) {
    const room = document.getElementById('path-room');
    const scoreVal = document.getElementById('path-score');
    const levelBadge = document.getElementById('path-level-badge');
    const overlay = document.getElementById('path-overlay');
    const finalScore = document.getElementById('path-final-score');
    const finalStatus = document.getElementById('path-final-status');
    const prompt = document.getElementById('path-prompt');
    const grid = document.getElementById('path-grid');
    
    room.style.display = 'flex';
    levelBadge.textContent = `Level ${level}`;
    scoreVal.textContent = `SCORE: 0`;
    grid.innerHTML = '';
    
    const gridSize = level < 5 ? 3 : (level < 15 ? 4 : 5);
    const pathLength = 3 + Math.floor(level / 2);
    
    grid.style.gridTemplateColumns = `repeat(${gridSize}, 1fr)`;
    
    const totalCells = gridSize * gridSize;
    const targetPath = [];
    for (let i = 0; i < pathLength; i++) {
        targetPath.push(Math.floor(Math.random() * totalCells));
    }
    
    // Create cells
    const cells = [];
    for (let i = 0; i < totalCells; i++) {
        const cell = document.createElement('div');
        cell.className = 'path-cell';
        grid.appendChild(cell);
        cells.push(cell);
    }
    
    // Phase 1: Show Path
    prompt.textContent = "Memorize the Path";
    prompt.style.color = 'var(--electric-blue)';
    
    // Highlight paths sequentially
    let pathIndex = 0;
    const highlightInterval = setInterval(() => {
        // Clear previous cell highlight
        cells.forEach(c => c.classList.remove('target-show'));
        
        if (pathIndex < targetPath.length) {
            cells[targetPath[pathIndex]].classList.add('target-show');
            pathIndex++;
        } else {
            clearInterval(highlightInterval);
            // End show path phase
            setTimeout(() => {
                cells.forEach(c => c.classList.remove('target-show'));
                enableUserReplication();
            }, 500);
        }
    }, 600);
    
    // Phase 2: User Input
    function enableUserReplication() {
        prompt.textContent = "Replicate the Path";
        prompt.style.color = 'var(--neon-purple)';
        
        const userPath = [];
        
        cells.forEach((cell, index) => {
            cell.onclick = () => {
                if (userPath.includes(index)) return; // Prevent double clicks
                
                cell.classList.add('clicked');
                userPath.push(index);
                
                // Audio/Visual Feedback
                setTimeout(() => cell.classList.remove('clicked'), 300);
                
                if (userPath.length === targetPath.length) {
                    // Check if path is correct
                    const isCorrect = userPath.every((val, idx) => val === targetPath[idx]);
                    const score = isCorrect ? 100 * level : 0;
                    
                    scoreVal.textContent = `SCORE: ${score}`;
                    
                    // Show game over overlay
                    finalStatus.textContent = isCorrect ? "SUCCESS" : "FAILED";
                    finalStatus.style.color = isCorrect ? 'var(--electric-blue)' : 'var(--red)';
                    finalScore.textContent = `Score: ${score}`;
                    overlay.style.display = 'flex';
                    
                    submitGameScore("Path Builder", score, level);
                }
            };
        });
    }
}

// 4. CODE BREAKER
let breakerTargetCode = [];
let breakerGuessesCount = 0;
let breakerCodeLength = 3;
let breakerCurrentGuess = [0, 0, 0];

function initCodeBreaker(level) {
    const room = document.getElementById('code-room');
    const scoreVal = document.getElementById('code-score');
    const levelBadge = document.getElementById('code-level-badge');
    const overlay = document.getElementById('code-overlay');
    const finalScore = document.getElementById('code-final-score');
    const instruction = document.getElementById('code-instruction');
    const inputsRow = document.getElementById('breaker-inputs');
    const log = document.getElementById('breaker-log');
    
    room.style.display = 'flex';
    levelBadge.textContent = `Level ${level}`;
    
    breakerGuessesCount = 0;
    scoreVal.textContent = `GUESSES: ${breakerGuessesCount}`;
    log.innerHTML = '<p style="color: var(--text-secondary); text-align: center;">Enter a guess and submit to crack the code!</p>';
    
    breakerCodeLength = level < 10 ? 3 : 4;
    instruction.textContent = `Crack the ${breakerCodeLength}-digit code`;
    
    // Generate Target Code
    breakerTargetCode = [];
    for (let i = 0; i < breakerCodeLength; i++) {
        breakerTargetCode.push(Math.floor(Math.random() * 10));
    }
    
    // Generate Input Buttons
    breakerCurrentGuess = Array(breakerCodeLength).fill(0);
    inputsRow.innerHTML = '';
    
    for (let i = 0; i < breakerCodeLength; i++) {
        const btn = document.createElement('div');
        btn.className = 'digit-input';
        btn.textContent = '0';
        btn.onclick = () => {
            breakerCurrentGuess[i] = (breakerCurrentGuess[i] + 1) % 10;
            btn.textContent = breakerCurrentGuess[i];
        };
        inputsRow.appendChild(btn);
    }
}

window.submitCodeGuess = function() {
    const log = document.getElementById('breaker-log');
    if (breakerGuessesCount === 0) {
        log.innerHTML = ''; // Clear help placeholder
    }
    
    breakerGuessesCount++;
    document.getElementById('code-score').textContent = `GUESSES: ${breakerGuessesCount}`;
    
    let bulls = 0;
    let cows = 0;
    
    const secretUsed = Array(breakerCodeLength).fill(false);
    const guessUsed = Array(breakerCodeLength).fill(false);
    
    // Step 1: Calculate Bulls (Match value and position)
    for (let i = 0; i < breakerCodeLength; i++) {
        if (breakerCurrentGuess[i] === breakerTargetCode[i]) {
            bulls++;
            secretUsed[i] = true;
            guessUsed[i] = true;
        }
    }
    
    // Step 2: Calculate Cows (Match value in incorrect position)
    for (let i = 0; i < breakerCodeLength; i++) {
        if (!guessUsed[i]) {
            for (let j = 0; j < breakerCodeLength; j++) {
                if (!secretUsed[j] && breakerCurrentGuess[i] === breakerTargetCode[j]) {
                    cows++;
                    secretUsed[j] = true;
                    break;
                }
            }
        }
    }
    
    // Add to log
    const item = document.createElement('div');
    item.className = 'breaker-log-item';
    item.innerHTML = `
        <span class="breaker-guess">${breakerCurrentGuess.join('')}</span>
        <span class="breaker-hint">🐂 ${bulls}   🐄 ${cows}</span>
    `;
    log.insertBefore(item, log.firstChild);
    
    // Check if Win
    if (bulls === breakerCodeLength) {
        const score = Math.floor((1000 * activeLevel) / (breakerGuessesCount + 1));
        
        document.getElementById('code-final-score').textContent = `Score: ${score}`;
        document.getElementById('code-overlay').style.display = 'flex';
        
        submitGameScore("Code Breaker", score, activeLevel);
    }
};
