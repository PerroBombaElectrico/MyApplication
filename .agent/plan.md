# Project Plan

Reescribe todo el proyecto para que sea una aplicación ultra básica y simple, emulando un proyecto académico pequeño de una sola capa, SIN crear subcarpetas complejas. Consolida todo el proyecto en SOLO 3 archivos en la raíz del paquete (com.example.miprueba): 1) MainActivity.kt (con LoginScreen, MonitoringScreen, ActuatorScreen en Jetpack Compose con navegación sencilla mediante estado), 2) SecurityHelper.kt (utilizando EncryptedSharedPreferences para guardar el nombre de usuario de forma cifrada), 3) IoTConnectivity.kt (funciones simples para simular el envío de datos vía Bluetooth BLE y Wi-Fi). Ajusta app/build.gradle.kts limpiando kotlinOptions/jvmTarget y añadiendo la dependencia androidx.security:security-crypto:1.1.0-alpha06.

## Project Brief

# Project Brief: Academic IoT Control & Monitoring App

## Features
- **Secure Login & Credential Encryption**: User authentication interface that securely encrypts and stores the username locally using `EncryptedSharedPreferences`.
- **IoT Data Monitoring**: Real-time display screen for viewing simulated IoT sensor measurements and system status.
- **Actuator Control & Wireless Connectivity**: Interactive control screen that allows triggering actuators and simulating data transmission over Bluetooth BLE and Wi-Fi.

## High-Level Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose & Compose Material Adaptive
- **Navigation**: Jetpack Navigation 3 (state-driven)
- **Asynchronous & Threading**: Kotlin Coroutines
- **Security**: AndroidX Security Crypto (`androidx.security:security-crypto`) for `EncryptedSharedPreferences`

## Implementation Steps
**Total Duration:** 54m 20s

### Task_1_SetupDependencies: Configure app/build.gradle.kts with androidx.security:security-crypto:1.1.0-alpha06 dependency, remove conflicting kotlinOptions/jvmTarget settings, and verify gradle sync.
- **Status:** COMPLETED
- **Updates:** Added androidx.security:security-crypto:1.1.0-alpha06 to app/build.gradle.kts and verified build with assembleDebug cleanly.
- **Acceptance Criteria:**
  - build pass
  - androidx.security:security-crypto dependency added
- **Duration:** 22m 37s

### Task_2_ImplementHelpers: Create SecurityHelper.kt for EncryptedSharedPreferences storage and IoTConnectivity.kt for simulated IoT data monitoring, actuator control, and wireless state under package com.example.miprueba.
- **Status:** COMPLETED
- **Updates:** Implemented SecurityHelper.kt and IoTConnectivity.kt in package com.example.miprueba, cleaned legacy subdirectories, and verified build with assembleDebug.
- **Acceptance Criteria:**
  - SecurityHelper.kt created with EncryptedSharedPreferences implementation
  - IoTConnectivity.kt created with IoT simulation and wireless connectivity logic
- **Duration:** 8m 31s

### Task_3_ImplementMainActivity: Implement MainActivity.kt in package com.example.miprueba featuring Jetpack Compose screens for Secure Login, IoT Data Monitoring, and Actuator Control. Ensure structure consists strictly of the three required files.
- **Status:** COMPLETED
- **Updates:** Implemented MainActivity.kt with state-based navigation and the 3 screens (LoginScreen, MonitoringScreen, ActuatorScreen). Verified package contains strictly 3 files and build succeeds with assembleDebug.
- **Acceptance Criteria:**
  - MainActivity.kt created with Jetpack Compose UI for Login, Monitoring, and Actuator Control
  - Package com.example.miprueba simplified to only 3 files (MainActivity.kt, SecurityHelper.kt, IoTConnectivity.kt)
- **Duration:** 23m 12s

### Task_4_RunAndVerify: Build and run the application. Instruct critic_agent to verify application stability (no crashes), confirm alignment with user requirements, and report critical UI issues.
- **Status:** IN_PROGRESS
- **Acceptance Criteria:**
  - build pass
  - app does not crash
  - make sure all existing tests pass
  - critic_agent verified application stability and requirement alignment
- **StartTime:** 2026-11-02 09:58:18 GMT-03:00

