# Changelog

Todos los cambios notables de este proyecto se documentan aquí.

El formato está basado en [Keep a Changelog](https://keepachangelog.com/es/1.1.0/) y el proyecto sigue [Versionado Semántico](https://semver.org/lang/es/).

---

## [1.0.0] - 2024-12

### Añadido
- Autenticación con Google mediante Firebase Auth.
- Búsqueda de series en tiempo real usando la API de TMDB (español).
- Organización por estado: **Viendo**, **Pendiente**, **Terminada**, **Abandonada**.
- Sistema de puntuación numérica (1–10) para series terminadas.
- Listas personalizadas: crear, eliminar y gestionar.
- Compartir listas mediante código único de 8 caracteres.
- Seguir listas de otros usuarios por código.
- Sincronización en la nube con Firebase Firestore.
- Modo offline con caché local mediante Room (metadatos de series).
- Precarga automática de carátulas con Coil al iniciar la app.
- Tema oscuro y claro configurable por usuario (persiste en Firestore).
- Pantalla de detalle de serie: sinopsis, fecha de estreno, reparto y plataformas de streaming disponibles.
- Eliminación de cuenta completa (datos en Firestore + auth).
- Suite de pruebas unitarias con ~90% de cobertura (JUnit 4, MockK, Coroutines Test, Turbine).
- Tests de UI con Jetpack Compose Testing.
