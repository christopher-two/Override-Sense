# Override Sense 🦻

**Empowering sound awareness through AI.**

**Override Sense** es una aplicación de accesibilidad para Android diseñada para ayudar a personas con discapacidad auditiva. Utilizando inteligencia artificial en el dispositivo (On-Device AI), la app detecta, clasifica y visualiza sonidos ambientales críticos como alarmas de incendio, timbres o el llanto de un bebé, transformando el audio en alertas visuales y hápticas.

## 📲 Descargar la App

<p align="center">
  <a href="https://pub-d9e5f32907414250918a7f45da3c437e.r2.dev/Sense.apk">
    <img src="https://img.shields.io/badge/Descargar_APK-v1.0-brightgreen?style=for-the-badge&logo=android&logoColor=white" alt="Descargar APK">
  </a>
</p>

**Requisitos:** Android 10 (API 29) o superior

---

## ✨ Características Principales

### 🧠 Detección Inteligente
Utiliza el modelo **YAMNet** de TensorFlow Lite para identificar más de 500 tipos de sonidos en tiempo real, categorizados por prioridad:
- 🔴 **Crítico:** Alarmas de incendio, detectores de humo, sirenas de emergencia.
- 🟡 **Advertencia:** Timbres, golpes en la puerta, zumbadores.
- 🔵 **Información:** Llanto de bebé, risas, habla.

### 👁️ Feedback Visual y Sensorial
- **Animación de Pulso:** Visualización reactiva que cambia de color e intensidad según el tipo de sonido.
- **Notificaciones de Alto Impacto:** Alertas heads-up que funcionan incluso con la pantalla bloqueada.
- **Patrones de Vibración:** 7 patrones personalizados (Doble, Triple, Latido, etc.) con intensidad ajustable.
- **Alertas LED:** Uso del flash de la cámara para alertas críticas en entornos oscuros.

### 🎨 Personalización Total
- **Temas Dinámicos:** Soporte para Material You y temas Oscuro/Claro.
- **Ajuste de Sensibilidad:** Control preciso de la ganancia del micrófono y umbrales de detección.
- **Optimización de Batería:** Modos de ahorro de energía y ejecución solo en carga.

### 🔒 Privacidad Primero
Todo el procesamiento de audio ocurre **100% en el dispositivo**. Ningún audio es grabado, almacenado ni enviado a la nube.

---

## 🛠️ Stack Tecnológico

El proyecto está construido con las últimas tecnologías de desarrollo Android moderno:

- **Lenguaje:** [Kotlin](https://kotlinlang.org/)
- **UI Toolkit:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material3)
- **Arquitectura:** MVVM + Clean Architecture (Capas de Dominio, Datos y Presentación)
- **Inyección de Dependencias:** [Koin](https://insert-koin.io/)
- **Asincronía:** Coroutines & Kotlin Flow
- **AI/ML:** [TensorFlow Lite](https://www.tensorflow.org/lite) (Audio Classification)
- **Background Work:** WorkManager (Foreground Services)
- **Persistencia:** DataStore Preferences
- **Build System:** Gradle (Kotlin DSL)

## 📱 Estructura del Proyecto

```
org.override.sense
├── core                # Componentes comunes (UI, Notificaciones, Logger)
├── feature
│   ├── monitor         # Lógica principal de detección (Worker, TFLite, UI)
│   ├── settings        # Gestión de preferencias y configuración
│   ├── home            # Pantalla principal y navegación
│   └── onboarding      # Flujo de bienvenida
├── di                  # Módulos de Koin
└── app                 # Clase Application y MainActivity
```

## 🚀 Instalación y Uso

1. **Clonar el repositorio:**
   ```bash
   git clone https://github.com/tu-usuario/override-sense.git
   ```

2. **Abrir en Android Studio:**
   Se recomienda usar la versión más reciente (Ladybug o superior).

3. **Compilar y Ejecutar:**
   Conecta un dispositivo físico (el emulador no siempre simula el micrófono correctamente) y ejecuta la app.

4. **Permisos:**
   Asegúrate de conceder los permisos de **Micrófono** y **Notificaciones** para que la detección funcione correctamente.

## 🧪 Estado del Código

Para ver un análisis detallado de la salud del código y las refactorizaciones planeadas, consulta [docs/REFAC.md](docs/REFAC.md).

## 📄 Licencia

Este proyecto está bajo la Licencia MIT - ver el archivo [LICENSE](LICENSE) para más detalles.

---
*Desarrollado con ❤️ para mejorar la accesibilidad.*
