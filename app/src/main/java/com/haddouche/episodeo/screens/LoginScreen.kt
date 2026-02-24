package com.haddouche.episodeo.screens

import android.app.Activity
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.ktx.Firebase
import com.haddouche.episodeo.R
import com.haddouche.episodeo.navigation.AppRoutes
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "PantallaLogin"

@Composable
fun LoginScreen(navController: NavController) {

    var errorMessage by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var hasNavigated by remember { mutableStateOf(false) }
    val auth: FirebaseAuth = Firebase.auth
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    fun navigateIfNotDone() {
        if (!hasNavigated) {
            hasNavigated = true
            Log.d(TAG, "Navegando a PantallaPrincipal")
            navController.navigate(AppRoutes.MainScreen.route) {
                popUpTo(AppRoutes.LoginScreen.route) { inclusive = true }
            }
        }
    }

    val googleLoginLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        Log.d(TAG, "Resultado del launcher: ${result.resultCode}")
        
        when (result.resultCode) {
            Activity.RESULT_OK -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    val googleAccount = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "Cuenta Google obtenida: ${googleAccount.email}")
                    
                    coroutineScope.launch {
                        try {
                            isLoading = true
                            errorMessage = "" // Limpiar errores previos
                            signInFirebaseWithGoogle(googleAccount.idToken!!, auth)
                            Log.d(TAG, "Login Firebase exitoso")
                            
                            registerUserInFirestore(auth)
                            Log.d(TAG, "Registro Firestore exitoso")
                            
                            isLoading = false
                            navigateIfNotDone()
                            
                        } catch (e: Exception) {
                            Log.e(TAG, "ERROR CRÍTICO EN LOGIN", e)
                            errorMessage = "Error: ${e.message}"
                            isLoading = false
                            // Mostrar Toast para que el usuario lo vea seguro
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                    
                } catch (e: ApiException) {
                    Log.e(TAG, "Error Google ApiException: código=${e.statusCode}", e)
                    errorMessage = "Error de Google (${e.statusCode}): Verifica SHA-1 en Firebase."
                    isLoading = false
                    Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                }
            }
            Activity.RESULT_CANCELED -> {
                Log.d(TAG, "Login cancelado por el usuario")
                isLoading = false
            }
            else -> {
                Log.w(TAG, "Resultado inesperado: ${result.resultCode}")
                errorMessage = "Login cancelado o fallido"
                isLoading = false
            }
        }
    }

    LaunchedEffect(Unit) {
        if (auth.currentUser != null) {
            Log.d(TAG, "Usuario ya autenticado: ${auth.currentUser?.email}")
            isLoading = true
            try {
                registerUserInFirestore(auth)
                navigateIfNotDone()
            } catch (e: Exception) {
                Log.e(TAG, "Error al verificar usuario existente", e)
                errorMessage = "Error de sesión: ${e.message}"
                Toast.makeText(context, "Error al iniciar: ${e.message}", Toast.LENGTH_LONG).show()
            } finally {
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Bienvenido a Episodeo")
        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text("Iniciando sesión...")
        } else {
            Button(onClick = {
                Log.d(TAG, "Botón clickeado")
                errorMessage = ""
                isLoading = true
                coroutineScope.launch {
                    try {
                        val googleClient = getGoogleClient(context)
                        Log.d(TAG, "Lanzando intent de Google")
                        googleLoginLauncher.launch(googleClient.signInIntent)
                    } catch (e: Exception) {
                        Log.e(TAG, "Error al preparar Google", e)
                        errorMessage = "Error al preparar Google: ${e.message}"
                        isLoading = false
                    }
                }
            }) {
                Text("Conectar con Google")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(8.dp)
            )
        }
    }
}

private fun getGoogleClient(context: Context): GoogleSignInClient {
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken(context.getString(R.string.default_web_client_id))
        .requestEmail()
        .build()
    return GoogleSignIn.getClient(context, gso)
}

private suspend fun signInFirebaseWithGoogle(token: String, auth: FirebaseAuth) {
    val credential = GoogleAuthProvider.getCredential(token, null)
    auth.signInWithCredential(credential).await()
}

private suspend fun registerUserInFirestore(auth: FirebaseAuth) {
    val firebaseUser = auth.currentUser ?: throw Exception("No hay usuario autenticado")
    
    Log.d(TAG, "Registrando usuario: ${firebaseUser.email}")
    
    val db = FirebaseFirestore.getInstance()
    val docRef = db.collection("usuarios").document(firebaseUser.uid)
    
    // Aquí es donde suele fallar si las reglas de Firestore no permiten lectura/escritura
    val existingDocument = docRef.get().await()
    
    val userData = if (existingDocument.exists()) {
        Log.d(TAG, "Usuario existente, actualizando último login")
        hashMapOf(
            "lastLoginDate" to Timestamp.now()
        )
    } else {
        Log.d(TAG, "Usuario nuevo, creando perfil")
        hashMapOf(
            "email" to (firebaseUser.email ?: ""),
            "username" to (firebaseUser.displayName ?: "Usuario"),
            "isDarkTheme" to false,
            "registrationDate" to Timestamp.now(),
            "lastLoginDate" to Timestamp.now()
        )
    }
    
    docRef.set(userData, SetOptions.merge()).await()
    Log.d(TAG, "Datos guardados en Firestore correctamente")
}
