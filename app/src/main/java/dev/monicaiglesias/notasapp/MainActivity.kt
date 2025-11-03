package dev.monicaiglesias.notasapp
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
//import androidx.lint.kotlin.metadata.Visibility
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NotasAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotasApp()
                }
            }
        }
    }
}

@Composable
fun NotasAppTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6200EE),
            secondary = Color(0xFF03DAC6),
            background = Color(0xFFF5F5F5),
            surface = Color.White,
            error = Color(0xFFB00020)
        ),
        content = content
    )
}

@Composable
fun NotasApp() {
    val navController = rememberNavController()
    var userEmail by remember { mutableStateOf("") }

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = { email ->
                    userEmail = email
                    navController.navigate("welcome")
                }
            )
        }
        composable("welcome") {
            WelcomeScreen(
                userEmail = userEmail,
                onContinue = {
                    navController.navigate("grades")
                }
            )
        }
        composable("grades") {
            GradesInputScreen(
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
    }
}

// ============ PANTALLA DE LOGIN ============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo/Icono
        Icon(
            imageVector = Icons.Default.School,
            contentDescription = "Logo",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Sistema de Notas",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Ingresa tus credenciales",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Campo de Correo
        OutlinedTextField(
            value = email,
            onValueChange = {
                email = it
                errorMessage = ""
            },
            label = { Text("Correo Electrónico") },
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = "Email")
            },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            isError = errorMessage.isNotEmpty()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo de Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                errorMessage = ""
            },
            label = { Text("Contraseña") },
            leadingIcon = {
                Icon(Icons.Default.Lock, contentDescription = "Password")
            },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility
                        else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar" else "Mostrar"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None
            else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            isError = errorMessage.isNotEmpty()
        )

        // Mensaje de Error
        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón de Iniciar Sesión
        Button(
            onClick = {
                val validation = validateLogin(email, password)
                if (validation.isValid) {
                    onLoginSuccess(email)
                } else {
                    errorMessage = validation.message
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.Login, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Iniciar Sesión", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Información de demo
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = "💡 Demo",
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Usa cualquier correo válido y contraseña (mínimo 6 caracteres)",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

data class ValidationResult(val isValid: Boolean, val message: String)

fun validateLogin(email: String, password: String): ValidationResult {
    return when {
        email.isBlank() -> ValidationResult(false, "El correo no puede estar vacío")
        password.isBlank() -> ValidationResult(false, "La contraseña no puede estar vacía")
        !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() ->
            ValidationResult(false, "Formato de correo electrónico inválido")
        password.length < 6 ->
            ValidationResult(false, "La contraseña debe tener al menos 6 caracteres")
        else -> ValidationResult(true, "")
    }
}

// ============ PANTALLA DE BIENVENIDA ============
@Composable
fun WelcomeScreen(userEmail: String, onContinue: () -> Unit) {
    val userName = userEmail.substringBefore("@").replaceFirstChar { it.uppercase() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "Usuario",
            modifier = Modifier.size(100.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "¡Bienvenido/a!",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = userName,
            style = MaterialTheme.typography.headlineMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📚 Sistema de Gestión de Notas",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ingresa tus calificaciones y calcula tu promedio automáticamente",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onContinue,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Continuar al Ingreso de Notas", fontSize = 16.sp)
            Spacer(modifier = Modifier.width(8.dp))
            Icon(Icons.Default.ArrowForward, contentDescription = null)
        }
    }
}

// ============ PANTALLA DE INGRESO DE NOTAS ============
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradesInputScreen(onLogout: () -> Unit) {
    var grade1 by remember { mutableStateOf("") }
    var grade2 by remember { mutableStateOf("") }
    var grade3 by remember { mutableStateOf("") }
    var grade4 by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    var showResult by remember { mutableStateOf(false) }
    var average by remember { mutableStateOf(0.0) }
    var isPassed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ingreso de Notas",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            IconButton(onClick = onLogout) {
                Icon(
                    Icons.Default.Logout,
                    contentDescription = "Cerrar Sesión",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (!showResult) {
            // Formulario de Notas
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Ingresa tus calificaciones",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Escala de 0 a 10",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    GradeTextField(
                        value = grade1,
                        onValueChange = {
                            grade1 = it
                            errorMessage = ""
                        },
                        label = "Nota 1",
                        number = 1
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GradeTextField(
                        value = grade2,
                        onValueChange = {
                            grade2 = it
                            errorMessage = ""
                        },
                        label = "Nota 2",
                        number = 2
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GradeTextField(
                        value = grade3,
                        onValueChange = {
                            grade3 = it
                            errorMessage = ""
                        },
                        label = "Nota 3",
                        number = 3
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    GradeTextField(
                        value = grade4,
                        onValueChange = {
                            grade4 = it
                            errorMessage = ""
                        },
                        label = "Nota 4 (Opcional)",
                        number = 4
                    )
                }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = errorMessage,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val validation = validateGrades(grade1, grade2, grade3, grade4)
                    if (validation.isValid) {
                        val grades = listOfNotNull(
                            grade1.toDoubleOrNull(),
                            grade2.toDoubleOrNull(),
                            grade3.toDoubleOrNull(),
                            grade4.toDoubleOrNull()?.takeIf { grade4.isNotBlank() }
                        )
                        average = grades.average()
                        isPassed = average >= 6.0
                        showResult = true
                    } else {
                        errorMessage = validation.message
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Calculate, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Calcular Promedio", fontSize = 16.sp)
            }
        } else {
            // Pantalla de Resultado
            ResultScreen(
                average = average,
                isPassed = isPassed,
                onReset = {
                    grade1 = ""
                    grade2 = ""
                    grade3 = ""
                    grade4 = ""
                    errorMessage = ""
                    showResult = false
                },
                onLogout = onLogout
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GradeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    number: Int
) {
    OutlinedTextField(
        value = value,
        onValueChange = {
            if (it.isEmpty() || it.toDoubleOrNull() != null) {
                onValueChange(it)
            }
        },
        label = { Text(label) },
        leadingIcon = {
            Text(
                text = "$number",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true
    )
}

fun validateGrades(g1: String, g2: String, g3: String, g4: String): ValidationResult {
    val grades = listOf(g1, g2, g3)

    return when {
        grades.any { it.isBlank() } ->
            ValidationResult(false, "Las primeras 3 notas son obligatorias")
        grades.any { it.toDoubleOrNull() == null } ->
            ValidationResult(false, "Todas las notas deben ser números válidos")
        g4.isNotBlank() && g4.toDoubleOrNull() == null ->
            ValidationResult(false, "La nota 4 debe ser un número válido")
        grades.any { val n = it.toDouble(); n < 0 || n > 10 } ->
            ValidationResult(false, "Las notas deben estar entre 0 y 10")
        g4.isNotBlank() && (g4.toDouble() < 0 || g4.toDouble() > 10) ->
            ValidationResult(false, "Las notas deben estar entre 0 y 10")
        else -> ValidationResult(true, "")
    }
}

// ============ PANTALLA DE RESULTADO ============
@Composable
fun ResultScreen(
    average: Double,
    isPassed: Boolean,
    onReset: () -> Unit,
    onLogout: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = if (isPassed) Icons.Default.CheckCircle else Icons.Default.Cancel,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = if (isPassed) Color(0xFF4CAF50) else Color(0xFFF44336)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (isPassed) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Promedio Final",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = String.format("%.2f", average),
                    style = MaterialTheme.typography.displayLarge,
                    fontWeight = FontWeight.Bold,
                    color = if (isPassed) Color(0xFF2E7D32) else Color(0xFFC62828)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Divider()

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isPassed) "¡APROBADO!" else "REPROBADO",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isPassed) Color(0xFF2E7D32) else Color(0xFFC62828)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = if (isPassed)
                        "¡Felicidades! Has alcanzado el promedio mínimo de aprobación."
                    else
                        "Necesitas un promedio mínimo de 6.0 para aprobar.",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onReset,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Icon(Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Ingresar Nuevas Notas", fontSize = 16.sp)
        }

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedButton(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Icon(Icons.Default.Logout, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Cerrar Sesión", fontSize = 16.sp)
        }
    }
}

// ============ ARCHIVO build.gradle.kts (Module: app) ============
/*
dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.8.2")
    implementation(platform("androidx.compose:compose-bom:2024.02.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.navigation:navigation-compose:2.7.6")
}
*/

















//
//import android.os.Bundle
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import dev.monicaiglesias.notasapp.ui.theme.NotasAppTheme
//
//class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContent {
//            NotasAppTheme {
//                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Android",
//                        modifier = Modifier.padding(innerPadding)
//                    )
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun Greeting(name: String, modifier: Modifier = Modifier) {
//    Text(
//        text = "Hello $name!",
//        modifier = modifier
//    )
//}
//
//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    NotasAppTheme {
//        Greeting("Android")
//    }
//}