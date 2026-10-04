package negocio;
import java.util.List;
import java.util.ArrayList;

public abstract class Mision{
    private final String nombre;
    private final AsistenteDeComando asistenteDeComando;
    private final Requisitos requisitos;
    private EtapaMision etapa;
    private ResultadoMision resultado;
    private RecursosIniciales recursosIniciales;
    private final List<String> acciones;
    private String observaciones;
    private InformeMision informe;

    protected Mision(String nombre, AsistenteDeComando asistenteDeComando, Requisitos requisitos){
        if (nombre == null || asistenteDeComando == null || requisitos == null)
            throw new IllegalArgumentException("La misión necesita nombre, asistente y requisitos");
        this.nombre=nombre;
        this.asistenteDeComando=asistenteDeComando;
        this.requisitos=requisitos;
        this.etapa= EtapaMision.CREADA;
        this.acciones= new ArrayList<>();
        this.observaciones="";
    }

    private boolean rechazar(String motivo){
        resultado= ResultadoMision.RECHAZADA;
        observaciones=motivo;
        asistenteDeComando.registrar("Mision rechazada: "+motivo); //El asistente la guarda en la bitacora.
        return false;
    }

    public final void realizar(){
        if(preparar()) {
            ejecutarAccion();
            evaluar();
        }
        cerrar();
    }

    private boolean preparar(){
        //Si la mision todavia no fue instanciada, no se puede preparar
        if(etapa!= EtapaMision.CREADA){
            throw new IllegalStateException("La mision ya fue preparada");
        }

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
        recursosIniciales= new RecursosIniciales(
                asistenteDeComando.getCombustible(),
                asistenteDeComando.getEnergia(),
                asistenteDeComando.getDesgaste()
        );

        //Guardamos cronologicamente como se fue desarollando la mision.
        acciones.add("Mision preparada");
        //Ahora la mision esta PREPARADA
        this.etapa= EtapaMision.PREPARADA;
        return true;
    }

    private void ejecutarAccion() {
        //Consumimos de la nave considerando los requisitos de la mision.
        asistenteDeComando.consumir(
                requisitos.getCombustible(),
                requisitos.getEnergia(),
                requisitos.getDesgaste()
        );
        acciones.add("Recursos consumidos");
        cumplirObjetivo();   //propio de cada misión: Nos dice si cumplio el objetivo de la mision.
        this.etapa=EtapaMision.EJECUTADA; //Para este punto la mision ya fue EJECUTADA.
    }

    protected abstract void cumplirObjetivo(); //Redefinida en cada mision distinta

    // Paso común del template
    private void evaluar() {
        if (etapa != EtapaMision.EJECUTADA)
            throw new IllegalStateException("No se puede evaluar una misión no ejecutada");

        if (cumplioObjetivo()) {
            resultado = ResultadoMision.EXITOSA;
        } else {
            resultado = ResultadoMision.FALLIDA;
        }
        asistenteDeComando.registrar("Misión evaluada: " + resultado);
        etapa = EtapaMision.EVALUADA;
    }

    // Hook: cada hija responde si logró su objetivo
    protected abstract boolean cumplioObjetivo();

    private void cerrar(){
        //PRE: el resultado ya tiene que estar decidido
        if(resultado==null)
            throw new IllegalStateException("No se puede cerrar una mision sin resultado");

        if (resultado == ResultadoMision.EXITOSA) {
            boolean salto = asistenteDeComando.prepararSalto() && asistenteDeComando.saltar();
            if (salto) {
                acciones.add("Salto realizado");
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

        informe = new InformeMision(nombre,resultado,acciones,combConsumido,enerConsumida,desgGenerado,combFinal,enerFinal,desgFinal,observaciones);
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

    protected void registrarAccion(String accion) {
        acciones.add(accion);
    }

    protected boolean naveOperativa(){
        return !asistenteDeComando.requiereMantenimiento();
    }
}
