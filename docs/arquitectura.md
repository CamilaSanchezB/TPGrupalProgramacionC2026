# Arquitectura simple

El proyecto se organiza en tres paquetes, uno por capa:

```text
src/main/java/ar/edu/unmdp/startrek/
├── presentacion/   # Main de consola en E1; Swing se agrega en E2
├── negocio/        # Modelos, reglas, servicios y patrones del TP
└── datos/          # Acceso a persistencia; JDBC y DAO en E2
```

## Capas

- `presentacion` recibe las acciones y muestra resultados. No implementa reglas del dominio ni modifica recursos directamente.
- `negocio` contiene Nave, tripulacion, recursos, Motor Warp, misiones, bitacora, haberes e informes. Los patrones State, Factory, Template Method y Decorator se implementan en las clases que resuelven esos comportamientos, sin paquetes adicionales.
- `datos` concentra las consultas y la persistencia. Puede permanecer sin implementaciones durante E1, porque esa entrega no requiere JDBC.

La direccion de llamadas es `presentacion -> negocio -> datos`. En E2, las vistas y controladores Swing pueden agruparse dentro de `presentacion`; los DAO y DTO JDBC, dentro de `datos`.
