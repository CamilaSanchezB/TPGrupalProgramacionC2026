# TP Grupal Programación C2026

Aplicación Java de simulación de una nave estelar. Incluye inicio de sesión,
selección de nave, gestión de tripulantes, navegación por un tablero y
ejecución de misiones.
**La documentación completa está en la [wiki del proyecto](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Home)**: arquitectura, contratos (PRE/POST/INV), diagramas UML especializados de los patrones State, Template Method, Decorator y
Factory, escenarios de prueba e instrucciones de compilación.

## Patrones aplicados

| Patrón          | Uso                                                     |
| --------------- | ------------------------------------------------------- |
| State           | Estados del motor Warp                                  |
| Factory         | Creación de naves exploradora, carguera y de combate    |
| Template Method | Preparación, ejecución, evaluación y cierre de misiones |
| Decorator       | Liquidación de haberes de tripulantes                   |
| MVC             | Separación entre vistas Swing, controladores y modelo   |

## Requisitos

- JDK 8 o superior
- Maven 3
- Dependencias administradas por Maven

El proyecto usa SQLite para el login y Swing para la interfaz gráfica.

## Compilar

```powershell
mvn compile
```

## Ejecutar la aplicación

El punto de entrada es `view.Main`.

```powershell
java -ea -cp "target/classes;target/lib/*" view.Main
```

También puede usarse el script incluido:

```powershell
.\compileExec.ps1
```

## Flujo de uso

1. Iniciar sesión.
2. Seleccionar una nave.
3. Consultar o administrar tripulantes.
4. Iniciar una partida.
5. Observar el avance por el tablero hasta llegar al objetivo o finalizar la nave.

`CrearNavePanel` no forma parte del flujo actual: no se instancia ni se registra
en `view.Main` ni se agrega a `view.Ventana`. La pantalla que permite elegir nave
es `view.SelectorNavePanel`.

`view.Ventana` usa `CardLayout` para cambiar entre las pantallas registradas
mediante `agregarPantalla` y mostrarlas mediante `mostrar`.

## Pruebas manuales

Las clases dentro de `src/main/java/pruebas` contienen métodos `main` ejecutables
después de compilar:

```powershell
java -ea -cp "target/classes;target/lib/*" pruebas.PruebaUniverso
java -ea -cp "target/classes;target/lib/*" pruebas.PruebaNavegador
java -ea -cp "target/classes;target/lib/*" pruebas.PruebaMisiones
java -ea -cp "target/classes;target/lib/*" pruebas.PruebaBatallas
java -ea -cp "target/classes;target/lib/*" pruebas.PruebaRadares
java -ea -cp "target/classes;target/lib/*" pruebas.PruebaABMTripulantes
java -ea -cp "target/classes;target/lib/*" pruebas.PruebaLogin
```

## Estructura

```text
src/main/java/
├── view/           Interfaz Swing
├── controller/     Conecta vistas y modelo
├── model/          Nave, misiones, tripulantes y tablero
├── persistencia/   Usuarios y acceso SQLite
├── pruebas/        Escenarios ejecutables manualmente
└── exceptions/     Excepciones del dominio
```

## Documentación

- [Diagrama de clases](docs/uml_entrega1.md)
- [Arquitectura](docs/arquitectura.md)
- [Uso de IA](docs/uso-ia.md)
- [Wiki del proyecto](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Home)
