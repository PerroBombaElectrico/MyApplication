# Project Plan

Crea una aplicación Android completa en Kotlin utilizando Jetpack Compose para un sistema de control IoT. La aplicación debe incluir 3 pantallas principales: 1. Pantalla de Login (campos de usuario y contraseña con botón de ingreso), 2. Pantalla de Monitoreo (visualización de datos de sensores IoT como temperatura, estado y alertas usando Cards y listas), 3. Pantalla de Activador (controles interactivos con Sliders, Switches y RadioButtons para enviar comandos a dispositivos). Incluye gestión de estado básica para navegar entre estas pantallas, aplica guías de Material Design 3 e implementa una función auxiliar de EncryptedSharedPreferences para almacenamiento local seguro.

## Project Brief

# IoT Control System App - Project Brief (MVP)

## Features

1. **Authentication Screen**: Secure user login interface with username and password fields, integrating `EncryptedSharedPreferences` for safe local session storage.
2. **Sensor Monitoring Screen**: Real-time IoT dashboard showcasing key sensor data (temperature, connection status, and active system alerts) utilizing Material Design 3 Cards and lists.
3. **Actuator Controller Screen**: Interactive control panel utilizing Sliders, Switches, and RadioButtons to configure settings and send direct commands to IoT devices.
4. **Adaptive Navigation & State Management**: State-driven UI routing between screens using Jetpack Navigation 3 alongside adaptive layout support.

## High-Level Tech Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose with Material Design 3
- **Navigation & Adaptive Strategy**: **Jetpack Navigation 3** (state-driven) & **Compose Material Adaptive** library
- **Asynchronous & State Management**: Kotlin Coroutines, StateFlow, & ViewModel
- **Secure Local Storage**: Jetpack Security Crypto (`EncryptedSharedPreferences`)

## Implementation Steps

