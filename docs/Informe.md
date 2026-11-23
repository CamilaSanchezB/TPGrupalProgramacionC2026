# Informe del Trabajo Práctico Grupal 2026

**Software embarcado de una nave interestelar tripulada**

Programación C

Facultad de Ingeniería, Universidad Nacional de Mar del Plata

Año académico 2026, segundo cuatrimestre

Grupo G13

Integrantes:

— Bruses Tomas

— Cameranesi Valentín

— Fernández Monteverde Dolores

— Ferrando Ignacio

— Salemme Alonso Agustín

— Sánchez Bonaldo Camila

## Introducción y objetivos

El trabajo consiste en construir el software embarcado de una nave interestelar tripulada. El sistema representa la nave y su tripulación, administra sus recursos, controla el Motor Warp, ejecuta misiones que recorren un universo con obstáculos, ayudas y otras naves, registra todo lo que ocurre en una bitácora, calcula los haberes de la tripulación y ofrece una interfaz gráfica con acceso por usuario y contraseña.

El desafío no es solo que el programa funcione. La consigna pide un sistema orientado a objetos comprensible, extensible y verificable, donde cada decisión pueda responder tres preguntas:

1. ¿Qué objeto es responsable de este comportamiento?
2. ¿Qué contrato debe cumplir?
3. ¿Cómo se demuestra que funciona?

El trabajo se desarrolló en dos entregas sobre el mismo proyecto. La primera construyó el núcleo orientado a objetos (nave, recursos, tripulación, Motor Warp, misiones, bitácora y contratos). La segunda lo amplió con un universo dinámico, la navegación con radares, las batallas entre naves, la persistencia de usuarios y las interfaces gráficas. Este informe describe el programa tal como está hoy: cuando la segunda entrega cambió algo de la primera, se explica solo el resultado final. Los temas que todavía no están implementados figuran solo con su título.

## Tecnologías y organización del proyecto

| Aspecto | Decisión |
| --- | --- |
| Lenguaje | Java 8 (`maven.compiler.release` = 8) |
| Construcción | Maven, codificación UTF-8 |
| Base de datos de usuarios | SQLite, con el driver JDBC `sqlite-jdbc` |
| Interfaz gráfica | Swing, con FlatLaf (apariencia) y MigLayout (disposición de los componentes) |
| Control de versiones | Git y GitHub |
| Documentación | Javadoc con contratos (INV, PRE, POST), diagramas UML y este informe |

Para compilar y ejecutar la aplicación (el classpath incluye las dependencias que Maven copia a `target/lib`; en Windows el separador es `;`):

```powershell
mvn compile
java -ea -cp "target/classes;target/lib/*" view.Main
```

La opción `-ea` activa las aserciones (`assert`), con las que se verifican algunos contratos internos. Sin ella la JVM las ignora. La aplicación gráfica se inicia desde `view.Main`. Los programas del paquete `pruebas` permiten ejecutar escenarios manuales por tema.

## Arquitectura general

El código se organiza en las tres capas del patrón MVC más una capa de persistencia:

| Paquete | Rol | Contenido principal |
| --- | --- | --- |
| `model` | Dominio: reglas y estado | `nave` (naves y batallas), `ClasesMotorWarp`, `Mision`, `Universo` (elementos, radares y navegador), `destinoytablero` (tablero y turnos provistos por la cátedra), `tripulantes`, `ClasesBitacora`, `Concurrencia` (universo paralelo), `CentroDeControl`, `Recursos`, `Requisitos`, `Coordenada` |
| `controller` | Coordinación | `AsistenteDeComando`, `ControladorNave`, `ControladorPartida`, `ControladorVisor`, `LiquidadorHaberes`, `LoginController`, `TripulantesController`, y los modelos de vista `EstadoNaveVista` y `ResumenMisionVista` |
| `view` | Presentación | `ILogin` y `LoginView`, `ITripulantes` y `TripulantesView`, paneles de la partida (`MainPanel`, `TableroUniversoPanel`, `ResultadoPanel`), `VistaNaves` (visor), `Main` (arranque y navegación entre pantallas, sin lógica de negocio), `ConfigView` (apariencia común) |
| `persistencia` | Acceso a datos | `ServicioUsuarios`, `HasheadorContrasenas`, `dao` (`UsuarioDAO`, `UsuarioDAOSqlite`, `ConexionSQLite`), `dto` (`UsuarioDTO`) y excepciones propias |
| `pruebas` | Demostraciones ejecutables | Escenarios con `main` para misiones, navegador, radares, batallas, universo, login, centro de control y tripulantes |

**Regla central:** el dominio no conoce ninguna biblioteca gráfica. La coordinación de la nave pasa por `AsistenteDeComando`, que registra sus acciones en la bitácora. Algunas demostraciones del paquete `pruebas` sí escriben resultados en consola; la interfaz principal los presenta mediante Swing.

**Tres flujos principales.** Para operar la nave, quien la usa habla con el `AsistenteDeComando`, que consulta a la `Nave`, que delega en sus componentes; cada resultado, exitoso o rechazado, queda registrado en la `Bitacora`. Para mostrar la nave en pantalla, el controlador (`ControladorNave`) consulta al asistente y entrega a la vista un modelo de vista ya armado. Para el acceso al sistema, la vista le pasa los datos al controlador, que llama al servicio, que consulta al DAO, que accede a la base SQLite.

## Modelo del dominio

### La nave

`Nave` es una clase abstracta que compone tres objetos en lugar de concentrar toda la lógica:

- un `Recursos` (combustible, energía, desgaste y gemas),
- un `MotorWarp` (el motor, con su comportamiento por estado),
- una `TripulantesCollection` (la tripulación).

La nave delega en esos objetos y mantiene sus invariantes: los recursos respetan sus límites y la tripulación se administra mediante `TripulantesCollection`. La tripulación se expone como una vista de solo lectura. La nave se considera destruida si el desgaste llega a 100, la energía o el combustible llegan a 0, o pierde una batalla (`destruir()`). La destrucción por batalla se conserva como un estado de la nave.

Hay tres tipos de nave, que difieren en su configuración inicial:

| Tipo | Combustible inicial | Energía inicial | Desgaste inicial |
| --- | --- | --- | --- |
| Exploradora | 60 | 80 | 0 |
| Carguero | 100 | 60 | 0 |
| Combate | 80 | 100 | 0 |

Todas tienen capacidad máxima 100 de combustible y de energía, y el Motor Warp comienza Disponible. Cada nave nace con una tripulación mínima: capitán, alférez, consejero y teniente. Los tres tipos también se diferencian en las batallas (ver Double Dispatch).

### Recursos y mantenimiento

`Recursos` protege los límites:

- Combustible y energía: entre 0 y 100.
- Desgaste: entre 0 y 100. Con 80 o más la nave requiere mantenimiento, que lo lleva a 0.
- Gemas: nunca negativas; es el recurso que se obtiene en la travesía.
- Una operación que supere la capacidad o deje un valor fuera de rango se rechaza sin modificar los recursos.

La operación `consumir(combustible, energía, desgaste)` comprueba antes de modificar, de modo que no queda un consumo a medias. Además de las órdenes (`cargarCombustible`, `cargarEnergia`, `consumir`, `realizarMantenimiento`), la clase ofrece consultas que no modifican nada (`puedeConsumir`, `puedeCargarCombustible`, `puedeCargarEnergia`, `requiereMantenimiento`), para que quien llama pueda preguntar antes de actuar. Para la travesía se sumaron `agregarGemas`, `recargarHastaTope` (recarga sin superar el tope), `recibirImpacto` (descuenta energía y suma desgaste respetando los límites), `estaDestruida` y `copia()`, que permite obtener una copia de los recursos.

### Tripulación

`TripulantesCollection` encapsula la lista: permite agregar, editar y eliminar tripulantes mediante sus métodos, consultar por cargo y verificar si existe un tripulante por nombre. Los cargos son capitán, consejero, teniente y alférez; los orígenes son terrícola, vulcano y marciano. Cada tripulante tiene nombre, cargo, origen, antigüedad y remuneración. El cálculo de los haberes se explica en el patrón Decorator.

### Universo y navegación

- **`Coordenada`** es un par (x, y) inalterable.
- **`Universo`** es una interfaz mínima: indica si una coordenada está dentro y qué elementos hay en ella. Los radares y el navegador trabajan contra esta interfaz.
- **`ElementoDelUniverso`** es lo que puede haber en una casilla: tiene posición, interactúa con la nave y devuelve un texto para la bitácora. Los elementos concretos son:
    - `Asteroide`, que se considera obstáculo y descuenta hasta 20 puntos de energía.
    - `EstacionDeCarga`, que recarga hasta 20 puntos de combustible y 20 de energía sin superar el tope.
    - `NaveEnemiga`, que contiene una nave de cualquiera de los tres tipos y se enfrenta con la nave propia cuando coinciden en una casilla.
- **`Tablero` y `Destino`** están incluidos en el proyecto para integrar el tablero provisto por la cátedra. El tablero representa una grilla, admite un elemento por casilla y es observable; `Destino` intenta mover aleatoriamente hasta la mitad de los elementos en cada turno.
- **`RadarCortoAlcance`** escanea los elementos cercanos a una posición y los ordena por distancia.
- **`RadarLargoAlcance`** calcula rutas con búsqueda en anchura, mediante movimientos en cuatro direcciones, considerando los obstáculos.
- **`Navegador`** lleva la nave hacia un destino. Al iniciar el viaje calcula una ruta con el radar largo. En cada turno consulta el radar corto. Si detecta un obstáculo en la siguiente coordenada, intenta un salto Warp de dos casilleros si el motor está disponible, hay al menos dos pasos de ruta, el aterrizaje está libre y se cumple la comprobación de energía del navegador. Si no puede iniciar el Warp, recalcula la ruta con el radar largo. En los demás turnos intenta un salto normal de una casilla. El motor cobra los costos: el salto normal consume 2 de combustible, el Warp consume 5 de energía y el enfriamiento suma 3 de desgaste. El Warp requiere turnos de preparación, salto y enfriamiento. El navegador avanza por la ruta según los casilleros que devuelve el motor.

**Observación de implementación:** el navegador exige 15 puntos de energía para iniciar el Warp (`3 * COSTO_ENERGIA_WARP`), aunque el motor verifica 5 en cada turno de preparación y otros 5 al materializar el salto. Ese requisito preventivo puede impedir el Warp cuando hay menos de 15 puntos disponibles. Conviene confirmar que esta restricción adicional sea intencional.

### Asistente de comando y bitácora

El `AsistenteDeComando` es el canal de coordinación para operar la nave. Consulta los recursos antes de las operaciones que pueden rechazarse; si una operación no puede realizarse, registra el motivo y lanza `OperacionRechazadaException`. En operaciones exitosas delega en la nave y registra el resultado.

Para las órdenes del motor (`saltar()` y `saltarWarp()`) devuelve los casilleros avanzados y registra el avance y el cambio de estado. También delega las misiones (`encomendarMision` ejecuta el ciclo completo y guarda el informe en el historial) y aplica la interacción con elementos del universo (`interactuarCon`), dejando constancia en la bitácora.

Cada acontecimiento se guarda en la `Bitacora` como un `RegitroBitacora` con fecha, hora (`LocalDateTime`) y descripción, en orden cronológico. La bitácora no acepta eventos vacíos.

### Centro de control

`CentroDeControl` permite registrar asistentes asociados a naves y recuperarlos mediante un identificador. No construye naves ni asistentes: recibe objetos ya armados. Rechaza identificadores vacíos o repetidos y asistentes nulos. Aunque puede mantener varias naves registradas, la interfaz gráfica selecciona una nave para operar. En la aplicación lo usa `ControladorNave`: al elegir una nave, la crea con la fábrica, la registra con su asistente y la recupera del centro.

El flujo de alta y operación también puede usarse desde `PruebaCentroDeControl`, sin abrir la interfaz gráfica.

### Misiones e informe

Hay tres misiones, cada una con requisitos y condición de éxito:

| Misión | Combustible | Energía | Desgaste | Condición de éxito |
| --- | --- | --- | --- | --- |
| M-01 Intercepción y asistencia | 4 | 5 | 4 | Llega al destino y conserva los recursos de auxilio; al cerrar los entrega |
| M-02 Recolección | 4 | 10 | 4 | Llega al destino con la nave viva; al cerrar recibe entre 20 y 100 gemas |
| M-03 Retorno seguro | 4 | 0 | 4 | Llega a la base de origen; al cerrar recarga combustible y energía al 100% y reduce el desgaste a 0 |

Al cerrar, cada misión produce un `InformeMision` inmutable que contiene misión, resultado, eventos, balance de recursos, recursos finales, observaciones y gemas ganadas. El balance de combustible, energía y desgaste se mide antes de aplicar los efectos finales; las gemas ganadas se miden después. `GestorHistorialMisiones` conserva los informes ejecutados y los expone en una lista no modificable.

## Patrones de diseño aplicados

Un patrón de diseño es una solución conocida a un problema que se repite. En este proyecto, los patrones se identifican por el reparto de responsabilidades entre objetos, no solo por los nombres de las clases.

### State — Motor Warp

**El problema.** Hay objetos que se comportan distinto según la situación en que están. El motor Warp no hace lo mismo cuando está disponible, preparándose, en Warp o enfriándose.

**Cómo lo implementamos.** `MotorWarp` mantiene un estado (`EstadoMotorBase`) y los recursos de la nave. Hacia afuera expone las operaciones `saltar()` y `saltarWarp()`. Cada llamada representa un turno del motor. Hay cuatro estados: `Disponible`, `PreparandoSalto`, `EnWarp` y `Enfriamiento`. Las operaciones devuelven cuántos casilleros avanzó la nave (0, 1 o 2).

| Estado | `saltar()` | `saltarWarp()` |
| --- | --- | --- |
| Disponible | Avanza 1 casillero y gasta 2 de combustible; si no alcanza, no avanza | Pasa a Preparando salto; ese turno inicia la preparación |
| Preparando salto | No avanza: el motor está ocupado | Con energía disponible cuenta el turno de preparación; al completar los dos turnos pasa a En Warp. Sin energía, no avanza la preparación |
| En Warp | No avanza | Con 5 de energía la consume, avanza 2 casilleros y pasa a Enfriamiento; sin energía aborta y vuelve a Preparando salto |
| Enfriamiento | Cumple el turno de enfriamiento: suma 3 de desgaste y vuelve a Disponible | Igual que `saltar()` |

Cuando una operación no corresponde al estado actual, la nave no avanza y se devuelve 0. El asistente registra cada llamada y el cambio de estado. El contador de preparación vive dentro de `PreparandoSalto`. El cambio de estado (`setEstado`) solo es accesible dentro del paquete del motor.

**Beneficio.** Un estado nuevo puede agregarse mediante otra clase que extienda `EstadoMotorBase`, sin concentrar la lógica en condicionales del motor.

### Factory — Creación de naves

**El problema.** Si cada cliente elige con `new` qué clase concreta crear, queda acoplado a las clases y a sus constructores.

**Cómo lo implementamos.** `NaveFactory.crearNave(tipo)` recibe el tipo (`EXPLORADORA`, `CARGUERO` o `COMBATE`) y devuelve una `Nave`. Cada variante se crea con sus valores iniciales y la tripulación mínima. La fábrica también acepta `CARGUERA` como nombre alternativo del carguero. Los controladores y escenarios de prueba solicitan la nave mediante la fábrica.

**Beneficio.** El cliente depende de la clase base `Nave` y no necesita construir directamente la clase concreta.

### Template Method — Ciclo de las misiones

**El problema.** Varias actividades siguen los mismos pasos en el mismo orden y solo cambian algunos detalles.

**Cómo lo implementamos.** `Mision.realizar()` es `final` y fija el ciclo: preparar, ejecutar, evaluar y cerrar. `MisionIntercepcion`, `MisionRecoleccion` y `MisionRetorno` implementan los *hooks* obligatorios `cumplirObjetivo()` y `cumplioObjetivo()`, y los opcionales `verificarEnDestino()` y `aplicarEfectosFinales()`.

| Paso | Qué hace |
| --- | --- |
| preparar | Verifica disponibilidad y recursos, comprueba las coordenadas y calcula la ruta con el radar largo. No consume recursos. Si no hay ruta, rechaza la misión sin modificar la nave. |
| ejecutar | Consume los requisitos iniciales y realiza turnos hasta llegar, destruirse o alcanzar los límites de seguridad. El código actual avanza con `Navegador.avanzarUnTurno()` y luego inspecciona la posición resultante; después mueve los elementos con `Destino`. Cada iteración incluye `Thread.sleep(1000)`. Al llegar se ejecutan las verificaciones de destino y el objetivo de la subclase. |
| evaluar | Determina éxito o fallo según la destrucción de la nave y el cumplimiento del objetivo. |
| cerrar | Si la misión fue exitosa, completa el enfriamiento pendiente del motor cuando corresponde y aplica los efectos finales de la subclase. Luego genera el informe, también cuando la misión fue rechazada o falló. |

La misión avanza por las etapas `CREADA`, `PREPARADA`, `EJECUTADA`, `EVALUADA` y `CERRADA`. Una rechazada pasa de `CREADA` a `CERRADA`. Los resultados son `EXITOSA`, `RECHAZADA` o `FALLIDA`.

**Decisiones de diseño sobre el ciclo.**

- *La pausa del turno.* La misión llama a `Thread.sleep(1000)` dentro del bucle. `ControladorPartida` ejecuta la misión en un hilo aparte para que la interfaz no se congele; al finalizar, `Main` vuelve al hilo de Swing con `SwingUtilities.invokeLater`.
- *Dónde se genera el informe.* El informe se construye en `cerrar`, después de evaluar el resultado y aplicar los efectos finales que correspondan. Se mide el balance de viaje antes de esos efectos; las gemas ganadas se registran después.

**Beneficio.** El orden del ciclo vive en un solo lugar y una misión nueva puede especializar los *hooks* de la clase base.

### Decorator — Haberes de la tripulación

**El problema.** Hay objetos que pueden tener combinaciones de características opcionales. Resolver cada combinación con herencia obliga a crear muchas clases.

**Cómo lo implementamos.** El haber combina cargo, antigüedad, origen y, en el caso del consejero, consejos registrados. `Tripulante` es la base; `Capitan`, `Consejero`, `Teniente` y `Alferez` fijan la remuneración del cargo y el porcentaje por antigüedad. `Decorator` también es un `Tripulante`, pero envuelve a otro. `Terricola` (+20), `Marciano` (+18) y `Vulcano` (+30) agregan el subsidio del origen. Cada decorador delega la identidad en el objeto envuelto y agrega su concepto al detalle. `LiquidadorHaberes` arma el recibo y el total; al liquidar reinicia el período del consejero.

| Cargo | Remuneración | Adicional por año de antigüedad | Extra |
| --- | --- | --- | --- |
| Capitán | 1000 PG | 20 % de la remuneración |  |
| Consejero | 600 PG | 5 % | 2 PG por consejo del período |
| Teniente | 400 PG | 3 % |  |
| Alférez | 200 PG | 0,5 % |  |

Ejemplos de cálculo:

- Un capitán vulcano con 10 años cobra 1000 + 1000 × 0,20 × 10 + 30 = 3030 PG.
- Un consejero terrícola con 5 años y 10 consejos cobra 600 + 600 × 0,05 × 5 + 10 × 2 + 20 = 790 PG.

**Beneficio.** Cargo y origen se pueden combinar en tiempo de ejecución mediante decoradores.

### Double Dispatch — Batallas entre naves

**El problema.** El resultado depende del tipo de dos objetos a la vez. Sin doble despacho habría que anidar condiciones para ambos tipos de nave.

**Cómo lo implementamos.** Cuando dos naves coinciden, el resultado depende del tipo de cada una:

| Nave propia \ nave enemiga | Exploradora | Carguero | Combate |
| --- | --- | --- | --- |
| Exploradora | Pacto de paz | Gana | Pierde |
| Carguero | Pierde | Pacto de paz | Gana |
| Combate | Gana | Pierde | Pacto de paz |

`Nave.batallar(enemiga)` hace el primer despacho: la nave propia llama a `enemiga.recibirBatallaDe(this)`. Java selecciona la sobrecarga correspondiente al tipo concreto de la nave propia; ese es el segundo despacho. El resultado es `GANA`, `PIERDE` o `PACTO_DE_PAZ` (`ResultadoBatalla`). `NaveEnemiga.interactuar` aplica las consecuencias: si la propia gana, destruye la nave enemiga; si pierde, destruye la propia; con pacto, ninguna cambia. La batalla no modifica recursos.

El tablero de la partida se arma con cinco naves enemigas de cada tipo, además de asteroides y estaciones de carga. `Destino` intenta mover los elementos en cada turno.

**Beneficio.** Las reglas de cada combinación quedan implementadas en los tipos concretos de nave.

### Adapter — Tablero de la cátedra como Universo

**El problema.** Una clase puede ofrecer los datos necesarios mediante una interfaz distinta de la que espera el resto del programa y no se puede modificar.

**Cómo lo implementamos.** `Tablero` ofrece sus datos con sus propios métodos. Los radares y el navegador trabajan contra `Universo`. `AdaptadorTableroUniverso` implementa `UniversoDinamico` y adapta las consultas al tablero, incluyendo la ubicación de la nave y el avance de Destino. Las pruebas también pueden usar una grilla propia que implementa la interfaz `Universo`.

### DAO y DTO — Persistencia de usuarios

**El problema.** Si las consultas a la base de datos se mezclan con las reglas y las pantallas, un cambio obliga a tocar varias capas.

**Cómo lo usamos en el repositorio.** Solo se persisten las credenciales de los usuarios.

- `UsuarioDTO` transporta id, nombre de usuario y contraseña hasheada, sin lógica de negocio.
- `UsuarioDAO` define operaciones de acceso; `UsuarioDAOSqlite` las implementa con JDBC sobre SQLite y `ConexionSQLite` abre conexiones.
- `ServicioUsuarios` aplica las reglas de registro e inicio de sesión.
- `HasheadorContrasenas` usa PBKDF2 con sal aleatoria; la contraseña no se guarda en texto plano.
- Las excepciones propias de persistencia llegan al controlador, que informa el resultado a la vista. El acceso incorrecto se informa sin distinguir entre usuario inexistente y contraseña incorrecta. La interfaz obtiene la contraseña como `char[]`.

**Beneficio.** El servicio de usuarios depende de la interfaz DAO, de modo que el acceso a datos se puede sustituir sin mezclar JDBC con la lógica de la interfaz.

### MVC — Interfaces gráficas

**El problema.** Si la ventana contiene también la lógica, no se puede cambiar una sin afectar la otra. MVC reparte responsabilidades: la Vista muestra, el Controlador recibe las acciones del usuario y el Modelo resuelve la lógica.

**Cómo lo implementamos.** El usuario actúa sobre la vista; un `ActionListener` captura el evento y delega en el controlador, que invoca al servicio o al modelo y coordina qué debe mostrar la vista. Hay cuatro conjuntos de pantallas:

| Pantallas | Vista | Controlador | Modelo |
| --- | --- | --- | --- |
| Login y registro | `LoginView` | `LoginController` | `ServicioUsuarios` |
| Tripulantes | `TripulantesView` | `TripulantesController` | `Nave`, `TripulantesCollection`, `LiquidadorHaberes` |
| Centro de comando, tablero y resultado de la misión | `MainPanel`, `TableroUniversoPanel`, `ResultadoPanel` | `ControladorNave` y `ControladorPartida` | `AsistenteDeComando`, `Nave`, misiones, universo y bitácora |
| Visor del universo paralelo | `VistaNaves` | `ControladorVisor` | `UniversoParalelo` |

**Modelos de vista.** Las pantallas del centro de comando y del resultado reciben datos preparados para mostrar. `EstadoNaveVista` contiene la telemetría, el estado del motor, la tripulación, los requisitos de misión y el historial. `ResumenMisionVista` contiene el resultado, el balance de recursos y los eventos del viaje. `ControladorNave` construye esos modelos consultando al asistente. No son DTO de persistencia: el DTO del proyecto es `UsuarioDTO`.

**Reparto de responsabilidades en la partida.**

- `ControladorNave` crea la nave elegida con la fábrica, la registra en el `CentroDeControl` y arma los modelos de vista.
- `ControladorPartida` arma el tablero y la misión solicitada, muestra el tablero y ejecuta la misión en un hilo aparte.
- `Main` conecta las pantallas con los controladores y coordina la navegación.

**Qué se actualiza durante la partida.** La vista del tablero recibe notificaciones del tablero y la bitácora. Además, `TableroUniversoPanel` usa un temporizador de Swing para refrescar la telemetría de la nave y recorrer los eventos nuevos de la bitácora.

Las vistas de login y tripulantes implementan interfaces (`ILogin`, `ITripulantes`) para conectar con sus controladores. `ConfigView` concentra la apariencia común.

**Beneficio.** La lógica de negocio puede cambiar sin concentrarla en las ventanas.

### Observer — Actualización automática de las vistas

**El problema.** El modelo cambia mientras corre la misión o mientras trabajan los hilos del universo paralelo; la vista debe enterarse sin que el modelo dependa de una ventana concreta.

**Cómo lo implementamos.** Hay dos usos del patrón:

- *Partida.* `Bitacora` y `Tablero` extienden `java.util.Observable`. `BitacoraView` y `CasillaView` observan esos objetos. Las notificaciones visuales se coordinan con el hilo de Swing.
- *Universo paralelo.* El modelo publica eventos por medio de `ObservadorVisor`, cuya operación es `actualizar(EventoVisor)`. `EventoVisor` contiene un tipo, un nombre y un texto. `VistaNaves` implementa la interfaz; el modelo conoce el observador y no la vista concreta.

**Decisiones.** Se usa `Observable` en el tablero y la bitácora, y una interfaz propia para los eventos del visor paralelo.

**Beneficio.** La lógica del modelo publica cambios sin acoplarse directamente con los paneles de la interfaz.

### Productor–consumidor con monitores — Universo paralelo

**El problema.** Varios hilos comparten un recurso limitado: las generadoras producen baterías y las naves las consumen. Si la plataforma está llena, el productor debe esperar; si está vacía, espera el consumidor.

**Cómo lo implementamos.**

| Clase | Rol |
| --- | --- |
| `PlataformaDeCarga` | Monitor: recurso compartido con capacidad de 5 baterías |
| `NaveGeneradora` | Productor: ejecuta en un hilo y repite producción y depósito |
| `NaveConsumidora` | Consumidor: ejecuta en un hilo y repite retiro y consumo |
| `UniversoParalelo` | Crea las entidades durante la ejecución, inicia sus hilos y publica eventos |
| `Bateria` | Elemento que se produce y consume |

Los métodos `depositar` y `retirar` son `synchronized`. Las condiciones de guarda se evalúan en ciclos `while`; cuando no se cumplen, el hilo ejecuta `wait()`. Cuando cambia el estado de la plataforma, se ejecuta `notifyAll()`. Los hilos son daemon y se interrumpen al cerrar el visor.

**Registro de eventos.** La plataforma informa producción, espera por falta de espacio, solicitud de batería, espera por falta de baterías, depósito y retiro. `UniversoParalelo` también informa cuando se agrega una entidad. Las actualizaciones de la vista se encolan en el hilo de Swing.

**Beneficio.** La exclusión mutua y la espera se concentran en la plataforma compartida, y se pueden agregar productores y consumidores mientras la simulación está activa.

## Interfaz gráfica

### Acceso al sistema (Login y Registro)

La ventana de acceso permite iniciar sesión con un usuario existente o registrar uno nuevo. La vista toma y muestra los datos; el controlador pide al servicio autenticar o registrar y presenta el resultado.

### Gestión de tripulantes

La pantalla muestra la lista de tripulantes y un formulario con nombre, cargo, origen, antigüedad y, para el consejero, cantidad de consejos. Permite crear, actualizar y eliminar tripulantes, deseleccionar y liquidar los haberes de la tripulación, mostrando el recibo con el detalle por concepto y el total.

### Control de la nave

Tras elegir la nave, el panel principal muestra la telemetría (combustible, energía, desgaste, gemas, estado del motor y tripulación embarcada), ofrece las tres misiones con sus requisitos, da acceso a la gestión de tripulantes y lista los informes de misiones con el contador de éxitos. Al iniciar una misión se abre el tablero y se actualizan sus casillas y la bitácora durante el recorrido. La misión corre en un hilo aparte para que la ventana no se congele. Al terminar, una pantalla muestra el resultado e informa si se puede volver al centro de comando o elegir otra nave.

El panel principal muestra nombre y cargo de cada tripulante, pero no el salario individual. El recibo con los haberes se consulta al liquidar la tripulación desde su pantalla de gestión; conviene confirmar que ese flujo satisface el requisito de consultar los salarios.

### Visor del universo paralelo

Es una ventana aparte, accesible desde la pantalla del tablero de la misión. Muestra generadoras, consumidoras, baterías de la plataforma y una consola de texto con el flujo de eventos. Dos botones permiten agregar generadoras y naves consumidoras durante la simulación. La vista recibe los eventos del modelo y actualiza los componentes en el hilo de Swing.

## Diseño por contrato y manejo de errores

Las clases incluyen contratos de invariante (INV), precondición (PRE) y postcondición (POST). Por ejemplo, las operaciones de `Recursos` protegen los límites y el asistente consulta los recursos antes de consumirlos. El proyecto usa `assert` en algunas validaciones internas y excepciones para errores que deben propagarse al controlador o a la vista.

| Mecanismo | Uso |
| --- | --- |
| `assert` | Validaciones internas documentadas en clases como `Tripulante`, servicio y acceso a datos |
| Excepciones | Errores de argumentos, estados inválidos, operaciones de nave rechazadas y fallos de persistencia |

Las aserciones solo se ejecutan con `-ea`; no deben ser el único mecanismo para proteger una operación frente a entradas del usuario. Las operaciones principales de recursos y el asistente también realizan comprobaciones explícitas y lanzan excepciones cuando corresponde.

## Verificación funcional

El repositorio contiene programas ejecutables manualmente bajo `src/main/java/pruebas`. La presencia de estas clases no demuestra por sí sola que se hayan ejecutado en esta revisión.

| Prueba | Escenario que contiene |
| --- | --- |
| `PruebaMisiones` | Integración de tablero, adaptador, fábrica y misiones encomendadas al asistente |
| `PruebaNavegador` | Saltos, Warp, aterrizaje ocupado, recursos, replanificación y ruta inexistente |
| `PruebaNavesEnemigas` | Combinaciones de batalla, consecuencias e integración con misiones |
| `PruebaRadares` | Rutas, obstáculos y elementos cercanos |
| `PruebaUniverso` | Destrucción, asteroides y estaciones |
| `PruebaBatallas` | Combinaciones de batalla y validación de nave enemiga |
| `PruebaCentroDeControl` | Registro y operación de una nave mediante el centro de control |
| `PruebaLogin` | Registro e inicio de sesión con persistencia |
| `PruebaABMTripulantes` | Alta, edición, baja y liquidación de tripulantes |

## Uso de inteligencia artificial

El uso de IA está declarado en `uso-ia.md`: se utilizó como herramienta de apoyo para orientarse, explorar alternativas de diseño y entender puntos poco claros. Las decisiones de diseño, la integración y la revisión final las tomó el equipo.