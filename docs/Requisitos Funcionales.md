# **Requisitos Funcionales: Override Sense (Detector en Segundo Plano)**

Este documento detalla las capacidades y restricciones necesarias para que el sistema de detección de sonidos críticos de **Override Sense** funcione de manera óptima en un entorno Android nativo.

## **1\. Monitoreo Persistente (Foreground Service)**

El sistema debe garantizar la escucha continua sin depender de que la aplicación esté abierta en pantalla.

* **RF01 \- Ejecución en Segundo Plano:** El motor de detección debe residir en un Foreground Service para evitar que el sistema operativo Android cierre el proceso para ahorrar memoria.  
* **RF02 \- Notificación de Estado:** El servicio debe mostrar una notificación persistente obligatoria que informe al usuario que el sistema de protección está activo.  
* **RF03 \- Reinicio Automático:** En caso de que el sistema detenga el servicio por falta de recursos críticos, este debe intentar reiniciarse automáticamente (START\_STICKY).  
* **RF04 \- Operación con Pantalla Bloqueada:** El detector debe procesar el audio y disparar alertas hápticas incluso cuando el dispositivo tiene la pantalla apagada o está bloqueado.

## **2\. Detección y Clasificación (IA On-Device)**

Utilizando el modelo **YAMNet (LiteRT)**, el sistema debe identificar patrones sonoros con alta precisión.

* **RF05 \- Procesamiento Local:** Todo el análisis de audio debe realizarse 100% en el dispositivo. Bajo ninguna circunstancia se enviará audio a la nube, garantizando la privacidad absoluta.  
* **RF06 \- Categorización Crítica:** El sistema debe identificar prioritariamente:  
  * Alarmas de incendio / Humo.  
  * Sirenas de emergencia.  
  * Timbres y golpes en la puerta.  
  * Llanto de bebés.  
* **RF07 \- Umbral de Confianza:** El sistema solo disparará alertas cuando la probabilidad del modelo sea superior al 60% (ajustable) para minimizar falsos positivos.

## **3\. Respuesta Sensorial (Háptica y Visual)**

La comunicación del evento detectado debe ser multimodal y distinguible por el tacto.

* **RF08 \- Patrones Hápticos Diferenciados:**  
  * **Urgente (Alarmas/Sirenas):** Vibraciones largas, intensas y repetitivas.  
  * **Informativo (Timbres/Puerta):** Vibraciones cortas e intermitentes (doble pulso).  
* **RF09 \- Alerta Visual de Alto Contraste:** Al detectar un sonido, la pantalla debe emitir un destello de color (rojo para urgencias, amarillo para avisos) que ocupe toda la interfaz si la app está abierta.  
* **RF10 \- Prioridad de Accesibilidad:** Las alertas deben ignorar el estado de "Silencio" del teléfono si el usuario así lo configura, utilizando los atributos de accesibilidad del sistema.

## **4\. Requisitos Técnicos y Permisos**

Para cumplir con las políticas de Android modernas y asegurar el rendimiento.

* **RT01 \- Gestión de Permisos:** La aplicación debe solicitar y verificar en tiempo real los permisos de RECORD\_AUDIO, VIBRATE y POST\_NOTIFICATIONS.  
* **RT02 \- Tipo de Servicio:** El servicio de primer plano debe declararse específicamente como foregroundServiceType="microphone".  
* **RT03 \- Optimización de Batería:** El análisis se realizará en intervalos (ej. cada 500ms) para balancear la seguridad con el consumo energético.  
* **RT04 \- Baja Latencia:** El tiempo transcurrido desde que el sonido ocurre hasta que el teléfono vibra no debe superar los 200ms.

**Nota sobre Privacidad:** Este sistema cumple con los estándares de diseño "Privacy by Design", asegurando que el micrófono solo se utiliza para la extracción de características matemáticas y nunca para la grabación o almacenamiento de conversaciones.