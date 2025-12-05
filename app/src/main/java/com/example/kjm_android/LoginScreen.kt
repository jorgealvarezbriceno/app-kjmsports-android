package com.example.kjm_android

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.kjm_android.viewmodel.LoginState
import com.example.kjm_android.viewmodel.LoginViewModel
import com.example.kjm_android.viewmodel.UserViewModel

@Composable
fun LoginScreen(
    navController: NavController, 
    loginViewModel: LoginViewModel = viewModel(), 
    userViewModel: UserViewModel
) {
    var email by remember { mutableStateOf("admin.pablo@gmail.com") } 
    var password by remember { mutableStateOf("admin123") }
    val loginState by loginViewModel.loginState.collectAsState()
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1C1C)), // Dark background
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // --- LOGO FROM DRAWABLE ---
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo de KJMSports",
                modifier = Modifier
                    .size(160.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Bienvenido a KJMSports",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Email Field
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                enabled = loginState !is LoginState.Loading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password Field
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = Color.Gray,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    cursorColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = loginState !is LoginState.Loading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Login Button and Loading State
            Box(modifier = Modifier.fillMaxWidth().height(48.dp), contentAlignment = Alignment.Center) {
                 when (val state = loginState) {
                    is LoginState.Idle -> {
                        Button(
                            onClick = { loginViewModel.login(email, password) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Ingresar", fontSize = 16.sp)
                        }
                    }
                    is LoginState.Loading -> {
                        CircularProgressIndicator()
                    }
                    is LoginState.Success -> {
                        LaunchedEffect(state.user) {
                            userViewModel.setUser(state.user)
                            navController.navigate("home") { popUpTo("login") { inclusive = true } }
                        }
                    }
                    is LoginState.Error -> {
                        Toast.makeText(context, state.message, Toast.LENGTH_SHORT).show()
                        loginViewModel.resetState()
                    }
                }
            }
        }
    }
}