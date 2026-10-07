package negocio;
public class AsistenteBasico implements AsistenteDeComando {
    private final Recursos recursos;
    private final Bitacora bitacora;
    private final MotorWarp motor;
    //Los tres son private final: se asignan una vez en el constructor y no cambian, "un asistente, una nave".


    public AsistenteBasico(Recursos r, Bitacora b, MotorWarp m){
        this.recursos = r;
        this.motor = m;
        this.bitacora = b;
    }
    @Override 
    public boolean estaDisponible(){
        return motor.getEstadoActual().getNombreEstado().equals("Disponible");
    }
    @Override 
    public int getCombustible(){
        return motor.getCombustible();
    }
    @Override 
    public int getEnergia(){
        return motor.getEnergia();
    }
    @Override 
    public int getDesgaste(){
        return motor.getDesgaste;
    }
    @Override 
    public puedeConsumir(int combustible, int energia, int desgaste){
        return recursos.puedeConsumir(combustible,energia,desgaste);
    }
    @Override 
    public void consumir(int combustible, int energia, int desgaste){
        try {
            recursos.consumir(combustible,energia,desgaste);
        } catch (IllegalStateException | IllegalArgumentException e) {
            registrar("Consumo rechazado: "+ e.getMessage());
        }
        registrar("Consumo realizado, combustible: "+ combustible +", energia: " + energia +", desgaste: "+ desgaste+".");
    }
    @Override 
    public void cargarCombustible(int cantidad){
        try {
            recursos.cargarCombustible(cantidad);
        } catch (IllegalArgumentException | IllegalStateException e) {
            registrar("Error en la carga de combustible: " + e.getMessage());
        }
        registrar("Carga de combustible realizada, combustible actual: "+ recursos.getCombustible());
    }
    @Override 
    public void cargarEnergia(int cantidad){
        try {
            recursos.cargarEnergia(cantidad);
        } catch (IllegalArgumentException | IllegalStateException e) {
            registrar("Error en la carga de energia: " + e.getMessage());
        }
        registrar("Carga de energia realizada, energia actual: "+ recursos.getEnergia());
    }
    @Override 
    public void realizarMantenimiento(){
        recursos.realizarMantenimiento();
    }
    @Override 
    public boolean prepararSalto(){
        try {
            motor.prepararSalto();
        } catch (IllegalStateException e) {
            registrar(e.getMessage());
            return false;
        }
        registrar("Preparando salto...");
        return true;    
    }
    @Override
    public boolean saltar(){
        try {
            motor.entrarEnWarp();
        } catch (IllegalStateException e) {
            registrar(e.getMessage());
            return false;
        }
        registrar("Salto realizado, entrando en Warp...");
        return true;
    } 
}
