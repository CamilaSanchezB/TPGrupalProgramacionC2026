package model.Mision;

import controller.AsistenteDeComando;
import model.Recursos;
import model.Requisitos;
import model.Coordenada;
import model.Universo.Universo;
import model.Universo.Navegador;
import model.Universo.ResultadoAvance;
import model.Universo.ElementoDelUniverso;
/**
 * Misión genérica de la nave. Aplica Template Method: realizar() fija el orden
 * preparar -> ejecutarAccion -> evaluar -> cerrar y las subclases solo completan los
 * pasos propios (cumplirObjetivo y cumplioObjetivo).
 * INV: nombre, asistenteDeComando, requisitos, acciones y observaciones nunca son null
 * INV: etapa nunca es null y avanza solo en el orden
 *      CREADA -> PREPARADA -> EJECUTADA -> EVALUADA -> CERRADA
 *      (si la misión se rechaza, pasa de CREADA directamente a CERRADA)
 * INV: recursosIniciales != null desde que etapa >= PREPARADA; si fue RECHAZADA queda en null
 * INV: resultado != null cuando etapa es EVALUADA o CERRADA
 *      (una misión rechazada tiene resultado RECHAZADA aunque su etapa todavía sea CREADA)
 * INV: informe != null si y solo si etapa == CERRADA
 * INV: una misión RECHAZADA no modifica la nave; los requisitos se consumen una sola vez
 */

public abstract class Mision{
    private final String nombre;
    private final AsistenteDeComando asistenteDeComando;
    private final Universo universo;
    private final Coordenada origen;
    private final Coordenada destino;
    private final Requisitos requisitos;

    private EtapaMision etapa;
    private ResultadoMision resultado;
    private Recursos recursosIniciales;
    private String observaciones;
    private InformeMision informe;
    private Navegador navegador;

    /**
     * @param nombre Nombre de la misión
     * @param asistenteDeComando Asistente encomendado
     * @param universo Entorno del mapa
     * @param origen Coordenada inicial
     * @param destino Coordenada final
     * @param requisitos Requisitos de la misión
     */

    protected Mision(String nombre, AsistenteDeComando asistenteDeComando, Universo universo, Coordenada origen, Coordenada destino, Requisitos requisitos) {
        assert nombre != null && !nombre.trim().isEmpty() : "El nombre no puede ser nulo ni vacío";
        assert asistenteDeComando != null : "El asistente de comando no puede ser nulo";
        assert universo != null : "El universo no puede ser nulo";
        assert origen != null && destino != null : "Las coordenadas no pueden ser nulas";
        assert requisitos != null : "Los requisitos no pueden ser nulos";

        this.nombre = nombre;
        this.asistenteDeComando = asistenteDeComando;
        this.universo = universo;
        this.origen = origen;
        this.destino = destino;
        this.requisitos = requisitos;
        this.etapa = EtapaMision.CREADA;
        this.observaciones = "";
    }

    /**
     * @param motivo Razón del rechazo de la mision
     * @return siempre false (para que preparar() pueda hacer "return rechazar(...)")
     * PRE: motivo != null y no vacío (el asistente no acepta eventos vacíos)
     * POST: resultado == RECHAZADA, observaciones == motivo, el rechazo queda registrado
     *       en la bitácora y la nave no fue modificada
     */

    private boolean rechazar(String motivo){
        assert motivo != null && !motivo.trim().isEmpty() : "El motivo del rechazo no puede ser nulo ni vacio";
        resultado = ResultadoMision.RECHAZADA;
        observaciones=motivo;
        asistenteDeComando.registrar("Mision rechazada: " + motivo); //El asistente la guarda en la bitacora.
        return false;
    }

    /**
     * Template Method: fija el orden del ciclo y las subclases no pueden alterarlo.
     * PRE: etapa == CREADA (una misión se realiza una sola vez)
     * POST: etapa == CERRADA, resultado != null e informe != null
     *       (también cuando la misión es rechazada o falla)
     *       si resultado == RECHAZADA, la nave no fue modificada
     */

    public final void realizar(){
        if(preparar()) {
            ejecutarAccion();
            evaluar();
        }
        cerrar();
    }

    /**
     * Verifica que la misión pueda realizarse. No consume ningún recurso.
     * @return true si la misión quedó preparada, false si fue rechazada
     * PRE: etapa == CREADA
     * POST: si devuelve true, etapa == PREPARADA, recursosIniciales != null y
     *       la nave no fue modificada;
     *       si devuelve false, resultado == RECHAZADA, recursosIniciales == null,
     *       la nave no fue modificada y el motivo quedó en observaciones y en la bitácora
     */

    private boolean preparar(){
        assert etapa == EtapaMision.CREADA : "La mision ya fue preparada";

        //La nave esta disponible?(el motor debe estar disponible)
        //Crear en asistente de comando una funcion que diga si la nave esta disponible!!
        if(!asistenteDeComando.estaDisponible()){
            return rechazar("La nave no esta disponible");
        }

        //Alcanzan los recursos?
        if(!asistenteDeComando.puedeConsumir(requisitos.getCombustible(), requisitos.getEnergia(), requisitos.getDesgaste())){
            return rechazar("Recursos Insuficientes");
        }

        //Guardamos los recursos iniciales, nos servira para obtener el informe final.
        recursosIniciales= new Recursos(
                asistenteDeComando.getCombustible(),
                asistenteDeComando.getEnergia(),
                asistenteDeComando.getDesgaste()
        );

        //Guardamos cronologicamente como se fue desarollando la mision.
        asistenteDeComando.registrar("Mision preparada cronologicamente hacia destino: "+destino);
        //Ahora la mision esta PREPARADA
        this.etapa= EtapaMision.PREPARADA;
        return true;
    }

    /**
     * Fase 2: Consume recursos de despegue y ejecuta el bucle de travesía por turnos.
     * PRE: etapa == PREPARADA
     * POST: recursos de despegue consumidos, Navegador avanzó turno a turno interactuando
     *       con elementos e invocando verificación en destino al arribar; etapa == EJECUTADA.
     */

    private void ejecutarAccion() {
        assert etapa == EtapaMision.PREPARADA : "No se puede ejecutar una mision que no fue preparada";
        //Consumimos de la nave considerando los requisitos de la mision.
        asistenteDeComando.consumir(
                requisitos.getCombustible(),
                requisitos.getEnergia(),
                requisitos.getDesgaste()
        );
        asistenteDeComando.registrar("Recursos consumidos");
        // Inicializamos el Navegador con el origen y destino
        this.navegador = new Navegador(asistenteDeComando, universo, origen);
        boolean viajeIniciado = navegador.iniciarViaje(destino);

        if (!viajeIniciado) {
            asistenteDeComando.registrar("Sin ruta calculable hacia " + destino);
        } else {
            // Bucle de travesía turno por turno con pausa de 800ms
            while (!asistenteDeComando.estaDestruida() && !navegador.haLlegado()) {
                ResultadoAvance avance = navegador.avanzarUnTurno();
                if (avance == ResultadoAvance.SIN_RUTA) {
                    asistenteDeComando.registrar("Sin ruta disponible en este turno");
                }
                // Interacción con obstáculos o estaciones en la casilla actual
                Coordenada posActual = navegador.getPosicion();
                for (ElementoDelUniverso elem : universo.elementosEn(posActual)) {
                    asistenteDeComando.interactuarCon(elem);
                }
                // Pausa de 800ms para la visualización del avance en vivo
                try {
                    Thread.sleep(800);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    asistenteDeComando.registrar("Ejecución de travesía interrumpida");
                    break;
                }
            }
        }

        // Verificación hook en destino si se llegó correctamente
        if (navegador.haLlegado() && !asistenteDeComando.estaDestruida()) {
            verificarEnDestino();
        }

        cumplirObjetivo();
        this.etapa = EtapaMision.EJECUTADA;
    }


    /**
     * Hook opcional: permite a las subclases verificar condiciones extra al arribar a destino.
     * PRE: etapa == PREPARADA (invocado al finalizar el bucle en ejecutarAccion())
     * POST: no modifica la nave en la clase base.
     */
    protected void verificarEnDestino() {}

    /**
     * Hook: la parte propia de cada misión (asistir, recolectar, regresar).
     * PRE: etapa == PREPARADA (se llama dentro de ejecutarAccion())
     * POST: la subclase registró su acción con registrarAccion() y su estado interno
     *       refleja que el objetivo se intentó cumplir; no modifica la nave directamente
     */
    protected abstract void cumplirObjetivo();

    /**
     * Decide si la misión fue exitosa o falló.
     * PRE: etapa == EJECUTADA
     * POST: resultado == EXITOSA o FALLIDA (nunca RECHAZADA), la evaluación queda
     *       registrada en la bitácora y etapa == EVALUADA
     */
    private void evaluar() {
        assert etapa == EtapaMision.EJECUTADA : "No se puede evaluar una misión no ejecutada";

        if (asistenteDeComando.estaDestruida()) {
            resultado = ResultadoMision.FALLIDA;
            observaciones = "Nave destruida durante la travesía";
        } else if (cumplioObjetivo()) {
            resultado = ResultadoMision.EXITOSA;
        } else {
            resultado = ResultadoMision.FALLIDA;
        }

        asistenteDeComando.registrar("Misión evaluada: " + resultado);
        etapa = EtapaMision.EVALUADA;
    }

    /**
     * Hook abstracto: condición de éxito propia de cada variante de misión.
     * PRE: etapa == EJECUTADA
     * POST: devuelve true si y solo si se cumplió el objetivo; es una consulta inmutable.
     */
    protected abstract boolean cumplioObjetivo();

    /**
     * Cierra la misión, impacta efectos finales, ejecuta el salto final y genera el informe inmutable.
     * PRE: resultado != null (fijado por evaluar() o rechazar())
     * POST: informe != null, etapa == CERRADA.
     */
    private void cerrar() {
        assert resultado != null : "No se puede cerrar una mision sin resultado";

        if (resultado == ResultadoMision.EXITOSA) {
            aplicarEfectosFinales();

            try {
                int casilleros = asistenteDeComando.saltar();
                asistenteDeComando.registrar("Salto final realizado: avanza " + casilleros + " casillero(s).");
            } catch (RuntimeException e) {
                resultado = ResultadoMision.FALLIDA;
                observaciones = "No se pudo realizar el salto final";
                asistenteDeComando.registrar("Salto fallido en " + nombre);
            }
        }

        int combFinal = asistenteDeComando.getCombustible();
        int enerFinal = asistenteDeComando.getEnergia();
        int desgFinal = asistenteDeComando.getDesgaste();

        int combConsumido = 0;
        int enerConsumida = 0;
        int desgGenerado = 0;

        if (recursosIniciales != null) {
            combConsumido = recursosIniciales.getCombustible() - combFinal;
            enerConsumida = recursosIniciales.getEnergia() - enerFinal;
            desgGenerado = desgFinal - recursosIniciales.getDesgaste();
        }

        this.informe = new InformeMision(
                nombre,
                resultado,
                asistenteDeComando.getBitacora(),
                combConsumido,
                enerConsumida,
                desgGenerado,
                combFinal,
                enerFinal,
                desgFinal,
                observaciones
        );
        this.etapa = EtapaMision.CERRADA;
        asistenteDeComando.registrar("Mision cerrada: " + nombre + " - " + resultado);
    }

    /**
     * Hook opcional: permite a las subclases transferir recursos o efectuar cambios al cerrar con éxito.
     * PRE: resultado == EXITOSA (se llama dentro de cerrar())
     * POST: impacta de forma definitiva en el estado de la nave si la subclase lo especifica.
     */
    protected void aplicarEfectosFinales() {
        // Por defecto no realiza transferencias adicionales
    }

    public InformeMision getInforme(){
        return informe;
    }

    public ResultadoMision getResultado(){
        return resultado;
    }

    public EtapaMision getEtapa(){
        return etapa;
    }

    public AsistenteDeComando getAsistenteDeComando() { return asistenteDeComando; }

    public Navegador getNavegador() { return navegador; }

    /**
     * Permite a las subclases anotar acciones sin acceder a la lista.
     * @param accion Descripción de la acción realizada
     * PRE: accion != null y no vacía
     * POST: accion queda al final de acciones (el orden cronológico se conserva)
     */
    protected void registrarAccion(String accion) {
        asistenteDeComando.registrar(accion);
    }

    /**
     * @return true si la nave no necesita mantenimiento
     * POST: es una consulta, no modifica la nave
     */
    protected boolean naveOperativa(){
        return !asistenteDeComando.requiereMantenimiento();
    }
}
