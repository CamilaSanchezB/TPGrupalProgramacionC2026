# Informe del Trabajo Práctico Grupal 2026

**Software embarcado de una nave interestelar tripulada**

Programación C&#32;

Facultad de Ingeniería, Universidad Nacional de Mar del Plata&#32;

Año académico 2026, segundo cuatrimestre

Grupo G13&#32;

Integrantes:&#32;

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

Para compilar y ejecutar una prueba (el classpath incluye las dependencias que Maven copia a `target/lib`; en Linux o macOS el separador es `:`):

```
mvn compile
java -ea -cp "target/classes;target/lib/*" pruebas.PruebaMisiones
```

La opción `-ea` activa las aserciones (`assert`), con las que se verifican varios contratos internos. Sin ella la JVM las ignora. El programa no tiene una única clase principal: cada tema se ejecuta con su propia clase del paquete `pruebas`, y las ventanas de login y de tripulantes con `PruebaLogin` y `PruebaABMTripulantes`.

## Arquitectura general

El código se organiza en las tres capas del patrón MVC más una capa de persistencia:

| Paquete | Rol | Contenido principal |
| --- | --- | --- |
| `model` | Dominio: reglas y estado | `nave` (naves y batallas), `ClasesMotorWarp`, `Mision`, `Universo` (elementos, radares y navegador), `destinoytablero` (tablero y turnos provistos por la cátedra), `tripulantes`, `ClasesBitacora`, `Recursos`, `Requisitos`, `Coordenada` |
| `controller` | Coordinación | `AsistenteDeComando`, `LiquidadorHaberes`, `LoginController`, `TripulantesController` |
| `view` | Presentación | `ILogin` y `LoginView`, `ITripulantes` y `TripulantesView`, `ConfigView` (apariencia común) y `Test` |
| `persistencia` | Acceso a datos | `ServicioUsuarios`, `HasheadorContrasenas`, `dao` (`UsuarioDAO`, `UsuarioDAOSqlite`, `ConexionSQLite`), `dto` (`UsuarioDTO`) y excepciones propias |
| `pruebas` | Demostraciones ejecutables | Un `main` por tema (misiones, navegador, radares, batallas, universo, login, tripulantes) |

**Regla central:** el dominio no lee ni escribe en consola y no conoce ninguna biblioteca gráfica. Las consultas del modelo devuelven datos y es la capa de presentación la que decide cómo mostrarlos. Esto permite conectar los mismos objetos con ventanas Swing sin reescribir la lógica.

**Dos flujos principales.** Para operar la nave, quien la usa habla con el `AsistenteDeComando`, que consulta a la `Nave`, que delega en sus componentes; cada resultado, exitoso o rechazado, queda en la `Bitacora`. Para el acceso al sistema, la vista le pasa los datos al controlador, que llama al servicio, que consulta al DAO, que accede a la base SQLite.

## Modelo del dominio

### La nave

`Nave` es una clase abstracta que compone tres objetos en lugar de concentrar toda la lógica:

- un `Recursos` (combustible, energía, desgaste y gemas),
- un `MotorWarp` (el motor, con su comportamiento por estado),
- una `TripulantesCollection` (la tripulación).

La nave solo delega en ellos y mantiene sus invariantes: el motor siempre está en exactamente uno de sus cuatro estados, los recursos respetan sus límites y la tripulación nunca contiene elementos nulos. Hacia afuera, la tripulación se expone como una **vista de solo lectura**. La nave también sabe si **quedó destruida**: desgaste en 100, energía en 0, combustible en 0 o derrota en una batalla (`destruir()`); una vez destruida por una batalla, no vuelve atrás.

Hay tres tipos de nave, que difieren solo en su configuración inicial:

| Tipo | Combustible inicial | Energía inicial | Desgaste inicial |
| --- | --- | --- | --- |
| Exploradora | 60 | 80 | 0 |
| Carguero | 100 | 60 | 0 |
| Combate | 80 | 100 | 0 |

Todas tienen capacidad máxima 100 de combustible y de energía, y el Motor Warp comienza Disponible. Cada nave nace con una tripulación mínima: capitán, alférez, consejero y teniente. Los tres tipos también se diferencian en las batallas (ver Double Dispatch).

### Recursos y mantenimiento

`Recursos` es el único responsable de proteger los límites:

- Combustible y energía: entre 0 y 100.
- Desgaste: entre 0 y 100. Con 80 o más la nave requiere mantenimiento, que lo lleva a 0.
- Gemas: nunca negativas; es el recurso que se obtiene en la travesía.
- Una carga que supere la capacidad, o una operación que deje un valor fuera de rango, se rechaza sin modificar nada.

La operación `consumir(combustible, energía, desgaste)`comprueba antes de modificar, de modo que nunca queda un consumo a medias. Además de las órdenes (`cargarCombustible`, `cargarEnergia`, `consumir`, `realizarMantenimiento`), la clase ofrece consultas que no modifican nada (`puedeConsumir`, `puedeCargarCombustible`, `puedeCargarEnergia`, `requiereMantenimiento`), para que quien llama pueda preguntar antes de actuar. Para la travesía se sumaron `agregarGemas`, `recargarHastaTope` (llena sin superar el tope en vez de rechazar, como hace una estación de carga), `recibirImpacto` (descuenta energía y suma desgaste deteniéndose en los límites), `estaDestruida` y `copia()`, que permite comparar los recursos antes y después de una misión.

### Tripulación

`TripulantesCollection` encapsula la lista: se agrega, se edita y se elimina solo a través de sus métodos, se puede consultar por cargo (sin distinguir mayúsculas) y verificar si existe un tripulante por nombre. Los cargos son capitán, consejero, teniente y alférez, y los orígenes son terrícola, vulcano y marciano. Cada tripulante tiene nombre, cargo, origen, antigüedad y la remuneración que le corresponde; el cálculo de los haberes se explica en el patrón Decorator.

### Universo y navegación

- **`Coordenada`** es un par (x, y) inalterable que sabe calcular distancias y desplazarse.
- **`Universo`** es una interfaz mínima: dice si una coordenada está dentro y qué elementos hay en ella. Los radares y el navegador trabajan contra esta interfaz.
- **`ElementoDelUniverso`** es lo que puede haber en una casilla: tiene posición, sabe interactuar con la nave (y devuelve un texto para la bitácora) y dice si es un obstáculo. Hay dos: el `Asteroide`, que es un obstáculo y descuenta 20 de energía, y la `EstacionDeCarga`, que recarga 20 de combustible y 20 de energía sin superar el tope.
- **`Tablero` y `Destino`** los provee la cátedra. El tablero es la grilla con los elementos y es observable; `Destino` mueve al azar parte de los elementos en cada turno.
- **`RadarCortoAlcance`** escanea los elementos cercanos a una posición (radio 2 por defecto) y los ordena por distancia.
- **`RadarLargoAlcance`** calcula la ruta más corta entre dos coordenadas con una búsqueda en anchura, moviéndose en cuatro direcciones y rodeando los obstáculos; devuelve la lista de coordenadas o vacía si no hay camino.
- **`Navegador`** lleva la nave hacia un destino. Al iniciar el viaje calcula la ruta con el radar largo. En cada turno escanea con el radar corto, recalcula la ruta si hay un obstáculo en la siguiente casilla y decide entre un salto simple y un salto warp (el warp solo se usa si el motor está disponible, queda recorrido suficiente, hay recursos para sobrevivir y no hay elementos en el tramo). Cada paso se registra en la bitácora a través del asistente.

### Asistente de comando y bitácora

El `AsistenteDeComando` es el único canal de acceso a la nave y cada Nave puede contar con solo uno.  Su política es siempre la misma:

1. Consulta antes de operar (`puedeConsumir`, `puedeCargarCombustible`, ...).
2. Si no se puede, registra el motivo en la bitácora y lanza `OperacionRechazadaException`, sin modificar la nave.
3. Si se puede, opera y registra el resultado.

Para las órdenes del motor (`saltar()` y `saltarWarp()`) devuelve los casilleros que avanzó la nave y registra el avance y el cambio de estado del motor. También delega las misiones (`encomendarMision` ejecuta el ciclo completo y guarda el informe en el historial) y aplica la interacción de la nave con los elementos del universo (`interactuarCon`), dejando todo registrado.

Cada acontecimiento se guarda en la `Bitacora` como un `RegitroBitacora` con su fecha y hora (`LocalDateTime`) y su descripción, en orden cronológico. La bitácora no acepta eventos vacíos.

### Misiones e informe

Hay tres misiones, cada una con sus requisitos (un objeto inmutable `Requisitos`) y su condición de éxito:

| Misión | Combustible | Energía | Desgaste | Condición de éxito |
| --- | --- | --- | --- | --- |
| M-01 Intercepción y asistencia | 4 | 5 | 4 | Llega al destino y conserva los recursos de auxilio; al cerrar los entrega |
| M-02 Recolección | 4 | 5 | 4 | Llega al destino y la nave sigue operativa |
| M-03 Retorno seguro | 4 | 0 | 4 | Llega a la base sin necesitar mantenimiento; al cerrar se le hace mantenimiento |

Al cerrar, cada misión produce un `InformeMision`, un objeto inalterable que no imprime nada y solo ofrece datos: misión, resultado, bitácora, recursos consumidos, recursos finales y observaciones. Los consumos se calculan como la diferencia entre los recursos iniciales (guardados al preparar) y los finales. `GestorHistorialMisiones` conserva los informes de todas las misiones ejecutadas, en orden, y los expone como una lista no modificable.

## Patrones de diseño aplicados

Un patrón de diseño es una solución conocida a un problema que se repite. La consigna exige que cada patrón sea funcional, justificable y verificable: no alcanza con que existan clases con el nombre del patrón, sino que se logre un desacoplamiento real.&#32;

### State — Motor Warp

**El problema.** Hay objetos que se comportan distinto según la situación en que están. El motor del warp no hace lo mismo cuando está libre, preparándose, saltando o enfriándose. Sin el patrón, cada operación del motor tendría que preguntar el estado con un `if` o un `switch`, y cada estado nuevo obligaría a modificar todas esas preguntas.

**Cómo lo implementamos****.** `MotorWarp` guarda el estado en el que está (un objeto `EstadoMotorBase`) y los recursos de su nave. Hacia afuera expone solo dos operaciones, `saltar()` y `saltarWarp()`, y cada llamada equivale a un turno del motor. Al motor se la pasa al estado actual y este decide qué hacer. Hay cuatro estados, cada uno en su clase: `Disponible`, `PreparandoSalto`, `EnWarp` y `Enfriamiento`. Ambas operaciones devuelven cuántos casilleros avanzó la nave (0, 1 o 2).

| Estado | `saltar()` | `saltarWarp()` |
| --- | --- | --- |
| Disponible | Avanza 1 casillero y gasta 2 de combustible; si no alcanza, no avanza | Pasa a Preparando salto; ese turno cuenta como el primero de preparación |
| Preparando salto | No avanza: el motor está ocupado | Con 5 de energía cumple un turno de preparación y, al segundo, pasa a En warp; sin energía se queda preparando |
| En warp | No avanza | Con 5 de energía la gasta, avanza 2 casilleros y pasa a Enfriamiento; sin energía aborta y vuelve a Preparando salto |
| Enfriamiento | Cumple el turno de enfriamiento: suma 3 de desgaste y vuelve a Disponible | Igual que `saltar()` |

Cuando una operación no corresponde en el estado actual (por ejemplo, saltar mientras se prepara el warp), la nave simplemente no avanza y se devuelve 0. El asistente registra en la bitácora el avance y el cambio de estado de cada llamada (por ejemplo, `Disponible -> Preparando salto`), de modo que nada queda silencioso. El contador de turnos de la preparación vive dentro de `PreparandoSalto` y se reinicia solo al crear un estado nuevo. El cambio de estado (`setEstado`) es visible solo dentro del paquete, para que únicamente los estados puedan cambiar el estado del motor.

**Beneficio.** Si en el futuro se desea añadir un estado nuevo, el código no se llena de condicionales: solo hace falta una clase que extienda `EstadoMotorBase`.&#32;

### Factory — Creación de naves

**El problema.** Cuando una parte del programa necesita un objeto y decide con `new` qué clase concreta crear, queda atada a esas clases, si cambia la forma de crearlas, hay que modificar todos los lugares donde se crean.

**Cómo lo implementamos****.** `NaveFactory.crearNave(tipo)` recibe el nombre del tipo (`EXPLORADORA`, `CARGUERO` o `COMBATE`, sin distinguir mayúsculas) y devuelve una `Nave`. Cada tipo se crea con sus valores iniciales de combustible, energía y desgaste, y con la tripulación mínima, así que toda nave nace en un estado válido. Los constructores de `NaveExploradora`, `NaveCarguero` y `NaveCombate` son `protected`, por lo que desde otro paquete no se puede hacer `new` sobre ellas. Las pruebas y los controladores piden una nave con `crearNave("EXPLORADORA")` sin conocer la clase concreta.

**Beneficio.** El acoplamiento es menor, ya que el cliente no instancia las clases concretas sino que depende de la clase padre `Nave`, de la cual extienden.&#32;

### Template Method — Ciclo de las misiones

**El problema.** Varias actividades siguen los mismos pasos en el mismo orden y solo cambian algunos detalles. Si cada una escribe su ciclo completo, el código se repite y cualquier cambio hay que hacerlo en todas las clases.

**Cómo lo implementamos****.** `Mision.realizar()` es un método `final` (ninguna misión puede alterarlo) que fija el orden: preparar, ejecutar, evaluar y cerrar. `MisionIntercepcion` (M-01), `MisionRecoleccion` (M-02) y `MisionRetorno` (M-03) solo completan los *hooks* obligatorios `cumplirObjetivo()` (qué hace la misión) y `cumplioObjetivo()` (cuál es su condición de éxito), y los opcionales `verificarEnDestino()` y `aplicarEfectosFinales()`.

| Paso | Qué hace |
| --- | --- |
| preparar | Verifica que el motor esté disponible y que alcancen los recursos. No consume nada. Si falla, la misión es rechazada y la nave no se modifica. |
| ejecutarAccion | Consume los requisitos de despegue (una sola vez), crea el `Navegador`, calcula la ruta y repite turnos, con una pausa de 800 ms (`Thread.sleep`), hasta llegar o quedar destruida. En cada turno la nave interactúa con los elementos de la casilla en la que está y, al llegar, se ejecuta `verificarEnDestino()`. |
| evaluar | Si la nave fue destruida, la misión es fallida. Si no, depende de `cumplioObjetivo()`: exitosa o fallida. |
| cerrar | Si fue exitosa, aplica los efectos finales (`aplicarEfectosFinales()`: la entrega de recursos en M-01 y el mantenimiento en M-03) y hace el salto final. Genera el informe, también cuando la misión se rechaza o falla. |

La misión avanza por las etapas `CREADA`, `PREPARADA`, `EJECUTADA`, `EVALUADA` y `CERRADA` (una rechazada pasa de `CREADA` a `CERRADA`), modeladas con un enumerado, y su resultado es `EXITOSA`, `RECHAZADA` o `FALLIDA`.

**Beneficio.** El orden del ciclo vive en un solo lugar y una misión nueva es una subclase nueva, sin modificar las existentes.

### Decorator — Haberes de la tripulación

**El problema.** Hay objetos que pueden tener muchas combinaciones de características opcionales. Resolverlo con herencia obliga a crear una clase por cada combinación, y esa cantidad crece con cada característica nueva, lo que genera mucha cantidades de clases.

**Cómo lo implementamos****.** El haber de cada tripulante combina su cargo, su antigüedad, su origen y, en el caso del consejero, los consejos registrados. `Tripulante` es la base, y `Capitan`, `Consejero`, `Teniente` y `Alferez` fijan la remuneración del cargo y el porcentaje por antigüedad. `Decorator` también es un `Tripulante`, pero envuelve a otro y le suma un concepto: `Terricola` (+20), `Marciano` (+18) y `Vulcano` (+30) agregan el subsidio de su origen. Cada decorador delega la identidad (nombre, cargo, antigüedad) en el objeto que envuelve y agrega su `ConceptoHaber` al detalle, de modo que el aporte de cada concepto queda identificable en el recibo. `LiquidadorHaberes` arma el recibo y el total, y al liquidar reinicia el período (por ejemplo, los consejos del consejero vuelven a 0).

| Cargo | Remuneración | Adicional por año de antigüedad | Extra |
| --- | --- | --- | --- |
| Capitán | 1000 PG | 20 % de la remuneración |  |
| Consejero | 600 PG | 5 % | 2 PG por consejo del período |
| Teniente | 400 PG | 3 % |  |
| Alférez | 200 PG | 0,5 % |  |

Ejemplos verificados: u

- Un capitán vulcano con 10 años cobra 1000 + 1000 × 0,20 × 10 + 30 = 3030 PG
- Un consejero terrícola con 5 años y 10 consejos cobra 600 + 600 × 0,05 × 5 + 10 × 2 + 20 = 790 PG.

**Beneficio.** Combinar cargo y origen por herencia daría 4 × 3 = 12 clases, pero con el patrón son 7 y la combinación se arma al ejecutar. Agregar un concepto nuevo es agregar una clase.&#32;

### Double Dispatch — Batallas entre naves

**El problema.** A veces el resultado depende del tipo de dos objetos a la vez. Java elige qué método ejecutar mirando el tipo de uno solo, así que, sin ayuda, habría que anidar `if` sobre ambos tipos, y ese embrollo crece con cada tipo nuevo.

**Cómo lo implementamos****.** Cuando dos naves coinciden, el resultado depende solo del tipo de cada una:

| Nave propia \\ enemiga | Explorador | Carguero | Combate |
| --- | --- | --- | --- |
| Explorador | Pacto de paz | Gana | Pierde |
| Carguero | Pierde | Pacto de paz | Gana |
| Combate | Gana | Pierde | Pacto de paz |

`Nave.batallar(enemiga)` hace el primer despacho: la nave propia llama a `enemiga.recibirBatallaDe(this)`. Como `this` tiene un tipo concreto (por ejemplo, `NaveCarguero`), Java elige la sobrecarga `recibirBatallaDe(NaveCarguero)` de la nave enemiga, y ese es el segundo despacho, donde está la respuesta de la tabla. Devuelve `GANA`, `PIERDE` o `PACTO_DE_PAZ` (`ResultadoBatalla`) y quien llama aplica las consecuencias (`destruir()` si la nave propia pierde). Se verifica con `PruebaBatallas`, que compara las nueve combinaciones con la tabla del enunciado.

**Beneficio.** Las reglas quedan repartidas en cada tipo de nave en lugar de mezcladas en un solo lugar.&#32;

### Adapter — Tablero de la cátedra como Universo

**El problema.** Tenemos una clase que ofrece lo que necesitamos pero con una interfaz distinta de la que espera el resto del programa, y no podemos modificarla.

**Cómo lo implementamos****.** El `Tablero` lo provee la cátedra y ofrece sus datos con sus propios métodos. Los radares y el navegador trabajan contra la interfaz `Universo`. `AdaptadorTableroUniverso` implementa `Universo` y traduce cada pregunta a consultas sobre el tablero. Así el código de navegación no conoce el tablero, y las pruebas pueden usar una grilla propia (`GrillaDePrueba`) que implementa la misma interfaz.

### DAO y DTO — Persistencia de usuarios

**El problema.** Si las consultas a la base de datos se mezclan con las reglas y con las pantallas, cualquier cambio (otra base, otra regla) obliga a tocar todo. Además, conviene pasar los datos entre capas sin exponer el modelo interno.

**Cómo lo usamos en el repositorio.** Solo se persisten las credenciales de los usuarios.

- `UsuarioDTO` es el objeto de transporte: id, nombre de usuario y contraseña (siempre el hash), sin lógica de negocio.
- `UsuarioDAO` es la interfaz con las operaciones: guardar, buscar por id o por nombre, listar, actualizar y eliminar. `UsuarioDAOSqlite` las resuelve con JDBC sobre SQLite y `ConexionSQLite` solo abre conexiones. La tabla `usuarios` (id, nombre de usuario único y contraseña) se define en `init.sql`.
- `ServicioUsuarios` aplica las reglas: nombre y contraseña no vacíos y nombre no repetido al registrar; al autenticar, devuelve `null` si el usuario no existe o la contraseña no coincide.
- `HasheadorContrasenas` guarda la contraseña como hash PBKDF2 con sal aleatoria (210 000 iteraciones), nunca en texto plano.
- Las excepciones propias (`UsuarioInvalidoException`, `NombreUsuarioEnUsoException`, `PersistenciaException`) llegan al controlador, que las traduce a mensajes para la vista. Para no revelar si un usuario existe, ingresar con datos incorrectos devuelve un único mensaje. La contraseña viaja como `char[]` y se borra de memoria después de usarla.

**Beneficio.** Cada capa se puede cambiar sin tocar las otras (por ejemplo, otra base de datos reemplazando a `UsuarioDAOSqlite`).

### MVC — Interfaces gráficas

**El problema.** Si la ventana contiene también la lógica, no se puede cambiar una sin romper la otra, ni probar la lógica sin abrir ventanas. MVC reparte las responsabilidades: la Vista muestra, el Controlador recibe las acciones del usuario y el Modelo resuelve la lógica.

**Cómo lo implementamos****.** El usuario actúa sobre la vista (por ejemplo, un botón); un `ActionListener`, que es el controlador, recibe el evento, mira su comando, invoca al servicio o al modelo y le indica a la vista qué mostrar. Hay dos pantallas completas:

- Login y Registro: `LoginView` (vista), `LoginController` (controlador) y `ServicioUsuarios` (modelo).
- Tripulantes: `TripulantesView`, `TripulantesController`, y como modelo la `Nave`, `TripulantesCollection` y `LiquidadorHaberes`.

Cada vista implementa una interfaz (`ILogin`, `ITripulantes`), de modo que el controlador depende de la interfaz y no de la ventana concreta. Los comandos de los botones son constantes de esas interfaces. `ConfigView` concentra la apariencia común.

**Beneficio.** Se puede cambiar la pantalla sin tocar la lógica y probar el controlador con una vista falsa.

### Observer (falta)

### Productor–consumidor con monitores (falta)

## Interfaz gráfica

### Acceso al sistema (Login y Registro)

La ventana de acceso permite ingresar con un usuario existente o registrar uno nuevo. La vista solo toma y muestra datos, el controlador pide al servicio autenticar o registrar y muestra el resultado: un aviso si el registro fue correcto, o un mensaje de error si faltan datos, el nombre ya está en uso o la base no responde.

### Gestión de tripulantes

La pantalla muestra la lista de tripulantes de la nave y un formulario con nombre, cargo, origen, antigüedad y, para el consejero, la cantidad de consejos. Permite crear, actualizar y eliminar tripulantes, deseleccionar y liquidar los haberes de toda la tripulación, mostrando el recibo con el detalle por concepto y el total.

### Control de la nave (falta)

### Visor del universo paralelo (falta)

## Diseño por contrato y manejo de errores

Cada clase documenta su **invariante (INV)**, y cada método sus **precondiciones (PRE)** y **postcondiciones (POST)**. Por ejemplo, `Recursos.consumir` exige consumos no negativos y que alcancen los recursos, y garantiza que el consumo se efectúa completo. Se usan dos mecanismos, con criterios distintos:

| Mecanismo | Se usa para | Ejemplos |
| --- | --- | --- |
| `assert` | Violaciones de contrato, es decir, errores de programación de quien llama | Límites de `Recursos`, invariante del motor, orden de etapas de `Mision`, datos de `Tripulante`, eventos vacíos en `Bitacora` |
| Excepciones | Situaciones que deben llegar al coordinador o a la vista | `OperacionRechazadaException` del asistente; `IllegalArgumentException` en `Requisitos`, `InformeMision`, elementos del universo y radares; `IllegalStateException` en el `Navegador`; excepciones propias de persistencia que el controlador traduce a mensajes |

El criterio sigue la consigna: las precondiciones se comprueban cerca de las operaciones que protegen y las excepciones se reservan para lo que debe propagarse. Una falta de recursos no es un error de programación sino una decisión normal, por eso el asistente consulta antes y rechaza con registro. Como las aserciones solo actúan con `-ea`, las validaciones del asistente se hacen con excepciones y funcionan con o sin esa opción.

## Verificación funcional

La verificación se ejecuta con las clases del paquete `pruebas` y con `view.Test`; cada tema tiene su propio `main`:

| Prueba | Qué demuestra |
| --- | --- |
| `PruebaMisiones` | Integración: tablero de la cátedra con su adaptador, nave creada con la fábrica y misiones encomendadas a través del asistente |
| `PruebaNavegador` | Secuencia de warp y enfriamiento, que un recorrido corto no usa warp y que una estación en el camino no se saltea |
| `PruebaRadares` | Radar de largo alcance (rutas y obstáculos) y de corto alcance (elementos a distintas distancias) |
| `PruebaUniverso` | Nave destruida, choque con asteroides y recarga en estaciones |
| `PruebaBatallas` | Las combinaciones de la tabla de batallas y el rechazo de una nave enemiga nula |
| `PruebaLogin` | Registro e ingreso de usuarios con persistencia (ventana de acceso) |
| `PruebaABMTripulantes` | Alta, edición, baja y liquidación de tripulantes (ventana) |
| `view.Test` | Los escenarios A a D de la Ficha de Inicio y la liquidación de haberes |

## Uso de inteligencia artificial

El uso de IA está declarado en `docs/uso-ia.md`: se utilizó como herramienta de apoyo para orientarse, explorar alternativas de diseño y entender puntos poco claros. Las decisiones de diseño, la integración y la revisión final las tomó el equipo.
