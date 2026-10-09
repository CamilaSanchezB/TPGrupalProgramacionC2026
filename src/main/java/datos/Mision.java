package datos;

import negocio.AsistenteDeComando;

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
    private final Requisitos requisitos;
    private EtapaMision etapa;
    private ResultadoMision resultado;
    private Recursos recursosIniciales;
    private String observaciones;
    private InformeMision informe;

    /**
     * @param nombre Nombre de la misión (aparece en el informe y en la bitácora)
     * @param asistenteDeComando Asistente al que se le encomienda la misión
     * @param requisitos Lo que la misión necesita y consume
     * PRE: nombre != null, asistenteDeComando != null y requisitos != null
     * POST: etapa == CREADA, resultado == null, informe == null y acciones vacía
     */

    protected Mision(String nombre, AsistenteDeComando asistenteDeComando, Requisitos requisitos){
        assert nombre != null && asistenteDeComando != null && requisitos != null : "La misión necesita nombre, asistente y requisitos";
        this.nombre=nombre;
        this.asistenteDeComando=asistenteDeComando;
        this.requisitos=requisitos;
        this.etapa= EtapaMision.CREADA;
        this.observaciones="";
    }

    /**
     * @param motivo Razón del rechazo
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
        asistenteDeComando.registrar("Mision preparada");
        //Ahora la mision esta PREPARADA
        this.etapa= EtapaMision.PREPARADA;
        return true;
    }

    /**
     * Consume los requisitos de la misión y ejecuta su parte propia.
     * PRE: etapa == PREPARADA y la nave puede consumir los requisitos
     *      (lo verificó preparar())
     * POST: combustible y energía de la nave bajan y el desgaste sube según los
     *       requisitos (una sola vez), acciones registra el consumo y la acción propia
     *       de la misión, y etapa == EJECUTADA
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
        cumplirObjetivo();   //propio de cada misión: Nos dice si cumplio el objetivo de la mision.
        this.etapa=EtapaMision.EJECUTADA; //Para este punto la mision ya fue EJECUTADA.
    }

    /**
     * Hook: la parte propia de cada misión (asistir, recolectar, regresar).
     * PRE: etapa == PREPARADA (se llama dentro de ejecutarAccion())
     * POST: la subclase registró su acción con registrarAccion() y su estado interno
     *       refleja que el objetivo se intentó cumplir; no modifica la nave directamente
     */
    protected abstract void cumplirObjetivo();

    // Paso común del template
    /**
     * Decide si la misión fue exitosa o falló.
     * PRE: etapa == EJECUTADA
     * POST: resultado == EXITOSA o FALLIDA (nunca RECHAZADA), la evaluación queda
     *       registrada en la bitácora y etapa == EVALUADA
     */
    private void evaluar() {
        assert etapa == EtapaMision.EJECUTADA : "No se puede evaluar una misión no ejecutada";
        if (cumplioObjetivo()) {
            resultado = ResultadoMision.EXITOSA;
        } else {
            resultado = ResultadoMision.FALLIDA;
        }
        asistenteDeComando.registrar("Misión evaluada: " + resultado);
        etapa = EtapaMision.EVALUADA;
    }

    // Hook: cada hija responde si logró su objetivo
    /**
     * Hook: condición de éxito propia de cada misión (según la Ficha de Inicio).
     * PRE: etapa == EJECUTADA
     * POST: devuelve true si y solo si se cumplió el objetivo; es una consulta
     *       y no modifica la misión ni la nave
     */
    protected abstract boolean cumplioObjetivo();

    /**
     * Cierra la misión y genera siempre el informe (también si fue rechazada o falló).
     * Si la misión resultó EXITOSA, hace que la nave prepare su salto y salte.
     * PRE: resultado != null (lo fijó evaluar() o rechazar())
     * POST: informe != null y etapa == CERRADA;
     *       si el resultado final es EXITOSA, la nave saltó y volvió a estar disponible;
     *       si el salto no pudo realizarse, resultado == FALLIDA y el motivo quedó en
     *       observaciones y en la bitácora;
     *       el informe refleja los recursos consumidos y el estado final de la nave
     */
    private void cerrar(){
        //PRE: el resultado ya tiene que estar decidido
        assert resultado != null : "No se puede cerrar una mision sin resultado";

        if (resultado == ResultadoMision.EXITOSA) {
            boolean salto = asistenteDeComando.prepararSalto() && asistenteDeComando.saltar();
            if (salto) {
                asistenteDeComando.registrar("Salto realizado");
                asistenteDeComando.terminarSalto();   // vuelve a Disponible
            } else {
                resultado= ResultadoMision.FALLIDA;
                observaciones="No se pudo realizar el salto";
                asistenteDeComando.registrar("Salto fallido en "+nombre);
            }
        }

        //Estado final de la nave:
        int combFinal= asistenteDeComando.getCombustible();
        int enerFinal= asistenteDeComando.getEnergia();
        int desgFinal = asistenteDeComando.getDesgaste();

        int combConsumido=0;
        int enerConsumida=0;
        int desgGenerado=0;

        if(recursosIniciales!=null){
            combConsumido=recursosIniciales.getCombustible()-combFinal;
            enerConsumida=recursosIniciales.getEnergia()-enerFinal;
            desgGenerado=desgFinal-recursosIniciales.getDesgaste();
        }

        informe = new InformeMision(nombre,resultado, asistenteDeComando.getBitacora() ,combConsumido,enerConsumida,desgGenerado,combFinal,enerFinal,desgFinal,observaciones);
        etapa=EtapaMision.CERRADA;
        asistenteDeComando.registrar("Mision cerrada: "+ nombre + " - "+ resultado);
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
