package com.example.miprueba

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Pantallas navegables del sistema
 */
enum class Screen(val title: String, val icon: ImageVector) {
    LOGIN("Login", Icons.Default.Lock),
    MONITORING("Monitoreo", Icons.Default.Sensors),
    ACTUATOR("Actuadores", Icons.Default.Tune)
}

/**
 * MainActivity - Punto de entrada de la aplicación de Evaluación Sumativa 2 IoT
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val securityHelper = SecurityHelper(applicationContext)
        val iotConnectivity = IoTConnectivity()

        setContent {
            AppTheme {
                MipruebaApp(
                    securityHelper = securityHelper,
                    iotConnectivity = iotConnectivity
                )
            }
        }
    }
}

/**
 * Configuración del tema de la aplicación (Material 3)
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colorScheme = when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme()
        else -> lightColorScheme()
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}

/**
 * MipruebaApp - Composable raíz que gestiona el Scaffold y la barra de navegación inferior
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MipruebaApp(
    securityHelper: SecurityHelper? = null,
    iotConnectivity: IoTConnectivity = IoTConnectivity()
) {
    var currentScreen by remember { mutableStateOf(Screen.LOGIN) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = when (currentScreen) {
                                Screen.LOGIN -> "Inicio de Sesión"
                                Screen.MONITORING -> "Monitoreo de Sensores"
                                Screen.ACTUATOR -> "Control de Actuadores"
                            },
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                actions = {
                    BadgedBox(
                        badge = {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text("ISO 27400", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        },
                        modifier = Modifier.padding(end = 12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "ISO 27400 Security",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            NavigationBar {
                Screen.entries.forEach { screen ->
                    NavigationBarItem(
                        selected = currentScreen == screen,
                        onClick = { currentScreen = screen },
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            when (currentScreen) {
                Screen.LOGIN -> LoginScreen(
                    securityHelper = securityHelper,
                    onLoginSuccess = { currentScreen = Screen.MONITORING }
                )
                Screen.MONITORING -> MonitoringScreen(
                    iotConnectivity = iotConnectivity,
                    onNavigateToActuator = { currentScreen = Screen.ACTUATOR },
                    onNavigateToLogin = { currentScreen = Screen.LOGIN }
                )
                Screen.ACTUATOR -> ActuatorScreen(
                    iotConnectivity = iotConnectivity,
                    onNavigateToMonitoring = { currentScreen = Screen.MONITORING },
                    onNavigateToLogin = { currentScreen = Screen.LOGIN }
                )
            }
        }
    }
}

/**
 * Pantalla 1: LoginScreen con soporte para almacenamiento seguro ISO 27400 (EncryptedSharedPreferences)
 */
@Composable
fun LoginScreen(
    securityHelper: SecurityHelper?,
    onLoginSuccess: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    var savedUsername by remember {
        mutableStateOf(securityHelper?.getUsername() ?: "")
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Security,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Acceso Seguro IoT",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Text(
            text = "Cifrado AES-256 Android Keystore (ISO 27400)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (savedUsername.isNotBlank()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Usuario Guardado (Cifrado Keystore):",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = savedUsername,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = { username = savedUsername },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Usar usuario guardado")
                    }
                }
            }
        }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Nombre de Usuario") },
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (username.isNotBlank()) {
                    securityHelper?.saveUsername(username)
                    savedUsername = securityHelper?.getUsername() ?: username
                }
                onLoginSuccess()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text("Iniciar Sesión", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

/**
 * Pantalla 2: MonitoringScreen con flujo de lecturas en tiempo real
 */
@Composable
fun MonitoringScreen(
    iotConnectivity: IoTConnectivity,
    onNavigateToActuator: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var sensorReadings by remember { mutableStateOf(iotConnectivity.getSensorReadingsSnapshot()) }
    var isLiveActive by remember { mutableStateOf(true) }

    // Recolectar Flow en tiempo real cuando la simulación en vivo está activa
    LaunchedEffect(isLiveActive) {
        if (isLiveActive) {
            iotConnectivity.getSensorDataFlow().collect { updatedSensors ->
                sensorReadings = updatedSensors
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Sensores IoT Activos",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        text = "Actualización en tiempo real vía Flow",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isLiveActive) Color(0xFF4CAF50) else Color.Gray)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isLiveActive) "LIVE" else "PAUSED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(sensorReadings, key = { it.id }) { sensor ->
                SensorReadingCard(sensor = sensor)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Login")
            }

            Button(
                onClick = onNavigateToActuator,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Tune, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ir a Actuadores")
            }
        }
    }
}

/**
 * Tarjeta individual para presentar lecturas de sensores con su estado e icono correspondiente
 */
@Composable
fun SensorReadingCard(sensor: SensorReading) {
    val icon = when (sensor.type) {
        SensorType.TEMPERATURE -> Icons.Default.Thermostat
        SensorType.HUMIDITY -> Icons.Default.WaterDrop
        SensorType.PRESSURE -> Icons.Default.Speed
        SensorType.VIBRATION -> Icons.Default.Vibration
    }

    val iconColor = when (sensor.type) {
        SensorType.TEMPERATURE -> Color(0xFFE53935)
        SensorType.HUMIDITY -> Color(0xFF1E88E5)
        SensorType.PRESSURE -> Color(0xFF8E24AA)
        SensorType.VIBRATION -> Color(0xFFFB8C00)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = iconColor
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = sensor.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = sensor.value,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = sensor.unit,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = sensor.timestamp,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Spacer(modifier = Modifier.height(4.dp))
                Badge(
                    containerColor = if (sensor.isNormalStatus) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                    contentColor = if (sensor.isNormalStatus) Color(0xFF2E7D32) else Color(0xFFC62828)
                ) {
                    Text(
                        text = if (sensor.isNormalStatus) "Normal" else "Alerta",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

/**
 * Compatible con SensorCard básico para interoperabilidad
 */
@Composable
fun SensorCard(sensorName: String, sensorValue: String) {
    SensorReadingCard(
        sensor = SensorReading(
            id = "legacy",
            name = sensorName,
            value = sensorValue.split(" ").firstOrNull() ?: sensorValue,
            unit = sensorValue.split(" ").getOrNull(1) ?: "",
            type = when {
                sensorName.contains("Temp", ignoreCase = true) -> SensorType.TEMPERATURE
                sensorName.contains("Hum", ignoreCase = true) -> SensorType.HUMIDITY
                sensorName.contains("Pres", ignoreCase = true) -> SensorType.PRESSURE
                else -> SensorType.VIBRATION
            }
        )
    )
}

/**
 * Pantalla 3: ActuatorScreen para control de actuadores vía Bluetooth BLE y Wi-Fi
 */
@Composable
fun ActuatorScreen(
    iotConnectivity: IoTConnectivity,
    onNavigateToMonitoring: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var sliderValue by remember { mutableFloatStateOf(50f) }
    var isActuatorOn by remember { mutableStateOf(true) }

    val modeOptions = ActuatorMode.entries
    var selectedMode by remember { mutableStateOf(ActuatorMode.AUTOMATIC) }

    var transmissionResult by remember { mutableStateOf<TransmissionResult?>(null) }
    var isTransmitting by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PowerSettingsNew,
                            contentDescription = null,
                            tint = if (isActuatorOn) MaterialTheme.colorScheme.primary else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Estado: ${if (isActuatorOn) "ENCENDIDO" else "APAGADO"}",
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Switch(
                        checked = isActuatorOn,
                        onCheckedChange = { isActuatorOn = it }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Potencia / Velocidad: ${sliderValue.toInt()}%",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Slider(
                    value = sliderValue,
                    onValueChange = { sliderValue = it },
                    valueRange = 0f..100f,
                    enabled = isActuatorOn
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Modo de Operación:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Column(modifier = Modifier.selectableGroup()) {
                    modeOptions.forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                                .selectable(
                                    selected = (selectedMode == mode),
                                    onClick = { selectedMode = mode },
                                    role = Role.RadioButton
                                ),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (selectedMode == mode),
                                onClick = null
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = mode.displayName)
                        }
                    }
                }
            }
        }

        Text(
            text = "Transmisión Inalámbrica IoT",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                enabled = !isTransmitting,
                onClick = {
                    coroutineScope.launch {
                        isTransmitting = true
                        val command = ActuatorCommand(
                            isEnabled = isActuatorOn,
                            powerPercentage = sliderValue.toInt(),
                            mode = selectedMode
                        )
                        val result = iotConnectivity.sendActuatorCommandAsync(command, TransmissionProtocol.BLUETOOTH_BLE)
                        transmissionResult = result
                        isTransmitting = false
                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                if (isTransmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Bluetooth, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bluetooth BLE")
            }

            Button(
                enabled = !isTransmitting,
                onClick = {
                    coroutineScope.launch {
                        isTransmitting = true
                        val command = ActuatorCommand(
                            isEnabled = isActuatorOn,
                            powerPercentage = sliderValue.toInt(),
                            mode = selectedMode
                        )
                        val result = iotConnectivity.sendActuatorCommandAsync(command, TransmissionProtocol.WIFI)
                        transmissionResult = result
                        isTransmitting = false
                        Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.weight(1f)
            ) {
                if (isTransmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Default.Wifi, contentDescription = null)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text("Wi-Fi TCP")
            }
        }

        AnimatedVisibility(
            visible = transmissionResult != null,
            enter = slideInVertically() + fadeIn(),
            exit = fadeOut()
        ) {
            transmissionResult?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiaryContainer
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Confirmación [${result.protocol.displayName} - ${result.timestamp}]",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                            Text(
                                text = result.message,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onTertiaryContainer
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Login")
            }

            Button(
                onClick = onNavigateToMonitoring,
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Sensors, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ir a Monitoreo")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MipruebaAppPreview() {
    AppTheme {
        MipruebaApp()
    }
}
