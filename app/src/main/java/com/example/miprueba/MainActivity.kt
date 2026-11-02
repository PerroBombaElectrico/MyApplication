package com.example.miprueba

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PowerSettingsNew
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class Screen(val title: String, val icon: ImageVector) {
    LOGIN("Login", Icons.Default.Lock),
    MONITORING("Monitoreo", Icons.Default.Sensors),
    ACTUATOR("Actuadores", Icons.Default.Tune)
}

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
                    Text(
                        text = when (currentScreen) {
                            Screen.LOGIN -> "Inicio de Sesión"
                            Screen.MONITORING -> "Monitoreo de Sensores"
                            Screen.ACTUATOR -> "Control de Actuadores"
                        },
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
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

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Acceso Seguro IoT",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
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
                    Text(
                        text = "Usuario Guardado (Encriptado):",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = savedUsername,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.SemiBold,
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
            Text("Iniciar Sesión", fontSize = 16.sp)
        }
    }
}

@Composable
fun MonitoringScreen(
    iotConnectivity: IoTConnectivity,
    onNavigateToActuator: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val sensorList = remember { iotConnectivity.getSensorData() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Lecturas de Sensores en Tiempo Real",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(sensorList) { sensor ->
                SensorCard(sensorName = sensor.first, sensorValue = sensor.second)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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

@Composable
fun SensorCard(sensorName: String, sensorValue: String) {
    val icon = when {
        sensorName.contains("Temp", ignoreCase = true) -> Icons.Default.Thermostat
        sensorName.contains("Hum", ignoreCase = true) -> Icons.Default.WaterDrop
        sensorName.contains("Pres", ignoreCase = true) -> Icons.Default.Speed
        sensorName.contains("Vib", ignoreCase = true) -> Icons.Default.Vibration
        else -> Icons.Default.Sensors
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = sensorName,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = sensorValue,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Composable
fun ActuatorScreen(
    iotConnectivity: IoTConnectivity,
    onNavigateToMonitoring: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val context = LocalContext.current
    var sliderValue by remember { mutableFloatStateOf(50f) }
    var isActuatorOn by remember { mutableStateOf(true) }

    val modeOptions = listOf("Automático", "Manual", "Eco")
    var selectedMode by remember { mutableStateOf(modeOptions[0]) }

    var statusMessage by remember { mutableStateOf("") }

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
                        Icon(Icons.Default.PowerSettingsNew, contentDescription = null)
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
                    style = MaterialTheme.typography.bodyMedium
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
                            Text(text = mode)
                        }
                    }
                }
            }
        }

        Text(
            text = "Transmisión de Comandos IoT",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = {
                    val command = "Actuador [Estado=${if (isActuatorOn) "ON" else "OFF"}, Potencia=${sliderValue.toInt()}%, Modo=$selectedMode]"
                    val response = iotConnectivity.sendBluetoothData(command)
                    statusMessage = response
                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Bluetooth, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Bluetooth")
            }

            Button(
                onClick = {
                    val command = "Actuador [Estado=${if (isActuatorOn) "ON" else "OFF"}, Potencia=${sliderValue.toInt()}%, Modo=$selectedMode]"
                    val response = iotConnectivity.sendWifiData(command)
                    statusMessage = response
                    Toast.makeText(context, response, Toast.LENGTH_SHORT).show()
                },
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.Wifi, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Wi-Fi")
            }
        }

        AnimatedVisibility(visible = statusMessage.isNotEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = statusMessage,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
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
