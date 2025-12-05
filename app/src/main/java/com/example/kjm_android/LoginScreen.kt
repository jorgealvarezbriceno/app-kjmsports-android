package com.example.kjm_android

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
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
    var email by remember { mutableStateOf("admin.pablo@gmail.com") } // Default for admin testing
    var password by remember { mutableStateOf("admin123") } // Default for admin testing
    val loginState by loginViewModel.loginState.collectAsState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo electrónico") },
            modifier = Modifier.fillMaxWidth(),
            enabled = loginState !is LoginState.Loading
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            enabled = loginState !is LoginState.Loading
        )

        Spacer(modifier = Modifier.height(16.dp))

        when (val state = loginState) {
            is LoginState.Idle -> {
                Button(
                    onClick = { loginViewModel.login(email, password) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Ingresar")
                }
            }
            is LoginState.Loading -> {
                CircularProgressIndicator()
            }
            is LoginState.Success -> {
                LaunchedEffect(state.user) {
                    userViewModel.setUser(state.user) // Save the user
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