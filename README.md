# TP Grupal Programación C2026

Aplicación Java de simulación de una nave estelar. Incluye inicio de sesión y
registro de usuarios, selección de nave, gestión de tripulantes, navegación por
un tablero con misiones, y un visor de un universo paralelo donde generadoras de
energía y naves compiten por una plataforma de carga.

**La documentación completa está en la [wiki del proyecto](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Home)** y en el [informe](docs/Informe.md): arquitectura, contratos (PRE/POST/INV), diagramas UML de los patrones, escenarios de prueba e instrucciones de compilación.

## Patrones aplicados

| Patrón | Uso |
| --- | --- |
| State | Estados del motor Warp (disponible, preparando salto, en warp, enfriamiento) |
| Template Method | Ciclo de vida de las misiones: preparar, ejecutar, evaluar y cerrar |
| Factory | Creación de naves exploradora, carguera y de combate, y de tripulantes |
| Decorator | Liquidación de haberes de los tripulantes |
| Double Dispatch | Batallas entre la nave propia y las naves enemigas |
| Observer | Actualización automática de la bitácora, el tablero y el visor del universo paralelo |
| Productor–Consumidor (monitores) | Universo paralelo: generadoras, naves y plataforma de carga con `synchronized`, `wait()` y `notifyAll()` |
| DAO + DTO | Persistencia de usuarios (login y registro) |
| MVC | Vistas Swing, controladores y modelo separados; las vistas de la partida reciben modelos de vista ya armados |

## Requisitos

- JDK 8 o superior
- Maven 3
- Dependencias administradas por Maven (SQLite JDBC, FlatLaf y MigLayout)

## Compilar

```powershell
mvn compile
```

## Ejecutar la aplicación

El punto de entrada es `view.Main`. Hay que ejecutarlo **desde la raíz del
proyecto**, porque la base de usuarios se busca en una ruta relativa
(`src/main/resources/usuarios.db`).

```powershell
java -ea -cp "target/classes;target/lib/*" view.Main
```

También puede usarse el script incluido:

```powershell
.\compileExec.ps1
```

La opción `-ea` activa las aserciones (`assert`), con las que se verifican algunos
contratos internos. Sin ella la JVM las ignora; las validaciones principales de
`Recursos` y `Bitacora` usan excepciones y funcionan con o sin esa opción.

## Flujo de uso

1. Registrarse o iniciar sesión. Solo se persisten usuario y contraseña (esta
   última con hash y salt).
2. Seleccionar una nave.
3. Consultar o administrar los tripulantes y sus salarios.
4. En el centro de comando, ver el estado de la nave y su historial de misiones
   e iniciar una misión:
   - **M-01 Intercepción y asistencia:** alcanzar a una nave aliada y transferirle recursos.
   - **M-02 Recolección:** llegar a una coordenada remota y extraer gemas.
   - **M-03 Retorno a punto seguro:** volver a la base para recargar y reparar la nave.
5. Observar el avance por el tablero, con el movimiento, la bitácora de eventos y
   los imprevistos del camino (asteroides, estaciones de carga, otras naves).
   Al terminar se muestra el informe final con el balance de recursos.
6. Desde la pantalla de la partida se abre el visor del universo paralelo, con
   botones para agregar naves consumidoras y generadoras de energía en vivo.

`view.Ventana` usa `CardLayout` para cambiar entre las pantallas registradas
mediante `agregarPantalla` y mostrarlas mediante `mostrar`. `view.Main` solo crea
las pantallas, las conecta con los controladores y navega entre ellas.

## Pruebas manuales

Las clases dentro de `src/main/java/pruebas` contienen métodos `main` ejecutables
después de compilar. Para correr cualquiera:

```powershell
java -ea -cp "target/classes;target/lib/*" pruebas.<NombreDeLaClase>
```

| Clase | Qué prueba |
| --- | --- |
| `PruebaCentroDeControl` | Modelo sin interfaz: alta de la nave en el centro de control y ejecución de una misión por consola |
| `PruebaMisiones` | Ciclo de vida de las misiones |
| `PruebaNavegador` | Navegación y ruta de la nave |
| `PruebaRadares` | Radares de corto y largo alcance |
| `PruebaUniverso` | Universo y tablero |
| `PruebaBatallas` | Resolución de batallas (Double Dispatch) |
| `PruebaNavesEnemigas` | Naves enemigas en el tablero |
| `PruebaABMTripulantes` | Alta, baja y modificación de tripulantes |
| `PruebaLogin` | Registro e inicio de sesión |

## Estructura

```text
src/main/java/
├── view/           Interfaz Swing (pantallas, visor y arranque)
├── controller/     Controladores, asistente de comandos y modelos de vista
├── model/          Nave, motor Warp, misiones, universo, tripulantes,
│                   bitácora, centro de control y universo paralelo
├── persistencia/   Usuarios, DAO, DTO y acceso SQLite
├── pruebas/        Escenarios ejecutables manualmente
└── exceptions/     Excepciones del dominio
```

## Documentación

- [Informe](docs/Informe.md)
- [Arquitectura](docs/arquitectura.md)
- [Diagrama de clases (entrega 1)](docs/uml_entrega1.md)
- [Misiones y navegación](docs/uml_dominio_misiones_navegacion.md)
- [Persistencia, interfaz y concurrencia](docs/uml_persistencia_interfaz_concurrencia.md)
- [Tripulación y haberes](docs/uml_tripulacion_haberes.md)
- [Uso de IA](docs/uso-ia.md)
- [Wiki del proyecto](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Home)