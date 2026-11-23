package model.Mision;

import controller.AsistenteDeComando;
import model.Recursos;
import model.Requisitos;
import model.Coordenada;
import model.Universo.Navegador;
import model.Universo.ResultadoAvance;
import model.Universo.ElementoDelUniverso;
import model.Universo.UniversoDinamico;
import exceptions.OperacionRechazadaException;
import model.ClasesBitacora.RegitroBitacora;
import java.util.ArrayList;
import java.util.List;

/**
 * Misión genérica de la nave. Aplica Template Method: realizar() fija el orden
 * preparar -> ejecutarAccion -> evaluar -> cerrar y las subclases solo completan los
 * pasos propios (cumplirObjetivo y cumplioObjetivo).
 * INV: nombre, asistenteDeComando, universo, requisitos y observaciones nunca son null
 * INV: etapa nunca es null y avanza solo en el orden
 *      CREADA -> PREPARADA -> EJECUTADA -> EVALUADA -> CERRADA
 *      (si la misión se rechaza, pasa de CREADA directamente a CERRADA)
 * INV: recursosIniciales != null desde que etapa >= PREPARADA; si fue RECHAZADA queda en null
 * INV: resultado != null cuando etapa es EVALUADA o CERRADA
 *      (una misión rechazada tiene resultado RECHAZADA aunque su etapa todavía sea CREADA)
 * INV: informe != null si y solo si etapa == CERRADA
 * INV: una misión RECHAZADA no modifica la nave; los requisitos se consumen una sola vez
 * INV: la travesía dura como máximo MAX_TURNOS turnos
 * INV: el balance del informe es el del viaje (antes de las recompensas de aplicarEfectosFinales)
 */

public abstract class Mision{
    private final UniversoDinamico universo;
    private final String nombre;
    private final AsistenteDeComando asistenteDeComando;
    private final Coordenada origen;
    private final Coordenada destino;
    private final Requisitos requisitos;

    private EtapaMision etapa;
    private ResultadoMision resultado;
    private Recursos recursosIniciales;
    private String observaciones;
    private InformeMision informe;
    private Navegador navegador;
    private int inicioBitacora;   // desde dónde empiezan los eventos de ESTA misión
    private int gemasIniciales;
    public static final int MAX_TURNOS = 300;   // evita bucle infinito si la nave no puede avanzar
    public static final int MAX_TURNOS_SIN_AVANCE = 10;

    /**
     * @param nombre Nombre de la misión
     * @param asistenteDeComando Asistente encomendado
     * @param universoDinamico Entorno del mapa
     * @param origen Coordenada inicial
     * @param destino Coordenada final
     * @param requisitos Requisitos de la misión
     * PRE: ningún parámetro es null y nombre no es vacío
     * POST: misión en etapa CREADA, sin resultado ni informe
     * @throws IllegalArgumentException si se viola la PRE
     */

    protected Mision(String nombre, AsistenteDeComando asistenteDeComando, UniversoDinamico universoDinamico, Coordenada origen, Coordenada destino, Requisitos requisitos) {
        if (nombre == null || nombre.trim().isEmpty())
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacío");
        if (asistenteDeComando == null)
            throw new IllegalArgumentException("El asistente de comando no puede ser nulo");
        if (universoDinamico == null)
            throw new IllegalArgumentException("El universo no puede ser nulo");
        if (origen == null || destino == null)
            throw new IllegalArgumentException("Las coordenadas no pueden ser nulas");
        if (requisitos == null)
            throw new IllegalArgumentException("Los requisitos no pueden ser nulos");

        this.nombre = nombre;
        this.asistenteDeComando = asistenteDeComando;
        this.universo = universoDinamico;
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
        if (motivo == null || motivo.trim().isEmpty())
            throw new IllegalArgumentException("El motivo del rechazo no puede ser nulo ni vacio");
        resultado = ResultadoMision.RECHAZADA;
        observaciones=motivo;
        asistenteDeComando.registrar("Mision rechazada: " + motivo); //El asistente la guarda en la bitacora.
        return false;
    }

    /**
     * Template Method: fija el orden del ciclo y las subclases no pueden alterarlo.
     * PRE: etapa == CREADA (una misión se realiza una sola vez)
     * @throws IllegalStateException si la misión ya se realizó
     * POST: etapa == CERRADA, resultado != null e informe != null
     *       (también cuando la misión es rechazada o falla)
     *       si resultado == RECHAZADA, la nave no fue modificada
     */

    public final void realizar(){
        // Se valida ANTES de tocar nada: una segunda llamada no debe pisar inicioBitacora
        if (etapa != EtapaMision.CREADA)
            throw new IllegalStateException("La mision ya fue realizada");
        inicioBitacora = asistenteDeComando.getBitacora().getEventos().size();
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
     * POST: si devuelve true, etapa == PREPARADA, recursosIniciales != null, la ruta hacia
     *       destino está calculada (navegador con viaje en curso) y la nave no fue modificada;
     *       se rechaza también si origen/destino están fuera del universo o no hay ruta;
     *       si devuelve false, resultado == RECHAZADA, recursosIniciales == null,
     *       la nave no fue modificada y el motivo quedó en observaciones y en la bitácora
     */

    private boolean preparar(){
        if (etapa != EtapaMision.CREADA)
            throw new IllegalStateException("La mision ya fue preparada");

        //La nave esta disponible?(el motor debe estar disponible)
        //Crear en asistente de comando una funcion que diga si la nave esta disponible!!
        if(!asistenteDeComando.estaDisponible()){
            return rechazar("La nave no esta disponible");
        }

        //Alcanzan los recursos?
        if (!asistenteDeComando.puedeIniciarMision(
                requisitos.getCombustible(),
                requisitos.getEnergia(),
                requisitos.getDesgaste())) {
            return rechazar("Recursos insuficientes para iniciar la misión");
        }

        // Fase 1, paso 2 del enunciado: calcular la ruta con el radar de largo alcance.
        // Si el origen o el destino están fuera del universo, o no hay camino, se rechaza
        // sin consumir nada (antes lanzaba IllegalArgumentException y la misión no cerraba).
        if (!universo.contiene(origen) || !universo.contiene(destino)) {
            return rechazar("Coordenadas fuera del universo");
        }
        Navegador nuevo = new Navegador(asistenteDeComando, universo, origen);
        if (!nuevo.iniciarViaje(destino)) {
            return rechazar("No existe una ruta hacia " + destino);
        }
        this.navegador = nuevo;

        //Guardamos los recursos iniciales, nos servira para obtener el informe final.
        recursosIniciales= new Recursos(
                asistenteDeComando.getCombustible(),
                asistenteDeComando.getEnergia(),
                asistenteDeComando.getDesgaste()
        );
        gemasIniciales = asistenteDeComando.getGemas();

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
        if (etapa != EtapaMision.PREPARADA)
            throw new IllegalStateException("No se puede ejecutar una mision que no fue preparada");
        //Consumimos de la nave considerando los requisitos de la mision.
        asistenteDeComando.consumir(
                requisitos.getCombustible(),
                requisitos.getEnergia(),
                requisitos.getDesgaste()
        );
        asistenteDeComando.registrar("Recursos consumidos");
        if (asistenteDeComando.estaDestruida()) {
            asistenteDeComando.registrar(
                    "Misión fallida: la nave se destruyó al consumir los recursos de inicio");
            this.etapa = EtapaMision.EJECUTADA;
            return;
        }
        universo.ubicarNave(origen);   // el tablero tiene que saber dónde está la nave
        // La ruta ya se calculó en preparar(); el navegador quedó listo con el viaje en curso.

        int turno = 0;
        int sinAvance = 0;
        ElementoDelUniverso ultimo = null;   // para no interactuar dos veces con el mismo elemento
        while (!asistenteDeComando.estaDestruida() && !navegador.haLlegado() && turno < MAX_TURNOS && sinAvance < MAX_TURNOS_SIN_AVANCE) {
            turno++;
            Coordenada antes = navegador.getPosicion();
            boolean seMovio = false;

            try {
                if (navegador.avanzarUnTurno() == ResultadoAvance.SIN_RUTA) {
                    asistenteDeComando.registrar("Sin ruta disponible en este turno");
                }

                universo.ubicarNave(navegador.getPosicion());
                ElementoDelUniverso aca = universo.inspeccionarNave();
                seMovio = !antes.equals(navegador.getPosicion());

                if (aca != null && (seMovio || aca != ultimo)) {
                    asistenteDeComando.interactuarCon(aca);
                }
                ultimo = aca;
            } catch (OperacionRechazadaException e) {
                asistenteDeComando.registrar("Turno " + turno + " rechazado: " + e.getMessage());
            }

            sinAvance = seMovio ? 0 : sinAvance + 1;

            universo.turnoDestino();   // Destino mueve los elementos
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                asistenteDeComando.registrar("Ejecución de travesía interrumpida");
                break;
            }
        }
        if (!navegador.haLlegado() && (turno >= MAX_TURNOS || sinAvance >= MAX_TURNOS_SIN_AVANCE)) {
            asistenteDeComando.registrar("Travesía abortada: la nave no pudo avanzar");
        }

        // Verificación hook en destino si se llegó correctamente
        if (navegador.haLlegado() && !asistenteDeComando.estaDestruida()) {
            verificarEnDestino();
        }

        if (!asistenteDeComando.estaDestruida()) {
            cumplirObjetivo();
        }
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
        if (etapa != EtapaMision.EJECUTADA)
            throw new IllegalStateException("No se puede evaluar una misión no ejecutada");

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
     * Cierra la misión: enfría el motor, mide el balance del viaje, aplica las recompensas y
     * genera el informe inmutable (también si fue rechazada o falló).
     * PRE: resultado != null (fijado por evaluar() o rechazar())
     * POST: informe != null con solo los eventos de esta misión y etapa == CERRADA;
     *       si EXITOSA, el motor quedó Disponible y se aplicaron los efectos finales;
     *       si el enfriamiento o los efectos finales fallan, resultado == FALLIDA y el motivo
     *       queda en observaciones; el informe se genera igual.
     */
    private void cerrar() {
        if (resultado == null)
            throw new IllegalStateException("No se puede cerrar una mision sin resultado");

        // 1) Si el warp dejó el motor en Enfriamiento, un turno más lo devuelve a Disponible.
        //    Va ANTES de las recompensas: el enfriamiento suma desgaste y no debe pisar el mantenimiento.
        if (resultado == ResultadoMision.EXITOSA
                && !asistenteDeComando.estaDisponible() && !asistenteDeComando.estaDestruida()) {
            try {
                asistenteDeComando.saltar();
            } catch (RuntimeException e) {
                resultado = ResultadoMision.FALLIDA;
                observaciones = "No se pudo realizar el salto final";
                asistenteDeComando.registrar("Salto fallido en " + nombre);
            }
        }

        // Solo si la misión llegó a ejecutarse: una RECHAZADA no debe tocar la nave (ni el motor)
        if (recursosIniciales != null
                && !asistenteDeComando.estaDisponible() && !asistenteDeComando.estaDestruida()) {
            asistenteDeComando.reiniciarMotor();   // no se arrastra una maniobra a medias
        }

        // 2) Balance del viaje: se mide ANTES de las recompensas (si no, M-03 daría consumo
        //    negativo y M-01 incluiría lo transferido al aliado)
        int combConsumido = 0;
        int enerConsumida = 0;
        int desgGenerado = 0;
        if (recursosIniciales != null) {
            combConsumido = recursosIniciales.getCombustible() - asistenteDeComando.getCombustible();
            enerConsumida = recursosIniciales.getEnergia() - asistenteDeComando.getEnergia();
            desgGenerado = asistenteDeComando.getDesgaste() - recursosIniciales.getDesgaste();
        }

        // 3) Recompensas / entregas propias de la misión. Si fallan, la misión no queda en el aire:
        //    se marca FALLIDA y el informe se genera igual.
        if (resultado == ResultadoMision.EXITOSA) {
            try {
                aplicarEfectosFinales();
            } catch (RuntimeException e) {
                resultado = ResultadoMision.FALLIDA;
                observaciones = "No se pudieron aplicar los efectos finales: " + e.getMessage();
                asistenteDeComando.registrar("Efectos finales fallidos en " + nombre);
            }
        }

        // Gemas ganadas: se mide después de las recompensas (las entrega aplicarEfectosFinales)
        int gemasGanadas = recursosIniciales == null ? 0 : asistenteDeComando.getGemas() - gemasIniciales;

        asistenteDeComando.registrar("Mision cerrada: " + nombre + " - " + resultado);

        // 4) Informe con SOLO los eventos de esta misión (copia desde inicioBitacora)
        List<RegitroBitacora> todos = asistenteDeComando.getBitacora().getEventos();
        List<RegitroBitacora> propios = new ArrayList<RegitroBitacora>(todos.subList(inicioBitacora, todos.size()));

        this.informe = new InformeMision(
                nombre,
                resultado,
                propios,
                combConsumido,
                enerConsumida,
                desgGenerado,
                asistenteDeComando.getCombustible(),
                asistenteDeComando.getEnergia(),
                asistenteDeComando.getDesgaste(),
                observaciones,
                gemasGanadas
        );
        this.etapa = EtapaMision.CERRADA;
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