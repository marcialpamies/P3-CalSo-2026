# Guía técnica y metodológica · JUnit 5 y Selenium

## 1. Qué está preparado

El proyecto Maven incluye dependencias de **JUnit Jupiter**, **Selenium Java** y **Spring Boot**, además de perfiles para ejecutar pruebas unitarias y pruebas de navegador. Se facilitan las clases E2E `DojoSearchE2ETest` y `ReservationCostE2ETest` **sin los escenarios resueltos**, junto con `BrowserE2ESupport`.

Esta clase de apoyo abre/cierra Chrome, espera la navegación y proporciona:

| Método disponible | Función |
|---|---|
| `open(String relativePath)` | Abre una ruta del servidor web; admite parámetros de consulta |
| `searchDojos(String teamId, String trainingDate)` | Rellena y envía el formulario de búsqueda |
| `calculateCost(String teamId, String dojoId, String trainingDate)` | Rellena y envía el formulario de coste |
| `textOf(String elementId)` | Espera y devuelve el texto del elemento cuyo ID se indica |
| `textAt(By locator)` | Espera y devuelve el texto de un elemento localizado por Selenium |

Los métodos esperan fechas en formato **`AAAA-MM-DD`**. Para evaluar identificadores inválidos no presentes en desplegables, utilizar `open` con la URL preparada.

**No es necesario** programar ChromeDriver, las esperas, el servidor ni la conexión Selenium/Spring Boot.

## 2. Ejecución en Eclipse

Importar el módulo de la P3 mediante **File → Import → Maven → Existing Maven Projects**. Después ejecutar **Maven → Update Project**.

Crear dos configuraciones en **Run As → Maven build...**:

| Configuración | Goals | Profiles |
|---|---|---|
| Pruebas unitarias | `clean verify` | Ninguno |
| Pruebas unitarias y Selenium | `clean verify` | `e2e` |

En el terminal, los comandos equivalentes son:

```powershell
mvn clean verify
mvn -Pe2e clean verify
mvn -Pmutation test pitest:mutationCoverage
```

El perfil E2E arranca Spring Boot, ejecuta Chrome sin interfaz gráfica, detiene la aplicación y genera resultados. Antes de ejecutarlo debe estar libre el **puerto 8080**.

El reloj de aplicación en ese perfil está fijado al **2026-10-20**, en la zona **Europe/Madrid**; el sistema habitual usa la fecha real. Esta separación permite reproducir los límites de «hoy» y «mañana» sin depender del ordenador del estudiante.

## 3. Dónde incorporar los tests

Los métodos de caja blanca se crearán en clases JUnit situadas bajo `src/test/java/`, dentro del paquete de pruebas de los servicios de dominio. Las pruebas E2E se incorporarán **únicamente** a los archivos suministrados para cada pantalla, reutilizando `BrowserE2ESupport`.

### Estructura de un test

Cada `@Test` debe contener una situación verificable:

```java
@Test
void testGiven<CondicionesConcretas>When<Accion>Then<ResultadoConcreto>() {
    // Preparar entradas y estado del escenario diseñado.
    // Ejecutar el método o interacción.
    // Verificar un resultado esperado derivado de la especificación.
}
```

**El fragmento anterior es una plantilla de estructura, no código Java compilable**. Sustituir los textos entre corchetes angulares por identificadores válidos.

En pruebas de dominio, preparar explícitamente los datos y fechas. En E2E, llamar a `searchDojos`, `calculateCost` o `open` y usar las aserciones JUnit para observar una salida concreta. Una prueba puede efectuar varias instrucciones de preparación; la unidad que debe permanecer indivisible es el **escenario funcional**.

## 4. Técnicas y tablas exigidas

### Caminos independientes

| Método | Nodo o camino identificado | Datos de entrada y fecha | Resultado esperado según especificación | Nombre del test | Resultado ejecución |
|---|---|---|---|---|---|

Primero grafo; después complejidad; a continuación base mínima y tabla. Una excepción **inviable** debe justificarse; no debe fabricarse un dato inconsistente para fingir cobertura.

### Clases de equivalencia

**Primero** una tabla de clases:

| Código de clase | Variable | Condición de pertenencia | Válida / inválida | Comportamiento exigido |
|---|---|---|---|---|

**Después** una tabla de casos:

| ID del caso | Clases cubiertas | Equipo/dojo/fecha o URL concreta | Estado inicial | Resultado único esperado | Nombre prueba |
|---|---|---|---|---|---|

No limitarse a repetir cada opción del desplegable; deducir las clases según las reglas y la validación del servidor. Una misma combinación de entradas puede servir para comprobar salidas diferentes, siempre que sean métodos separados y cada resultado esté justificado.

### Valores límite

| Variable | Valor del límite | Valor elegido | Expectativas |
|---|---|---|---|

Para límites relativos al reloj, utilizar el **reloj fijo del perfil E2E**. Para los relacionados con edad y paro, consultar los equipos de prueba predefinidos en el catálogo o utilizar la llamada correspondiente de la url.

## 5. Identificación de elementos web

| Pantalla | Campos y acciones | Elementos de resultado |
|---|---|---|
| Búsqueda | `#teamId`, `#trainingDate`, `#search` | `#dojo-id`, `#dojo-table`, `#no-dojo`, `#error` |
| Costes | `#teamId`, `#dojoId`, `#trainingDate`, `#calculate` | `#base-price`, `#weapons-price`, `#subtotal`, `#discount`, `#final-price`, `#error` |

El selector CSS de las filas de búsqueda permite localizar un dojo por `data-dojo-id`. Los métodos auxiliares ya realizan esperas para evitar leer elementos de una página sustituida después de enviar el formulario.

## 6. Diferencia entre prueba fallida e infraestructura defectuosa

- Una **aserción diferente del resultado esperado** exige investigar el código bajo la especificación.
- Un navegador que no arranca, una dependencia incompatible o el puerto ocupado requieren corregir el **entorno**, no cambiar expectativas.
- Un parámetro de fecha imposible en HTTP puede producir un error de conversión antes de entrar en el controlador; la técnica debe identificar el nivel al que se dirige el caso.
- La suite E2E comprueba el sistema a través de navegador; la cobertura JaCoCo de las pruebas unitarias no demuestra por sí sola que todas las clases E2E hayan sido probadas.

Consultar la [guía de versiones](ENTORNO_Y_VERSIONES.md) para registrar y resolver incompatibilidades.

## 7. Ejecución local de cobertura y mutaciones

La ejecución `mvn clean verify` genera los informes de JaCoCo en `target/site/jacoco/`. Para el análisis PIT de los servicios del dominio, ejecutar `mvn -Pmutation test pitest:mutationCoverage` y consultar `target/pit-reports/`. PIT solamente utiliza las clases de prueba JUnit de los servicios de dominio, no las pruebas Selenium. En GitHub Actions se ejecutan automáticamente JUnit, JaCoCo y PIT, y se conservan los informes como artefactos. Los fallos de tests o mutaciones supervivientes deben interpretarse, sin ocultarlos ni cambiar las expectativas de prueba.

## 8. Antes de entregar

Comprobar que todas las entradas de las tablas existen, que cada expectativa se deriva de la especificación, que hay correspondencia caso → método JUnit, que las ramas/caminos declarados están justificados y que los informes de pruebas permiten reproducir los resultados.

### Cobertura de condiciones seleccionadas dentro del diseño de caja blanca

Durante el mismo diseño de caja blanca, tras proponer los caminos independientes y antes de implementar las pruebas, cada estudiante examinara exclusivamente la decision asignada en README_P3.md. Documentara las dos condiciones atomicas, los resultados verdadero y falso efectivamente evaluados, y las situaciones sin evaluacion por cortocircuito. Reutilizara las pruebas del primer ejercicio y agregara solo los casos adicionales necesarios y factibles.

| Condicion atomica | Caso que evalua verdadero | Caso que evalua falso | Casos adicionales | Justificacion si no es factible |
|---|---|---|---|---|

## Plantilla de GitHub Actions para el repositorio del grupo

La pareja debe crear \`.github/workflows/p3-quality.yml\` a partir del siguiente **esqueleto**. Los marcadores \`<<...>>\` son espacios de configuración: **hay que sustituirlos por valores reales antes de guardar el fichero**; no son sintaxis válida de GitHub Actions. En el esquema recomendado, el módulo Maven se encuentra en \`P3/P3-CalSo-base/\`. Si el grupo utiliza otra ubicación, debe sustituir **todas** las apariciones de la ruta, también en los filtros y los artefactos. Los nombres de ramas permiten aislar las ejecuciones de la P3 de otras prácticas.

~~~yaml
name: P3 - JUnit JaCoCo PIT

on:
  push:
    branches:
      - <<RAMA_PRINCIPAL>>
      - <<RAMA_P3_ALUMNO_1>>
      - <<RAMA_P3_ALUMNO_2>>
    paths:
      - '<<RUTA_MODULO_MAVEN>>/**'
      - '.github/workflows/p3-quality.yml'
  pull_request:
    branches:
      - <<RAMA_PRINCIPAL>>
    paths:
      - '<<RUTA_MODULO_MAVEN>>/**'
      - '.github/workflows/p3-quality.yml'
  workflow_dispatch:

permissions:
  contents: read

jobs:
  unit-coverage:
    name: JUnit y JaCoCo
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: <<RUTA_MODULO_MAVEN>>
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '<<VERSION_JAVA>>'
          cache: maven
          cache-dependency-path: <<RUTA_MODULO_MAVEN>>/pom.xml
      - name: Ejecutar JUnit y JaCoCo
        run: mvn --batch-mode --no-transfer-progress clean verify
      - name: Publicar informes JUnit
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: p3-junit
          path: <<RUTA_MODULO_MAVEN>>/target/surefire-reports/
          if-no-files-found: warn
      - name: Publicar JaCoCo
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: p3-jacoco
          path: <<RUTA_MODULO_MAVEN>>/target/site/jacoco/
          if-no-files-found: warn

  mutation-testing:
    name: PIT - servicios de dominio
    runs-on: ubuntu-latest
    defaults:
      run:
        working-directory: <<RUTA_MODULO_MAVEN>>
    steps:
      - uses: actions/checkout@v4
      - uses: actions/setup-java@v4
        with:
          distribution: temurin
          java-version: '<<VERSION_JAVA>>'
          cache: maven
          cache-dependency-path: <<RUTA_MODULO_MAVEN>>/pom.xml
      - name: Ejecutar PIT
        run: mvn --batch-mode --no-transfer-progress -Pmutation test pitest:mutationCoverage
      - name: Publicar informe PIT
        if: always()
        uses: actions/upload-artifact@v4
        with:
          name: p3-pit
          path: <<RUTA_MODULO_MAVEN>>/target/pit-reports/
          if-no-files-found: warn
~~~

| Marcador | Sustitución prevista |
|---|---|
| \`<<RAMA_PRINCIPAL>>\` | \`main\`, salvo que el grupo haya utilizado otro nombre |
| \`<<RAMA_P3_ALUMNO_1>>\` | Rama de la P3 del primer integrante; por ejemplo, \`P3-alumno1\` |
| \`<<RAMA_P3_ALUMNO_2>>\` | Rama de la P3 del segundo integrante; por ejemplo, \`P3-alumno2\` |
| \`<<RUTA_MODULO_MAVEN>>\` | Directorio que contiene el \`pom.xml\`; por ejemplo, \`P3/P3-CalSo-base\`, sin barra final |
| \`<<VERSION_JAVA>>\` | Versión JDK compatible, por defecto \`21\` |

**Importante:** la selección de servicios de dominio y clases JUnit para PIT está definida en el perfil \`mutation\` del \`pom.xml\`; no debe ampliarse a Selenium. El workflow únicamente ejecuta JUnit, JaCoCo y PIT; las pruebas E2E se ejecutan en local. Revisar en la pestaña **Actions** de GitHub los resultados de ambas ramas individuales, los PR y la versión integrada en \`main\`.
