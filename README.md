# TP Grupal Programación C2026

Simulador de una nave estelar en Java 8. **La documentación completa está en la
[wiki del proyecto](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Home)**: arquitectura, contratos (PRE/POST/INV),
diagramas UML especializados de los patrones State, Template Method, Decorator y
Factory, escenarios de prueba e instrucciones de compilación.

| | |
|---|---|
| [Arquitectura](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Arquitectura) | Capas, paquetes y dependencias |
| [Modelo de dominio](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Modelo-de-Dominio) | Diagrama de clases completo |
| [Contratos](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Contratos) | PRE / POST / INV por clase |
| [State](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Patron-State-MotorWarp) | Motor Warp |
| [Template Method](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Patron-Template-Method-Mision) | Misiones |
| [Decorator](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Patron-Decorator-Tripulantes) | Origen del tripulante |
| [Factory](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Patron-Factory-Naves) | Creación de naves |
| [Compilar y ejecutar](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Compilar-y-Ejecutar) | Build y opción `-ea` |

## Patrones aplicados

| Patrón | Dónde se aplica |
|---|---|
| State | Motor Warp (Disponible, Preparando salto, En warp, Enfriamiento) |
| Factory | Creación de naves exploradora, carguero y de combate |
| Template Method | Ciclo de las misiones: preparar → ejecutar → evaluar → cerrar |
| Decorator | Liquidación de haberes de la tripulación (cargo, antigüedad y origen) |

## Requisitos de ejecución

| Herramienta | Versión |
|---|---|
| JDK | 8 o superior (el `pom.xml` compila con `maven.compiler.release` = 8) |
| Maven | 3.x |

Codificación del proyecto: UTF-8. No requiere base de datos ni configuración local adicional: la Entrega 1 no usa persistencia.

## Compilar

```bash
mvn compile
```

## Ejecutar

El programa principal es `presentacion.Main`. Ejecuta los cuatro escenarios de la Ficha de Inicio y una demostración de liquidación de haberes.

**Forma recomendada** (permite activar las aserciones con `-ea`):

```bash
mvn compile
java -ea -cp target/classes presentacion.Main
```

Alternativa con Maven (requiere conexión a internet la primera vez para bajar el plugin `exec`):

```bash
mvn exec:java -Dexec.mainClass=presentacion.Main
```

En Windows / PowerShell también se puede usar el script incluido, que compila y ejecuta:

```powershell
.\compileExec.ps1
```

### Aserciones (`-ea`)

Varias precondiciones e invariantes internos se verifican con `assert`, y la JVM las ignora por defecto. Para verificar los contratos hay que ejecutar con `-ea`, como en el comando recomendado de arriba. Las validaciones del Asistente de Comando (rechazo de operaciones inválidas) funcionan con o sin `-ea`.

## Verificación

Las evidencias funcionales se obtienen ejecutando `presentacion.Main`, que muestra por consola, en este orden:

| Escenario | Qué demuestra | Resultado esperado |
|---|---|---|
| **A — Ejecución correcta** | Ciclo completo de las misiones M-01, M-02 y M-03 con Template Method, recursos finales, informe y bitácora | Las tres misiones terminan `EXITOSA` y en etapa `CERRADA` |
| **B — Recursos insuficientes** | Una misión sobre una nave sin recursos | Misión `RECHAZADA`, recursos sin cambios y motivo en la bitácora |
| **C — Motor Warp** | Secuencias válidas del estado del motor y una transición inválida | La transición válida se completa; la inválida se rechaza y queda registrada en la bitácora |
| **D — Contrato inválido** | Cargas de combustible que exceden la capacidad o son negativas | La carga se rechaza, el combustible conserva su valor anterior y el rechazo queda en la bitácora |
| **Haberes** | Liquidación con composición de decoradores (cargo, antigüedad y origen) | Detalle por concepto y total a pagar |

Los detalles de cada escenario y su resultado esperado están en la wiki: [Escenarios de prueba](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Escenarios-de-Prueba).

## Estructura del proyecto

```text
src/main/java/
├── presentacion/   Main de demostración (único lugar que usa consola para ejecutar)
├── negocio/        AsistenteDeComando, LiquidadorHaberes
└── datos/          Nave y tipos, NaveFactory, Recursos, MotorWarp (State),
                    Mision y variantes (Template Method), Bitacora,
                    tripulantes (Decorator)
docs/               Diagrama de clases, arquitectura y uso de IA
```

## Documentación

| Documento | Contenido |
|---|---|
| [Diagrama de clases](docs/uml_entrega1.md) | Diagrama UML del proyecto (también en `docs/UML_v1.png` y `docs/TPGrupalProgramacionC2026.drawio`) |
| [Arquitectura](docs/arquitectura.md) | Capas y dependencias |
| [Uso de IA](docs/uso-ia.md) | Registro del uso de inteligencia artificial |
| [Wiki del proyecto](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Home) | Documentación ampliada |

Páginas de la wiki:

| | |
|---|---|
| [Arquitectura](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Arquitectura) | Capas, paquetes y dependencias |
| [Modelo de dominio](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Modelo-de-Dominio) | Diagrama de clases completo |
| [Contratos](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Contratos) | Precondiciones, postcondiciones e invariantes por clase |
| [State](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Patron-State-MotorWarp) | Motor Warp |
| [Template Method](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Patron-Template-Method-Mision) | Misiones |
| [Decorator](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Patron-Decorator-Tripulantes) | Tripulantes y haberes |
| [Factory](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Patron-Factory-Naves) | Creación de naves |
| [Compilar y ejecutar](https://github.com/CamilaSanchezB/TPGrupalProgramacionC2026/wiki/Compilar-y-Ejecutar) | Build y aserciones |

## Entrega

La versión presentada para evaluación es la señalada por la etiqueta Git **`entrega-e1`**:

```bash
git checkout entrega-e1
```
