# Especificación funcional · Dojo Reservation

## 1. Datos y reglas generales

Un equipo tiene las siguientes propiedades: 
- Identificador
- Nombre
- Fecha de inscripción
- **exactamente cinco luchadores**
- Un conjunto de armas requeridas

Cada luchador tiene: 
- Nombre
- Fecha de nacimiento
- Opcionalmente, una fecha de inicio de la situación de desempleo

Para que un individuo pueda inscribirse en un equipo debe haber cumplido **16 años** en la fecha de inscripción. No se permite registrar desempleo con anterioridad a los 16 años.

Un dojo tiene: 
- Identificador
- Nombre
- Precio base
- Conjunto de armas disponibles

Las reservas contienen: 
- Identificador de dojo
- Fecha de entrenamiento.

Se comparan **identificador y fecha** para determinar la ocupación; una reserva de otro dojo o de otro día no bloquea al candidato.

El entrenamiento solicitado debe celebrarse **estrictamente después de la fecha actual fijada en la aplicación**. El entorno automatizado E2E proporciona su propio reloj fijo; véase la guía técnica.

## 2. Método `DojoSearchService.containsAllRequiredWeapons`

**Firma:** `boolean containsAllRequiredWeapons(Set<Weapon> available, Set<Weapon> required)`.

**Propósito:** determinar si el conjunto `available` contiene **todas** las armas de `required`, por igualdad de arma.

**Contrato:** devuelve `true` cuando están todas presentes, incluso si `required` está vacío. Devuelve `false` en cuanto falta una requerida. No se presupone ningún orden de iteración en los `Set`.

**Entradas inválidas:** si `available == null`, se lanza `NullPointerException` con mensaje `Available weapons are required`; si `required == null` con `available` válido, se lanza `NullPointerException` con mensaje `Required weapons are required`.

## 3. Método `DojoSearchService.findFirstAvailableDojo`

**Firma:** `Optional<Dojo> findFirstAvailableDojo(List<Dojo> dojos, Set<Weapon> requiredWeapons, List<Reservation> reservations, LocalDate trainingDate)`.

**Propósito:** devolver el **primer dojo de la lista recibida**, según su orden, que cumpla simultáneamente:

1. Tener todas las armas requeridas.
2. No disponer de una reserva con el mismo identificador de dojo y la fecha exacta del entrenamiento.

**Salida:** `Optional.of(dojo)` con ese **primer** dojo; si no hay ninguno elegible, `Optional.empty()`. Una lista de dojos vacía devuelve vacío. No basta con encontrar un dojo compatible si está ocupado: debe continuar la búsqueda.

**Precondiciones explícitas:** `dojos`, `requiredWeapons`, `reservations` y `trainingDate` no pueden ser `null`. Si lo son, se lanza `NullPointerException` con mensajes, en ese orden: `Dojos are required`, `Required weapons are required`, `Reservations are required`, `Training date is required`. Para probar el contrato de este servicio puede utilizarse una lista pequeña de dojos y reservas: no se exige crear una pantalla web.

Los elementos de las colecciones que forman parte de una entrada válida son objetos de dominio válidos. Un `trainingDate` no nulo no tiene una precondición temporal adicional **dentro de este método**: la validación «fecha futura» corresponde al caso de uso de aplicación.

## 4. Método `ReservationDiscountService.findYoungestFighter`

**Firma:** `Optional<Fighter> findYoungestFighter(List<Fighter> fighters, LocalDate referenceDate)`.

**Propósito:** encontrar al luchador **con fecha de nacimiento más reciente** de una lista.

**Salida:** `Optional.empty()` si la lista está vacía. Si hay luchadores, devuelve el más joven; en empate exacto de fecha de nacimiento debe conservarse **el primero que aparece en la lista**.

**Precondiciones:** la lista `fighters` no puede ser nula; tampoco `referenceDate` ni ninguno de los elementos de la lista. Estos supuestos producen `NullPointerException` con mensajes `Fighters are required`, `Reference date is required` y `Null fighter`, respectivamente. La fecha de referencia se valida, pero la **comparación de juventud se realiza entre fechas de nacimiento**, no por edades enteras.

## 5. Método `ReservationDiscountService.calculateBestDiscount`

**Firma:** `Discount calculateBestDiscount(List<Fighter> fighters, LocalDate trainingDate)`.

**Propósito:** obtener una única bonificación porcentual, aplicando **solo la mayor** de las disponibles.

- **Juventud:** si el luchador más joven tiene menos de **18 años** en la fecha de entrenamiento, corresponde una bonificación del **15 %**; con 18 o más no hay bonificación juvenil.
- **Desempleo:** para cada luchador con período de desempleo se cuentan sus **años completos** desde la fecha de inicio hasta la fecha de entrenamiento. Si son menos de 1 año: **0 %**; de 1 a menos de 2: **10 %**; de 2 a menos de 3: **20 %**; con 3 años o más: **30 %**.
- Si concurren varios luchadores o bonificaciones, **se selecciona exclusivamente el mayor porcentaje**. Nunca se suman los descuentos.
- Una lista vacía devuelve `Discount(0)`.

**Precondiciones:** `fighters` y `trainingDate` no pueden ser `null` (mensajes `Fighters are required` y `Training date is required`). Una lista con un luchador `null` provoca `NullPointerException("Null fighter")`, que puede originarse en la llamada al método auxiliar `findYoungestFighter`. Las operaciones `Optional.orElseThrow()` forman parte del código que debe analizarse, pero **no es correcto asumir que todas sus ramas excepcionales son ejecutables**.

Para las pruebas directas de estos métodos se pueden utilizar listas de uno o dos luchadores, aunque los equipos persistidos exigen cinco.

## 6. Pantalla de búsqueda `GET /dojos`

**Entradas:** identificador `teamId` y fecha `trainingDate`. La pantalla permite seleccionar equipos conocidos; la petición HTTP puede recibir también identificadores inexistentes.

**Resultado:** tabla con los dojos en su orden de selección, indicando para cada uno compatibilidad de armas, disponibilidad y selección. Se resalta el primer dojo compatible y libre, si existe, y se facilita un enlace a la pantalla de coste. Si no existe ninguno, se informa de la ausencia de un dojo elegible.

**Validación de la aplicación:** equipo vacío → `Team id is required`; equipo inexistente → `Team does not exist`; fecha de hoy o anterior → `Training date must be in the future`.

Los errores de formato de fecha enviados directamente por HTTP pueden ser rechazados por Spring MVC **antes de ejecutar el caso de uso**; no se presupone que su mensaje sea el de fecha no futura.

## 7. Pantalla de coste `GET /coste`

**Entradas:** `teamId`, `dojoId` y `trainingDate`.

**Precio:** suplemento de **5,00 € por cada arma distinta requerida por el equipo**, independientemente de cuántas unidades de esa arma tenga el dojo. El subtotal es **precio base del dojo + suplemento**. El precio final es el subtotal tras aplicar **el porcentaje mayor** definido en el apartado 5. Los importes se representan con dos decimales.

**Validación:** `Team id is required`, `Dojo id is required`, `Team does not exist`, `Dojo does not exist` o `Training date must be in the future`, según la condición. La pantalla permite seleccionar IDs válidos; el servidor debe comprobar también parámetros manipulados en la URL.

No se impone en este caso de uso la búsqueda de disponibilidad del dojo: **calcular un presupuesto y reservar un dojo son operaciones diferentes**.

## 8. Datos de referencia accesibles en la aplicación

Se proporcionan dos equipos habituales (`EQ-001` y `EQ-002`) y tres equipos con datos específicos para explorar fronteras de edad o desempleo (`EQ-003`, `EQ-004`, `EQ-005`). Los datos exactos constan en el catálogo `InMemoryCatalog` que acompaña el proyecto: **deberán estudiarse como estados de entrada**, no alterarse arbitrariamente.

Dojos: `D-001` (Dojo Norte; 40,00 €; Katana y Bokken), `D-002` (Dojo Centro; 50,00 €; Katana, Bokken y Bo) y `D-003` (Dojo Sur; 35,00 €; Bo). Hay reservas predefinidas para `D-001`, consultables en el catálogo.

Los datos concretos de nacimiento, desempleo, inscripción y reservas forman parte de los **fixtures**, no de las soluciones de prueba. El alumnado deberá decidir qué combinaciones representan cada clase de equivalencia o límite.

## 9. Oráculos y excepciones

Las expectativas deberán determinarse usando **este contrato** y las entradas elegidas. Un defecto introducido en el código puede producir resultados diferentes: el fallo de la prueba es entonces información que debe investigarse.

Al analizar caminos independientes, separar los recorridos del método estudiado de los posibles errores internos de un auxiliar o de una biblioteca. Identificar expresamente las salidas excepcionales no factibles cuando se haya modelado una ruta que no puede alcanzarse con entradas coherentes.
