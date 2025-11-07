//Top-level build file
// Top-level build file
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false

    id("com.google.gms.google-services") version "4.4.1" apply false
    id("org.sonarqube") version "6.3.1.5724"
}
sonarqube {
    properties {
        // 1. Clave y Nombre del Proyecto (debe coincidir con lo que registres en SonarQube)
        property (".projectKey", "gestor_RRHH_")
        property (".projectName", "gestor_RRHH")

        // 2. Ubicación de tu Servidor SonarQube
        property (".host.url", "http://localhost:9000")

        // 3. Token de Seguridad (para autenticación)
        property (".token", "sqp_3b7e34fd148a63f9a2ee98e41a6474d210d6a7ed") // REEMPLAZA ESTE VALOR

        // 4. Ubicación de los archivos fuente (necesario para proyectos Android/Kotlin)
        // Esto le dice a SonarQube dónde buscar el código a analizar
        property (".sources", "src/main/java,src/main/kotlin")

        // 5. Ubicación de las clases compiladas y reportes de cobertura (si usas cobertura)
        // property "sonar.java.binaries", "build/intermediates/javac/debug"
    }
}