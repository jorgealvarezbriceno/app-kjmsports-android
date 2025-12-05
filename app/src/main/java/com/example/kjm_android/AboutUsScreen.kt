package com.example.kjm_android

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(navController: NavController) {
    Scaffold(
        containerColor = Color(0xFF212121),
        topBar = {
            TopAppBar(
                title = { Text("Quiénes Somos") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1C1C1C),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AboutSection(
                imageUrl = "https://images.unsplash.com/photo-1551632811-561732d1e306?q=80&w=2070&auto=format&fit=crop",
                title = "Nuestra Misión",
                text = "En KJM Sports, nuestra misión es equipar a cada atleta, desde el principiante entusiasta hasta el profesional experimentado, con las herramientas de la más alta calidad para que puedan superar sus límites y alcanzar sus metas. Creemos que el deporte es una fuente de salud, disciplina y superación personal."
            )
            AboutSection(
                imageUrl = "https://www.elisaribau.com/wp-content/uploads/2018/03/deporte2.jpg",
                title = "Nuestro Compromiso",
                text = "Nos comprometemos a ofrecer una selección curada de los mejores productos del mercado, combinada con un servicio al cliente excepcional. Cada artículo en nuestra tienda ha sido seleccionado y probado por expertos para garantizar su durabilidad, rendimiento y confort. Tu éxito es nuestro éxito, y estamos aquí para apoyarte en cada paso de tu viaje deportivo."
            )
            AboutSection(
                imageUrl = "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS-XDs7L58mWS69rAOIkLJwE74nbNgf3Q5_qQ&s",
                title = "Nuestra Visión",
                text = "Aspiramos a ser más que una tienda; queremos construir una comunidad de apasionados por el deporte. Organizamos eventos, ofrecemos guías de entrenamiento y creamos un espacio donde los atletas pueden conectar, compartir sus logros y motivarse mutuamente. Únete a la familia KJM Sports y vive el deporte al máximo."
            )
        }
    }
}

@Composable
private fun AboutSection(imageUrl: String, title: String, text: String) {
    Column {
        AsyncImage(
            model = imageUrl,
            contentDescription = title,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = Color.LightGray
        )
    }
}