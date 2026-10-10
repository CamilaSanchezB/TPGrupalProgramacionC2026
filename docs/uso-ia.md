# Uso de inteligencia artificial
## Declaración general

| | |
|---|---|
| **Herramienta utilizada** | Claude |
| **Entrega** | Entrega 1 |
| **Responsabilidad** | El equipo conserva la responsabilidad total sobre todo lo incorporado al repositorio. Todos los integrantes pueden explicar el código y la documentación entregados. |

La IA se utilizó como **herramienta de apoyo para orientarnos en el proyecto, consultar ideas, proponer soluciones, debatir si eran correctas y entender cuestiones que nos quedaron poco claras de la lectura de la documentación**. Las decisiones de diseño, la integración y la revisión final las tomó el equipo.

---

## Usos registrados

### 1. Orientación en el proyecto

- **Herramienta:** Claude.
- **Propósito de la consulta:** entender qué pedía el trabajo en su conjunto y cómo se relacionaban los documentos de la cátedra (Guía general, Enunciado de la Entrega 1, Ficha de Inicio, Tabla de recursos, Reglas operativas, Rúbrica y la Aclaración del TP). También ubicar qué estaba hecho, qué faltaba y qué correspondía a cada requerimiento (E1-01 a E1-10).
- **Componente o documento afectado:** planificación y reparto de tareas del grupo; documentación general.
- **Resultado aprovechado:** una visión ordenada de los requerimientos, los patrones obligatorios (State, Factory, Template Method y Decorator) y los criterios de la rúbrica, que usamos para priorizar el trabajo.
- **Revisión o modificación realizada por el equipo:** contrastamos esa lectura con los documentos originales y con las comunicaciones de la cátedra. Ante cualquier diferencia prevaleció el enunciado.
- **Forma de verificación:** relectura de los documentos de la cátedra y discusión entre los integrantes.

### 2. Consulta de ideas de diseño

- **Herramienta:** Claude.
- **Propósito de la consulta:** explorar alternativas de diseño para las piezas del sistema (ciclo de las misiones con Template Method, relación entre misión y asistente, manejo de recursos y mantenimiento, bitácora e informe).
- **Componente o documento afectado:** `Mision` y sus variantes, `InformeMision`, `Recursos`, `AsistenteDeComando` y `Bitacora`.
- **Resultado aprovechado:** ideas y criterios de diseño (por ejemplo, separar el orden fijo del ciclo de los pasos propios de cada misión, y que el dominio no dependa de la consola). Las ideas se tomaron como punto de partida para la discusión.
- **Revisión o modificación realizada por el equipo:** cada integrante escribió e integró su parte en el repositorio. El equipo adaptó, descartó o reescribió lo que no se ajustaba a sus decisiones o a los enunciados.
- **Forma de verificación:** compilación y ejecución del `Main` con los escenarios de la Ficha de Inicio, y revisión de los cambios entre integrantes.

### 3. Propuesta de soluciones

- **Herramienta:** Claude.
- **Propósito de la consulta:** obtener propuestas concretas para problemas puntuales del diseño y del código, y comparar enfoques.
- **Componente o documento afectado:** los componentes mencionados arriba y los escenarios de demostración.
- **Resultado aprovechado:** propuestas de estructura que el equipo evaluó antes de adoptar algo.
- **Revisión o modificación realizada por el equipo:** ninguna propuesta se incorporó sin ser leída, comprendida y adaptada por quien la integró. No se aceptó código que el equipo no pudiera explicar.
- **Forma de verificación:** ejecución de los escenarios A, B, C y D, y comprobación de los contratos (precondiciones, invariantes y casos de rechazo).

### 4. Debate sobre la corrección de las soluciones

- **Herramienta:** Claude.
- **Propósito de la consulta:** discutir si las soluciones propuestas, tanto por la IA como por el propio equipo, eran correctas, si respetaban los enunciados y los principios de diseño, y qué problemas tenían.
- **Componente o documento afectado:** diseño general, contratos de clases y métodos, y aplicación de los patrones.
- **Resultado aprovechado:** una revisión crítica que ayudó a detectar inconsistencias y a justificar mejor las decisiones. Cuando el equipo no estuvo de acuerdo con una observación, la defendió o la descartó con argumentos.
- **Revisión o modificación realizada por el equipo:** las observaciones se contrastaron con el enunciado, la Ficha de Inicio y la rúbrica antes de aplicar cualquier cambio.
- **Forma de verificación:** comparación con los documentos de la cátedra, ejecución del programa y revisión cruzada entre integrantes.

### 5. Comprensión de cuestiones poco claras

- **Herramienta:** Claude.
- **Propósito de la consulta:** entender conceptos y puntos de la consigna que quedaron poco claros con la lectura (por ejemplo, el funcionamiento de los patrones, el alcance de la Entrega 1 frente a la Entrega 2, o el significado de algunos requisitos de la Aclaración).
- **Componente o documento afectado:** comprensión del problema y preparación de la defensa.
- **Resultado aprovechado:** explicaciones que nos ayudaron a leer mejor los enunciados.
- **Revisión o modificación realizada por el equipo:** lo explicado se contrastó con el material de la materia y con la documentación oficial.
- **Forma de verificación:** consulta de las fuentes oficiales y puesta en común dentro del grupo.

---

## Lo que no se hizo

- No se presentó código ni documentación que el equipo no pueda explicar.
- No se usó IA para fabricar evidencias, ejecuciones ni resultados: las evidencias se obtienen ejecutando el programa del repositorio.
- No se ocultaron usos relevantes de IA.
- No se usará IA durante la defensa.
- No se copiaron soluciones de otros grupos ni se incorporó código externo sin indicar su fuente.
