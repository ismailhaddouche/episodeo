package com.haddouche.episodeo.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage // <-- La librería de fotos
import com.google.firebase.auth.FirebaseAuth

/**
 * Profe, este es tu "Componente Account".
 * Es reutilizable y solo se encarga de mostrar
 * la foto y el nombre del usuario actual.
 */
@Composable
fun MenuHeader() {
    // Obtenemos al usuario actual de Firebase
    val currentUser = FirebaseAuth.getInstance().currentUser

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp), // Un buen espacio arriba y abajo
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Usamos AsyncImage (de Coil) para cargar la foto de Google
            AsyncImage(
                model = currentUser?.photoUrl, // La URL de la foto de Google
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(100.dp) // Tamaño de la foto
                    .clip(CircleShape), // La hacemos redonda
                contentScale = ContentScale.Crop // Para que rellene el círculo
            )

            Spacer(modifier = Modifier.height(16.dp)) // Espacio entre foto y nombre

            // Mostramos el nombre del usuario (si lo tiene)
            Text(
                text = currentUser?.displayName ?: "Usuario",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}