# Guía del alumnado · Entorno, versiones y resolución de incidencias

> Material para la **rama del alumnado**. Forma parte de las instrucciones de la P3, pero **no contiene soluciones de los casos de prueba**. Las versiones son las establecidas por el `pom.xml` y el workflow docente de referencia; el equipo local no tiene por qué coincidir exactamente.

## 1. Versiones de referencia y alcance

| Componente | Referencia del proyecto | ¿Qué debe hacer el alumnado? |
|---|---|---|
| Java | Proyecto compilado para **Java 17** mediante `maven.compiler.release=17`; CI con **JDK 21** | Utilizar JDK 17 o posterior compatible; preferentemente JDK 21 para reproducir CI |
| Maven | Proyecto Maven con `pom.xml` en el módulo P3 | Ejecutar las metas sobre **el módulo que contiene este `pom.xml`** |
| JUnit Jupiter | **5.11.4** | No añadir dependencias individuales que dupliquen o mezclen motores JUnit |
| Selenium Java | **4.51.0** | Utilizar la versión declarada en Maven; no copiar JAR manualmente |
| Spring Boot | **3.4.5** | Se arranca y detiene automáticamente con el perfil Maven `e2e` |
| Maven Surefire / Failsafe | **3.5.2** | Surefire ejecuta pruebas unitarias; Failsafe las E2E |
| JaCoCo | **0.8.14** | Los informes se generan al ejecutar `verify` |
| PIT | Configurado en el perfil Maven `mutation` | Ejecutar localmente el análisis de mutaciones de los servicios de dominio |
| Chrome | Versión compatible con el WebDriver disponible | Selenium Manager gestiona ChromeDriver; puede necesitar conexión para descargarlo |
| Eclipse | IDE recomendado | Actualizar la configuración Maven después de hacer Pull |

**No se exige que todos los integrantes tengan la misma versión exacta de Eclipse o Chrome.** Sí deben justificar y resolver las incompatibilidades que impidan reproducir los resultados. No cambiar la versión de una dependencia por iniciativa propia sin comprobar antes su compatibilidad y conservar los tests.

## 2. Comprobación del entorno

Desde la terminal, situándose en el módulo Maven de la práctica:

```powershell
java -version
mvn -version
mvn clean verify
mvn -Pe2e clean verify
mvn -Pmutation test pitest:mutationCoverage
```

`mvn -version` indica **con qué Java se ejecuta Maven**, que puede diferir del JRE de Eclipse. En Eclipse, revisar **Window → Preferences → Java → Installed JREs** y la configuración **Run Configurations → Maven Build → JRE**.

Para realizar la práctica en Eclipse: importar el proyecto como **Existing Maven Projects**, ejecutar **Maven → Update Project**, y crear una configuración Maven con `Goals = clean verify` y `Profiles = e2e`. Asegurarse de que el puerto 8080 no está ocupado por otra ejecución de Spring Boot.

## 3. Qué hacer cuando aparecen diferencias de versiones

| Síntoma | Comprobación | Actuación razonada |
|---|---|---|
| `UnsupportedClassVersionError` | `java -version`, `mvn -version`, JDK elegido en Eclipse | Ajustar el JDK de ejecución a una versión compatible y volver a compilar |
| `release version 17 not supported` | JDK que utiliza Maven | Elegir JDK 17 o superior; no cambiar `maven.compiler.release` solo para eliminar el mensaje |
| JUnit no encuentra pruebas o aparecen errores de descubrimiento | Dependencias Jupiter y proveedor Surefire en `pom.xml` | Actualizar proyecto Maven; comprobar anotación `org.junit.jupiter.api.Test` y patrón de clases |
| `NoSuchMethodError` / `ClassNotFoundException` de Selenium o JUnit | Árbol de dependencias de Maven | Identificar versiones incompatibles o duplicadas; no copiar JAR a mano |
| Chrome y ChromeDriver incompatibles | Versión de Chrome, Selenium y trazas del arranque | Comprobar Selenium Manager y la compatibilidad; actualizar controladamente solo si se demuestra necesario |
| Advertencia `Unable to find CDP implementation matching ...` | Versiones Selenium/Chrome y si la suite realmente usa DevTools | Puede ser una advertencia sin fallo E2E; revisar compatibilidad antes de añadir artefactos `selenium-devtools-v...` |
| `SessionNotCreatedException` / navegador no localizado | Chrome instalado, ejecución headless, mensajes de Selenium Manager | Verificar navegador y red si necesita descargar controlador |
| `StaleElementReferenceException` | Navegación tras pulsar formularios | Usar las esperas y localizadores ya disponibles en la clase de apoyo, no pausas arbitrarias |
| `Connection refused` en `localhost:8080` | Arranque previo de Spring Boot y puerto 8080 | Ejecutar con `-Pe2e`, revisar logs de arranque y conflictos del puerto |
| Resultados diferentes por fecha actual | Perfil Spring `e2e` y reloj fijo del proyecto | Reproducir mediante el perfil de pruebas; no depender de la fecha real del ordenador |
| Tests de dominio fallidos tras introducir una prueba | Entradas y **oráculo de la especificación** | Analizar el posible defecto: no modificar la expectativa únicamente para conseguir GREEN |

La referencia de versiones se encuentra en `pom.xml` y en el workflow; una guía impresa puede quedarse desactualizada si hay cambios posteriores.

## 4. Reglas de actuación ante una incidencia

1. Registrar el mensaje de error relevante, el comando, el sistema operativo y las versiones de Java/Maven/Selenium/Chrome.
2. Distinguir si se trata de **configuración**, **problema del test**, **defecto funcional** o **incidencia de ejecución de Maven o Actions**.
3. Proponer el cambio mínimo y justificarlo técnicamente.
4. Volver a ejecutar el test afectado y la regresión correspondiente.
5. Anotar en `README_P3.md` las desviaciones significativas respecto del entorno de referencia, las decisiones adoptadas y los resultados.

**No se deben** borrar pruebas, rebajar aserciones, desactivar plugins ni cambiar el código de producción únicamente para ocultar un fallo de entorno. Los tokens y contraseñas no se adjuntarán a evidencias ni se subirán al repositorio.

## 5. Resultados y evidencias

Los informes de JUnit se guardan en `target/surefire-reports/`; los E2E, en `target/failsafe-reports/`; y JaCoCo, en `target/site/jacoco/`. Un `BUILD SUCCESS` demuestra que la ejecución finalizó correctamente, **no** que se hayan detectado todos los defectos ni que se hayan cubierto todas las clases de equivalencia y caminos independientes.

## Análisis de calidad en local y GitHub Actions

Las pruebas se diseñan, implementan y analizan en Eclipse. Maven genera JaCoCo con `mvn clean verify` y PIT de los servicios de dominio con `mvn -Pmutation test pitest:mutationCoverage`. GitHub Actions ejecuta las mismas verificaciones unitarias y almacena los informes. Las pruebas E2E con Selenium se ejecutan localmente mediante `mvn -Pe2e clean verify`.
