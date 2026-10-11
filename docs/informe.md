# Informe del proyecto — Simulador de nave estelar (TPG 2026)

**Materia:** Programación C — Facultad de Ingeniería, UNMdP
**Entrega:** 1 (etiqueta `entrega-e1`)
**Integrantes:** (completar con nombres)

## 1. De qué trata el proyecto

Es un simulador del software embarcado de una nave estelar, hecho en Java 8 con Maven. Permite crear naves de distintos tipos (con su tripulación mínima a bordo), controlar el Motor Warp, administrar los recursos (combustible, energía y desgaste), ejecutar misiones, llevar una bitácora y liquidar los haberes de la tripulación.

## 2. Cómo está organizado

| Paquete | Qué contiene |
|---|---|
| `presentacion` | `Main`, que ejecuta por consola los escenarios de demostración |
| `negocio` | `AsistenteDeComando` (único canal de acceso a la nave) y `LiquidadorHaberes` |
| `datos` | El modelo: naves, tripulantes, Motor Warp, misiones, recursos y bitácora |

El `AsistenteDeComando` actúa de intermediario: consulta antes de operar, rechaza lo que no corresponde y deja constancia en la bitácora, sin modificar la nave cuando rechaza.

## 3. Patrones de diseño y por qué los usamos

| Patrón | Dónde | Por qué |
|---|---|---|
| **State** | `MotorWarp` y sus estados (`Disponible`, `PreparandoSalto`, `EnWarp`, `Enfriamiento`) | Lo que se puede hacer con el motor depende de su estado actual. Con State, cada estado define sus transiciones válidas y rechaza las demás, sin cadenas de `if` dentro del motor. |
| **Factory** | `NaveFactory` | Crea las tres naves (exploradora, carguero y de combate) con sus valores iniciales. Quien la usa no necesita conocer las clases concretas. |
| **Template Method** | `Mision` y sus variantes (`MisionIntercepcion`, `MisionRecoleccion`, `MisionRetorno`) | Las tres misiones comparten el mismo ciclo: preparar, ejecutar, evaluar y cerrar. El orden se define una sola vez, en el método `final` `realizar()`, y cada misión solo completa lo propio: su objetivo y sus requisitos. |
| **Decorator** | `Tripulante` (con `Capitan`, `Consejero`, `Teniente` y `Alferez`) y `Decorator` (con `Terricola`, `Marciano` y `Vulcano`) | El origen agrega un subsidio al haber. Por herencia harían falta 4 cargos × 3 orígenes = 12 clases; con Decorator son 7 y la combinación se arma al ejecutar. |

Qué garantiza cada uno:

- **State:** una transición inválida no cambia el estado del motor y queda registrada en la bitácora.
- **Factory:** es una fábrica simple. Para agregar un tipo de nave se crea una subclase de `Nave` y se suma un caso en la fábrica.
- **Template Method:** una misión no se ejecuta sin prepararse, no se cierra sin resultado e informe, y si se rechaza (por ejemplo, por recursos insuficientes) no deja cambios en la nave.
- **Decorator:** el subsidio aparece como un concepto aparte en el recibo, y un tripulante solo puede tener un origen. Por ejemplo, un capitán vulcano con 10 años cobra 3030 PG (1000 del cargo, 2000 de antigüedad y 30 de subsidio).

## 4. Otras decisiones de diseño

- **Recursos:** combustible, energía y desgaste se mantienen entre 0 y 100. Con desgaste de 80 o más la nave requiere mantenimiento, que lo lleva a 0. Una carga o un consumo inválido se rechaza y conserva el estado anterior.
- **Bitácora:** cada acontecimiento se guarda con su fecha y hora, en orden.
- **Informe de misión:** al cerrar cada misión se genera un informe con la misión, el resultado, los recursos consumidos, el estado final de la nave y las observaciones.
- **Contratos:** las precondiciones, postcondiciones e invariantes están documentados en el código y en la wiki. Los internos se verifican con `assert` (hay que ejecutar con `-ea`) y el asistente valida con excepciones.
- **Extensibilidad:** una misión nueva es una subclase nueva de `Mision`, y un origen nuevo es un decorador nuevo. En ambos casos no se modifican las clases existentes.
