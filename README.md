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

---

# Diagrama de clases UML:

```mermaid
classDiagram
    class Nave {
        -id: int
        -nombre: String
        -tipo: String
        -masa: double
        -combustible: int
        -energia: int
        -desgaste: int
        +asignarTripulante(t: Tripulante): void
        +cargarCombustible(cant: int): void
        +consumirCombustible(cant: int): void
        +cargarEnergia(cant: int): void
        +consumirEnergia(cant: int): void
        +realizarMantenimiento(): void
    }

    class MotorWarp {
        -estadoActual: EstadoMotor
        -bitacora: Bitacora
        -getEstadoActual: EstadoMotor
        +setEstado(nuevoEstado: EstadoMotor): void
        +prepararSalto(nuevoEstado: EstadoMotor): void
        +entrarEnWarp(nuevoEstado: EstadoMotor): void
        +iniciarEnfriamiento(nuevoEstado: EstadoMotor): void
        +estarDisponible(nuevoEstado: EstadoMotor): void
    }

    class Tripulante {
        -id: int
        -nombre: String
        -cargo: String
        -origen: String
        #remuneracion: double
        #porcentajeAntiguedad: double
        -antiguedad: int
        +asignarNave(n: Nave): void
        +calcularHaberes(): double
    }

    class AsistenteComando {
        +recibirOrden(): void
        +coordinarSubsistemas(): void
    }

    class Bitacora {
        -bitacora: List~Evento~
        +agregarEvento(evento: String): void
        +mostrarBitacora(): List~Evento~
    }

    class Mision {
        -id: int
        -descripcion: String
        -destino: String
        -fechaInicio: Date
        -fechaFin: Date
        +preparar(): void
        +ejecutar(): void
        +evaluar(): void
        +cerrar(): void
        +generarInforme(): InformeMision
    }

    Nave *-- MotorWarp : contiene
    Nave *-- Bitacora : contiene
    Nave o-- Tripulante : tripulada por
    AsistenteComando ..> Mision : coordina
    AsistenteComando ..> Bitacora : edita
    AsistenteComando --> Nave : coordina
```
