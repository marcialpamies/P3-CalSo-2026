# P3 · Arquitectura y modelo del dominio

Los diagramas representan **las clases y dependencias que existen en el proyecto Java de partida**. Ayudan a localizar responsabilidades, pero **no resuelven los grafos de flujo, los caminos independientes ni los casos de prueba**. Los resultados esperados se deducen de [la especificación funcional](ESPECIFICACION.md), no de la implementación inicial, que puede contener defectos.

## 1. Arquitectura de la aplicación

El proyecto separa presentación e infraestructura, aplicación y dominio. Las interfaces de repositorio (`TeamRepository`, `DojoRepository`, `ReservationRepository`) pertenecen al dominio. Su implementación concreta en memoria y el ensamblaje de dependencias corresponden a infraestructura.

```mermaid
flowchart TB
    subgraph INF_IN["Infraestructura: entrada web"]
      Browser["Navegador: /dojos y /coste"]
      Controller["DojoReservationController"]
      Projection["DojoEvaluationService: filas informativas"]
      Browser --> Controller
      Controller --> Projection
    end

    subgraph APPLICATION["Aplicación: puertos de entrada y casos de uso"]
      SearchPort["FindAvailableDojoInputPort"]
      CostPort["CalculateReservationCostInputPort"]
      SearchUC["FindAvailableDojoUseCase"]
      CostUC["CalculateReservationCostUseCase"]
      SearchPort -.->|"implementado por"| SearchUC
      CostPort -.->|"implementado por"| CostUC
    end

    subgraph DOMAIN["Dominio: modelos, servicios y puertos de salida"]
      SearchService["DojoSearchService"]
      DiscountService["ReservationDiscountService"]
      PricingService["ReservationPricingService"]
      Models["Team, Fighter, Dojo, Reservation, Weapon, Money, Discount, UnemploymentPeriod"]
      Repos["TeamRepository, DojoRepository, ReservationRepository"]
      SearchService --> Models
      DiscountService --> Models
      PricingService --> Models
    end

    subgraph INF_OUT["Infraestructura: persistencia y configuración"]
      Catalog["InMemoryCatalog: datos de ejemplo"]
      Config["UseCaseConfiguration: beans y Clock"]
    end

    Controller --> SearchPort
    Controller --> CostPort
    SearchUC --> SearchService
    CostUC --> DiscountService
    CostUC --> PricingService
    SearchUC --> Repos
    CostUC --> Repos
    Projection --> Repos
    Config -.->|"ensambla"| SearchUC
    Config -.->|"ensambla"| CostUC
    Config -.->|"adapta interfaces a catálogo"| Repos
    Config --> Catalog
```

**Lectura de las flechas:** las continuas expresan uso/dependencia de ejecución; las discontinuas, implementación o ensamblaje. `DojoEvaluationService` es una proyección de presentación para la tabla web, no el servicio de dominio encargado de seleccionar el dojo. El dominio no depende de Spring Boot ni de Selenium.

## 2. Diagrama UML de clases del contexto acotado

Se representan los atributos y operaciones relevantes para comprender el problema, sin pretender enumerar todos los constructores, validaciones o métodos auxiliares.

```mermaid
classDiagram
    direction LR
    class Team {
      +String id
      +String name
      +LocalDate registrationDate
      +List~Fighter~ fighters
      +Set~Weapon~ requiredWeapons
    }
    class Fighter {
      +String name
      +LocalDate birthDate
      +Optional~UnemploymentPeriod~ unemployment()
      +int ageAt(LocalDate referenceDate)
    }
    class UnemploymentPeriod {
      +LocalDate startDate
      +long completeYearsAt(LocalDate referenceDate)
    }
    class Weapon {
      +String name
    }
    class Dojo {
      +String id
      +String name
      +Money basePrice
      +Set~Weapon~ weapons
    }
    class Reservation {
      +String dojoId
      +LocalDate trainingDate
    }
    class Money {
      +BigDecimal amount
      +Money plus(Money other)
      +Money after(Discount discount)
    }
    class Discount {
      +int percentage
      +Discount max(Discount other)
    }
    class DojoSearchService {
      +Optional~Dojo~ findFirstAvailableDojo(...)
      +boolean containsAllRequiredWeapons(...)
    }
    class ReservationDiscountService {
      +Optional~Fighter~ findYoungestFighter(...)
      +Discount calculateBestDiscount(...)
    }
    class ReservationPricingService {
      +Money calculateBaseWithWeapons(Team team, Dojo dojo)
      +Money calculateFinalPrice(Money subtotal, Discount discount)
    }

    Team "1" *-- "5" Fighter : fighters
    Team "1" o-- "0..*" Weapon : requiredWeapons
    Fighter "1" --> "0..1" UnemploymentPeriod : unemployment
    Dojo "1" o-- "0..*" Weapon : weapons
    Dojo --> Money : basePrice
    Reservation ..> Dojo : dojoId

    DojoSearchService ..> Dojo
    DojoSearchService ..> Reservation
    DojoSearchService ..> Weapon
    ReservationDiscountService ..> Fighter
    ReservationDiscountService ..> Discount
    ReservationPricingService ..> Team
    ReservationPricingService ..> Dojo
    ReservationPricingService ..> Money
    ReservationPricingService ..> Discount
```

**Aclaraciones sobre el modelo:**

- `Team` contiene **exactamente cinco** referencias a luchadores; los valores pueden repetirse, como ocurre en los datos de ejemplo. El diagrama expresa la cardinalidad del equipo, no la unicidad de los objetos.
- `Team.requiredWeapons` describe armas requeridas; `Dojo.weapons` describe armas disponibles. Los conjuntos contienen objetos `Weapon`.
- `Reservation` guarda el **identificador** del dojo y la fecha del entrenamiento; la dependencia del dibujo no representa un campo de tipo `Dojo`.
- `Fighter` puede tener un período de desempleo o no tenerlo.
- Las operaciones de `DojoSearchService` y `ReservationDiscountService` son objeto de las pruebas de caja blanca; las pruebas Selenium recorren la aplicación desde la interfaz web.

## 3. Cómo utilizar los diagramas en la práctica

Para la **caja blanca**, localizar los métodos asignados en los servicios de dominio y construir sus propios grafos a partir del código real. Este UML no es un grafo de control.

Para la **caja negra**, deducir clases de equivalencia, límites y resultados esperados de la especificación y contrastarlos mediante las pantallas web. La arquitectura permite entender qué capas intervienen, pero no revela por sí sola todas las reglas de negocio.

Los servicios y repositorios se pueden probar sin navegador; Selenium es una prueba de extremo a extremo desde la interfaz web.
