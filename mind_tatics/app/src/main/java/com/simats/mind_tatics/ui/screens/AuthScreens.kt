package com.simats.mind_tatics.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.simats.mind_tatics.api.LoginRequest
import com.simats.mind_tatics.api.SignupRequest
import com.simats.mind_tatics.ui.AuthEvent
import com.simats.mind_tatics.ui.AuthViewModel
import com.simats.mind_tatics.ui.components.FuturisticButton
import com.simats.mind_tatics.ui.theme.DeepSpace
import com.simats.mind_tatics.ui.theme.ElectricBlue
import com.simats.mind_tatics.ui.theme.NeonPurple

@Composable
fun LoginScreen(viewModel: AuthViewModel, onNavigateToSignup: () -> Unit, onSuccess: () -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.event.collect { event ->
            when (event) {
                is AuthEvent.Success -> onSuccess()
                is AuthEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DeepSpace
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "WELCOME BACK",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = NeonPurple
                )
            )
            Text("Login to continue your training", color = ElectricBlue.copy(0.7f))
            
            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            FuturisticButton(
                text = "LOGIN",
                isLoading = isLoading,
                onClick = { viewModel.login(LoginRequest(email, password)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Don't have an account? Sign Up",
                color = ElectricBlue,
                modifier = Modifier.clickable { onNavigateToSignup() }
            )
        }
    }
}

@Composable
fun SignupScreen(viewModel: AuthViewModel, onNavigateToLogin: () -> Unit, onSuccess: () -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isLoading by viewModel.isLoading.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.event.collect { event ->
            when (event) {
                is AuthEvent.Success -> onSuccess()
                is AuthEvent.Error -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = DeepSpace
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "CREATE ACCOUNT",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = NeonPurple
                )
            )
            Text("Start your evolution today", color = ElectricBlue.copy(0.7f))
            
            Spacer(modifier = Modifier.height(48.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            FuturisticButton(
                text = "SIGN UP",
                isLoading = isLoading,
                onClick = { viewModel.signup(SignupRequest(name, email, password)) }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Already have an account? Login",
                color = ElectricBlue,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
}
