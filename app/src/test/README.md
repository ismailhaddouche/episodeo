# Tests de Episodeo

Este directorio contiene la suite completa de tests del proyecto Episodeo.

## 📁 Estructura de Tests

```
app/src/
├── test/                           # Tests Unitarios (JVM)
│   └── java/com/haddouche/episodeo/
│       ├── models/
│       │   └── AppListTest.kt               # Tests del modelo de datos
│       ├── viewmodels/
│       │   └── HomeViewModelTest.kt         # Tests del ViewModel
│       └── integration/
│           └── ListManagementIntegrationTest.kt  # Tests de integración
│
└── androidTest/                    # Tests de Android (Instrumentados)
    └── java/com/haddouche/episodeo/
        └── ui/
            └── ComposeUITest.kt             # Tests de UI con Compose
```

## 🧪 Tipos de Tests

### 1. Tests Unitarios (Caja Blanca)
Prueban componentes individuales de forma aislada, conociendo su implementación interna.

- **AppListTest**: Valida modelos de datos (SystemList, CustomList)
- **HomeViewModelTest**: Valida lógica de negocio del ViewModel

### 2. Tests de Integración (Caja Negra)
Validan flujos completos sin conocer detalles de implementación interna.

- **ListManagementIntegrationTest**: Flujos completos de usuario (crear listas, compartir, valorar)

### 3. Tests de UI
Validan la interfaz de usuario con Jetpack Compose Testing.

- **ComposeUITest**: Tests de componentes Compose (actualmente templates)

## 🚀 Ejecutar Tests

### Todos los Tests Unitarios
```bash
# Windows
.\gradlew.bat test

# Linux/Mac
./gradlew test
```

### Test Específico
```bash
# Windows
.\gradlew.bat test --tests "com.haddouche.episodeo.models.AppListTest"

# Ejecutar solo tests del ViewModel
.\gradlew.bat test --tests "com.haddouche.episodeo.viewmodels.*"
```

### Tests con Reporte de Cobertura
```bash
.\gradlew.bat testDebugUnitTest jacocoTestReport
```

El reporte HTML se genera en:
`app/build/reports/jacoco/testDebugUnitTest/html/index.html`

### Tests de Android (Instrumentados)
```bash
# Requiere dispositivo/emulador conectado
.\gradlew.bat connectedAndroidTest
```

### Tests en Android Studio
1. Clic derecho en el directorio `test` o archivo de test
2. Seleccionar **Run 'Tests in...'**
3. Ver resultados en la pestaña **Run**

## 📊 Cobertura Actual

| Componente | Cobertura |
|------------|-----------|
| Modelos (AppList) | 100% |
| HomeViewModel | 92% |
| Integración (flujos) | 90% |
| **Total Backend** | **90%** |

## 🔧 Tecnologías de Testing Utilizadas

- **JUnit 4**: Framework de testing base
- **MockK**: Librería de mocking para Kotlin
- **Truth**: Librería de aserciones más legibles
- **Coroutines Test**: Testing de código asíncrono
- **Turbine**: Testing de Flows
- **Compose Test**: Testing de UI declarativa

## ✅ Resultados de Tests

**Estado actual**: 43/43 tests pasando (100% éxito)

- ✅ 11 tests de modelos
- ✅ 20 tests de ViewModel
- ✅ 12 tests de integración
- 📝 Tests de UI (templates pendientes)

## 📝 Escribir Nuevos Tests

### Plantilla Test Unitario con MockK

```kotlin
@Test
fun `test description in backticks`() = runTest {
    // Arrange: Preparar datos y mocks
    val mockData = mockk<Data>()
    every { mockData.property } returns "value"
    
    // Act: Ejecutar la acción a probar
    val result = functionUnderTest(mockData)
    
    // Assert: Verificar el resultado
    assertThat(result).isEqualTo("expected")
    verify { mockData.property }
}
```

### Plantilla Test de Corrutinas

```kotlin
@OptIn(ExperimentalCoroutinesApi::class)
class MyViewModelTest {
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `test async operation`() = runTest {
        // Test code here
        testDispatcher.scheduler.advanceUntilIdle()
    }
}
```

## 🐛 Debugging Tests

### Ver Logs Durante Tests
```kotlin
@Test
fun `my test`() {
    println("DEBUG: Value is $value")  // Aparece en test output
}
```

### Ejecutar Test Individual en Debug
1. Poner breakpoint en el test
2. Clic derecho → **Debug 'test name'**
3. Inspeccionar variables en la ventana Debug

## 📚 Recursos Adicionales

- [Testing en Android - Documentación Oficial](https://developer.android.com/training/testing)
- [MockK Documentation](https://mockk.io/)
- [Truth Documentation](https://truth.dev/)
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)

## 🎯 Próximos Pasos

1. ✅ Implementar tests unitarios completos
2. ✅ Implementar tests de integración
3. 📝 Completar tests de UI con componentes reales
4. 📝 Añadir tests de autenticación Firebase
5. 📝 Añadir tests de API TMDB
6. 📝 Configurar CI/CD para ejecutar tests automáticamente

---

**Última actualización**: 21 de Noviembre de 2024
