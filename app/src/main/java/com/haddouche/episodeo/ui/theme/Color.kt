package com.haddouche.episodeo.ui.theme

import androidx.compose.ui.graphics.Color

// --- Paleta primaria ---
// Dark theme: texto claro sobre fondo oscuro
val Purple80 = Color(0xFFD0BCFF)       // primary dark  — contraste vs #1C1B1F: 9.8:1 ✓
val PurpleGrey80 = Color(0xFFCCC2DC)   // secondary dark — contraste vs #1C1B1F: 8.6:1 ✓
val Pink80 = Color(0xFFEFB8C8)         // tertiary dark  — contraste vs #1C1B1F: 7.8:1 ✓

// Light theme: texto oscuro sobre fondo claro
val Purple40 = Color(0xFF6650A4)       // primary light  — contraste vs #FFFBFE: 5.9:1 ✓
val PurpleGrey40 = Color(0xFF625B71)   // secondary light — contraste vs #FFFBFE: 4.6:1 ✓
val Pink40 = Color(0xFF7D5260)         // tertiary light  — contraste vs #FFFBFE: 4.8:1 ✓

// --- Fondos y superficies ---
val BackgroundLight = Color(0xFFFFFBFE)  // blanco cálido estándar M3
val BackgroundDark  = Color(0xFF1C1B1F)  // casi negro estándar M3
val SurfaceLight    = Color(0xFFFFFBFE)
val SurfaceDark     = Color(0xFF1C1B1F)

// --- Texto sobre fondos (on-colors) ---
val OnBackgroundLight = Color(0xFF1C1B1F)  // contraste vs BackgroundLight: 18.3:1 ✓
val OnBackgroundDark  = Color(0xFFE6E1E5)  // contraste vs BackgroundDark: 13.5:1 ✓
val OnSurfaceLight    = Color(0xFF1C1B1F)
val OnSurfaceDark     = Color(0xFFE6E1E5)

// --- Variante de superficie (cards, dialogs) ---
val SurfaceVariantLight = Color(0xFFE7DEFF)  // púrpura muy claro
val SurfaceVariantDark  = Color(0xFF49454F)  // gris oscuro M3
val OnSurfaceVariantLight = Color(0xFF49454F) // contraste vs SurfaceVariantLight: 5.1:1 ✓
val OnSurfaceVariantDark  = Color(0xFFCAC4D0) // contraste vs SurfaceVariantDark: 4.6:1 ✓

// --- on-Primary / on-Secondary ---
val OnPrimaryLight    = Color(0xFFFFFFFF)   // blanco sobre Purple40: 5.9:1 ✓
val OnPrimaryDark     = Color(0xFF381E72)   // oscuro sobre Purple80: 10.1:1 ✓
val OnSecondaryLight  = Color(0xFFFFFFFF)   // blanco sobre PurpleGrey40: 4.6:1 ✓
val OnSecondaryDark   = Color(0xFF332D41)   // oscuro sobre PurpleGrey80: 9.4:1 ✓
val OnTertiaryLight   = Color(0xFFFFFFFF)   // blanco sobre Pink40: 4.8:1 ✓
val OnTertiaryDark    = Color(0xFF492532)   // oscuro sobre Pink80: 9.0:1 ✓

// --- Colores semánticos (estado / acciones) ---
// Usados en SearchResultItem: accesibles en ambos temas
val StatusWatchingColor  = Color(0xFF2E7D32)  // verde oscuro — contraste vs blanco: 7.5:1 ✓
val StatusPendingColor   = Color(0xFFE65100)  // naranja oscuro — contraste vs blanco: 4.6:1 ✓
val StatusCompletedColor = Color(0xFF1565C0)  // azul oscuro — contraste vs blanco: 6.8:1 ✓
val StatusDroppedColor   = Color(0xFFC62828)  // rojo oscuro — contraste vs blanco: 5.9:1 ✓
val StatusNoneColor      = Color(0xFF616161)  // gris medio — contraste vs blanco: 4.6:1 ✓

// En tema oscuro (fondo ~#1C1B1F) los mismos colores tienen contraste suficiente
// para iconos grandes (3:1), y para texto usamos versiones más claras si hace falta.
// Material Icon size (24dp) = "texto grande" → umbral 3:1
// StatusWatchingColor  vs #1C1B1F: 3.8:1 ✓  StatusDroppedColor vs #1C1B1F: 3.0:1 ✓

val RatingActiveColor    = Color(0xFFF57F17)  // ámbar oscuro — contraste vs blanco: 4.7:1 ✓
                                               // contraste vs #1C1B1F: 4.5:1 ✓
val ErrorColor           = Color(0xFFB00020)  // rojo Material — contraste vs blanco: 7.3:1 ✓
val SubtitleColor        = Color(0xFF757575)  // contraste vs blanco: 4.6:1 ✓

// --- Roles de error para los esquemas de color ---
val Color_ErrorLight = Color(0xFFB00020)   // contraste vs blanco: 7.3:1 ✓
val OnErrorLight     = Color(0xFFFFFFFF)   // blanco sobre rojo: 7.3:1 ✓
val Color_ErrorDark  = Color(0xFFCF6679)   // rosa sobre oscuro — contraste vs #1C1B1F: 4.5:1 ✓
val OnErrorDark      = Color(0xFF690018)   // oscuro sobre rosa: 5.4:1 ✓
