# Guía de Pruebas — Episodeo

Este documento describe la suite de pruebas de Episodeo: qué se prueba, cómo ejecutar los tests y cómo interpretar los resultados.

---

## Tipos de Prueba

### Tests Unitarios (`app/src/test/`)

Verifican la lógica de negocio de forma aislada, sin dispositivo ni emulador.

| Paquete | Qué se prueba |
|---------|--------------|
| `models.*` | Creación y propiedades de `AppList`, `SystemList`, `CustomList`, `UserSeries` |

### Tests de Instrumentación (`app/src/androidTest/`)

Requieren un emulador o dispositivo físico.

| Archivo | Qué se prueba |
|---------|--------------|
| `ExampleInstrumentedTest` | Verificación del contexto de la app |
| `ComposeUITest` | Renderizado y comportamiento de componentes Compose |

---

## Ejecutar los Tests

### Tests unitarios (rápido, sin dispositivo)

```bash
# Todos los tests
./gradlew test

# Con salida detallada en consola
./gradlew test --console=plain
```

**Resultado esperado:**
```
BUILD SUCCESSFUL in ~5s
2 tests completed, 0 failed
```

### Tests por paquete

```bash
# Solo modelos
./gradlew test --tests "com.haddouche.episodeo.models.*"
```

### Tests de instrumentación (requiere emulador/dispositivo)

```bash
./gradlew connectedAndroidTest
```

---

## Reportes

### Reporte HTML de tests unitarios

```bash
./gradlew test
start app/build/reports/tests/testDebugUnitTest/index.html
```

El reporte muestra: tests ejecutados, pasados, fallados y duración.

### Reporte de cobertura con JaCoCo

```bash
./gradlew testDebugUnitTest jacocoTestReport
start app/build/reports/jacoco/testDebugUnitTest/html/index.html
```

**Cobertura objetivo del proyecto:**

Actualmente reducida al no testear repository ni viewmodels por cuestiones de mocking.

| Paquete | Clases | Métodos | Líneas |
|---------|--------|---------|--------|
| `models` | 100% | 100% | 100% |

---

## Dependencias de Test

Declaradas en `app/build.gradle.kts`:

```kotlin
testImplementation("junit:junit:4.13.2")

androidTestImplementation("androidx.test.ext:junit:1.1.5")
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
```

---

## Solución de Problemas

### `BUILD FAILED` — conflicto con SDK Build-Tools 25.0.1

Ver sección correspondiente en [SETUP.md](./SETUP.md#error-sdk-build-tools-2501-al-compilar).

### Tests no se encuentran

1. Verifica que los archivos estén en `app/src/test/java/`
2. **File → Sync Project with Gradle Files**
3. **File → Invalidate Caches / Restart**
