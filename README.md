# Digitarra

Este proyecto ha sido desarrollado como parte del Trabajo de Fin del Grado en la Universidad Complutense de Madrid.

El proyecto consiste en una aplicación que realiza mediante el uso de programación dinámica la digitación de una partitura musical de guitarra, mostrando qué traste, qué cuerda y dedo derecho e izquierdo deben usarse en cada nota para poder tocar la pieza musical a la que representa. 

El proyecto se ha enfocado sobre todo en la especificación y el diseño de la aplicación, con el objetivo de aplicar los conocimientos adquiridos durante la carrera y poner en práctica lo que se aprendió de software.

## Instalación

1. Instalar Android Studio. Disponible en: https://developer.android.com/studio?hl=es-419

2. Si se necesita emular la ejecución de la aplicación desde el ordenador, descargar el emulador de móviles de Android Studio durante la instalación, con muchos modelos disponibles para emular. O, se puede descargar desde Tools -> Device Manager -> Add a new device (el “+” que hay en el panel derecho) -> Create virtual device.

3. El Android SDK (Kit de desarrollo software de Android), que, aunque se configure  de forma automática durante la instalación de Android Studio, se debe comprobar que se tiene Android SDK Platform 37, que se hace desde el proyecto al seleccionar Tools -> SDK Manager.

4. El JDK (Kit de desarrollo software de Java) se recomienda utilizar el que viene instalado en la instalación de Android Studio, así se hizo para este proyecto.

5. Instalar Git. Disponible en: https://git-scm.com/.

6. Clonar el repositorio: git clone https://github.com/Eloy999999/Aplicacion-ia-musical

7. Desde Android Studio seleccionar File -> Open...

8. Seleccionar desde el buscador de archivos la carpeta donde se hizo el clonado del paso 6. Esto abrirá el proyecto en Android Studio.

## Ejecución desde emulador de ordenador

1. Abrir el proyecto en Android Studio y acceder a Device Manager (Tools -> Device Manager).

2. En el panel de Device Manager, presionar “+” y “Create virtual device”.

3. Seleccionar un modelo de móvil Android que soporte SDK API 30 o superior como mínimo.

4. Seleccionar la imagen de sistema con SDK API 30 o mayor y dar a “Finish”.

5. Pulsar “Run”, el icono del triángulo verde arriba

## Ejecución en un Android físico

1. Abrir el proyecto en Android Studio y seleccionar Build -> Generate app bundles or APKs -> Generate APKs.

2. El archivo .apk se instalará en app/build/outputs/apk/debug/app-debug.apk, directorio relativo al directorio donde está alojado en el ordenador el proyecto de Android Studio.

3. Mandar ese apk a un dispositivo móvil Android con versión Android 11 o superior en el que se vaya a probar la aplicación e instalar el apk.

4. Ejecutar en el teléfono móvil la aplicación Digitarra.

## Pruebas

1. En el panel proyect, ya en el proyecto de Android Studio, ir a app -> src -> test -> java -> com.digitarra.app_tfg.

2. Click derecho sobre el paquete.

3. seleccionar new -> Kotlin class/file.

4. Crear la clase para las pruebas.

5. Para ejecutarlas, pulsar click derecho sobre la clase creada y después “Run”.

## Licencia

Este proyecto está licenciado bajo los términos de la [Licencia GPL](LICENSE).
